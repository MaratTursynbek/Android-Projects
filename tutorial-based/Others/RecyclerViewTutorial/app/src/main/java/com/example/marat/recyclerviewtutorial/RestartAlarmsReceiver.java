package com.example.marat.recyclerviewtutorial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class RestartAlarmsReceiver extends BroadcastReceiver {

    private static final String TAG = "myTag";

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Intent i = new Intent(context, BootService.class);
            ComponentName service = context.startService(i);

            if (null == service) {
                // something really wrong here
                Log.e(TAG, "Could not start service ");
            }
            else {
                Log.e(TAG, "Successfully started service ");
            }

        } else {
            Log.e(TAG, "Received unexpected intent " + intent.toString());
        }
    }
}
