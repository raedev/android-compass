//package com.github.raedev.compass.listener;
//
//import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.view.Surface;
//
//import com.github.raedev.compass.entity.CompassInfo;
//
///**
// * 加速度传感器回调处理
// * @author RAE
// * @date 2022/01/06
// * Copyright (c) https://github.com/raedev All rights reserved.
// */
//public class VectorSensorEventListener extends CompassSensorEventListener implements SensorEventListener {
//
//    private final float[] mRotationMatrix = new float[9];
//    private final float[] mOrientationAngles = new float[3];
//
//    public VectorSensorEventListener(Context context, CompassInfo compass, CompassChangedListener listener) {
//        super(context, compass, listener);
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
//            updateAzimuth(event.values);
//        }
//
//        // 加速度
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
////            SensorManager.getRotationMatrix(mRotationMatrix, null, event.values, null);
////            float[] orientation = SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
////            float degrees = (float) Math.toDegrees(orientation[0]);
////            Log.d("rae", "onSensorChanged: " + degrees);
//        }
//    }
//
//
//    private void updateAzimuth(float[] values) {
//        SensorManager.getRotationMatrixFromVector(mRotationMatrix, values);
//        float[] orientation = SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
//        float degrees = (float) Math.toDegrees(orientation[0]);
//        float pitch = (float) Math.toDegrees(orientation[1]);
//        float roll = (float) Math.toDegrees(orientation[2]);
//        float azimuth = (degrees + getDisplayRotation() + 360) % 360f;
//        int rotation = getRotation();
//        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
//            // 横屏时候，两个角度互换
//            float temp = roll;
//            roll = -pitch;
//            pitch = temp;
//        }
//        update(azimuth, pitch, roll);
//    }
//
//    private float getDisplayRotation() {
//        int rotation = getRotation();
//        if (rotation == Surface.ROTATION_90) {
//            return 90f;
//        } else if (rotation == Surface.ROTATION_180) {
//            return 180f;
//        } else if (rotation == Surface.ROTATION_270) {
//            return 270f;
//        }
//        return 0;
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//}
