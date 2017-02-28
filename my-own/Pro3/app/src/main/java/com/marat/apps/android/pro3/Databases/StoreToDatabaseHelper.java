package com.marat.apps.android.pro3.Databases;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StoreToDatabaseHelper {

    private Context context;

    public StoreToDatabaseHelper(Context context) {
        this.context = context;
    }

    public boolean saveUserLogInData(JSONObject userObject, String password) throws JSONException {
        //saving user credentials into shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone_number", userObject.getString("phone_number"));
        editor.putString("password", password);
        editor.putString("ACCESS_TOKEN", userObject.getString("token"));
        editor.apply();

        // car washing stations json object
        JSONArray allCarWashers = userObject.getJSONArray("city_carwashes");
        JSONArray favoriteCarWashers = userObject.getJSONArray("favorite_carwashes");

        //saving user data to internal SQLite database
        CWStationsDatabase db = new CWStationsDatabase(context);
        db.open();
        db.deleteUserInformation();
        db.addUserInformation(
                userObject.getInt("id"),
                userObject.getString("created_at"),
                userObject.getString("name"),
                userObject.getInt("city_id"),
                userObject.getString("city_name"),
                userObject.getInt("car_type_id"),
                userObject.getString("car_type_name"),
                userObject.getString("phone_number")
        );
        db.deleteAllStations();
        for (int i = 0; i < allCarWashers.length(); i++) {
            JSONObject carWash = allCarWashers.getJSONObject(i);
            db.addToAllCarWashingStations(
                    carWash.getInt("id"),
                    carWash.getString("name"),
                    carWash.getString("address"),
                    carWash.getJSONArray("example").getJSONObject(0).getInt("price"),
                    100,
                    200
            );
        }/*
        db.deleteFavoriteStations();
        for (int i = 0; i < favoriteCarWashers.length(); i++) {
            JSONObject carWash = favoriteCarWashers.getJSONObject(i);
            db.addToFavoriteCarWashingStations(
                    carWash.getInt("id"),
                    carWash.getString("name"),
                    carWash.getString("address"),
                    carWash.getJSONObject("example").getInt("price"),
                    100,
                    200
            );
        }*/
        db.close();

        return true;
    }

    public boolean saveNewUserData(JSONObject userObject) throws JSONException {
        JSONObject cityObject = userObject.getJSONObject("city");
        JSONObject carTypeObject = userObject.getJSONObject("car_type");

        //saving user data to internal SQLite database
        CWStationsDatabase db = new CWStationsDatabase(context);
        db.open();
        db.deleteUserInformation();
        db.addUserInformation(
                userObject.getInt("id"),
                userObject.getString("created_at"),
                userObject.getString("name"),
                cityObject.getInt("id"),
                cityObject.getString("name"),
                carTypeObject.getInt("id"),
                carTypeObject.getString("name"),
                userObject.getString("phone_number")
        );
        db.close();

        return true;
    }

    public boolean saveCitiesAndCarTypes(JSONArray cities, JSONArray carTypes, String todaysData) {
        CWStationsDatabase db = new CWStationsDatabase(context);
        db.open();
        db.deleteAllCities();
        db.deleteAllCarTypes();

        boolean isSuccessful = false;

        // saving all cities to internal SQLite database
        for (int i = 0; i < cities.length(); i++) {
            try {
                JSONObject city = cities.getJSONObject(i);
                int cityId = city.getInt("id");
                String cityName = city.getString("name");
                db.addToAllCities(cityId, cityName, todaysData);
                isSuccessful = true;
            } catch (JSONException e) {
                e.printStackTrace();
                isSuccessful = false;
            }
        }

        // saving all carTypes to internal SQLite database
        for (int i = 0; i < carTypes.length(); i++) {
            try {
                JSONObject car = carTypes.getJSONObject(i);
                int carId = car.getInt("id");
                String carName = car.getString("name");
                db.addToAllCarTypes(carId, carName, todaysData);
                isSuccessful = true;
            } catch (JSONException e) {
                e.printStackTrace();
                isSuccessful = false;
            }
        }
        db.close();

        return isSuccessful;
    }
}
