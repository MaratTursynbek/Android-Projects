package com.example.marat.days.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DaysDatabase {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TYPE = "since_or_until";
    public static final String KEY_FROM_DATE = "from_date";
    public static final String KEY_UNTIL_DATE = "until_date";
    public static final String KEY_TITLE = "events_title";
    public static final String KEY_DESCRIPTION = "events_description";

    private static final String DATABASE_NAME = "daysDB";
    private static final String DATABASE_TABLE = "daysTable";
    private static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TYPE + " TEXT NOT NULL, " +
                    KEY_FROM_DATE + " TEXT NOT NULL, " +
                    KEY_UNTIL_DATE + " TEXT NOT NULL, " +
                    KEY_TITLE + " TEXT NOT NULL, " +
                    KEY_DESCRIPTION + " TEXT);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DaysDatabase(Context c) {
        ourContext = c;
    }

    public DaysDatabase open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long addData(String type, String title, String from, String until, String description) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_TYPE, type);
        cv.put(KEY_TITLE, title);
        cv.put(KEY_FROM_DATE, from);
        cv.put(KEY_UNTIL_DATE, until);
        cv.put(KEY_DESCRIPTION, description);

        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public String[][] getData() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);

        int rows = c.getCount();
        int i = 0;

        String[][] data = new String[rows][6];

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            data[i][0] = c.getString(iRow);
            data[i][1] = c.getString(iType);
            data[i][2] = c.getString(iFrom);
            data[i][3] = c.getString(iUntil);
            data[i][4] = c.getString(iTitle);
            data[i][5] = c.getString(iDescription);
            i++;
        }

        return data;
    }

    public String[][] getSince() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);

        int rows = c.getCount();
        int i = 0;

        String[][] data = new String[rows][6];

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if("since".equals(c.getString(iType))){
                data[i][0] = c.getString(iRow);
                data[i][1] = c.getString(iType);
                data[i][2] = c.getString(iFrom);
                data[i][3] = c.getString(iUntil);
                data[i][4] = c.getString(iTitle);
                data[i][5] = c.getString(iDescription);
                i++;
            }
        }

        String[][] dataSince = new String[i][6];

        for (int j=0; j<rows; j++) {
            if(data[j][1] != null){
                dataSince[j][0] = data[j][0];
                dataSince[j][1] = data[j][1];
                dataSince[j][2] = data[j][2];
                dataSince[j][3] = data[j][3];
                dataSince[j][4] = data[j][4];
                dataSince[j][5] = data[j][5];
            }
        }

        return dataSince;
    }

    public String[][] getUntil() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);

        int rows = c.getCount();
        int i = 0;

        String[][] data = new String[rows][6];

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if("until".equals(c.getString(iType))){
                data[i][0] = c.getString(iRow);
                data[i][1] = c.getString(iType);
                data[i][2] = c.getString(iFrom);
                data[i][3] = c.getString(iUntil);
                data[i][4] = c.getString(iTitle);
                data[i][5] = c.getString(iDescription);
                i++;
            }
        }

        String[][] dataUntil = new String[i][6];

        for (int j=0; j<rows; j++) {
            if(data[j][1] != null){
                dataUntil[j][0] = data[j][0];
                dataUntil[j][1] = data[j][1];
                dataUntil[j][2] = data[j][2];
                dataUntil[j][3] = data[j][3];
                dataUntil[j][4] = data[j][4];
                dataUntil[j][5] = data[j][5];
            }
        }

        return dataUntil;
    }

    public String[] getRow(Long rowID) {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + rowID, null, null, null, null);

        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);

        String[] data = new String[5];
        c.moveToFirst();

        data[0] = c.getString(iType);
        data[1] = c.getString(iFrom);
        data[2] = c.getString(iUntil);
        data[3] = c.getString(iTitle);
        data[4] = c.getString(iDescription);

        return data;
    }

    public void updateEntry(long rowID, String type, String title, String from, String until, String description) {

        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(KEY_TYPE, type);
        cvUpdate.put(KEY_FROM_DATE, from);
        cvUpdate.put(KEY_UNTIL_DATE, until);
        cvUpdate.put(KEY_TITLE, title);
        cvUpdate.put(KEY_DESCRIPTION, description);
        ourDatabase.update(DATABASE_TABLE, cvUpdate, KEY_ROWID + "=" + rowID, null);
    }

    public void deleteEvent(long row) {
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
    }
}