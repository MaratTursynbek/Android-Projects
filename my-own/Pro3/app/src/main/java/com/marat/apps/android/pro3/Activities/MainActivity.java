package com.marat.apps.android.pro3.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import com.marat.apps.android.pro3.Adapters.MenuCityPickerAdapter;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Interfaces.ToolbarTitleChangeListener;
import com.marat.apps.android.pro3.MenuSections.AboutProjectFragment;
import com.marat.apps.android.pro3.MenuSections.AllCarWashesFragment;
import com.marat.apps.android.pro3.MenuSections.ContactsFragment;
import com.marat.apps.android.pro3.MenuSections.FavoriteCarWashesFragment;
import com.marat.apps.android.pro3.MenuSections.AccountFragment;
import com.marat.apps.android.pro3.MenuSections.UserOrdersFragment;
import com.marat.apps.android.pro3.Models.City;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ToolbarTitleChangeListener {

    private static final String TAG = "MainActivity";

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private AccountFragment accountFragment;
    private AllCarWashesFragment allCarWashesFragment;
    private FavoriteCarWashesFragment favoriteCarWashesFragment;
    private UserOrdersFragment userOrdersFragment;

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
        Log.v(TAG, "onBackPressed");
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
        Log.d(TAG, "onCreate");
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
                Log.d(TAG, "onDrawerClosed");
                if (isPickerShown) {
                    toggleMenu();
                }
                if (currentCityChanged) {
                    if (checkedFragmentId == R.id.nav_car_washers) {
                        allCarWashesFragment.updateDataForCity(currentCityId, currentCityName);
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

        getCitiesAndUserInfo();
        setUpStartingPage();
        setUpCitiesMenuList();
    }

    private void setUpStartingPage() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        String startPage = getIntent().getExtras().getString("start_page");
        Log.d(TAG, "start page is - " + startPage);

        if ("all_car_washes".equals(startPage)) {
            allCarWashesFragment = AllCarWashesFragment.newInstance(currentCityId, currentCityName);
            fragmentTransaction.replace(R.id.fragment_container, allCarWashesFragment);
            checkedFragmentId = R.id.nav_car_washers;
        } else if ("favorite_car_washes".equals(startPage)) {
            favoriteCarWashesFragment = new FavoriteCarWashesFragment();
            fragmentTransaction.add(R.id.fragment_container, favoriteCarWashesFragment);
            checkedFragmentId = R.id.nav_favorites;
        } else if ("user_orders".equals(startPage)) {
            userOrdersFragment = new UserOrdersFragment();
            fragmentTransaction.add(R.id.fragment_container, userOrdersFragment);
            checkedFragmentId = R.id.nav_user_orders;
        }
        fragmentTransaction.commit();
    }

    private void getCitiesAndUserInfo() {
        CarWashesDatabase db = new CarWashesDatabase(this);
        db.open();
        Cursor cursorCities = db.getAllCities();
        Cursor cursorUser = db.getUserInformation();

        cursorUser.moveToFirst();
        int downloadedCityId = cursorUser.getInt(cursorUser.getColumnIndex(CarWashesDatabase.KEY_USER_CITY_ID));

        for (cursorCities.moveToFirst(); !cursorCities.isAfterLast(); cursorCities.moveToNext()) {
            City city = new City();
            city.setRowID(cursorCities.getLong(cursorCities.getColumnIndex(CarWashesDatabase.ROW_ID)));
            city.setCityID(cursorCities.getInt(cursorCities.getColumnIndex(CarWashesDatabase.KEY_CITY_ID)));
            city.setCityName(cursorCities.getString(cursorCities.getColumnIndex(CarWashesDatabase.KEY_CITY_NAME)));
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
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
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
            allCarWashesFragment = AllCarWashesFragment.newInstance(currentCityId, currentCityName);
            fragmentTransaction.replace(R.id.fragment_container, allCarWashesFragment);
            checkedFragmentId = R.id.nav_car_washers;

        } else if (id == R.id.nav_favorites) {
            favoriteCarWashesFragment = new FavoriteCarWashesFragment();
            fragmentTransaction.replace(R.id.fragment_container, favoriteCarWashesFragment);
            checkedFragmentId = R.id.nav_favorites;

        } else if (id == R.id.nav_user_orders) {
            userOrdersFragment = new UserOrdersFragment();
            fragmentTransaction.replace(R.id.fragment_container, userOrdersFragment);
            checkedFragmentId = R.id.nav_user_orders;

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
}
