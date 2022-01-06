package com.github.raedev.compass.listener;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import com.github.raedev.compass.entity.CompassInfo;

/**
 * @author RAE
 * @date 2022/01/06
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public abstract class CompassSensorEventListener {

    protected final Display mDefaultDisplay;
    protected final CompassInfo mCompass;
    protected final CompassChangedListener mListener;

    public CompassSensorEventListener(Context context, CompassInfo compass, CompassChangedListener listener) {
        mCompass = compass;
        mListener = listener;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDefaultDisplay = manager.getDefaultDisplay();
    }

    protected int getRotation() {
        return mDefaultDisplay.getRotation();
    }

    protected void notifyCompassChanged() {
        mListener.onCompassChanged(mCompass);
    }

    protected void update(float azimuth, float pitch, float roll) {
        mCompass.setAzimuth((int) Math.rint(azimuth));
        mCompass.setPitch((int) -Math.rint(pitch));
        mCompass.setRoll((int) Math.rint(roll));
        notifyCompassChanged();
    }
}
