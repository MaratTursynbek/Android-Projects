package com.example.marat.recyclerviewtutorial;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    private static final String TAG = "myTag";

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onHandleIntent");

        Database db = new Database(this);
        db.open();
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        data = db.getData();
        db.close();

        Log.d(TAG, "db is openned");

        for (int i=0; i<data.size(); i++) {

            Intent notif = new Intent(this, AlarmBroadcastReceiver.class);
            notif.putExtra("Text", data.get(i).get(2));
            notif.putExtra("Position", data.get(i).get(0));

            int sec = Integer.parseInt(data.get(i).get(3));

            Log.d(TAG, "intent is created");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(data.get(i).get(0)), notif, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (sec * 1000), pendingIntent);

            Log.d(TAG, "Alarm is restarted");
        }

        Log.d(TAG, "All Alarms are set");
    }
}
