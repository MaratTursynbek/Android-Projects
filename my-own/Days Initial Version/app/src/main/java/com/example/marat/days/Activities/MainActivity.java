package com.example.marat.days.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.marat.days.MainTabs.AllTabView;
import com.example.marat.days.MainTabs.SinceTabView;
import com.example.marat.days.MainTabs.UntilTabView;
import com.example.marat.days.R;

public class MainActivity extends AppCompatActivity {

    FragmentTabHost mFragmentTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentTabHost.setup(MainActivity.this, getSupportFragmentManager(), android.R.id.tabcontent);

        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("all").setIndicator("All"), AllTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("since").setIndicator("Since"), SinceTabView.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("until").setIndicator("Until"), UntilTabView.class, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, ViewDatabaseActivity.class);
            startActivity(i);
            return true;
        }
        else if (id==R.id.one_time) {
            Intent i = new Intent(this, QuickCalculationActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.add_new) {
            Intent intent = new Intent(MainActivity.this, AddNewEventActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}