package com.marat.apps.android.pro3.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Databases.StoreToDatabaseHelper;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.UniversalGetRequest;
import com.marat.apps.android.pro3.Internet.UniversalPostRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Response;

public class SplashScreenActivity extends AppCompatActivity implements RequestResponseListener {

    private static final String TAG = "logtag";

    private static final String CITIES_AND_CAR_TYPES_URL = "https://propropro.herokuapp.com/api/v1/sessions";
    private static final String USER_AUTHORIZATION_URL = "https://propropro.herokuapp.com/api/v1/sessions";

    private Intent intent1;
    private final int SPLASH_TIME_OUT = 800;
    private long startTime, endTime;
    private boolean isSuccessful = false, loggedIn;
    private String todaysData;

    private String phoneNumber, password;         // user credentials

    private String[] data1 = new String[]{"1", "1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "2500", "123", "456"};
    private String[] data2 = new String[]{"2", "0", "Master Keruen", "ТЦ Керуен, ул. Достык 21, 0-этаж", "3000", "123", "456"};
    private String[] data3 = new String[]{"3", "0", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "2000", "123", "456"};
    private String[] data4 = new String[]{"4", "1", "АВТО Жуу", "Назарбаев Университет, пр. Кабанбай-Батыра 53, 1-этаж парковки", "2700", "123", "456"};

    private String[] order1 = new String[]{"1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "Внедорожник: Кузов + Салон + Багажник", "02.02.2017", "3500", "Активный"};
    private String[] order2 = new String[]{"3", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "Кроссовер: Кузов + Салон", "28.01.2017", "2800", "Завершен"};
    private String[] order3 = new String[]{"3", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "Кроссовер: Кузов + Салон", "25.01.2017", "2800", "Завершен"};
    private String[] order4 = new String[]{"1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "Седан: Кузов + Салон + Багажник + Двигатель", "20.01.2017", "4500", "Завершен"};
    private String[] order5 = new String[]{"1", "ECA Car Wash", "ТЦ Хан-Шатыр, ул. Туран 50, 0-этаж", "Малолитражка: Кузов + Салон", "15.01.2017", "2500", "Завершен"};
    private String[] order6 = new String[]{"2", "Master Keruen", "ТЦ Керуен, ул. Достык 21, 0-этаж", "Седан: Кузов + Салон", "13.01.2017", "2700", "Завершен"};
    private String[] order7 = new String[]{"3", "Fast Wash", "АЗС NOMAD, ул. Керей-Жанибек 63", "Седан: Кузов + Салон", "09.01.2017", "2800", "Завершен"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startTime = System.currentTimeMillis();
        initializeDatabase();

        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString("phone_number", "empty");
        password = sharedPreferences.getString("password", "empty");

        if ("empty".equals(phoneNumber) || "empty".equals(password)) {
            intent1 = new Intent(this, RegisterActivity.class);
            loggedIn = false;
            getCitiesAndCarTypes();
        } else {
            intent1 = new Intent(this, MainActivity.class);
            intent1.putExtra("startPage", "Favorites");
            loggedIn = true;
            logInUser();
        }
    }

    private void getCitiesAndCarTypes() {
        if (citiesAndCarTypesDataIsOld()) {
            Log.d(TAG, "SplashScreenActivity: " + "GetCitiesAndCarTypes: " + "data is to be updated");
            UniversalGetRequest getRequest = new UniversalGetRequest(this);
            getRequest.delegate = this;
            if (getRequest.isNetworkAvailable()) {
                getRequest.get(CITIES_AND_CAR_TYPES_URL);
            } else {
                Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
                goToNextActivity();
            }
        } else {
            Log.d(TAG, "SplashScreenActivity: " + "GetCitiesAndCarTypes: " +  "data is OK");
            goToNextActivity();
        }
    }

    private void logInUser() {
        UniversalPostRequest postRequest = new UniversalPostRequest(this);
        postRequest.delegate = this;
        if (postRequest.isNetworkAvailable()) {
            postRequest.post(USER_AUTHORIZATION_URL, createUserDataInJson());
        } else {
            showErrorToast(getString(R.string.error_no_internet_connection));
            goToNextActivity();
        }
    }

    private String createUserDataInJson() {
        return "{\"user\":{"
                + "\"phone_number\":"    +   "\""   + phoneNumber    + "\""     + ","
                + "\"password\":"        +   "\""   + password       + "\""
                + "}}";
    }

    @Override
    public void onFailure(IOException e) {
        showErrorToast(getString(R.string.error_could_not_load_data));
        Log.d(TAG, "SplashScreenActivity: " + "onFailure");
        e.printStackTrace();
        goToNextActivity();
    }

    @Override
    public void onResponse(Response response) {
        if (loggedIn) {
            logInResponse(response);
        } else {
            citiesAndCarTypesResponse(response);
        }
    }

    /**
     * Called from OkHttp onResponse method
     * Handles the response from server: 1. Gets and Stores the user information in local DB
     */

    private void logInResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "SplashScreenActivity: " + "LogInResponse: " +  responseMessage);

        if (getString(R.string.server_response_login_successful).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "SplashScreenActivity: " + "LogInResponse: " +  res);

                JSONObject result = new JSONObject(res);
                JSONObject userObject = result.getJSONObject("user");
                JSONArray cities = result.getJSONArray("cities");             // get cities array
                JSONArray carTypes = result.getJSONArray("car_types");        // get car_types array

                if (citiesAndCarTypesDataIsOld()) {
                    Log.d(TAG, "SplashScreenActivity: " + "LogInResponse: " + "data is to be updated");
                    saveCitiesAndCarTypes(cities, carTypes);                  // save two arrays to DB
                } else {
                    Log.d(TAG, "SplashScreenActivity: " + "LogInResponse: " +  "data is OK");
                }
                Log.d(TAG, "SplashScreenActivity: " + "LogInResponse: " +  "before data save");
                saveUserData(userObject);
                Log.d(TAG, "SplashScreenActivity: " + "LogInResponse: " +  "after data save");
                goToNextActivity();
                Log.d(TAG, "SplashScreenActivity: " + "LogInResponse: " +  "after go to next activity");
                return;
            } catch (IOException | JSONException e) {
                showErrorToast(getString(R.string.error_could_not_load_data));
                e.printStackTrace();
            }
        } else if (getString(R.string.server_response_login_failed).equals(responseMessage)) {
            showErrorToast(getString(R.string.error_wrong_phone_or_pass));
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        intent1 = new Intent(this, RegisterActivity.class);
        getCitiesAndCarTypes();
    }

