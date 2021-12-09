package com.github.raedev.compass.entity;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

/**
 * 指南针实体
 * @author RAE
 * @date 2021/12/08
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class CompassInfo implements Parcelable {

    /**
     * [0,360]方位角<br>
     * 0 = 北，90 = 东，180 = 南，270 = 西
     */
    private int azimuth;

    /**
     * 俯仰角[-180,180] <br>
     * 手机朝上时，取值变化为[0,90,180] <br>
     * 手机朝下时，取值变化为[0,-90,180]
     */
    private int pitch;

    /**
     * 侧倾角[-90,90] <br>
     * 手机朝上时，从左到右取值变化为[0,90,0]，从右到左取值变化为[0,-90,0] <br>
     * 手机朝下时，从左到右取值变化为[0,-90,0]，从右到左取值变化为[0,90,0]
     */
    private int roll;

    /**
     * 经度
     */
    private double longitude = Double.NaN;

    /**
     * 纬度
     */
    public double latitude = Double.NaN;

    /**
     * 当前位置
     */
    @Nullable
    private Location location;

    // region 构造函数

    public CompassInfo() {
    }

    // endregion

    // region GET/SET


    protected CompassInfo(Parcel in) {
        azimuth = in.readInt();
        pitch = in.readInt();
        roll = in.readInt();
        longitude = in.readDouble();
        latitude = in.readDouble();
        location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(azimuth);
        dest.writeInt(pitch);
        dest.writeInt(roll);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeParcelable(location, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CompassInfo> CREATOR = new Creator<CompassInfo>() {
        @Override
        public CompassInfo createFromParcel(Parcel in) {
            return new CompassInfo(in);
        }

        @Override
        public CompassInfo[] newArray(int size) {
            return new CompassInfo[size];
        }
    };

    public int getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }


    // endregion

    @Override
    public String toString() {
        return "方位角=" + azimuth +
                "\n俯仰角=" + pitch +
                "\n侧倾角=" + roll +
                "\n经度=" + longitude +
                "\n纬度=" + latitude;
    }
}
