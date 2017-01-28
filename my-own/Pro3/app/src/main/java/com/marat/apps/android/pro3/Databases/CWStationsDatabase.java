package com.marat.apps.android.pro3.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CWStationsDatabase {

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_CAR_WASH_ID = "car_wash_id";
    public static final String KEY_FAVORITE = "favorite";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PRICE = "price";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";

    private static final String DATABASE_NAME = "car_wash_database";
    private static final String DATABASE_TABLE_ALL = "car_washing_stations_table_all";
    private static final String DATABASE_TABLE_FAVORITES = "car_washing_stations_table_favorites";
    private static final int DATABASE_VERSION = 1;

    private DBHelper helper;
    private final Context context;
    private SQLiteDatabase database;

    private static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_ALL + " (" +
                    KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CAR_WASH_ID + " INTEGER, " +
                    KEY_FAVORITE + " INTEGER, " +
                    KEY_NAME + " TEXT NOT NULL, " +
                    KEY_ADDRESS + " TEXT NOT NULL, " +
                    KEY_PRICE + " INTEGER, " +
                    KEY_LONGITUDE + " INTEGER, " +
                    KEY_LATITUDE + " INTEGER);"

            );
            db.execSQL("CREATE TABLE " + DATABASE_TABLE_FAVORITES + " (" +
                    KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_CAR_WASH_ID + " INTEGER, " +
                    KEY_FAVORITE + " INTEGER, " +
                    KEY_NAME + " TEXT NOT NULL, " +
                    KEY_ADDRESS + " TEXT NOT NULL, " +
                    KEY_PRICE + " INTEGER, " +
                    KEY_LONGITUDE + " INTEGER, " +
                    KEY_LATITUDE + " INTEGER);"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALL);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FAVORITES);
            onCreate(db);
        }
    }

    public CWStationsDatabase(Context c) {
        context = c;
    }

    public CWStationsDatabase open() {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }

    public long addAllCarWashingStations(int carWashId, int favorite, String name, String address, int price, int longitude, int latitude) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CAR_WASH_ID, carWashId);
        cv.put(KEY_FAVORITE, favorite);
        cv.put(KEY_NAME, name);
        cv.put(KEY_ADDRESS, address);
        cv.put(KEY_PRICE, price);
        cv.put(KEY_LONGITUDE, longitude);
        cv.put(KEY_LATITUDE, latitude);
        return database.insert(DATABASE_TABLE_ALL, null, cv);
    }

    public long addFavoriteCarWashingStations(int carWashId, int favorite, String name, String address, int price, int longitude, int latitude) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CAR_WASH_ID, carWashId);
        cv.put(KEY_FAVORITE, favorite);
        cv.put(KEY_NAME, name);
        cv.put(KEY_ADDRESS, address);
        cv.put(KEY_PRICE, price);
        cv.put(KEY_LONGITUDE, longitude);
        cv.put(KEY_LATITUDE, latitude);
        return database.insert(DATABASE_TABLE_FAVORITES, null, cv);
    }

    public Cursor getAllStations() {
        String[] columns = new String[]{KEY_ROW_ID, KEY_CAR_WASH_ID, KEY_FAVORITE, KEY_NAME, KEY_ADDRESS, KEY_PRICE, KEY_LONGITUDE, KEY_LATITUDE};
        return database.query(DATABASE_TABLE_ALL, columns, null, null, null, null, null);
    }

    public Cursor getFavoriteStations() {
        String[] columns = new String[]{KEY_ROW_ID, KEY_CAR_WASH_ID, KEY_FAVORITE, KEY_NAME, KEY_ADDRESS, KEY_PRICE, KEY_LONGITUDE, KEY_LATITUDE};
        return database.query(DATABASE_TABLE_FAVORITES, columns, null, null, null, null, null);
    }

    public int deleteAllStations() {
        return database.delete(DATABASE_TABLE_ALL, "1", null);
    }

    public int deleteFavoriteStations() {
        return database.delete(DATABASE_TABLE_FAVORITES, "1", null);
    }
}
