package org.telegram.pravzhizn.location;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

abstract class BaseLocationProvider implements LocationListener {
    private static final double DEFAULT_SPEED_MPS = 5;

    protected boolean isLocationBetterThanLast(Location newLocation, Location lastLocation) {
        double speed = Math.max(DEFAULT_SPEED_MPS, (newLocation.getSpeed() + lastLocation.getSpeed()) / 2.0);
        double lastAccuracy = (lastLocation.getAccuracy() + speed * LocationUtils.getDiff(lastLocation, newLocation));
        return (newLocation.getAccuracy() < lastAccuracy);
    }

    final boolean isLocationBetterThanLast(Location newLocation) {
        if (newLocation == null)
            return false;

        final Location lastLocation = LocationHelper.INSTANCE.getSavedLocation();
        return (lastLocation == null || isLocationBetterThanLast(newLocation, lastLocation));
    }

    @Override
    public void onLocationChanged(Location location) {
        // Completely ignore locations without lat and lon
        if (location.getAccuracy() <= 0.0)
            return;

        if (isLocationBetterThanLast(location)) {
            LocationHelper.INSTANCE.onLocationUpdated(location);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    protected abstract boolean start();

    protected abstract void stop();
}
