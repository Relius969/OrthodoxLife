package org.telegram.pravzhizn.ui.photos;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;

import org.telegram.pravzhizn.pravzhizn.RemoteChurch.ImageObject;

import java.util.List;

/**
 * Created by matelskyvv on 6/23/16.
 */
public class ImageObjectAdapter extends PagerAdapter {

    private final List<ImageObject> mImages;
    private final Context mContext;

    public ImageObjectAdapter(Context context, List<ImageObject> images) {
        mImages = images;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ScaleType.CENTER);

        Glide.with(mContext)
                .load(mImages.get(i).big_url)
                .into(mImageView);

        container.addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        container.removeView((ImageView) obj);
    }
}
