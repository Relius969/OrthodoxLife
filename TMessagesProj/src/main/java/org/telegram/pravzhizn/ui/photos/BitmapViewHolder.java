package org.telegram.pravzhizn.ui.photos;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.ui.add_church.AddChurch.BitmapWithPath;
import org.telegram.pravzhizn.ui.photos.BitmapsAdapter.BitmapsAdapterListener;

/**
 * Created by matelskyvv on 7/18/16.
 */
public class BitmapViewHolder extends ViewHolder {

    ImageView mImagePlace;
    View mDeleteImageButton;
    private BitmapWithPath mBitmap;

    public BitmapViewHolder(final View itemView) {
        super(itemView);
        mImagePlace = (ImageView) itemView.findViewById(R.id.techranch_image);
        mDeleteImageButton = itemView.findViewById(R.id.techranch_delete_button);
    }

    public void bind(final BitmapWithPath bitmap, final int position, final BitmapsAdapterListener listener) {
        mBitmap = bitmap;
        mImagePlace.setImageBitmap(bitmap.getBitmap());
        mDeleteImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                listener.onImageDeleted(bitmap);
            }
        });
    }

    public BitmapWithPath getBitmapByPath() {
        return mBitmap;
    }
}
