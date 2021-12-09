package com.github.raedev.compass;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_base).setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), BaseDemoActivity.class));
        });
    }
}