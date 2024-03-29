package com.github.raedev.compass.provider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.Surface;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;

/**
 * @author RAE
 * @date 2022/01/06
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class VectorSensorProvider extends SensorProvider {

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private final DefaultSensorProvider mDefaultSensorProvider;
    private int mDefaultAzimuth;
    private int mDefaultPitch;

    public VectorSensorProvider(Context context, CompassInfo compass, CompassChangedListener listener) {
        super(context, compass, listener);
        register(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), "旋转矢量传感器");
        mDefaultSensorProvider = new DefaultSensorProvider(context, compass, new CompassChangedListener() {
            @Override
            public void onCompassChanged(CompassInfo compass) {
                mDefaultAzimuth = compass.getAzimuth();
                mDefaultPitch = compass.getPitch();
            }

            @Override
            public void onCompassException(Exception e) {

            }
        });
    }

    @Override
    public void register() {
        super.register();
        mDefaultSensorProvider.register();
    }

    @Override
    public void unregister() {
        super.unregister();
        mDefaultSensorProvider.unregister();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            updateAzimuth(values);
        }
    }

    private void updateAzimuth(float[] values) {
        SensorManager.getRotationMatrixFromVector(mRotationMatrix, values);
        float[] orientation = SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        float degrees = (float) Math.toDegrees(orientation[0]);
        float pitch = (float) Math.toDegrees(orientation[1]);
        float roll = (float) Math.toDegrees(orientation[2]);
        float azimuth = (degrees + getDisplayRotation() + 360) % 360f;
        int rotation = getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            // 横屏时候，两个角度互换
            float temp = roll;
            roll = -pitch;
            pitch = temp;
        } else if (mDefaultPitch >= 90) {
            // 修正当前手机角度大于90度时，方位角往背后方向偏移。
            azimuth = mDefaultAzimuth;
            pitch = -mDefaultPitch;
        }
        update(azimuth, pitch, roll);
    }

    private float getDisplayRotation() {
        int rotation = getRotation();
        if (rotation == Surface.ROTATION_90) {
            return 90f;
        } else if (rotation == Surface.ROTATION_180) {
            return 180f;
        } else if (rotation == Surface.ROTATION_270) {
            return 270f;
        }
        return 0;
    }
}
