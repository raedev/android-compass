package com.github.raedev.compass;

import android.content.Context;
import android.hardware.SensorListener;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * 指南针传感器
 * @author RAE
 * @date 2021/12/09
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
@SuppressWarnings("deprecation")
public class CompassSensorListener implements SensorListener {

    protected final WindowManager mWindowManager;
    private final Display mDefaultDisplay;

    public CompassSensorListener(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDefaultDisplay = mWindowManager.getDefaultDisplay();
    }

    protected int getRotation() {
        return mDefaultDisplay.getRotation();
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        int rotation = mDefaultDisplay.getRotation();
        if (rotation == Surface.ROTATION_90) {
            // 处理横版模式，修复仰角过大问题
            values[0] = fixValue(values);
        }
    }

    /**
     * 修复方位角横版时仰角过大漂移问题
     * @param values 原始数据
     * @return 方位角
     */
    protected float fixValue(float[] values) {
        // 方位角
        float x = values[0];
        // 仰角
        int pitch = (int) Math.rint(Math.abs(values[1]));
        // 当仰角处于[55,125] 产生漂移
        if (pitch >= 55 && pitch <= 125) {
            float fix = (x + 270) % 360;
            if (BuildConfig.DEBUG) {
                Log.w("rae", "after fix：[" + pitch + "] " + values[0] + " >> " + fix);
            }
            return fix;
        }

        return x;
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
