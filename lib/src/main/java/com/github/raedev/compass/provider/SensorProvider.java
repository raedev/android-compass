package com.github.raedev.compass.provider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 传感器提供程序
 * @author RAE
 */
public abstract class SensorProvider implements SensorEventListener {

    private final Context mContext;
    protected final SensorManager mSensorManager;
    private final List<Sensor> mSensors = new ArrayList<>();
    private final CompassChangedListener mListener;
    protected final Display mDefaultDisplay;
    private String mMessage = "";
    protected final CompassInfo mCompass;

    public SensorProvider(Context context, CompassInfo compass, CompassChangedListener listener) {
        mListener = listener;
        mContext = context;
        mCompass = compass;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDefaultDisplay = manager.getDefaultDisplay();
    }

    protected int getRotation() {
        return mDefaultDisplay.getRotation();
    }

    protected Context getContext() {
        return mContext;
    }

    protected void register(@Nullable Sensor sensor, String sensorName) {
        if (sensor == null) {
            mMessage += String.format("当前设备不支持%s；", sensorName);
            return;
        }
        mSensors.add(sensor);
    }

    /**
     * 是否可用
     * @return 当前传感器是否可用
     */
    public boolean available() {
        return mSensors.size() > 0;
    }

    public String getMessage() {
        return mMessage;
    }

    /**
     * 注册传感器
     */
    public void register() {
        for (Sensor sensor : mSensors) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * 取消注册
     */
    public void unregister() {
        for (Sensor sensor : mSensors) {
            mSensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Do Nothing...
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do Nothing...
    }

    protected void notifyChanged() {
        mListener.onCompassChanged(mCompass);
    }

    protected void update(float azimuth, float pitch, float roll) {
        mCompass.setAzimuth((int) Math.rint(azimuth));
        mCompass.setPitch((int) -Math.rint(pitch));
        mCompass.setRoll((int) Math.rint(roll));
        notifyChanged();
    }
}
