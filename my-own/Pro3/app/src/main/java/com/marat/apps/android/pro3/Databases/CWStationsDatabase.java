package com.marat.apps.android.pro3.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * SQLite Database for Pro3 KZ
 * <p>
 * Contains tables:
 * 1. Cities where car washing stations use our service
 * 2. Car types that can be washed via online booking
 * 3. User account information
 * 4. Types of services available for ONLINE booking
 * 5. Types of services available for OFFLINE booking
 * 6. All Car Washing Stations in user's city
 * 7. Favorite Car washing stations of user in his/her city
 * 8. User's all orders
 */

public class CWStationsDatabase {

    /**
     * row id column name shared by all tables
     */
    public static final String ROW_ID = "_id";

    /**
     * #1
     * table columns of:
     * all CITIES
     */
    public static final String KEY_CITY_ID = "city_id";
    public static final String KEY_CITY_NAME = "city_name";
    public static final String KEY_CITY_UPDATED = "city_updated";

    /**
     * #2
     * table columns of:
     * all CAR TYPES
     */
    public static final String KEY_CAR_TYPE_ID = "car_type_id";
    public static final String KEY_CAR_TYPE_NAME = "car_type_name";
    public static final String KEY_CAR_TYPE_UPDATED = "car_type_updated";

    /**
     * #3
     * table columns of:
     * user's ACCOUNT information
     */
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_CREATED_DATE = "user_created_date";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_CITY_ID = "user_city_id";
    public static final String KEY_USER_CITY_NAME = "user_city_name";
    public static final String KEY_USER_CAR_TYPE_ID = "user_car_type_id";
    public static final String KEY_USER_CAR_TYPE_NAME = "user_car_type_name";
    public static final String KEY_USER_PHONE_NUMBER = "user_phone_number";

    /**
     * #4 and #5
     * table columns of:
     * ONLINE AND OFFLINE SERVICES for all washing stations
     */
    public static final String KEY_SERVICE_ID = "online_service_id";
    public static final String KEY_SERVICE_NAME = "online_service_name";
    public static final String KEY_SERVICE_PRICE = "online_service_price";
    public static final String KEY_SERVICE_DESCRIPTION = "online_service_description";

    /**
     * #6 and #7
     * table columns of:
     * ALL and FAVORITE car washing stations in user's city
     */
    public static final String KEY_CAR_WASH_ID = "car_wash_id";
    public static final String KEY_CAR_WASH_NAME = "car_wash_name";
    public static final String KEY_CAR_WASH_ADDRESS = "car_wash_address";
    public static final String KEY_CAR_WASH_EXAMPLE_PRICE = "car_wash_example_price";
    public static final String KEY_CAR_WASH_LONGITUDE = "car_wash_longitude";
    public static final String KEY_CAR_WASH_LATITUDE = "car_wash_latitude";
    public static final String KEY_CAR_WASH_CITY_ID = "car_wash_city_id";
    public static final String KEY_CAR_WASH_CITY_NAME = "car_wash_city_name";

    /**
     * #8
     * table columns of:
     * user's all orders
     */
    public static final String KEY_USER_ORDER_SERVICES = "order_services";
    public static final String KEY_USER_ORDER_DATE = "order_date";
    public static final String KEY_USER_ORDER_PRICE = "order_price";
    public static final String KEY_USER_ORDER_STATUS = "order_status";

    /**
     * all table names
     */
    private static final String DATABASE_TABLE_1_ALL_CITIES = "table_all_cities";
    private static final String DATABASE_TABLE_2_ALL_CAR_TYPES = "table_all_car_types";
    private static final String DATABASE_TABLE_3_USER_ACCOUNT_INFO = "table_user_account_info";
    private static final String DATABASE_TABLE_4_ONLINE_SERVICES = "table_online_services";
    private static final String DATABASE_TABLE_5_OFFLINE_SERVICES = "table_offline_services";
    private static final String DATABASE_TABLE_6_ALL_STATIONS = "table_all_car_washing_stations";
    private static final String DATABASE_TABLE_7_FAVORITE_STATIONS = "table_favorite_car_washing_stations";
    private static final String DATABASE_TABLE_8_USER_ORDERS = "table_user_orders";

