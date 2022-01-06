package com.github.raedev.compass.listener;

import android.content.Context;
import android.hardware.SensorListener;
import android.os.Build;
import android.text.TextUtils;
import android.view.Surface;

import com.github.raedev.compass.entity.CompassInfo;

/**
 * 加速度传感器回调处理
 * @author RAE
 * @date 2022/01/06
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class AccelerometerSensorEventListener extends CompassSensorEventListener implements SensorListener {

    public AccelerometerSensorEventListener(Context context, CompassInfo compass, CompassChangedListener listener) {
        super(context, compass, listener);
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        int rotation = getRotation();
        if (rotation == Surface.ROTATION_90) {
            // 处理横版模式，修复仰角过大问题
            values[0] = fixValue(values);
        }
        update(values[0], values[1], values[2]);
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
        String model = Build.MODEL;
        // 华为Meta10
        if (TextUtils.equals("ALP-AL00", model)) {
            return calcX(x, pitch);
        }
        return x;
    }

    protected float calcX(float x, float pitch) {
        // 当仰角处于[55,125] 产生漂移
        if (pitch >= 55 && pitch <= 125) {
            return (x + 270) % 360;
        }
        return x;
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {
    }
}
