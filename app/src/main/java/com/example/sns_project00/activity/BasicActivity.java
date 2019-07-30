package com.example.sns_project00.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;



import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sns_project00.R;

public class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);    //이건 아님
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //android 화면 (세로 모드로 고정)

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        Toolbar myToolbar =findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    public void setToolbarTitle(String title){     //툴바 사용
        ActionBar actionBar =getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
        }
    }
}
