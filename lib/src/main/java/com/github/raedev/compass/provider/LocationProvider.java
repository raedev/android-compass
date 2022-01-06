package com.github.raedev.compass.provider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;

import java.util.List;

/**
 * 位置信息提供
 * @author RAE
 * @date 2022/01/06
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class LocationProvider extends SensorProvider implements LocationListener {

    /**
     * 最后一次的位置信息
     */
    private static final double[] LAST_LOCATION = new double[2];

    private boolean mAvailable = true;
    private final LocationManager mLocationManager;

    public LocationProvider(Context context, CompassInfo compass, CompassChangedListener listener) {
        super(context, compass, listener);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean available() {
        return mAvailable && hasPermission();
    }

    public void setAvailable(boolean available) {
        this.mAvailable = available;
    }

    /**
     * 是否有权限
     * @return 是授权了位置权限
     */
    protected boolean hasPermission() {
        boolean hasPermission = mLocationManager != null;
        hasPermission &= ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        hasPermission &= ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return hasPermission;
    }

    @SuppressLint("MissingPermission")
    @Nullable
    protected Location getLastLocation() {
        List<String> allProviders = mLocationManager.getAllProviders();
        for (String provider : allProviders) {
            if (mLocationManager.isProviderEnabled(provider)) {
                Location location = mLocationManager.getLastKnownLocation(provider);
                if (location != null) {
                    return location;
                }
            }
        }
        if (LAST_LOCATION[0] != 0 && LAST_LOCATION[1] != 0) {
            Location location = new Location("GPS");
            location.setLongitude(LAST_LOCATION[0]);
            location.setLatitude(LAST_LOCATION[1]);
            return location;
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void register() {
        List<String> allProviders = mLocationManager.getAllProviders();
        for (String provider : allProviders) {
            if (mLocationManager.isProviderEnabled(provider)) {
                mLocationManager.requestLocationUpdates(provider, 0, 0, this);
            }
        }
    }

    @Override
    public void unregister() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LAST_LOCATION[0] = location.getLongitude();
        LAST_LOCATION[1] = location.getLatitude();
        mCompass.setLongitude(location.getLongitude());
        mCompass.setLatitude(location.getLatitude());
        mCompass.setLocation(location);
        notifyChanged();
    }

    // region 默认实现

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    // endregion
}
