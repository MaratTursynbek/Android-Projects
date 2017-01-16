package com.example.marat.days.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.marat.days.R;
import com.example.marat.days.Tabs.DateTabView;
import com.example.marat.days.Tabs.DaysTabView;

public class QuickCalculationActivity extends AppCompatActivity  {

    FragmentTabHost mFragmentTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_calculation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(QuickCalculationActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);

        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("days").setIndicator("DAYS"), DaysTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("date").setIndicator("DATE"), DateTabView.class, null);
    }
}