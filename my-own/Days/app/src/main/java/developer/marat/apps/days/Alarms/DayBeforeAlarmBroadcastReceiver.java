package developer.marat.apps.days.Alarms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import developer.marat.apps.days.Activities.EventInformationActivity;
import developer.marat.apps.days.DB.DaysDatabase;
import developer.marat.apps.days.R;

import java.util.ArrayList;

public class DayBeforeAlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isOnDay;
        long rowId;
        int requestRowId;
        ArrayList<String> row;

        Bundle args = intent.getExtras();
        rowId = args.getLong("itemPosition");
        requestRowId = (int) rowId;

        DaysDatabase data = new DaysDatabase(context);
        data.open();
        row = data.getRow(rowId);

        Intent i = new Intent(context, EventInformationActivity.class);
        i.putExtra("EventPosition", rowId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestRowId, i, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle("1 day until");
        notificationBuilder.setContentText(row.get(3));
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setTicker("Reminder");
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(requestRowId, notificationBuilder.build());

        if ("1".equals(row.get(8))) {
            isOnDay = true;
        }
        else {
            isOnDay = false;
        }

        data.updateEntry(rowId, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), Integer.parseInt(row.get(5)), Integer.parseInt(row.get(6)), false, isOnDay);
        data.close();
    }
}
