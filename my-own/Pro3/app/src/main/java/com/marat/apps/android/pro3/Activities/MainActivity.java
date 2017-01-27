package com.marat.apps.android.pro3.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.CityPickerAdapter;
import com.marat.apps.android.pro3.MenuSections.AllCarWashersFragment;
import com.marat.apps.android.pro3.MenuSections.FavoriteFragment;
import com.marat.apps.android.pro3.MenuSections.HomeFragment;
import com.marat.apps.android.pro3.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private NavigationView navigationView;
    private TextView cityPickerTextView;
    private ListView citiesListView;
    private ImageView pickerArrow;

    private boolean isPickerShown = false;

    private String cities[] = {"Астана", "Алматы", "Шымкент", "Орал", "Қызылорда", "Атырау", "Ақтөбе", "Көкшетау", "Қостанай", "Қарағанды", "Семей", "Өскемен", "Тараз", "Ақтау", "Павлодар", "Петропавл"};
    private String userCity = cities[0];

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        FavoriteFragment favoriteFragment = new FavoriteFragment();
        fragmentTransaction.add(R.id.fragment_container, favoriteFragment);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setCheckedItem(R.id.nav_favorites);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.log_out) {
            SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            HomeFragment homeFragment = new HomeFragment();

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, homeFragment);
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

        } else if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_log_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
