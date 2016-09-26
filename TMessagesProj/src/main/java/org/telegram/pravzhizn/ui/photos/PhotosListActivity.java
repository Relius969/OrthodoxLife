package org.telegram.pravzhizn.ui.photos;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.ui.add_church.AddChurch.BitmapWithPath;
import org.telegram.pravzhizn.ui.photos.BitmapsAdapter.BitmapsAdapterListener;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.List;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Created by matelskyvv on 7/18/16.
 */
public class PhotosListActivity extends BaseFragment {

    private static final String SCREEN_NAME = "Church request photos";
    private final List<BitmapWithPath> mImages;
    private RecyclerListView mListView;
    private BitmapsAdapter mAdapter;

    public PhotosListActivity(List<BitmapWithPath> images) {
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

        configureListView(context);
        mAdapter = new BitmapsAdapter(getParentActivity(), mImages, new BitmapsAdapterListener() {
            @Override
            public void onImageDeleted(final BitmapWithPath bitmap) {
                mAdapter.removeItem(bitmap);
            }

            @Override
            public void onEmptyDataSet() {
                finishFragment();
            }
        });
        mListView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(final RecyclerView recyclerView, final ViewHolder viewHolder, final ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                mAdapter.removeItem(((BitmapViewHolder)viewHolder).getBitmapByPath());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mListView);

        topLayout.addView(mListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        return fragmentView;
    }

    void configureListView(final Context context) {
        mListView = new RecyclerListView(context);
        mListView.setVerticalScrollBarEnabled(true);
        mListView.setItemAnimator(null);
        mListView.setLayoutAnimation(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= 11) {
            mListView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        }
    }

}
