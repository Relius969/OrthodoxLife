package org.telegram.pravzhizn.ui.details;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matelskyvv on 6/2/16.
 */
public class ChurchDetailsAdapter extends Adapter {

    public interface Listener {
        void onPhotosClicked();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    private final RemoteChurch mChurch;
    private final Listener mListener;

    private List<SubItem> mSubItems = new ArrayList<>();

    public ChurchDetailsAdapter(RemoteChurch church, Listener listener) {
        mChurch = church;
        mListener = listener;

        mSubItems.add(SubItem.Photos);
        mSubItems.add(SubItem.Information);

        if (!TextUtils.isEmpty(church.contact_phones)) {
            mSubItems.add(SubItem.Phone);
            mSubItems.add(SubItem.Separator);
        }

        if (!TextUtils.isEmpty(church.site)) {
            mSubItems.add(SubItem.Site);
            mSubItems.add(SubItem.Separator);
        }

        if (canDisplayMap(church)) {
            mSubItems.add(SubItem.Map);
        }
        mSubItems.add(SubItem.Description);
    }

    boolean canDisplayMap(final RemoteChurch church) {
        return church.map_lat != null && church.map_lng != null;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        for (SubAdapter adapter : SubAdapter.values()) {
            if (adapter.toViewType() == viewType) {
                return adapter.toViewHolder(parent);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mSubItems.get(position).toSubAdapter().bind(holder, position, mChurch, mListener);
    }

    @Override
    public int getItemCount() {
        return mSubItems.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return mSubItems.get(position).toSubAdapter().toViewType();
    }

}