    /**
     * database constants
     */
    private static final String DATABASE_NAME = "car_wash_database";
    private static final int DATABASE_VERSION = 1;

    /**
     * private class constants
     */
    private DBHelper helper;
    private Context context;
    private SQLiteDatabase database;

    /**
     * public constructor
     */
    public CWStationsDatabase(Context c) {
        context = c;
    }

    /**
     * inner SQLiteOpenHelper class
     */
    private static class DBHelper extends SQLiteOpenHelper {

        // Default constructor
        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // ALL CITIES table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_1_ALL_CITIES + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CITY_ID + " INTEGER, " +
                    KEY_CITY_NAME + " TEXT NOT NULL, " +
                    KEY_CITY_UPDATED + " TEXT NOT NULL);"
            );

            // ALL CAR TYPES table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_2_ALL_CAR_TYPES + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CAR_TYPE_ID + " INTEGER, " +
                    KEY_CAR_TYPE_NAME + " TEXT NOT NULL, " +
                    KEY_CAR_TYPE_UPDATED + " TEXT NOT NULL);"
            );

            // USER ACCOUNT INFO table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_3_USER_ACCOUNT_INFO + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_USER_ID + " INTEGER, " +
                    KEY_USER_CREATED_DATE + " TEXT NOT NULL, " +
                    KEY_USER_NAME + " TEXT NOT NULL, " +
                    KEY_USER_CITY_ID + " INTEGER, " +
                    KEY_USER_CITY_NAME + " TEXT NOT NULL, " +
                    KEY_USER_CAR_TYPE_ID + " INTEGER, " +
                    KEY_USER_CAR_TYPE_NAME + " TEXT NOT NULL, " +
                    KEY_USER_PHONE_NUMBER + " TEXT NOT NULL);"
            );

            // ONLINE SERVICES table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_4_ONLINE_SERVICES + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SERVICE_ID + " INTEGER, " +
                    KEY_SERVICE_NAME + " TEXT NOT NULL, " +
                    KEY_SERVICE_DESCRIPTION + " TEXT NOT NULL, " +
                    KEY_SERVICE_PRICE + " INTEGER);"
            );

            // OFFLINE SERVICES table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_5_OFFLINE_SERVICES + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SERVICE_ID + " INTEGER, " +
                    KEY_SERVICE_NAME + " TEXT NOT NULL, " +
                    KEY_SERVICE_DESCRIPTION + " TEXT NOT NULL, " +
                    KEY_SERVICE_PRICE + " INTEGER);"
            );

            // ALL STATIONS table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_6_ALL_STATIONS + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CAR_WASH_ID + " INTEGER, " +
                    KEY_CAR_WASH_NAME + " TEXT NOT NULL, " +
                    KEY_CAR_WASH_ADDRESS + " TEXT NOT NULL, " +
                    KEY_CAR_WASH_EXAMPLE_PRICE + " INTEGER, " +
                    KEY_CAR_WASH_LONGITUDE + " INTEGER, " +
                    KEY_CAR_WASH_LATITUDE + " INTEGER, " +
                    KEY_CAR_WASH_CITY_ID + " INTEGER, " +
                    KEY_CAR_WASH_CITY_NAME + " TEXT NOT NULL);"
            );

            // FAVORITE STATIONS table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_7_FAVORITE_STATIONS + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CAR_WASH_ID + " INTEGER, " +
                    KEY_CAR_WASH_NAME + " TEXT NOT NULL, " +
                    KEY_CAR_WASH_ADDRESS + " TEXT NOT NULL, " +
                    KEY_CAR_WASH_EXAMPLE_PRICE + " INTEGER, " +
                    KEY_CAR_WASH_LONGITUDE + " INTEGER, " +
                    KEY_CAR_WASH_LATITUDE + " INTEGER, " +
                    KEY_CAR_WASH_CITY_ID + " INTEGER, " +
                    KEY_CAR_WASH_CITY_NAME + " TEXT NOT NULL);"
            );

            // USER ORDERS table
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_8_USER_ORDERS + " (" +
                    ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CAR_WASH_ID + " INTEGER, " +
                    KEY_CAR_WASH_NAME + " TEXT NOT NULL, " +
                    KEY_CAR_WASH_ADDRESS + " TEXT NOT NULL, " +
                    KEY_USER_ORDER_SERVICES + " TEXT NOT NULL, " +
                    KEY_USER_ORDER_DATE + " TEXT NOT NULL, " +
                    KEY_USER_ORDER_PRICE + " TEXT NOT NULL, " +
                    KEY_USER_ORDER_STATUS + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_1_ALL_CITIES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_2_ALL_CAR_TYPES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_3_USER_ACCOUNT_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_4_ONLINE_SERVICES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_5_OFFLINE_SERVICES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_6_ALL_STATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_7_FAVORITE_STATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_8_USER_ORDERS);
            onCreate(db);
        }
    }

    /**
     * opens the database and returns its instance
     */
    public CWStationsDatabase open() {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
        return this;
    }

    /**
     * closes the database
     */
    public void close() {
        helper.close();
    }

    /**
     * adds 1 city to DATABASE_TABLE_1_ALL_CITIES
     */
    public long addToAllCities(int cityId, String cityName, String date) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CITY_ID, cityId);
        cv.put(KEY_CITY_NAME, cityName);
        cv.put(KEY_CITY_UPDATED, date);
        return database.insert(DATABASE_TABLE_1_ALL_CITIES, null, cv);
    }

    /**
     * adds 1 car to DATABASE_TABLE_2_ALL_CAR_TYPES
     */
    public long addToAllCarTypes(int carTypeId, String carTypeName, String date) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CAR_TYPE_ID, carTypeId);
        cv.put(KEY_CAR_TYPE_NAME, carTypeName);
        cv.put(KEY_CAR_TYPE_UPDATED, date);
        return database.insert(DATABASE_TABLE_2_ALL_CAR_TYPES, null, cv);
    }

    /**
     * adds user account information to DATABASE_TABLE_3_USER_ACCOUNT_INFO
     */
    public long addUserInformation(int userId, String userCreatedDate, String userName, int userCityId, String userCityName, int userCarTypeId, String userCarTypeName, String userPhoneNumber) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USER_ID, userId);
        cv.put(KEY_USER_CREATED_DATE, userCreatedDate);
        cv.put(KEY_USER_NAME, userName);
        cv.put(KEY_USER_CITY_ID, userCityId);
        cv.put(KEY_USER_CITY_NAME, userCityName);
        cv.put(KEY_USER_CAR_TYPE_ID, userCarTypeId);
        cv.put(KEY_USER_CAR_TYPE_NAME, userCarTypeName);
        cv.put(KEY_USER_PHONE_NUMBER, userPhoneNumber);
        return database.insert(DATABASE_TABLE_3_USER_ACCOUNT_INFO, null, cv);
    }

    /**
     * adds 1 ONLINE service information to DATABASE_TABLE_4_ONLINE_SERVICES
     */
    public long addOnlineService(int serviceId, String serviceName, int servicePrice, String serviceDescription) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_SERVICE_ID, serviceId);
        cv.put(KEY_SERVICE_NAME, serviceName);
        cv.put(KEY_SERVICE_PRICE, servicePrice);
        cv.put(KEY_SERVICE_DESCRIPTION, serviceDescription);
        return database.insert(DATABASE_TABLE_4_ONLINE_SERVICES, null, cv);
    }

    /**
     * adds 1 OFFLINE service information to DATABASE_TABLE_5_OFFLINE_SERVICES
     */
    public long addOfflineService(int serviceId, String serviceName, int servicePrice, String serviceDescription) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_SERVICE_ID, serviceId);
        cv.put(KEY_SERVICE_NAME, serviceName);
        cv.put(KEY_SERVICE_PRICE, servicePrice);
        cv.put(KEY_SERVICE_DESCRIPTION, serviceDescription);
        return database.insert(DATABASE_TABLE_5_OFFLINE_SERVICES, null, cv);
    }

    /**
     * adds 1 car washing station to DATABASE_TABLE_6_ALL_STATIONS
     */
    public long addToAllCarWashingStations(int carWashId, String name, String address, int price, int longitude, int latitude, int carWashCityId, String carWashCityName) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CAR_WASH_ID, carWashId);
        cv.put(KEY_CAR_WASH_NAME, name);
        cv.put(KEY_CAR_WASH_ADDRESS, address);
        cv.put(KEY_CAR_WASH_EXAMPLE_PRICE, price);
        cv.put(KEY_CAR_WASH_LONGITUDE, longitude);
        cv.put(KEY_CAR_WASH_LATITUDE, latitude);
        cv.put(KEY_CAR_WASH_CITY_ID, carWashCityId);
        cv.put(KEY_CAR_WASH_CITY_NAME, carWashCityName);
        return database.insert(DATABASE_TABLE_6_ALL_STATIONS, null, cv);
    }

    /**
     * adds 1 car washing station to DATABASE_TABLE_7_FAVORITE_STATIONS
     */
    public long addToFavoriteCarWashingStations(int carWashId, String name, String address, int price, int longitude, int latitude, int carWashCityId, String carWashCityName) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CAR_WASH_ID, carWashId);
        cv.put(KEY_CAR_WASH_NAME, name);
        cv.put(KEY_CAR_WASH_ADDRESS, address);
        cv.put(KEY_CAR_WASH_EXAMPLE_PRICE, price);
        cv.put(KEY_CAR_WASH_LONGITUDE, longitude);
        cv.put(KEY_CAR_WASH_LATITUDE, latitude);
        cv.put(KEY_CAR_WASH_CITY_ID, carWashCityId);
        cv.put(KEY_CAR_WASH_CITY_NAME, carWashCityName);
        return database.insert(DATABASE_TABLE_7_FAVORITE_STATIONS, null, cv);
    }

    /**
     * adds 1 order to DATABASE_TABLE_8_USER_ORDERS
     */
    public long addToMyOrders(int carWashId, String name, String address, String services, String date, String price, String status) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CAR_WASH_ID, carWashId);
        cv.put(KEY_CAR_WASH_NAME, name);
        cv.put(KEY_CAR_WASH_ADDRESS, address);
        cv.put(KEY_USER_ORDER_SERVICES, services);
        cv.put(KEY_USER_ORDER_DATE, date);
        cv.put(KEY_USER_ORDER_PRICE, price);
        cv.put(KEY_USER_ORDER_STATUS, status);
        return database.insert(DATABASE_TABLE_8_USER_ORDERS, null, cv);
    }

    /**
     * returns Cursor pointing to all CITIES
     */
    public Cursor getAllCities() {
        String[] columns = new String[]{ROW_ID, KEY_CITY_ID, KEY_CITY_NAME, KEY_CITY_UPDATED};
        return database.query(DATABASE_TABLE_1_ALL_CITIES, columns, null, null, null, null, null);
    }

    /**
     * returns Cursor pointing to all CAR TYPES
     */
    public Cursor getAllCarTypes() {
        String[] columns = new String[]{ROW_ID, KEY_CAR_TYPE_ID, KEY_CAR_TYPE_NAME, KEY_CAR_TYPE_UPDATED};
        return database.query(DATABASE_TABLE_2_ALL_CAR_TYPES, columns, null, null, null, null, null);
    }

    /**
     * returns Cursor pointing USER INFORMATION
     */
    public Cursor getUserInformation() {
        String[] columns = new String[]{ROW_ID, KEY_USER_ID, KEY_USER_CREATED_DATE, KEY_USER_NAME, KEY_USER_CITY_ID, KEY_USER_CITY_NAME, KEY_USER_CAR_TYPE_ID, KEY_USER_CAR_TYPE_NAME, KEY_USER_PHONE_NUMBER};
        return database.query(DATABASE_TABLE_3_USER_ACCOUNT_INFO, columns, null, null, null, null, null);
    }

    /**
     * returns Cursor pointing ONLINE services
     */
    public Cursor getOnlineServices() {
        String[] columns = new String[]{ROW_ID, KEY_SERVICE_ID, KEY_SERVICE_NAME, KEY_SERVICE_PRICE, KEY_SERVICE_DESCRIPTION};
        return database.query(DATABASE_TABLE_4_ONLINE_SERVICES, columns, null, null, null, null, null);
    }

    /**
     * returns Cursor pointing OFFLINE services
     */
    public Cursor getOfflineServices() {
        String[] columns = new String[]{ROW_ID, KEY_SERVICE_ID, KEY_SERVICE_NAME, KEY_SERVICE_PRICE, KEY_SERVICE_DESCRIPTION};
        return database.query(DATABASE_TABLE_5_OFFLINE_SERVICES, columns, null, null, null, null, null);
    }

    /**
     * returns Cursor pointing to ALL car washing stations
     */
    public Cursor getAllStations() {
        String[] columns = new String[]{ROW_ID, KEY_CAR_WASH_ID, KEY_CAR_WASH_NAME, KEY_CAR_WASH_ADDRESS, KEY_CAR_WASH_EXAMPLE_PRICE, KEY_CAR_WASH_LONGITUDE, KEY_CAR_WASH_LATITUDE, KEY_CAR_WASH_CITY_ID, KEY_CAR_WASH_CITY_NAME};
        return database.query(DATABASE_TABLE_6_ALL_STATIONS, columns, null, null, null, null, null);
    }

    /**
     * returns Cursor pointing to FAVORITE car washing stations
     */
    public boolean userHasFavoriteStations() {
        String[] columns = new String[]{ROW_ID, KEY_CAR_WASH_ID, KEY_CAR_WASH_NAME, KEY_CAR_WASH_ADDRESS, KEY_CAR_WASH_EXAMPLE_PRICE, KEY_CAR_WASH_LONGITUDE, KEY_CAR_WASH_LATITUDE, KEY_CAR_WASH_CITY_ID, KEY_CAR_WASH_CITY_NAME};
        Cursor c1 = database.query(DATABASE_TABLE_6_ALL_STATIONS, columns, null, null, null, null, null);
        Cursor c2 = database.query(DATABASE_TABLE_7_FAVORITE_STATIONS, columns, null, null, null, null, null);
        return ((c1.getCount() > 0) && (c2.getCount() > 0));
    }

    public Cursor getFavoriteStations1(ArrayList<Integer> cityId) {
        String query = "";
        for (int i = 0; i < cityId.size(); i++) {
            query = query + "select * from " + DATABASE_TABLE_7_FAVORITE_STATIONS + " where " + KEY_CAR_WASH_CITY_ID + " =  " + cityId.get(i);
            if ((i + 1) < cityId.size()) {
                query = query + " union all ";
            }
        }
        return database.rawQuery(query, null);
    }

    public Cursor getFavoriteStations() {
        return database.rawQuery("select * from " + DATABASE_TABLE_7_FAVORITE_STATIONS + " where " + KEY_CAR_WASH_CITY_ID + "=1 " +
                "union " +
                "select * from " + DATABASE_TABLE_7_FAVORITE_STATIONS + " where " + KEY_CAR_WASH_CITY_ID + "!=1 " + "order by " + KEY_CAR_WASH_CITY_ID, null);
    }

    /**
     * returns Cursor pointing to user's all ORDERS
     */
    public Cursor getMyOrders() {
        String[] columns = new String[]{ROW_ID, KEY_CAR_WASH_ID, KEY_CAR_WASH_NAME, KEY_CAR_WASH_ADDRESS, KEY_USER_ORDER_SERVICES, KEY_USER_ORDER_DATE, KEY_USER_ORDER_PRICE, KEY_USER_ORDER_STATUS};
        return database.query(DATABASE_TABLE_8_USER_ORDERS, columns, null, null, null, null, null);
    }

    /**
     * returns Cursor pointing to the chosen Car Wash Station
     */
    public Cursor getStationAt(long rowId) {
        String[] columns = new String[]{ROW_ID, KEY_CAR_WASH_ID, KEY_CAR_WASH_NAME, KEY_CAR_WASH_ADDRESS, KEY_CAR_WASH_EXAMPLE_PRICE, KEY_CAR_WASH_LONGITUDE, KEY_CAR_WASH_LATITUDE};
        return database.query(DATABASE_TABLE_6_ALL_STATIONS, columns, ROW_ID + "=" + rowId, null, null, null, null);
    }

    /**
     * returns Cursor pointing to the chosen Car Wash Station
     */
    public Cursor getFavoriteStationAt(long rowId) {
        String[] columns = new String[]{ROW_ID, KEY_CAR_WASH_ID, KEY_CAR_WASH_NAME, KEY_CAR_WASH_ADDRESS, KEY_CAR_WASH_EXAMPLE_PRICE, KEY_CAR_WASH_LONGITUDE, KEY_CAR_WASH_LATITUDE};
        return database.query(DATABASE_TABLE_7_FAVORITE_STATIONS, columns, ROW_ID + "=" + rowId, null, null, null, null);
    }

    /**
     * returns Cursor pointing to user's 1 ORDERS
     */
    public Cursor getOrderAt(long rowId) {
        String[] columns = new String[]{ROW_ID, KEY_CAR_WASH_ID, KEY_CAR_WASH_NAME, KEY_CAR_WASH_ADDRESS, KEY_USER_ORDER_SERVICES, KEY_USER_ORDER_DATE, KEY_USER_ORDER_PRICE, KEY_USER_ORDER_STATUS};
        return database.query(DATABASE_TABLE_8_USER_ORDERS, columns, ROW_ID + "=" + rowId, null, null, null, null);
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_1_ALL_CITIES
     */
    public int deleteAllCities() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_1_ALL_CITIES + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_1_ALL_CITIES, "1", null);
        }
        return 0;
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_2_ALL_CAR_TYPES
     */
    public int deleteAllCarTypes() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_2_ALL_CAR_TYPES + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_2_ALL_CAR_TYPES, "1", null);
        }
        return 0;
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_3_USER_ACCOUNT_INFO
     */
    public int deleteUserInformation() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_3_USER_ACCOUNT_INFO + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_3_USER_ACCOUNT_INFO, "1", null);
        }
        return 0;
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_4_ONLINE_SERVICES
     */
    public int deleteOnlineServices() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_4_ONLINE_SERVICES + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_4_ONLINE_SERVICES, "1", null);
        }
        return 0;
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_5_OFFLINE_SERVICES
     */
    public int deleteOfflineServices() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_5_OFFLINE_SERVICES + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_5_OFFLINE_SERVICES, "1", null);
        }
        return 0;
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_6_ALL_STATIONS
     */
    public int deleteAllStations() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_6_ALL_STATIONS + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_6_ALL_STATIONS, "1", null);
        }
        return 0;
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_7_FAVORITE_STATIONS
     */
    public int deleteFavoriteStations() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_7_FAVORITE_STATIONS + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_7_FAVORITE_STATIONS, "1", null);
        }
        return 0;
    }

    /**
     * deletes and returns number of deleted rows in DATABASE_TABLE_8_USER_ORDERS
     */
    public int deleteMyOrders() {
        Cursor c = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DATABASE_TABLE_8_USER_ORDERS + "'", null);
        if (c.getCount() > 0) {
            return database.delete(DATABASE_TABLE_8_USER_ORDERS, "1", null);
        }
        return 0;
    }
}
