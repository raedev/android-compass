package com.github.raedev.compass.provider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;

/**
 * @author RAE
 * @date 2022/01/06
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class DefaultSensorProvider extends SensorProvider implements SensorListener {

    public DefaultSensorProvider(Context context, CompassInfo compass, CompassChangedListener listener) {
        super(context, compass, listener);
        register(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), "加速度计传感器");
    }

    @Override
    public void register() {
        mSensorManager.registerListener(this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void unregister() {
        mSensorManager.unregisterListener((SensorListener) this);
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        int rotation = getRotation();
        AccelerometerSensorCalc.calc(rotation, values);
        update(values[0], values[1], values[2]);
    }


    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
