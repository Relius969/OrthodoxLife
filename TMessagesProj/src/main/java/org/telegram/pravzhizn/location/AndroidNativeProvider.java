package org.telegram.pravzhizn.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import org.telegram.messenger.ApplicationLoader;

class AndroidNativeProvider extends BaseLocationProvider {
    private final LocationManager mLocationManager;
    private boolean mIsActive;

    AndroidNativeProvider() {
        mLocationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected boolean start() {
        if (mIsActive)
            return true;

        List<String> providers = filterProviders();
        if (providers.isEmpty())
            return false;

        mIsActive = true;
        for (String provider : providers)
            mLocationManager.requestLocationUpdates(provider, LocationHelper.INSTANCE.getInterval(), 0, this);

        Location location = findBestNotExpiredLocation(providers, LocationUtils.LOCATION_EXPIRATION_TIME_MILLIS_SHORT);
        if (!isLocationBetterThanLast(location)) {
            location = LocationHelper.INSTANCE.getSavedLocation();
            if (location == null || LocationUtils.isExpired(location, LocationHelper.INSTANCE.getSavedLocationTime(),
                    LocationUtils.LOCATION_EXPIRATION_TIME_MILLIS_SHORT))
                return true;
        }

        onLocationChanged(location);
        return true;
    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void stop() {
        mLocationManager.removeUpdates(this);
        mIsActive = false;
    }

    @Nullable
    @SuppressWarnings("MissingPermission")
    Location findBestNotExpiredLocation(List<String> providers, long expirationMs) {
        Location res = null;
        for (final String pr : providers) {
            final Location l = mLocationManager.getLastKnownLocation(pr);
            if (l != null && !LocationUtils.isExpired(l, l.getTime(), expirationMs)) {
                if (res == null || res.getAccuracy() > l.getAccuracy())
                    res = l;
            }
        }
        return res;
    }

    List<String> filterProviders() {
        List<String> allProviders = mLocationManager.getProviders(false);
        List<String> res = new ArrayList<>(allProviders.size());

        for (final String provider : allProviders) {
            if (LocationManager.PASSIVE_PROVIDER.equals(provider))
                continue;

            if (mLocationManager.isProviderEnabled(provider))
                res.add(provider);
        }

        return res;
    }
}