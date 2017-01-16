package com.marat.apps.android.pro3.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.marat.apps.android.pro3.R;

public class SplashScreenActivity extends AppCompatActivity {

    Intent intent1;
    String username, password, token;
    long startTime, endTime;
    private static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startTime = System.currentTimeMillis();

        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");
        token = sharedPreferences.getString("ACCESS_TOKEN", "");

        Log.v("myTag", username + " " + password + " " + token);

        if ("".equals(username) || "".equals(password) || "".equals(token)) {
            intent1 = new Intent(this, LoginActivity.class);
        }
        else {
            intent1 = new Intent(this, MainActivity.class);
        }

        endTime = System.currentTimeMillis();

        if ((endTime - startTime) >= SPLASH_TIME_OUT) {
            startActivity(intent1);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent1);
                    finish();
                }
            }, (SPLASH_TIME_OUT - (endTime - startTime)));
        }
    }
}
