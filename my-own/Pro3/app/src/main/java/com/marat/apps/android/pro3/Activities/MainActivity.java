package com.marat.apps.android.pro3.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.CityPickerAdapter;
import com.marat.apps.android.pro3.Interfaces.OnToolbarTitleChangeListener;
import com.marat.apps.android.pro3.MenuSections.AllCarWashersFragment;
import com.marat.apps.android.pro3.MenuSections.FavoriteFragment;
import com.marat.apps.android.pro3.MenuSections.AccountFragment;
import com.marat.apps.android.pro3.MenuSections.MyOrdersFragment;
import com.marat.apps.android.pro3.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnToolbarTitleChangeListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private NavigationView navigationView;
    private TextView cityPickerTextView;
    private ListView citiesListView;
    private ImageView pickerArrow;

    private boolean isPickerShown = false;

    private String cities[] = {"Астана", "Алматы", "Шымкент", "Орал", "Қызылорда", "Атырау", "Ақтөбе", "Көкшетау", "Қостанай", "Қарағанды", "Семей", "Өскемен", "Тараз", "Ақтау", "Павлодар", "Петропавл"};
    private String userCity = cities[0];

    private int checkedItem;

    @Override
    public void onBackPressed() {
        Log.v("MainActivity", "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LocalBroadcastManager.getInstance(this).registerReceiver(finishActivityReceiver, new IntentFilter("finish_main_activity"));

        findViewById(R.id.header).setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (isPickerShown) {
                    toggleMenu();
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        String startPage = getIntent().getExtras().getString("startPage");

        if ("Favorites".equals(startPage)) {
            FavoriteFragment favoriteFragment = new FavoriteFragment();
            fragmentTransaction.add(R.id.fragment_container, favoriteFragment);
            checkedItem = R.id.nav_favorites;
        } else if ("MyOrders".equals(startPage)) {
            MyOrdersFragment myOrdersFragment = new MyOrdersFragment();
            fragmentTransaction.replace(R.id.fragment_container, myOrdersFragment);
            checkedItem = R.id.nav_my_orders;
        } else if ("AllCarWashers".equals(startPage)) {
            AllCarWashersFragment allCarWashersFragment = new AllCarWashersFragment();
            fragmentTransaction.replace(R.id.fragment_container, allCarWashersFragment);
            checkedItem = R.id.nav_car_washers;
        }

        fragmentTransaction.commit();

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        cityPickerTextView = (TextView) headerView.findViewById(R.id.cityPicker);
        cityPickerTextView.setText(cities[0]);
        cityPickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });
        cityPickerTextView.bringToFront();

        pickerArrow = (ImageView) headerView.findViewById(R.id.pickerArrow);
        pickerArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);

        citiesListView = (ListView) findViewById(R.id.citiesListView);
        citiesListView.setVisibility(View.INVISIBLE);

        final CityPickerAdapter adapter = new CityPickerAdapter(this, cities, userCity);
        citiesListView.setAdapter(adapter);
        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userCity = cities[i];
                cityPickerTextView.setText(userCity);
                adapter.updateCurrentCity(userCity);
            }
        });
    }

    private void toggleMenu() {
        if (!isPickerShown) {
            pickerArrow.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
            setMenuItemsVisible(false);
            citiesListView.setVisibility(View.VISIBLE);
            isPickerShown = true;
        } else {
            pickerArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
            setMenuItemsVisible(true);
            citiesListView.setVisibility(View.INVISIBLE);
            isPickerShown = false;
        }
    }

    private void setMenuItemsVisible(boolean b) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); ++i) {
            menu.getItem(i).setVisible(b);
        }
    }

    @Override
    public void onTitleChanged(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setCheckedItem(checkedItem);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            AccountFragment accountFragment = new AccountFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, accountFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_favorites) {
            FavoriteFragment favoriteFragment = new FavoriteFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, favoriteFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_car_washers) {
            AllCarWashersFragment allCarWashersFragment = new AllCarWashersFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, allCarWashersFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_my_orders) {
            MyOrdersFragment myOrdersFragment = new MyOrdersFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, myOrdersFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_log_out) {
            SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BroadcastReceiver finishActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("finish_main_activity".equals(intent.getAction())) {
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(finishActivityReceiver);
                finish();
            }
        }
    };
}
