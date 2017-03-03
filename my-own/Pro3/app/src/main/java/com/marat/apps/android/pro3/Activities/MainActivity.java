package com.marat.apps.android.pro3.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import com.marat.apps.android.pro3.Adapters.MenuCityPickerAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.OnToolbarTitleChangeListener;
import com.marat.apps.android.pro3.MenuSections.AboutProjectFragment;
import com.marat.apps.android.pro3.MenuSections.AllCarWashersFragment;
import com.marat.apps.android.pro3.MenuSections.ContactsFragment;
import com.marat.apps.android.pro3.MenuSections.FavoriteFragment;
import com.marat.apps.android.pro3.MenuSections.AccountFragment;
import com.marat.apps.android.pro3.MenuSections.MyOrdersFragment;
import com.marat.apps.android.pro3.Models.City;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnToolbarTitleChangeListener {

    private static final String TAG = "logtag";

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private AccountFragment accountFragment;
    private AllCarWashersFragment allCarWashersFragment;
    private FavoriteFragment favoriteFragment;
    private MyOrdersFragment myOrdersFragment;

    private NavigationView navigationView;
    private TextView cityPickerTextView;
    private ListView citiesListView;
    private ImageView pickerArrow;

    private boolean isPickerShown = false, currentCityChanged = false;

    private ArrayList<City> cities = new ArrayList<>();
    private int currentCityId = 0;
    private String currentCityName;

    private int checkedFragmentId;

    @Override
    public void onBackPressed() {
        Log.v(TAG, "MainActivity: " + "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity: " + "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.header).setVisibility(View.INVISIBLE);

        LocalBroadcastManager.getInstance(this).registerReceiver(finishActivityReceiver, new IntentFilter("finish_main_activity"));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "MainActivity: " + "onDrawerClosed");
                if (isPickerShown) {
                    toggleMenu();
                }
                if (currentCityChanged) {
                    if (checkedFragmentId == R.id.nav_car_washers) {
                        allCarWashersFragment.updateDataForCity(currentCityId, currentCityName);
                    }
                    currentCityChanged = false;
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        citiesListView = (ListView) findViewById(R.id.citiesListView);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        cityPickerTextView = (TextView) headerView.findViewById(R.id.cityPicker);
        pickerArrow = (ImageView) headerView.findViewById(R.id.pickerArrow);

        downloadCitiesAndUserInfo();
        setUpStartingPage();
        setUpCitiesMenuList();
    }

    private void setUpStartingPage() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        String startPage = getIntent().getExtras().getString("startPage");
        Log.d(TAG, "MainActivity: " + "start Page is - " + startPage);

        if ("AllCarWashers".equals(startPage)) {
            allCarWashersFragment = AllCarWashersFragment.newInstance(currentCityId, currentCityName);
            fragmentTransaction.replace(R.id.fragment_container, allCarWashersFragment);
            checkedFragmentId = R.id.nav_car_washers;
        } else if ("Favorites".equals(startPage)) {
            favoriteFragment = new FavoriteFragment();
            fragmentTransaction.add(R.id.fragment_container, favoriteFragment);
            checkedFragmentId = R.id.nav_favorites;
        } else if ("MyOrders".equals(startPage)) {
            myOrdersFragment = new MyOrdersFragment();
            fragmentTransaction.add(R.id.fragment_container, myOrdersFragment);
            checkedFragmentId = R.id.nav_my_orders;
        }
        fragmentTransaction.commit();
    }

    private void downloadCitiesAndUserInfo() {
        CWStationsDatabase db = new CWStationsDatabase(this);
        db.open();
        Cursor cursorCities = db.getAllCities();
        Cursor cursorUser = db.getUserInformation();

        cursorUser.moveToFirst();
        int downloadedCityId = cursorUser.getInt(cursorUser.getColumnIndex(CWStationsDatabase.KEY_USER_CITY_ID));

        for (cursorCities.moveToFirst(); !cursorCities.isAfterLast(); cursorCities.moveToNext()) {
            City city = new City();
            city.setRowID(cursorCities.getLong(cursorCities.getColumnIndex(CWStationsDatabase.ROW_ID)));
            city.setCityID(cursorCities.getInt(cursorCities.getColumnIndex(CWStationsDatabase.KEY_CITY_ID)));
            city.setCityName(cursorCities.getString(cursorCities.getColumnIndex(CWStationsDatabase.KEY_CITY_NAME)));
            cities.add(city);
            if (city.getCityID() == downloadedCityId) {
                currentCityId = city.getCityID();
                currentCityName = city.getCityName();
                cityPickerTextView.setText(currentCityName);
            }
        }
        db.close();
    }

    private void setUpCitiesMenuList() {
        cityPickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });
        cityPickerTextView.bringToFront();

        pickerArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);

        citiesListView.setVisibility(View.INVISIBLE);

        final MenuCityPickerAdapter adapter = new MenuCityPickerAdapter(this, cities, currentCityId);
        citiesListView.setAdapter(adapter);
        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentCityId != cities.get(i).getCityID()) {
                    currentCityId = cities.get(i).getCityID();
                    currentCityName = cities.get(i).getCityName();
                    cityPickerTextView.setText(currentCityName);
                    adapter.updateCurrentCity(currentCityId);
                    currentCityChanged = true;
                }
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
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setCheckedItem(checkedFragmentId);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_home) {
            accountFragment = new AccountFragment();
            fragmentTransaction.replace(R.id.fragment_container, accountFragment);
            checkedFragmentId = R.id.nav_home;

        } else if (id == R.id.nav_car_washers) {
            allCarWashersFragment = AllCarWashersFragment.newInstance(currentCityId, currentCityName);
            fragmentTransaction.replace(R.id.fragment_container, allCarWashersFragment);
            checkedFragmentId = R.id.nav_car_washers;

        } else if (id == R.id.nav_favorites) {
            favoriteFragment = new FavoriteFragment();
            fragmentTransaction.replace(R.id.fragment_container, favoriteFragment);
            checkedFragmentId = R.id.nav_favorites;

        } else if (id == R.id.nav_my_orders) {
            myOrdersFragment = new MyOrdersFragment();
            fragmentTransaction.replace(R.id.fragment_container, myOrdersFragment);
            checkedFragmentId = R.id.nav_my_orders;

        } else if (id == R.id.nav_contacts) {
            ContactsFragment contactsFragment = new ContactsFragment();
            fragmentTransaction.replace(R.id.fragment_container, contactsFragment);
            checkedFragmentId = R.id.nav_contacts;

        } else if (id == R.id.nav_about) {
            AboutProjectFragment aboutProjectFragment = new AboutProjectFragment();
            fragmentTransaction.replace(R.id.fragment_container, aboutProjectFragment);
            checkedFragmentId = R.id.nav_about;

        } else if (id == R.id.nav_log_out) {
            SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity: " + "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "MainActivity: " + "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: " + "onResume");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "MainActivity: " + "onPostResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity: " + "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity: " + "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "MainActivity: " + "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "MainActivity: " + "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "MainActivity: " + "onRestoreInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.v(TAG, "MainActivity: " + "onRestoreInstanceState 2");
    }
}
