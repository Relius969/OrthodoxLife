package org.telegram.pravzhizn.ui.chat;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch.ImageObject;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;


import java.util.List;


import static org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight;

/**
 * Created by vlad on 6/13/16.
 */
public class ChurchChatHeaderContainer extends FrameLayout {

    private final RemoteChurch mChurch;

    private ImageView mChurchThumb;
    private SimpleTextView mTitleTextView;
    private boolean mOccupyStatusBar = Build.VERSION.SDK_INT >= 21;

    public ChurchChatHeaderContainer(final Context context, RemoteChurch church) {
        super(context);
        mChurch = church;

        String thumbnailUrl = thumbnailImageUrl();
        mChurchThumb = new ImageView(context);
        addView(mChurchThumb);
        if (thumbnailUrl != null) {
            Glide.with(context)
                    .load(thumbnailUrl)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(mChurchThumb) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            setChurchThumb(resource, context);
                        }
                    });
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.church_logo);
            setChurchThumb(bitmap, context);
        }

        mTitleTextView = new SimpleTextView(context);
        mTitleTextView.setTextColor(Theme.ACTION_BAR_TITLE_COLOR);
        mTitleTextView.setTextSize(18);
        mTitleTextView.setGravity(Gravity.LEFT);
        mTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        mTitleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        mTitleTextView.setRightDrawableTopPadding(-AndroidUtilities.dp(1.3f));

        mTitleTextView.setText(church.title);

        addView(mTitleTextView);
    }

    private void setChurchThumb(final Bitmap resource, final Context context) {
        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
        circularBitmapDrawable.setCircular(true);

        mChurchThumb.setImageDrawable(circularBitmapDrawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - AndroidUtilities.dp(54 + 16);
        mChurchThumb.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42), MeasureSpec.EXACTLY));

        mTitleTextView.setTextSize(!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 18 : 20);
        mTitleTextView.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24), MeasureSpec.AT_MOST));

        setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int actionBarHeight = getCurrentActionBarHeight();
        int additionalTop = mOccupyStatusBar ? AndroidUtilities.statusBarHeight : 0;
        int viewTop = (actionBarHeight - AndroidUtilities.dp(42)) / 2 + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);


        mChurchThumb.layout(AndroidUtilities.dp(8), viewTop, AndroidUtilities.dp(42 + 8), viewTop + AndroidUtilities.dp(42));
        final int textLeft = AndroidUtilities.dp(8 + 54);

        int textTop = (getCurrentActionBarHeight() - mTitleTextView.getTextHeight()) / 2;
        mTitleTextView.layout(textLeft, additionalTop + textTop, textLeft + mTitleTextView.getMeasuredWidth(), additionalTop + textTop + mTitleTextView.getTextHeight());
    }

    @Nullable
    private String thumbnailImageUrl() {
        final List<ImageObject> images = mChurch.images;
        if (images.size() > 0) {
            return images.get(0).thumb_url;
        }
        return null;
    }
}