    private void citiesAndCarTypesResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "SplashScreenActivity: " + "citiesAndCarTypesResponse: " +  responseMessage);

        if (getString(R.string.server_response_cities_received).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "SplashScreenActivity: " + "citiesAndCarTypesResponse: " +  res);
                JSONObject result = new JSONObject(res);
                JSONArray cities = result.getJSONArray("cities");             // get cities array
                JSONArray carTypes = result.getJSONArray("car_types");        // get car_types array
                saveCitiesAndCarTypes(cities, carTypes);                      // save two arrays to DB
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        goToNextActivity();
    }

    /**
     * saves the list of Cities and CarTypes received from server to SQLite.
     */
    private void saveCitiesAndCarTypes(JSONArray cities, JSONArray carTypes) {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(this);
        isSuccessful = helper.saveCitiesAndCarTypes(cities, carTypes, todaysData);
    }

    /**
     * saves User Data received from server to SQLite.
     */
    public void saveUserData(JSONObject userObject) throws JSONException {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(this);
        isSuccessful = helper.saveUserLogInData(userObject, password);
    }

    /**
     * returns true if cities or carTypes were downloaded before 6am today.
     */
    private boolean citiesAndCarTypesDataIsOld() {
        Calendar c = Calendar.getInstance();
        todaysData = c.get(Calendar.YEAR)      + "-" +   (c.get(Calendar.MONTH) + 1)    + "-" +    c.get(Calendar.DAY_OF_MONTH)   + " " +
                c.get(Calendar.HOUR_OF_DAY)    + ":" +    c.get(Calendar.MINUTE)        + ":" +    c.get(Calendar.SECOND);

        CWStationsDatabase db = new CWStationsDatabase(this);
        db.open();
        Cursor c1 = db.getAllCities();
        Cursor c2 = db.getAllCarTypes();

        String cityDate = "", carTypeDate = "";

        if (c1.getCount() > 0 && c2.getCount() > 0) {
            c1.moveToFirst();
            c2.moveToFirst();
            cityDate = c1.getString(c1.getColumnIndex(CWStationsDatabase.KEY_CITY_UPDATED));
            carTypeDate = c2.getString(c2.getColumnIndex(CWStationsDatabase.KEY_CAR_TYPE_UPDATED));
            db.close();
        } else {
            db.close();
            return true;
        }

        c.set(Calendar.HOUR_OF_DAY, 6);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        Calendar cityC = Calendar.getInstance();
        Calendar carTypeC = Calendar.getInstance();
        try {
            Date cityD = format.parse(cityDate);
            Date carTypeD = format.parse(carTypeDate);
            cityC.setTime(cityD);
            carTypeC.setTime(carTypeD);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ((cityC.getTimeInMillis() < c.getTimeInMillis()) || (carTypeC.getTimeInMillis() < c.getTimeInMillis()));
    }

    private void goToNextActivity() {
        endTime = System.currentTimeMillis();

        Log.d(TAG, "SplashScreenActivity: " + "start time = " + startTime + "");
        Log.d(TAG, "SplashScreenActivity: " + "end time   = " + endTime + "");
        Log.d(TAG, "SplashScreenActivity: " + "difference = " + (endTime - startTime) + "");
        Log.d(TAG, "SplashScreenActivity: " + "to wait    = " + (SPLASH_TIME_OUT - (endTime - startTime)) + "");

        if ((endTime - startTime) >= SPLASH_TIME_OUT) {
            startActivity(intent1);
            finish();
        } else {
            if ("main".equals(Thread.currentThread().getName())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent1);
                        SplashScreenActivity.this.finish();
                    }
                }, (SPLASH_TIME_OUT - (endTime - startTime)));
            } else {
                try {
                    Thread.sleep(SPLASH_TIME_OUT - (endTime - startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent1);
                finish();
            }
        }
    }

    private void showErrorToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashScreenActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initializeDatabase() {/*
        CWStationsDatabase db = new CWStationsDatabase(this);
        db.open();
        db.deleteAllStations();
        db.deleteFavoriteStations();
        db.deleteMyOrders();

        db.addToAllCarWashingStations(Integer.parseInt(data1[0]), Integer.parseInt(data1[1]), data1[2], data1[3], Integer.parseInt(data1[4]), Integer.parseInt(data1[5]), Integer.parseInt(data1[6]));
        db.addToAllCarWashingStations(Integer.parseInt(data2[0]), Integer.parseInt(data2[1]), data2[2], data2[3], Integer.parseInt(data2[4]), Integer.parseInt(data2[5]), Integer.parseInt(data2[6]));
        db.addToAllCarWashingStations(Integer.parseInt(data3[0]), Integer.parseInt(data3[1]), data3[2], data3[3], Integer.parseInt(data3[4]), Integer.parseInt(data3[5]), Integer.parseInt(data3[6]));
        db.addToAllCarWashingStations(Integer.parseInt(data4[0]), Integer.parseInt(data4[1]), data4[2], data4[3], Integer.parseInt(data4[4]), Integer.parseInt(data4[5]), Integer.parseInt(data4[6]));

        db.addToFavoriteCarWashingStations(Integer.parseInt(data1[0]), Integer.parseInt(data1[1]), data1[2], data1[3], Integer.parseInt(data1[4]), Integer.parseInt(data1[5]), Integer.parseInt(data1[6]));
        db.addToFavoriteCarWashingStations(Integer.parseInt(data4[0]), Integer.parseInt(data4[1]), data4[2], data4[3], Integer.parseInt(data4[4]), Integer.parseInt(data4[5]), Integer.parseInt(data4[6]));

        long p = db.addToMyOrders(Integer.parseInt(order1[0]), order1[1], order1[2], order1[3], order1[4], order1[5], order1[6]);
        Log.d(TAG, "SplashScreenActivity: " + "Insert Position - " + p + "");
        db.addToMyOrders(Integer.parseInt(order2[0]), order2[1], order2[2], order2[3], order2[4], order2[5], order2[6]);
        db.addToMyOrders(Integer.parseInt(order3[0]), order3[1], order3[2], order3[3], order3[4], order3[5], order3[6]);
        db.addToMyOrders(Integer.parseInt(order4[0]), order4[1], order4[2], order4[3], order4[4], order4[5], order4[6]);
        db.addToMyOrders(Integer.parseInt(order5[0]), order5[1], order5[2], order5[3], order5[4], order5[5], order5[6]);
        db.addToMyOrders(Integer.parseInt(order6[0]), order6[1], order6[2], order6[3], order6[4], order6[5], order6[6]);
        db.addToMyOrders(Integer.parseInt(order7[0]), order7[1], order7[2], order7[3], order7[4], order7[5], order7[6]);

        db.close();*/
    }
}
