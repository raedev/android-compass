package com.github.raedev.compass;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;

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
@SuppressWarnings("deprecation")
public final class CompassManager extends CompassSensorListener implements LifecycleEventObserver, LocationListener {

    /**
     * 最后一次的位置信息
     */
    private static double[] sLastLocation = new double[2];

    private final List<CompassChangedListener> mListeners = new ArrayList<>();
    /**
     * 是否允许位置
     */
    private boolean mEnableLocation;
    /**
     * 是否运行中
     */
    private boolean mRunning;
//    /**
//     * 加速度
//     */
//    private float[] mAccelerometerReading = new float[3];
//    /**
//     * 磁场
//     */
//    private float[] mMagnetometerReading = new float[3];
//    private final float[] mRotationMatrix = new float[9];
//    private final float[] mCoordinateMatrix = new float[9];
//    private final float[] mOrientationAngles = new float[3];

    private final Context mContext;
    private final SensorManager mSensorManager;
    private final LocationManager mLocationManager;
    //    private final WindowManager mWindowManager;
    private final CompassInfo mCompass;

    public CompassManager(Context context) {
        mContext = context;
        mEnableLocation = true;
        mCompass = new CompassInfo();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setEnableLocation(boolean enableLocation) {
        mEnableLocation = enableLocation;
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

    private void notifyException(Exception e) {
        for (CompassChangedListener listener : mListeners) {
            listener.onCompassException(e);
        }
    }

    private void notifyCompassChanged() {
        for (CompassChangedListener listener : mListeners) {
            listener.onCompassChanged(mCompass);
        }
    }

    /**
     * 开始监听
     */
    public void start() {
        if (mRunning) {
            Log.w("rae", "CompassManager已经运行，请先停止");
            return;
        }
        registerLocation();
        registerSensor();
        mRunning = true;
    }


    /**
     * 注册位置监听
     */
    private void registerLocation() {
        try {
            if (!mEnableLocation) {
                Log.w("rae", "配置不启用位置监听");
                return;
            }
            if (mLocationManager == null
                    || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                    || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                notifyException(new UnsupportedOperationException("没有位置权限"));
                return;
            }
            List<String> allProviders = mLocationManager.getAllProviders();
            for (String provider : allProviders) {
                if (mLocationManager.isProviderEnabled(provider)) {
                    mLocationManager.requestLocationUpdates(provider, 0, 0, this);
                    Location location = mLocationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        onLocationChanged(location);
                    }
                }
            }
            // 最后一次位置信息回调
            if (sLastLocation[0] != 0 && sLastLocation[1] != 0) {
                Location location = new Location("GPS");
                location.setLongitude(sLastLocation[0]);
                location.setLatitude(sLastLocation[1]);
                onLocationChanged(location);
            }
        } catch (Exception e) {
            notifyException(e);
        }
    }

    /**
     * 注册传感器
     */
    private void registerSensor() {
        if (mSensorManager == null) {
            return;
        }

//        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        Sensor magneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//
//        // 加速度+地磁场传感器
//        if (accelerometer != null && magneticField != null) {
//            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            mSensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//        // 旋转矢量传感器
//        else if (rotationVectorSensor != null) {
//            notifyException(new UnsupportedOperationException("当前手机不支持磁场加速度传感器，俯仰角和侧倾角无法获取。"));
//            mSensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        } else {
//            notifyException(new UnsupportedOperationException("当前手机传感器无法满足方位角功能需求"));
//        }

        mSensorManager.registerListener(this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, Sensor.TYPE_GYROSCOPE, SensorManager.SENSOR_DELAY_NORMAL);
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

//
//    /**
//     * 传感器回调
//     * @param event
//     */
//    @Override
//    protected void onSensorChanged(SensorEvent event, float[] vr) {
//        super.onSensorChanged(event, vr);
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            Log.d("rae", "onSensorChanged: " + Arrays.toString(vr));
//        }
//    }


//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        float[] values = event.values;
//        int type = event.sensor.getType();
//
//        if (type == Sensor.TYPE_ACCELEROMETER) {
//            // 加速度传感器
//            mAccelerometerReading = values;
//            return;
//        }
//        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
//            // 磁场传感器（方位角）
//            mMagnetometerReading = values;
//            int[] xyz = calcAzimuth(values, mAccelerometerReading, mMagnetometerReading);
//            if (enableCallback(xyz)) {
//                mCompass.setAzimuth(xyz[0]);
//                mCompass.setPitch(xyz[1]);
//                mCompass.setRoll(xyz[2]);
//                notifyCompassChanged();
//            }
//            return;
//        }
//        if (type == Sensor.TYPE_ROTATION_VECTOR) {
//            // 旋转矢量传感器
//            int[] xyz = calcAzimuth(event.values, null, null);
//            if (enableCallback(xyz)) {
//                // 只有方位角
//                mCompass.setAzimuth(xyz[0]);
//                notifyCompassChanged();
//            }
//        }
//    }


    @Override
    public void onSensorChanged(int sensor, float[] values) {
        super.onSensorChanged(sensor, values);
        mCompass.setAzimuth((int) Math.rint(values[0]));
        mCompass.setPitch((int) -Math.rint(values[1]));
        mCompass.setRoll((int) Math.rint(values[2]));
        notifyCompassChanged();
    }

//    /**
//     * 检查是否满足回调标准
//     * @param xyz 方位角信息
//     * @return
//     */
//    private boolean enableCallback(float[] xyz) {
//        if (xyz == null) {
//            return false;
//        }
//        // 方位角偏差1度的时候回调
//        if (Math.abs(xyz[0] - mCompass.getAzimuth()) < 1) {
//            return false;
//        }
//        return true;
//    }

//
//    /**
//     * 计算方位角<br>
//     * 当用（磁场+加速度）得到的数据范围是[-180,180] <br>
//     * 也就是说，0表示正北，90表示正东，180/-180表示正南，-90表示正西。而直接通过方向感应器数据范围是（0～360）360/0表示正北，90表示正东，180表示正南，270表示正西。<br>
//     * <p>返回值说明：[0] 方位角、[1] 俯仰角、[2] 侧倾角</p>
//     * @param vr float[3] 当前传感器的值
//     * @param accelerometerReading float[3] 加速度传感器值
//     * @param magnetometerReading float[3] 磁场传感器值
//     * @return float[3]
//     */
//    private int[] calcAzimuth(float[] vr, float[] accelerometerReading, float[] magnetometerReading) {
//        int x = 0, y = 0, z = 0;
//        // 旋转坐标
//        int[] axis = getOrientationAxis();
//        SensorManager.remapCoordinateSystem(mRotationMatrix, axis[0], axis[1], mCoordinateMatrix);
//        // 计算R向量
//        if (accelerometerReading != null && magnetometerReading != null) {
//            if (!SensorManager.getRotationMatrix(mRotationMatrix, null, accelerometerReading, magnetometerReading)) {
//                // 官方文档：失败时为false （例如，如果设备处于自由落体状态）。 自由落体定义为重力大小小于标称值的 1/10 的情况。 失败时不修改输出矩阵。
//                return null;
//            }
//        } else {
//            // 没有磁场加速度传感器的情况
//            SensorManager.getRotationMatrixFromVector(mRotationMatrix, vr);
//        }
//        // 计算角度，取值为弧度
//        SensorManager.getOrientation(mCoordinateMatrix, mOrientationAngles);
//        // 方位角 [0,360]
//        x = (int) fixAzimuthAngle(Math.toDegrees(mOrientationAngles[0]));
//        // 俯仰角 [-90,90]
//        y = (int) Math.rint(Math.toDegrees(mOrientationAngles[1]));
//        // 侧倾角 [-180,180]
//        z = (int) Math.rint(Math.toDegrees(mOrientationAngles[2]));
//
//        // 但俯仰角处于[85,90]
//        return new int[]{x, y, z};
//    }
//
//
//    /**
//     * 根据当前屏幕方向获取坐标系
//     * 返回值：
//     * int[0] = axisX;
//     * int[1] = axisY;
//     * @return int[2]
//     */
//    private int[] getOrientationAxis() {
//        int axisX;
//        int axisY;
//        int rotation = mWindowManager.getDefaultDisplay().getRotation();
//        switch (rotation) {
//            case Surface.ROTATION_90:
//                axisX = SensorManager.AXIS_Y;
//                axisY = SensorManager.AXIS_MINUS_X;
//                break;
//            case Surface.ROTATION_180:
//                axisX = SensorManager.AXIS_MINUS_X;
//                axisY = SensorManager.AXIS_MINUS_Y;
//                break;
//            case Surface.ROTATION_270:
//                axisX = SensorManager.AXIS_MINUS_Y;
//                axisY = SensorManager.AXIS_X;
//                break;
//            default:
//                axisX = SensorManager.AXIS_X;
//                axisY = SensorManager.AXIS_Y;
//                break;
//        }
//        return new int[]{
//                axisX, axisY
//        };
//    }
//
//    /**
//     * 方位角[-180,180] 转成 [0,360]
//     * @param azimuth 方位角
//     * @return 角度方位角
//     */
//    private float fixAzimuthAngle(double azimuth) {
//        float value = (float) (azimuth >= 0 ? azimuth : (360 + azimuth) % 360);
//        value = (float) Math.rint(value);
//        return value;
//    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        sLastLocation[0] = location.getLongitude();
        sLastLocation[1] = location.getLatitude();
        mCompass.setLongitude(location.getLongitude());
        mCompass.setLatitude(location.getLatitude());
        mCompass.setLocation(location);
        notifyCompassChanged();
    }

    /**
     * 停止监听
     */
    public void pause() {
        // 取消注册
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        mSensorManager.unregisterListener(this);
        mRunning = false;
    }

    /**
     * 释放资源
     */
    public void destroy() {
        mRunning = false;
        mListeners.clear();
    }

}
