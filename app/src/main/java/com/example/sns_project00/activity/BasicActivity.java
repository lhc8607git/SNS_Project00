package com.example.sns_project00.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);    //이건 아님
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //android 화면 (세로 모드로 고정)
    }
}
