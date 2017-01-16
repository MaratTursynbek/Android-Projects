package com.example.marat.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.marat.stormy.R;
import com.example.marat.stormy.adapters.HourAdapter;
import com.example.marat.stormy.weather.Hour;

import java.util.Arrays;

public class HourlyForecastActivity extends ListActivity {

    private Hour[] mHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(this, mHours);
        setListAdapter(adapter);
    }
}
