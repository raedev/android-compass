package com.github.raedev.compass;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.raedev.compass.entity.CompassInfo;
import com.github.raedev.compass.listener.CompassChangedListener;
import com.github.raedev.compass.view.CompassImageView;

/**
 * @author RAE
 * @date 2021/12/08
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class BaseDemoActivity extends AppCompatActivity implements CompassChangedListener {

    private TextView mMessageView;
    private TextView mErrorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_demo_base);
        CompassImageView compassView = findViewById(R.id.iv_compass);
        mMessageView = findViewById(R.id.tv_message);
        mErrorView = findViewById(R.id.tv_error);
        CompassManager manager = new CompassManager(this);
        manager.addCompassChangedListener(this);
        manager.addCompassChangedListener(compassView);
        manager.register(this);
    }

    @Override
    public void onCompassChanged(CompassInfo compass) {
        mMessageView.setText(compass.toString());
    }

    @Override
    public void onCompassException(Exception e) {
        mErrorView.setText(e.getMessage());
    }
}
