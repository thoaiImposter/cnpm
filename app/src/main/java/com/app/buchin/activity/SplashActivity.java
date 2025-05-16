package com.app.buchin.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import com.app.buchin.R;
import com.app.buchin.constant.GlobalFuntion;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(this::goToFeatureActivity, 2000);
    }

    private void goToFeatureActivity() {
        GlobalFuntion.startActivity(this, FeatureActivity.class);
        finish();
    }
}
