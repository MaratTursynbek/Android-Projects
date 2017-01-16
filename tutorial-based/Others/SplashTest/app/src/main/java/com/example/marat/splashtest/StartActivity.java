package com.example.marat.splashtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_start);

        Intent i = new Intent(StartActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
