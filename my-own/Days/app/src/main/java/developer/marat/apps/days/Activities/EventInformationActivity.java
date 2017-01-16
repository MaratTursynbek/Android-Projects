package developer.marat.apps.days.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import developer.marat.apps.days.Alarms.DayBeforeAlarmBroadcastReceiver;
import developer.marat.apps.days.Alarms.OnDayAlarmBroadcastReceiver;
import developer.marat.apps.days.DB.DaysDatabase;
import developer.marat.apps.days.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventInformationActivity extends AppCompatActivity {

    long position;

    TextView titleInfoTextView, descriptionInfoTextView;
    TextView numberOfDaysTextView, typeTextView, setDateTextView, daysText;
    TextView remindersTextView, dayBeforeReminderTextView, onDayReminderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleInfoTextView = (TextView) findViewById(R.id.titleInfo);
        descriptionInfoTextView = (TextView) findViewById(R.id.descriptionInfo);
        numberOfDaysTextView = (TextView) findViewById(R.id.numberOfDaysInfo);
        typeTextView = (TextView) findViewById(R.id.typeInfo);
        setDateTextView = (TextView) findViewById(R.id.setDateInfo);
        remindersTextView = (TextView) findViewById(R.id.remindersTextView);
        dayBeforeReminderTextView = (TextView) findViewById(R.id.dayBeforeReminderTextView);
        onDayReminderTextView = (TextView) findViewById(R.id.onDayReminderTextView);
        daysText = (TextView) findViewById(R.id.daysTextView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        position = extras.getLong("EventPosition");

        setWindowInformation(position);
    }

    @Override
    protected void onResume() {
        super.onResume();

        remindersTextView.setVisibility(View.GONE);
        dayBeforeReminderTextView.setVisibility(View.GONE);
        onDayReminderTextView.setVisibility(View.GONE);

        setWindowInformation(position);
    }

    public void setWindowInformation(long p) {

        long resultingDays = 0;

        DaysDatabase db = new DaysDatabase(this);
        db.open();
        ArrayList<String> data = new ArrayList<String>();
        data = db.getRow(position);
        db.close();

        // extracting information from db to Strings

        String type = data.get(0);
        String fromText = data.get(1);
        String untilText = data.get(2);
        String title = data.get(3);
        String description = data.get(4);
        String isDayBefore = data.get(7);
        String isOnDay = data.get(8);

        // preparing the Dates for calculations

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        Calendar current, fromCal, untilCal;
        Date fromDate = null, untilDate = null;

        try {
            fromDate = sdf.parse(fromText);
            untilDate = sdf.parse(untilText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        current = Calendar.getInstance(TimeZone.getDefault());
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        fromCal = Calendar.getInstance(TimeZone.getDefault());
        untilCal = Calendar.getInstance(TimeZone.getDefault());

        fromCal.setTime(fromDate);
        untilCal.setTime(untilDate);

        // calculating the difference in days and setting event SetDateTextView

        if ("since".equals(type)) {
            resultingDays = (current.getTime().getTime() - fromCal.getTime().getTime()) / (1000 * 60 * 60 * 24);
            setDateTextView.setText(df.format(fromDate));
        }
        else if ("until".equals(type)) {
            resultingDays = (untilCal.getTime().getTime() - current.getTime().getTime()) / (1000 * 60 * 60 * 24);
            setDateTextView.setText(df.format(untilDate));
        }

        // setting the information to TextViews

        typeTextView.setText(type + " :");
        titleInfoTextView.setText(title);
        descriptionInfoTextView.setText(description);
        numberOfDaysTextView.setText(resultingDays + "");

        if (resultingDays == 1){
            daysText.setText("day");
        }

        // checking the reminders and setting them

        if ("1".equals(isDayBefore) || "1".equals(isOnDay)) {

            remindersTextView.setVisibility(View.VISIBLE);

            if ("1".equals(isDayBefore))
                dayBeforeReminderTextView.setVisibility(View.VISIBLE);

            if ("1".equals(isOnDay))
                onDayReminderTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_event) {
            Intent i = new Intent(this, EditSavedEventActivity.class);
            i.putExtra("EventPosition", position);
            startActivity(i);
            return true;
        }
        else if (id == R.id.delete_event) {

            final DaysDatabase db = new DaysDatabase(this);
            db.open();
            ArrayList<String> row = db.getRow(position);
            db.close();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);

            builder.setTitle(row.get(3));
            builder.setMessage("Are you sure that you want to delete this event?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.open();
                    db.deleteEvent(position);
                    db.close();
                    dialog.dismiss();

                    long RowId = position;
                    int requestRowId = (int) RowId;

                    Intent cancelIntent1 = new Intent(EventInformationActivity.this, OnDayAlarmBroadcastReceiver.class);
                    cancelIntent1.putExtra("itemPosition", RowId);

                    PendingIntent pendingIntent1 = PendingIntent.getBroadcast(EventInformationActivity.this, requestRowId, cancelIntent1, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am1.cancel(pendingIntent1);


                    Intent cancelIntent2 = new Intent(EventInformationActivity.this, DayBeforeAlarmBroadcastReceiver.class);
                    cancelIntent2.putExtra("itemPosition", RowId);

                    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(EventInformationActivity.this, requestRowId, cancelIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am2.cancel(pendingIntent2);

                    finish();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
