package org.telegram.pravzhizn.ui.profile.saint;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.SaintObject;

import java.util.List;

/**
 * Created by vlad on 9/8/16.
 */
public class SaintsAdapter extends RecyclerView.Adapter<SaintViewHolder> {

    private final List<SaintObject> mItems;
    private final Listener mListener;

    public interface Listener {
        void onSaintClicked(SaintObject saint);
    }

    public SaintsAdapter(final List<SaintObject> data, final Listener listener) {
        super();
        mItems = data;
        mListener = listener;
    }

    @Override
    public SaintViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new SaintViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.saint_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(final SaintViewHolder holder, final int position) {
        holder.bind(mItems.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
