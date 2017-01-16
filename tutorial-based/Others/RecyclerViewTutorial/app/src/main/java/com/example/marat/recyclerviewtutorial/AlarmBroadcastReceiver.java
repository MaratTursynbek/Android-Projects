package com.example.marat.recyclerviewtutorial;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "myTag";

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(TAG, "onReceive()");
        String info, itemPosition;
        ArrayList<String> row;

        Bundle extras = intent.getExtras();
        info = extras.getString("Text");
        itemPosition = extras.getString("Position");

        Database db = new Database(context);
        db.open();
        row = db.getRow(Long.parseLong(itemPosition));
        db.close();

        Intent i = new Intent(context, ItemInformationActivity.class);
        i.putExtra("Text", info);
        i.putExtra("Position", itemPosition);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(itemPosition), i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(row.get(2));
        notificationBuilder.setContentText("There is a long description of each event");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setTicker("1 day until " + row.get(2));
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.v(TAG, "before notify");
        nm.notify(NOTIFICATION_ID, notificationBuilder.build());

    }
}
