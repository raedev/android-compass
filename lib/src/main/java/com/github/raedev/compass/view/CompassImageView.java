package com.github.raedev.compass.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.github.raedev.compass.R;
import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;

/**
 * 指南针图片View <br>
 * 设置图片规范：12点钟方向为正北方向
 * @author RAE
 * @date 2021/12/08
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class CompassImageView extends AppCompatImageView implements CompassChangedListener {

    protected long mInvalidateTime;

    /**
     * 刷新间隔
     */
    protected int mRefreshTime = 150;

    @Nullable
    protected CompassInfo mCompass;
    private int mLastAzimuth;

    public CompassImageView(Context context) {
        this(context, null);
    }

    public CompassImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    protected void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        setImageResource(R.drawable.ic_camera_compass);
    }

    @Override
    public void onCompassChanged(CompassInfo compass) {
        // 计算角度
        if (System.currentTimeMillis() - mInvalidateTime < mRefreshTime) {
            // 刷新太快
            return;
        }
        // 方位角偏差1度的时候回调
        if (Math.abs(mLastAzimuth - compass.getAzimuth()) < 1) {
            return;
        }
        mInvalidateTime = System.currentTimeMillis();
        float rotation = getCompassRotation();
        setRotation(rotation);
        mCompass = compass;
        mLastAzimuth = compass.getAzimuth();
    }

    /**
     * 获取指南针旋转角度
     * @return 旋转角度
     */
    protected float getCompassRotation() {
        return mCompass == null ? 0f : (360 - mCompass.getAzimuth()) % 360;
    }

    @Override
    public void onCompassException(Exception e) {
        // 不处理
    }
}
