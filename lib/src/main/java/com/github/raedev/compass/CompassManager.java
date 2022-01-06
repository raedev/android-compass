package com.github.raedev.compass;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;
import com.github.raedev.compass.provider.DefaultSensorProvider;
import com.github.raedev.compass.provider.LocationProvider;
import com.github.raedev.compass.provider.SensorProvider;
import com.github.raedev.compass.provider.VectorSensorProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 指南针管理器，提供方位角、俯仰角、侧倾角、位置信息数据。<br>
 * 推荐使用{@link #register(LifecycleOwner)} 注册监听，自动管理生命周期；<br>
 * 否则则需手动管理start(),pause(), destroy();
 * @author RAE
 * @date 2021/12/08
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
@SuppressWarnings({"deprecation", "unused"})
public final class CompassManager implements LifecycleEventObserver {

    private final List<CompassChangedListener> mListeners = new ArrayList<>();
    private final CompassInfo mCompass = new CompassInfo();
    private final Context mContext;
    private final CompassChangedListener mProxyEventListener;
    /**
     * 是否允许位置
     */
    private boolean mEnableLocation = true;
    /**
     * 是否运行中
     */
    private boolean mRunning;
    private SensorProvider mCurrentSensorProvider;
    private LocationProvider mLocationProvider;

    public CompassManager(Context context) {
        mContext = context;
        mProxyEventListener = new CompassChangedListener() {
            @Override
            public void onCompassChanged(CompassInfo compass) {
                for (CompassChangedListener listener : mListeners) {
                    listener.onCompassChanged(compass);
                }
            }

            @Override
            public void onCompassException(Exception e) {
                for (CompassChangedListener listener : mListeners) {
                    listener.onCompassException(e);
                }
            }
        };
        mLocationProvider = new LocationProvider(mContext, mCompass, mProxyEventListener);
    }

    public void setEnableLocation(boolean enableLocation) {
        mLocationProvider.setAvailable(enableLocation);
    }

    public void addCompassChangedListener(CompassChangedListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * 注册回调监听
     * @param owner
     */
    public void register(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    /**
     * 开始监听
     */
    public void start() {
        if (mRunning) {
            // 防止重复注册
            return;
        }
        mRunning = true;
        if (mLocationProvider.available()) {
            mLocationProvider.register();
        }
        mCurrentSensorProvider = new VectorSensorProvider(mContext, mCompass, mProxyEventListener);
        if (mCurrentSensorProvider.available()) {
            mCurrentSensorProvider.register();
            return;
        }
        mCurrentSensorProvider = new DefaultSensorProvider(mContext, mCompass, mProxyEventListener);
        if (!mCurrentSensorProvider.available()) {
            mProxyEventListener.onCompassException(new UnsupportedOperationException(mCurrentSensorProvider.getMessage()));
            return;
        }
        mCurrentSensorProvider.register();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            start();
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            pause();
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            destroy();
            source.getLifecycle().removeObserver(this);
        }
    }


    /**
     * 停止监听
     */
    public void pause() {
        // 取消注册
        mLocationProvider.unregister();
        if (mCurrentSensorProvider != null) {
            mCurrentSensorProvider.unregister();

        }
        mRunning = false;
    }

    /**
     * 释放资源
     */
    public void destroy() {
        mRunning = false;
        mCurrentSensorProvider = null;
        mListeners.clear();
    }

}
