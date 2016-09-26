package org.telegram.pravzhizn.ui.churches.mode.list;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.ViewGroup;
import android.widget.Toast;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.config.PravzhiznConfig;
import org.telegram.pravzhizn.location.LocationHelper;
import org.telegram.pravzhizn.location.LocationHelper.UiCallback;
import org.telegram.pravzhizn.pravzhizn.CityObject;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.ui.churches.ChurchesPresenterListener;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.ByCity;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.ByLocation;
import org.telegram.pravzhizn.ui.churches.mode.ChurchQuery.ByName;
import org.telegram.pravzhizn.ui.churches.mode.IPresenter;
import org.telegram.pravzhizn.ui.churches.mode.InfiniteAdapter;
import org.telegram.ui.Components.LayoutHelper;

/**
 * Created by vlad on 6/5/16.
 */
public class ChurchesPresenter implements
        InfiniteAdapter.Listener,
        IPresenter,
        IChurchView {

    private final PravzhiznConfig mConfig;
    private final Activity mActivity;
    private ChurchesView mView;

    private InfiniteAdapter mChurchesAdapter;
    private ChurchesPresenterListener mListener;
    private Location mCurrentLocation;
    private ChurchQuery mQuery;

    public ChurchesPresenter(final Activity activity, PravzhiznService service, ChurchesPresenterListener listener) {
        mConfig = new PravzhiznConfig(activity);
        mChurchesAdapter = new InfiniteAdapter(activity, service, this);
        mListener = listener;
        mActivity = activity;
    }

    @Override
    public void activate(final Context context, final ViewGroup root) {
        if (mView == null) {
            mView = new ChurchesView(context, this);
            root.addView(mView.content(), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        if (mView.getListView().getAdapter() != mChurchesAdapter) {
            mView.getListView().setAdapter(mChurchesAdapter);
        }

        final String newHeaderCountryText = textTofillCountryFieldFromConfig(mConfig);
        if (!newHeaderCountryText.equals(mView.getSelectedCountryView().getText().toString())) {
            mView.displaySelectedCountry(newHeaderCountryText);
        }

        final String newHeaderCityText = textTofillCityFieldFromConfig(mConfig);
        if (!newHeaderCityText.equals(mView.getSelectedCityView().getText().toString())) {
            mView.displaySelectedCity(newHeaderCityText);
            requestTemples();
        }
    }

    public void requestTemples() {
        if (mConfig.isUseClosestToMe()) {
            if (ActivityCompat.checkSelfPermission(mActivity, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final String[] permissions = {permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION, permission.INTERNET};
                    mActivity.requestPermissions(permissions, 10);
                }
                return;
            }
            onLocationPermissionGranted();
        } else {
            query(mConfig.getSelectedCity());
        }
    }

    public void query(String term) {
        LocationHelper.INSTANCE.detach();
        queryAdapter(new ByName(term));
    }

    public void query(CityObject city) {
        LocationHelper.INSTANCE.detach();
        queryAdapter(new ByCity(city));
    }

    private void requestTemplesByLocation() {
        onStartLoading();
        LocationHelper.INSTANCE.attach(new UiCallback() {
            @Override
            public Activity getActivity() {
                return mActivity;
            }

            @Override
            public void onLocationUpdated(final Location location) {
                handleNewLocation(location);
            }

            @Override
            public void onLocationError(final int errorCode) {
                notityUserToEnableLocationProvider();

//            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            if (intent.resolveActivity(ApplicationLoader.applicationContext.getPackageManager()) == null) {
//                intent = new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS);
//                if (intent.resolveActivity(ApplicationLoader.applicationContext.getPackageManager()) == null)
//                    return;
//            }
//
//            final Intent finIntent = intent;
//            new AlertDialog.Builder(mUiCallback.getActivity())
//                    .setTitle(R.string.enable_location_services)
//                    .setMessage(R.string.location_is_disabled_long_text)
//                    .setNegativeButton(R.string.close, null)
//                    .setPositiveButton(R.string.connection_settings, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (mUiCallback != null)
//                                mUiCallback.getActivity().startActivity(finIntent);
//                        }
//                    }).show();
            }

        });
    }

    void handleNewLocation(final Location newLocation) {
        if (newLocation == null) {
            return;
        }

        if (isBetterLocation(newLocation, mCurrentLocation)) {
            mCurrentLocation = newLocation;
            queryAdapter(new ByLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        }
    }

    private void queryAdapter(ChurchQuery query) {
        mQuery = query;
        mChurchesAdapter.query(query);
    }

    private void notityUserToEnableLocationProvider() {
        String message = LocaleController.getString("techranch_notityUserToEnableLocationProvider", R.string.notityUserToEnableLocationProvider);
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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
        mView.clearRefreshFlag();

        final boolean isAnyChurch = mChurchesAdapter.getItemCount() > 0;

        if (isAnyChurch) {
            mView.showListView();
            mView.showFAB();
            mView.hideNoChurchesView();
        } else {
            mView.hideListView();
            mView.hideFAB();
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

    protected String textTofillCityFieldFromConfig(final PravzhiznConfig config) {
        if (config.isUseClosestToMe()) {
            return LocaleController.getString("techranch_closest_to_me", R.string.techranch_closest_to_me);
        } else if (config.isCitySelected()) {
            return config.getSelectedCity().name;
        } else {
            return "---";
        }
    }

    protected String textTofillCountryFieldFromConfig(final PravzhiznConfig config) {
        if (config.isCountrySelected()) {
            return config.getSelectedCountry().name;
        } else {
            return "---";
        }
    }

    @Override
    public void deactivate(final ViewGroup root) {
        LocationHelper.INSTANCE.detach();
        root.removeAllViews();
        mView = null;
    }

    @Override
    public void onLoadMore(final int page, final int totalItemsCount) {
        mChurchesAdapter.queryNextPage(totalItemsCount);
    }

    @Override
    public void onRefreshRequested() {
        queryAdapter(mQuery);
    }

    @Override
    public void onLocationPermissionGranted() {
        if (mConfig.isUseClosestToMe()) {
            requestTemplesByLocation();
        } else {
            query(mConfig.getSelectedCity());
        }
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

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}