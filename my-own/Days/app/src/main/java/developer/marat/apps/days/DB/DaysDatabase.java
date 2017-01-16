package developer.marat.apps.days.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

public class DaysDatabase {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TYPE = "since_or_until";
    public static final String KEY_FROM_DATE = "from_date";
    public static final String KEY_UNTIL_DATE = "until_date";
    public static final String KEY_TITLE = "events_title";
    public static final String KEY_DESCRIPTION = "events_description";
    public static final String KEY_ALL_ORDER = "all_order";
    public static final String KEY_SINCE_ORDER = "since_order";
    public static final String KEY_ALARM_BEFORE = "day_before_alarms";
    public static final String KEY_ALARM_ON_DAY = "on_day_alarms";

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
                    KEY_DESCRIPTION + " TEXT, " +
                    KEY_ALL_ORDER + " INTEGER, " +
                    KEY_SINCE_ORDER + " INTEGER, " +
                    KEY_ALARM_BEFORE + " TEXT, " +
                    KEY_ALARM_ON_DAY + " TEXT);"
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

    public void close()  {
        ourHelper.close();
    }

    public long addData(String type, String title, String from, String until, String description, int allOrder, int sinceOrder, boolean dayBefore, boolean onDay) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_TYPE, type);
        cv.put(KEY_TITLE, title);
        cv.put(KEY_FROM_DATE, from);
        cv.put(KEY_UNTIL_DATE, until);
        cv.put(KEY_DESCRIPTION, description);
        cv.put(KEY_ALL_ORDER, allOrder);
        cv.put(KEY_SINCE_ORDER, sinceOrder);
        cv.put(KEY_ALARM_BEFORE, dayBefore);
        cv.put(KEY_ALARM_ON_DAY, onDay);

        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public ArrayList<ArrayList<String>> getData() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION, KEY_ALL_ORDER, KEY_SINCE_ORDER, KEY_ALARM_BEFORE, KEY_ALARM_ON_DAY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);
        int iAllOrder = c.getColumnIndex(KEY_ALL_ORDER);
        int iSinceOrder = c.getColumnIndex(KEY_SINCE_ORDER);
        int iDayBefore = c.getColumnIndex(KEY_ALARM_BEFORE);
        int iOnDay = c.getColumnIndex(KEY_ALARM_ON_DAY);

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(c.getString(iRow));
            temp.add(c.getString(iType));
            temp.add(c.getString(iFrom));
            temp.add(c.getString(iUntil));
            temp.add(c.getString(iTitle));
            temp.add(c.getString(iDescription));
            temp.add(c.getString(iAllOrder));
            temp.add(c.getString(iSinceOrder));
            temp.add(c.getString(iDayBefore));
            temp.add(c.getString(iOnDay));
            data.add(temp);
        }

        return data;
    }

    public ArrayList<ArrayList<String>> getSince() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION, KEY_ALL_ORDER, KEY_SINCE_ORDER, KEY_ALARM_BEFORE, KEY_ALARM_ON_DAY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);
        int iAllOrder = c.getColumnIndex(KEY_ALL_ORDER);
        int iSinceOrder = c.getColumnIndex(KEY_SINCE_ORDER);
        int iDayBefore = c.getColumnIndex(KEY_ALARM_BEFORE);
        int iOnDay = c.getColumnIndex(KEY_ALARM_ON_DAY);

        ArrayList<ArrayList<String>> dataSince = new ArrayList<ArrayList<String>>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if("since".equals(c.getString(iType))){
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(c.getString(iRow));
                temp.add(c.getString(iType));
                temp.add(c.getString(iFrom));
                temp.add(c.getString(iUntil));
                temp.add(c.getString(iTitle));
                temp.add(c.getString(iDescription));
                temp.add(c.getString(iAllOrder));
                temp.add(c.getString(iSinceOrder));
                temp.add(c.getString(iDayBefore));
                temp.add(c.getString(iOnDay));
                dataSince.add(temp);
            }
        }

        return dataSince;
    }

    public ArrayList<ArrayList<String>> getUntil() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION, KEY_ALL_ORDER, KEY_SINCE_ORDER, KEY_ALARM_BEFORE, KEY_ALARM_ON_DAY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);
        int iAllOrder = c.getColumnIndex(KEY_ALL_ORDER);
        int iSinceOrder = c.getColumnIndex(KEY_SINCE_ORDER);
        int iDayBefore = c.getColumnIndex(KEY_ALARM_BEFORE);
        int iOnDay = c.getColumnIndex(KEY_ALARM_ON_DAY);

        ArrayList<ArrayList<String>> dataUntil = new ArrayList<ArrayList<String>>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if("until".equals(c.getString(iType))){
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(c.getString(iRow));
                temp.add(c.getString(iType));
                temp.add(c.getString(iFrom));
                temp.add(c.getString(iUntil));
                temp.add(c.getString(iTitle));
                temp.add(c.getString(iDescription));
                temp.add(c.getString(iAllOrder));
                temp.add(c.getString(iSinceOrder));
                temp.add(c.getString(iDayBefore));
                temp.add(c.getString(iOnDay));
                dataUntil.add(temp);
            }
        }

        return dataUntil;
    }

    public ArrayList<String> getRow(Long rowID) {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION, KEY_ALL_ORDER, KEY_SINCE_ORDER, KEY_ALARM_BEFORE, KEY_ALARM_ON_DAY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + rowID, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iFrom = c.getColumnIndex(KEY_FROM_DATE);
        int iUntil = c.getColumnIndex(KEY_UNTIL_DATE);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iDescription = c.getColumnIndex(KEY_DESCRIPTION);
        int iAllOrder = c.getColumnIndex(KEY_ALL_ORDER);
        int iSinceOrder = c.getColumnIndex(KEY_SINCE_ORDER);
        int iDayBefore = c.getColumnIndex(KEY_ALARM_BEFORE);
        int iOnDay = c.getColumnIndex(KEY_ALARM_ON_DAY);

        ArrayList<String> rowData = new ArrayList<String>();
        c.moveToFirst();

        rowData.add(c.getString(iType));
        rowData.add(c.getString(iFrom));
        rowData.add(c.getString(iUntil));
        rowData.add(c.getString(iTitle));
        rowData.add(c.getString(iDescription));
        rowData.add(c.getString(iAllOrder));
        rowData.add(c.getString(iSinceOrder));
        rowData.add(c.getString(iDayBefore));
        rowData.add(c.getString(iOnDay));

        return rowData;
    }

    public void updateEntry(long rowID, String type, String from, String until, String title, String description, int all, int since, boolean dayBefore, boolean onDay) {

        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(KEY_TYPE, type);
        cvUpdate.put(KEY_FROM_DATE, from);
        cvUpdate.put(KEY_UNTIL_DATE, until);
        cvUpdate.put(KEY_TITLE, title);
        cvUpdate.put(KEY_DESCRIPTION, description);
        cvUpdate.put(KEY_ALL_ORDER, all);
        cvUpdate.put(KEY_SINCE_ORDER, since);
        cvUpdate.put(KEY_ALARM_BEFORE, dayBefore);
        cvUpdate.put(KEY_ALARM_ON_DAY, onDay);
        ourDatabase.update(DATABASE_TABLE, cvUpdate, KEY_ROWID + "=" + rowID, null);
    }

    public void deleteEvent(long row) {
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
    }

    public ArrayList<String> getLastOrders() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION, KEY_ALL_ORDER, KEY_SINCE_ORDER};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iAllOrder = c.getColumnIndex(KEY_ALL_ORDER);
        int iSinceOrder = c.getColumnIndex(KEY_SINCE_ORDER);

        ArrayList<String> allOrders = new ArrayList<String>();
        ArrayList<String> sinceOrders = new ArrayList<String>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            allOrders.add(c.getString(iAllOrder));
            sinceOrders.add(c.getString(iSinceOrder));
        }

        ArrayList<String> results = new ArrayList<String>();

        if (allOrders.size() == 0) {
            results.add("0");
        }
        else {
            Collections.sort(allOrders);
            results.add(allOrders.get(allOrders.size()-1));
        }
        if (sinceOrders.size() == 0) {
            results.add("0");
        }
        else {
            Collections.sort(sinceOrders);
            results.add(sinceOrders.get(sinceOrders.size()-1));
        }

        return results;
    }

    public void updateAllOrder(long rowID, int newPos) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ALL_ORDER, newPos);
        ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + "=" + rowID, null);
    }

    public void updateSinceOrder(long rowID, int newPos) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_SINCE_ORDER, newPos);
        ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + "=" + rowID, null);
    }

    public long getLastRowId() {
        String[] columns = new String[]{KEY_ROWID, KEY_TYPE, KEY_FROM_DATE, KEY_UNTIL_DATE, KEY_TITLE, KEY_DESCRIPTION, KEY_ALL_ORDER, KEY_SINCE_ORDER, KEY_ALARM_BEFORE, KEY_ALARM_ON_DAY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRow = c.getColumnIndex(KEY_ROWID);
        c.moveToLast();

        return Long.parseLong(c.getString(iRow));
    }
}