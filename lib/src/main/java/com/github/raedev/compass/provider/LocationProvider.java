package com.github.raedev.compass.provider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;

import java.util.ArrayList;
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
    private static Location LAST_LOCATION = null;

    private boolean mAvailable = true;
    private final LocationManager mLocationManager;

    @Nullable
    public static Location getLocation() {
        return LAST_LOCATION;
    }

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
        if (LAST_LOCATION != null) {
            return LAST_LOCATION;
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void register() {
        List<String> allProviders = new ArrayList<>();
        allProviders.add(LocationManager.GPS_PROVIDER);
        allProviders.add(LocationManager.NETWORK_PROVIDER);
        for (String provider : allProviders) {
            if (mLocationManager.isProviderEnabled(provider)) {
                Log.d("rae", "注册位置监听：" + provider);
                mLocationManager.requestLocationUpdates(provider, 1000, 1, this, Looper.getMainLooper());
            } else {
                Log.e("rae", "注册位置不可用：" + provider);
            }
        }
        Location lastLocation = getLastLocation();
        if (lastLocation != null) {
            Log.i("rae", "最后一次位置：" + lastLocation);
            onLocationChanged(lastLocation);
        }
    }

    @Override
    public void unregister() {
        Log.w("rae", "取消位置监听");
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LAST_LOCATION = location;
        mCompass.setLongitude(location.getLongitude());
        mCompass.setLatitude(location.getLatitude());
        mCompass.setLocation(location);
        Log.d("rae", "onLocationChanged: " + location.getLongitude() + "," + location.getLatitude());
        notifyChanged();
    }

    // region 默认实现

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        final int size = locations.size();
        for (int i = 0; i < size; i++) {
            onLocationChanged(locations.get(i));
        }
    }

    @Override
    public void onFlushComplete(int requestCode) {
        Log.d("rae", "onFlushComplete: " + requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("rae", "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("rae", "onProviderDisabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("rae", "onStatusChanged: " + provider);
    }

    // endregion
}
