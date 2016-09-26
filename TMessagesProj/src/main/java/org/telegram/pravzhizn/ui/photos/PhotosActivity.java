package org.telegram.pravzhizn.ui.photos;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch.ImageObject;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.util.List;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Created by matelskyvv on 6/23/16.
 */
public class PhotosActivity extends BaseFragment {

    private final List<ImageObject> mImages;
    private static final String SCREEN_NAME = "Pravzhizn church photos";

    public PhotosActivity(List<ImageObject> images) {
        mImages = images;
    }

    @Override
    public boolean onFragmentCreate() {
        trackScreenName(SCREEN_NAME);
        return super.onFragmentCreate();
    }

    @Override
    public View createView(final Context context) {
        Theme.loadRecources(context);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Techranch_Church_Details", R.string.Techranch_Church_Details));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout topLayout = new FrameLayout(context);
        fragmentView = topLayout;

        ViewPager viewPager = new ViewPager(context);
        viewPager.setAdapter(new ImageObjectAdapter(context, mImages));

        topLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        return fragmentView;
    }
}
