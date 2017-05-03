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

import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Databases.StoreToDatabaseHelper;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.Internet.PostRequest;
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

    private static final String TAG = "SplashScreenActivity";

    private static final String CITIES_AND_CAR_TYPES_URL = "https://propropro.herokuapp.com/api/v1/sessions";
    private static final String USER_AUTHORIZATION_URL = "https://propropro.herokuapp.com/api/v1/sessions";

    private Intent intent1;
    private final int SPLASH_TIME_OUT = 800;
    private long startTime, endTime;
    private boolean isSuccessful = false, loggedIn;
    private String todaysData;

    private String phoneNumber, password;         // user credentials

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startTime = System.currentTimeMillis();

        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString("phone_number", "empty");
        password = sharedPreferences.getString("password", "empty");

        if ("empty".equals(phoneNumber) || "empty".equals(password)) {
            intent1 = new Intent(this, RegisterActivity.class);
            loggedIn = false;
            getCitiesAndCarTypes();
        } else {
            intent1 = new Intent(this, MainActivity.class);
            loggedIn = true;
            logInUser();
        }
    }

    private void getCitiesAndCarTypes() {
        if (isCitiesAndCarTypesDataOld()) {
            Log.d(TAG, "GetCitiesAndCarTypes: " + "data is to be updated");
            GetRequest getRequest = new GetRequest(this);
            getRequest.delegate = this;
            if (getRequest.isNetworkAvailable()) {
                getRequest.get(CITIES_AND_CAR_TYPES_URL);
            } else {
                Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
                goToNextActivity();
            }
        } else {
            Log.d(TAG, "GetCitiesAndCarTypes: " + "data is OK");
            goToNextActivity();
        }
    }

    private void logInUser() {
        PostRequest postRequest = new PostRequest(this);
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
                + "\"phone_number\":"   +   "\""     +   phoneNumber    +   "\""   +   ","
                + "\"password\":"       +   "\""     +   password       +   "\""
                + "}}";
    }

    @Override
    public void onFailure(IOException e) {
        showErrorToast(getString(R.string.error_could_not_load_data));
        Log.d(TAG, "onFailure");
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
        Log.d(TAG, "response message - " + responseMessage);

        if (getString(R.string.server_response_created).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "response body - " + res);

                JSONObject result = new JSONObject(res);
                JSONObject userObject = result.getJSONObject("user");
                JSONArray cities = result.getJSONArray("cities");             // get cities array
                JSONArray carTypes = result.getJSONArray("car_types");        // get car_types array

                if (isCitiesAndCarTypesDataOld()) {
                    saveCitiesAndCarTypes(cities, carTypes);                  // save two arrays to DB
                }
                saveUserData(userObject);
                if (isSuccessful) {
                    goToNextActivity();
                    return;
                }
            } catch (IOException | JSONException e) {
                showErrorToast(getString(R.string.error_could_not_load_data));
                e.printStackTrace();
            }
        } else if (getString(R.string.server_response_unauthorized).equals(responseMessage)) {
            showErrorToast(getString(R.string.error_wrong_phone_or_pass));
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        intent1 = new Intent(this, RegisterActivity.class);
        getCitiesAndCarTypes();
    }

    private void citiesAndCarTypesResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "response message - " + responseMessage);

        if (getString(R.string.server_response_ok).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "response body - " + res);
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
    public void saveUserData(JSONObject userObject) {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(this);
        isSuccessful = helper.saveUserLogInData(userObject, password);
    }

    /**
     * returns true if cities or carTypes were downloaded before 6am today.
     */
    private boolean isCitiesAndCarTypesDataOld() {
        Calendar c = Calendar.getInstance();
        todaysData = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " +
                c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

        CarWashesDatabase db = new CarWashesDatabase(this);
        db.open();
        Cursor c1 = db.getAllCities();
        Cursor c2 = db.getAllCarTypes();

        String cityDate = "", carTypeDate = "";

        if (c1.getCount() > 0 && c2.getCount() > 0) {
            c1.moveToFirst();
            c2.moveToFirst();
            cityDate = c1.getString(c1.getColumnIndex(CarWashesDatabase.KEY_CITY_UPDATED));
            carTypeDate = c2.getString(c2.getColumnIndex(CarWashesDatabase.KEY_CAR_TYPE_UPDATED));
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
        CarWashesDatabase db = new CarWashesDatabase(this);
        db.open();
        if (db.userHasFavoriteStations()) {
            intent1.putExtra("start_page", "favorite_car_washes");
        } else {
            intent1.putExtra("start_page", "all_car_washes");
        }

        endTime = System.currentTimeMillis();

        Log.d(TAG, "start time = " + startTime + "");
        Log.d(TAG, "end time   = " + endTime + "");
        Log.d(TAG, "difference = " + (endTime - startTime) + "");
        Log.d(TAG, "to wait    = " + (SPLASH_TIME_OUT - (endTime - startTime)) + "");

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
}
