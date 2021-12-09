package com.github.raedev.compass.listener;

import com.github.raedev.compass.entity.CompassInfo;

/**
 * 指南针改变回调监听
 * @author RAE
 * @date 2021/12/08
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public interface CompassChangedListener {

    /**
     * 指南针回调
     * @param compass
     */
    void onCompassChanged(CompassInfo compass);

    /**
     * 指南针异常回调
     * @param e 异常信息
     */
    void onCompassException(Exception e);
}
