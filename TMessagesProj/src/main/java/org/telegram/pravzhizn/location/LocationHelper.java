package org.telegram.pravzhizn.location;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.pravzhizn.location.log.DebugLogger;
import org.telegram.pravzhizn.location.log.Logger;

import java.util.List;

public enum LocationHelper {
    INSTANCE;

    // These constants should correspond to values defined in platform/location.hpp
    // Leave 0-value as no any error.
    public static final int ERROR_DENIED = 2;

    private static final long INTERVAL_NAVIGATION_PEDESTRIAN_MS = 5000;


    static final int REQUEST_RESOLVE_ERROR = 101;

    public interface UiCallback {
        Activity getActivity();

        void onLocationUpdated(Location location);

        void onLocationError(final int errorCode);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationUpdated(Location location) {
        }

        @Override
        public void onCompassUpdated(long time, double magneticNorth, double trueNorth, double accuracy) {
        }

        @Override
        public void onLocationError(int errorCode) {
            if (mLocationStopped)
                return;
            notifyLocationError(errorCode);
        }
    };

    private final Logger mLogger = new DebugLogger(LocationHelper.class.getSimpleName());

    private boolean mActive;
    private boolean mLocationStopped;

    private Location mSavedLocation;
    private long mLastLocationTime;

    private BaseLocationProvider mLocationProvider;
    private UiCallback mUiCallback;

    LocationHelper() {
        initProvider(false);
    }

    public void initProvider(boolean forceNative) {
        final Context application = ApplicationLoader.applicationContext;
        final boolean containsGoogleServices = isGooglePlayServicesAvailable(application);

        if (!forceNative && containsGoogleServices) {
            mLocationProvider = new GoogleFusedLocationProvider();
        } else {
            mLocationProvider = new AndroidNativeProvider();
        }
    }

    public boolean isGooglePlayServicesAvailable(Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    public void onLocationUpdated(Location location) {
        mSavedLocation = location;
        mLastLocationTime = System.currentTimeMillis();
        notifyLocationUpdated();
    }

    /**
     * <p>Obtains last known saved location. It depends on "My position" button mode and is erased on "No follow, no position" one.
     * <p>If you need the location regardless of the button's state, use {@link #getLastKnownLocation()}.
     *
     * @return {@code null} if no location is saved or "My position" button is in "No follow, no position" mode.
     */
    public Location getSavedLocation() {
        return mSavedLocation;
    }

    public long getSavedLocationTime() {
        return mLastLocationTime;
    }

    void notifyLocationUpdated() {
        mLogger.d("notifyLocationUpdated()");

        if (mSavedLocation == null) {
            mLogger.d("No saved location - skip");
            return;
        }

        if (mUiCallback != null) {
            mUiCallback.onLocationUpdated(mSavedLocation);
        }
    }

    private void notifyLocationError(int errCode) {
        mLogger.d("notifyLocationError(): " + errCode);

        mUiCallback.onLocationError(errCode);

        if (mLocationListener != null) {
            mLocationListener.onLocationError(errCode);
        }
    }

    boolean isLocationStopped() {
        return mLocationStopped;
    }

    void stop() {
        mLogger.d("stop()");
        mLocationStopped = true;
        stopInternal();
    }

    public boolean onActivityResult(int requestCode, int resultCode) {
        if (requestCode != REQUEST_RESOLVE_ERROR)
            return false;

        mLocationStopped = (resultCode != Activity.RESULT_OK);
        mLogger.d("onActivityResult(): success: " + !mLocationStopped);

        return true;
    }


    long getInterval() {
        return INTERVAL_NAVIGATION_PEDESTRIAN_MS;
    }

    /**
     * Actually starts location polling.
     */
    private void startInternal() {
        mLogger.d("startInternal()");

        mActive = mLocationProvider.start();
        mLogger.d(mActive ? "SUCCESS" : "FAILURE");

        if (!mActive) {
            notifyLocationError(LocationHelper.ERROR_DENIED);
        }
    }

    /**
     * Actually stops location polling.
     */
    private void stopInternal() {
        mLogger.d("stopInternal()");

        mActive = false;
        mLocationProvider.stop();
    }

    /**
     * Attach UI to helper.
     */
    public void attach(UiCallback callback) {
        mUiCallback = callback;

        if (mSavedLocation != null) {
            callback.onLocationUpdated(mSavedLocation);
        }

        startInternal();

    }

    /**
     * Detach UI from helper.
     */
    public void detach() {
        mUiCallback = null;
        stopInternal();
    }

    /**
     * Obtains last known location regardless of "My position" button state.
     *
     * @return {@code null} on failure.
     */
    @Nullable
    public Location getLastKnownLocation(long expirationMs) {
        if (mSavedLocation != null)
            return mSavedLocation;

        AndroidNativeProvider provider = new AndroidNativeProvider() {
            @Override
            public void onLocationChanged(Location location) {
                // Block ancestor
            }
        };

        List<String> providers = provider.filterProviders();
        if (providers.isEmpty())
            return null;

        return provider.findBestNotExpiredLocation(providers, expirationMs);
    }

    @Nullable
    public Location getLastKnownLocation() {
        return getLastKnownLocation(LocationUtils.LOCATION_EXPIRATION_TIME_MILLIS_LONG);
    }
}
