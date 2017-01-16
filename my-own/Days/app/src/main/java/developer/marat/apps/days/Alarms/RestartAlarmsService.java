package developer.marat.apps.days.Alarms;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import developer.marat.apps.days.DB.DaysDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RestartAlarmsService extends IntentService{

    public RestartAlarmsService() {
        super("RestartAlarmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        DaysDatabase db = new DaysDatabase(this);
        db.open();
        ArrayList<ArrayList<String>> untilData = new ArrayList<ArrayList<String>>();
        untilData = db.getUntil();
        db.close();

        for (int i=0; i<untilData.size(); i++) {

            Calendar setDate = Calendar.getInstance(TimeZone.getDefault());
            Date from = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                from = format.parse(untilData.get(i).get(3));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setDate.setTime(from);

            setDate.set(Calendar.HOUR_OF_DAY, 0);
            setDate.set(Calendar.MINUTE, 0);
            setDate.set(Calendar.SECOND, 0);
            setDate.set(Calendar.MILLISECOND, 0);

            if("1".equals(untilData.get(i).get(9))) {

                Intent intent1 = new Intent(this, OnDayAlarmBroadcastReceiver.class);
                intent1.putExtra("itemPosition", Long.parseLong(untilData.get(i).get(0)));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(untilData.get(i).get(0)), intent1, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
            }

            if("1".equals(untilData.get(i).get(8))) {

                setDate.add(Calendar.DATE, -1);

                Intent intent2 = new Intent(this, DayBeforeAlarmBroadcastReceiver.class);
                intent2.putExtra("itemPosition", Long.parseLong(untilData.get(i).get(0)));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(untilData.get(i).get(0)), intent2, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
            }
        }
    }
}
