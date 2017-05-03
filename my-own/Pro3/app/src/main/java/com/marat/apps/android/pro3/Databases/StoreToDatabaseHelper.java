package com.marat.apps.android.pro3.Databases;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StoreToDatabaseHelper {

    private Context context;

    public StoreToDatabaseHelper(Context context) {
        this.context = context;
    }

    public boolean saveCitiesAndCarTypes(JSONArray cities, JSONArray carTypes, String todaysData) {
        CarWashesDatabase db = new CarWashesDatabase(context);
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

    public boolean saveUserLogInData(JSONObject userObject, String password) {
        // preparing shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // saving user credentials
        try {
            editor.putString("phone_number", userObject.getString("phone_number"));
            editor.putString("password", password);
            editor.putString("ACCESS_TOKEN", userObject.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        editor.apply();

        // preparing Database
        CarWashesDatabase db = new CarWashesDatabase(context);
        db.open();
        db.deleteUserInformation();
        db.deleteAllStations();
        db.deleteFavoriteStations();

        // saving user data to internal SQLite database
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        db.close();

        // all car washing stations saved to SQLite
        try {
            JSONArray allCarWashers = userObject.getJSONArray("city_carwashes");
            if (allCarWashers.length() > 0) {
                saveAllCarWashers(allCarWashers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // favorite car washing stations saved to SQLite
        try {
            JSONArray favoriteCarWashers = userObject.getJSONArray("favorite_carwashes");
            if (favoriteCarWashers.length() > 0) {
                saveFavoriteCarWashers(favoriteCarWashers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean saveAllCarWashers(JSONArray allCarWashers) throws JSONException {
        CarWashesDatabase db = new CarWashesDatabase(context);
        db.open();
        db.deleteAllStations();
        for (int i = 0; i < allCarWashers.length(); i++) {
            JSONObject carWash = allCarWashers.getJSONObject(i);
            int price = 0;
            if (carWash.getJSONArray("example").length() > 0) {
                price = carWash.getJSONArray("example").getJSONObject(0).getInt("price");
            }
            db.addToAllCarWashingStations(
                    carWash.getInt("id"),
                    carWash.getString("name"),
                    carWash.getString("address"),
                    price,
                    100,
                    200,
                    carWash.getInt("carwash_city_id"),
                    carWash.getString("carwash_city_name")
            );
        }
        db.close();
        return true;
    }

    public boolean saveFavoriteCarWashers(JSONArray favoriteCarWashers) throws JSONException {
        CarWashesDatabase db = new CarWashesDatabase(context);
        db.open();
        db.deleteFavoriteStations();
        for (int i = 0; i < favoriteCarWashers.length(); i++) {
            JSONObject carWash = favoriteCarWashers.getJSONObject(i);
            int price = 0;
            if (carWash.getJSONArray("example").length() > 0) {
                price = carWash.getJSONArray("example").getJSONObject(0).getInt("price");
            }
            db.addToFavoriteCarWashingStations(
                    carWash.getInt("id"),
                    carWash.getString("name"),
                    carWash.getString("address"),
                    price,
                    100,
                    200,
                    carWash.getInt("carwash_city_id"),
                    carWash.getString("carwash_city_name")
            );
        }
        db.close();
        return true;
    }

    public boolean saveNewUserData(JSONObject userObject) {
        CarWashesDatabase db = new CarWashesDatabase(context);
        db.open();
        db.deleteUserInformation();
        try {
            JSONObject cityObject = userObject.getJSONObject("city");
            JSONObject carTypeObject = userObject.getJSONObject("car_type");

            //saving user data to internal SQLite database
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
        } catch (JSONException e) {
            e.printStackTrace();
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean saveUserOrders(JSONArray ordersArray) {
        CarWashesDatabase db = new CarWashesDatabase(context);
        db.open();
        db.deleteMyOrders();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar date = Calendar.getInstance();
        String startTime, status = "";
        try {
            for (int i = 0; i < ordersArray.length(); i++) {
                JSONObject order = ordersArray.getJSONObject(i);
                startTime = order.getString("start_time");
                date.setTime(sdf1.parse(startTime.substring(0, startTime.length() - 1)));
                switch (order.getInt("status")) {
                    case 1:
                        status = "Активный";
                        break;
                    case 2:
                        status = "Завершен";
                        break;
                    case 3:
                        status = "Отменен Пользователем";
                        break;
                    case 4:
                        status = "Отменен Администратором";
                        break;
                }
                db.addToUserOrders(
                        order.getInt("id"),
                        order.getString("carwash_name"),
                        order.getString("carwash_address"),
                        order.getString("car_type") + ": " + order.getString("service_name"),
                        sdf2.format(date.getTime()),
                        order.getString("price"),
                        status
                );
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            db.close();
            return false;
        }
        db.close();
        return true;
    }
}
