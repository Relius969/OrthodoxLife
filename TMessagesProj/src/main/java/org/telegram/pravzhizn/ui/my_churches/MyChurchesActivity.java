package org.telegram.pravzhizn.ui.my_churches;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.pravzhizn.responses.SimpleResponse.RemoveMyTempleResponse;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.MyTemples;
import org.telegram.pravzhizn.ui.churches.mode.InfiniteAdapter;
import org.telegram.pravzhizn.ui.churches.mode.InfiniteAdapter.Listener;
import org.telegram.pravzhizn.ui.details.ChurchDetails;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;

/**
 * Created by vlad on 5/24/16.
 */
public class MyChurchesActivity extends BaseFragment implements Listener, NotificationCenter.NotificationCenterDelegate {

    private static final String SCREEN_NAME = "My churches";
    private View mNoChurchesView;
    private RecyclerListView mListView;
    private InfiniteAdapter mChurchesAdapter;
    private PravzhiznService mService;
    private ProgressBar mProgressView;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.churchIsRemovedFromMyChurches);
        trackScreenName(SCREEN_NAME);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.churchIsRemovedFromMyChurches);
    }

    @Override
    public View createView(final Context context) {
        Theme.loadRecources(context);

        configureActionBar();

        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final FrameLayout contentLayout = new FrameLayout(context);

        mListView = new RecyclerListView(context);
        mListView.setVerticalScrollBarEnabled(true);
        mListView.setItemAnimator(null);
        mListView.setInstantClick(true);
        mListView.setLayoutAnimation(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= 11) {
            mListView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
        }

        mRefreshLayout = new SwipeRefreshLayout(context);
        mRefreshLayout.addView(mListView);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });


        mNoChurchesView = configureEmptyView(context);
        contentLayout.addView(mNoChurchesView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        contentLayout.addView(mRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        
        addProgressView(context, contentLayout);

        layout.addView(contentLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 1.f));

        mService = PravzhiznService.instance.create(PravzhiznService.class);
        mChurchesAdapter = new InfiniteAdapter(context, mService, this);
        mListView.setAdapter(mChurchesAdapter);
        mListView.setEmptyView(mNoChurchesView);
        reload();

        fragmentView = layout;
        return fragmentView;
    }

    private void reload() {
        mChurchesAdapter.query(new MyTemples());
    }

    void addProgressView(final Context context, final FrameLayout contentLayout) {
        mProgressView = new ProgressBar(context);
        contentLayout.addView(mProgressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
    }

    private void configureActionBar() {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Techranch_My_Churches", R.string.Techranch_My_Churches));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
    }

    public View configureEmptyView(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.no_my_churches, null);

        TextView no_churches_found = (TextView) view.findViewById(R.id.no_churches_found);
        no_churches_found.setText(LocaleController.getString("Techranch_No_Churches", R.string.Techranch_No_Churches));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStartLoading() {
        mListView.setVisibility(View.GONE);
        mNoChurchesView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishLoading() {
        if (getParentActivity() == null) {
            return;
        }

        mRefreshLayout.setRefreshing(false);
        mProgressView.setVisibility(View.GONE);

//        final boolean isAnyChurch = mChurchesAdapter.getItemCount() > 0;
//
//        if (isAnyChurch) {
//            mRefreshLayout.setVisibility(View.VISIBLE);
//            mListView.setVisibility(View.VISIBLE);
//            mNoChurchesView.setVisibility(View.GONE);
//        } else {
//            mRefreshLayout.setVisibility(View.GONE);
//            mListView.setVisibility(View.GONE);
//            mNoChurchesView.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onItemSelected(final RemoteChurch selectedChurch) {
        presentFragment(new ChurchDetails(selectedChurch));
    }

    @Override
    public void onLongClicked(final int position, final RemoteChurch selectedChurch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));

        builder.setMessage(LocaleController.getString("Techranch_Remove_Church", R.string.Techranch_Remove_Church));
        builder.setNegativeButton(LocaleController.getString("Techranch_Cancel", R.string.Techranch_Cancel), null);
        builder.setPositiveButton(LocaleController.getString("Techranch_Confirm", R.string.Techranch_Confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int i) {
                mService.removeMyTemple(selectedChurch.id).enqueue(new Callback<RemoveMyTempleResponse>() {
                    @Override
                    public void onResponse(final Call<RemoveMyTempleResponse> call, final Response<RemoveMyTempleResponse> response) {
                        mChurchesAdapter.removeChurch(selectedChurch);
                        onFinishLoading();
                    }

                    @Override
                    public void onFailure(final Call<RemoveMyTempleResponse> call, final Throwable t) {

                    }
                });
            }
        });

        showDialog(builder.create(), true);
    }

    @Override
    public void onAddChurchClicked() {

    }

    @Override
    public void onSelectCityClicked() {

    }

    @Override
    public void onSelectCountryClicked() {

    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        if (id == NotificationCenter.churchIsRemovedFromMyChurches) {
            RemoteChurch church = (RemoteChurch) args[0];
            mChurchesAdapter.removeChurch(church);
        }
    }
}