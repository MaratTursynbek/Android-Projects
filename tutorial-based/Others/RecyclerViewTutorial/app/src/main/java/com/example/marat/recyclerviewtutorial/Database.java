package com.example.marat.recyclerviewtutorial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_ORDER = "order_numbers";
    public static final String KEY_NAMES = "names_text";
    public static final String KEY_ALARM = "alarm_times";

    public static final String DATABASE_NAME = "myDatabase";
    public static final String DATABASE_TABLE = "myTable";
    public static final int DATABASE_VERSION = 1;

    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context c) {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_ORDER + " INTEGER, " +
                    KEY_NAMES + " TEXT NOT NULL, " +
                    KEY_ALARM + " INTEGER);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public Database(Context c) {
        ourContext = c;
    }

    public Database open() {
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }


    public long addData(String name, int order, int seconds) {
        ContentValues cv = new ContentValues();

        cv.put(KEY_NAMES, name);
        cv.put(KEY_ORDER, order);
        cv.put(KEY_ALARM, seconds);

        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public void deleteData(String rowID) {
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowID, null);
    }

    public ArrayList<ArrayList<String>> getData() {
        String[] columns = new String[]{KEY_ROWID, KEY_ORDER, KEY_NAMES, KEY_ALARM};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iOrder = c.getColumnIndex(KEY_ORDER);
        int iNames = c.getColumnIndex(KEY_NAMES);
        int iAlarms = c.getColumnIndex(KEY_ALARM);

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        c.moveToFirst();

        for (int i=0; !c.isAfterLast(); i++, c.moveToNext()){
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(c.getString(iRow));
            temp.add(c.getString(iOrder));
            temp.add(c.getString(iNames));
            temp.add(c.getString(iAlarms));
            data.add(temp);
        }

        return data;
    }

    public ArrayList<String> getRow(long rowID) {
        String[] columns = new String[]{KEY_ROWID, KEY_ORDER, KEY_NAMES};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + rowID, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iOrder = c.getColumnIndex(KEY_ORDER);
        int iNames = c.getColumnIndex(KEY_NAMES);

        ArrayList<String> row = new ArrayList<>();
        c.moveToFirst();

        row.add(c.getString(iRow));
        row.add(c.getString(iOrder));
        row.add(c.getString(iNames));

        return row;
    }

    public void updateOrder(long rowID, int newPos) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ORDER, newPos);
        ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + "=" + rowID, null);
    }

    public ArrayList<String> getLastRowId() {
        String[] columns = new String[]{KEY_ROWID, KEY_ORDER, KEY_NAMES};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iNames = c.getColumnIndex(KEY_NAMES);

        c.moveToLast();
        ArrayList<String> temp = new ArrayList<String>();
        temp.add(c.getString(iRow));
        temp.add(c.getString(iNames));

        return temp;
    }
}
