package com.github.raedev.compass.provider;

import android.os.Build;
import android.text.TextUtils;
import android.view.Surface;

/**
 * 加速度传感器计算方位角
 * @author RAE
 * @date 2022/12/09
 * @copyright Copyright (c) https://github.com/raedev All rights reserved.
 */
class AccelerometerSensorCalc {

    /**
     * 返回值说明： [方位角, 俯仰角, 侧倾角]
     */
    public static float[] calc(int rotation, float[] values) {
        if (rotation == Surface.ROTATION_90) {
            // 处理横版模式，修复仰角过大问题
            values[0] = fixValue(values);
        }
        return values;
    }


    /**
     * 修复方位角横版时仰角过大漂移问题
     * @param values 原始数据
     * @return 方位角
     */
    private static float fixValue(float[] values) {
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

    private static float calcX(float x, float pitch) {
        // 当仰角处于[55,125] 产生漂移
        if (pitch >= 55 && pitch <= 125) {
            return (x + 270) % 360;
        }
        return x;
    }

}
