package org.telegram.pravzhizn.ui.churches.mode.search;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.ui.churches.ChurchesPresenterListener;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.ByName;
import org.telegram.pravzhizn.ui.churches.mode.IPresenter;
import org.telegram.pravzhizn.ui.churches.mode.InfiniteAdapter;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;

/**
 * Created by vlad on 6/5/16.
 */
public class ChurchesSearchPresenter implements InfiniteAdapter.Listener, IPresenter {

    private final PravzhiznConfig mConfig;
    private ChurchesSearchView mView;

    private InfiniteAdapter mChurchesAdapter;
    private ChurchesPresenterListener mListener;

    public ChurchesSearchPresenter(Activity activity, PravzhiznService service, ChurchesPresenterListener listener) {
        mConfig = new PravzhiznConfig(activity);
        mChurchesAdapter = new InfiniteAdapter(activity, service, this);
        mListener = listener;
    }

    @Override
    public void activate(Context context, final ViewGroup root) {
        if (mView == null) {
            mView = new ChurchesSearchView(context, this);
            root.addView(mView.content(), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        if (mView.getListView().getAdapter() != mChurchesAdapter) {
            mView.getListView().setAdapter(mChurchesAdapter);
        }

        final String newHeaderCountryText = textTofillCountryFieldFromConfig(mConfig);
        if (!newHeaderCountryText.equals(mView.getSelectedCountryView().getText().toString())) {
            mView.displaySelectedCountry(newHeaderCountryText);
        }

        final String newHeaderText = textTofillCityFieldFromConfig(mConfig);
        if (!newHeaderText.equals(mView.getSelectedCityView().getText().toString())) {
            mView.displaySelectedCity(newHeaderText);
        }

        mView.hideFAB();
    }

    protected String textTofillCountryFieldFromConfig(final PravzhiznConfig config) {
        if (config.isCountrySelected()) {
            return config.getSelectedCountry().name;
        } else {
            return "---";
        }
    }

    protected String textTofillCityFieldFromConfig(final PravzhiznConfig config) {
        if (config.isUseClosestToMe()) {
            return LocaleController.getString("techranch_closest_to_me", R.string.techranch_closest_to_me);
        } else if (config.isCitySelected()) {
            return config.getSelectedCity().name;
        } else {
            return "---";
        }
    }

    @Override
    public void deactivate(final ViewGroup root) {
        root.removeAllViews();
        mView = null;
    }

    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLoadMore(final int page, final int totalItemsCount) {
        mChurchesAdapter.queryNextPage(totalItemsCount);
    }

    @Override
    public void onRefreshRequested() {
        Toast.makeText(LaunchActivity.sActivity, "Refresh triggered", Toast.LENGTH_SHORT).show();
    }

    public void query(String term) {
        mChurchesAdapter.query(new ByName(term));
    }

    @Override
    public void onStartLoading() {
        mView.hideListView();
        mView.hideFAB();
        mView.hideNoChurchesView();
        mView.showProgressView();
    }

    @Override
    public void onFinishLoading() {
        if (!isActive()) {
            return ;
        }

        mView.hideProgressView();

        final boolean isAnyChurch = mChurchesAdapter.getItemCount() > 0;

        if (isAnyChurch) {
            mView.showListView();
            mView.hideNoChurchesView();
        } else {
            mView.hideListView();
            mView.showNoChurchesView();
        }
    }

    private boolean isActive() {
        return mView != null;
    }

    @Override
    public void onItemSelected(final RemoteChurch selectedChurch) {
        mListener.onItemSelected(selectedChurch);
    }

    @Override
    public void onLongClicked(final int position, final RemoteChurch selectedChurch) {
        mListener.onLongClicked(position, selectedChurch);
    }

    @Override
    public void onAddChurchClicked() {
        mListener.onAddChurchClicked();
    }

    @Override
    public void onSelectCityClicked() {
        mListener.onSelectCityClicked();
    }

    @Override
    public void onSelectCountryClicked() {
        mListener.onSelectCountryClicked();
    }
}
