package org.telegram.pravzhizn.ui.photos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.ui.add_church.AddChurch.BitmapWithPath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matelskyvv on 7/18/16.
 */
public class BitmapsAdapter extends RecyclerView.Adapter<BitmapViewHolder> {

    private Context mContext;
    private final BitmapsAdapterListener mListener;

    private List<BitmapWithPath> mData = new ArrayList<>();

    public void removeItem(final BitmapWithPath bitmapWithPath) {
        int position = mData.indexOf(bitmapWithPath);
        mData.remove(bitmapWithPath);
        notifyItemRemoved(position);

        if (mData.isEmpty()) {
            mListener.onEmptyDataSet();
        }
    }

    public interface BitmapsAdapterListener {
        void onImageDeleted(final BitmapWithPath bitmap);

        void onEmptyDataSet();
    }

    public BitmapsAdapter(
            Context context,
            final List<BitmapWithPath> data,
            BitmapsAdapterListener listener) {
        mContext = context;
        mListener = listener;
        mData = data;
    }

    @Override
    public BitmapViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.techranch_image_list_item, parent, false);
        return new BitmapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BitmapViewHolder holder, final int position) {
        BitmapWithPath bitmapWithPath = mData.get(position);
        holder.bind(bitmapWithPath, position, mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}