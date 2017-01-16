package developer.marat.apps.days.Activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import developer.marat.apps.days.R;

import developer.marat.apps.days.Alarms.DayBeforeAlarmBroadcastReceiver;
import developer.marat.apps.days.Alarms.OnDayAlarmBroadcastReceiver;
import developer.marat.apps.days.DB.DaysDatabase;
import developer.marat.apps.days.Dialogs.myDatePickerDialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EditSavedEventActivity extends AppCompatActivity {

    EditText eventDateEditText, eventTitleEditText, eventDescriptionEditText;
    RadioGroup radioGroup;
    RadioButton sinceRadioButton, untilRadioButton;
    CheckBox dayBeforeCheckBox, onDayCheckBox;

    String type, title, fromDate, untilDate, description;
    ArrayList<String> data;

    private int currentYear, currentMonth, currentDay;
    private int mYear, mMonth, mDay;
    private boolean isDayBefore, isOnDay;

    protected Calendar current, setDate;
    Date eventDate;

    int all, since;
    long position;
    boolean isTypeChanged = false, isDateChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        position = extras.getLong("EventPosition");

        eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditTextUpdate);
        eventDescriptionEditText = (EditText) findViewById(R.id.eventDescriptionEditTextUpdate);
        eventDateEditText = (EditText) findViewById(R.id.eventDateEditTextUpdate);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupEventTypeUpdate);
        sinceRadioButton = (RadioButton) findViewById(R.id.radioButtonSinceUpdate);
        untilRadioButton = (RadioButton) findViewById(R.id.radioButtonUntilUpdate);
        dayBeforeCheckBox = (CheckBox) findViewById(R.id.dayBeforeCheckBoxUpdate);
        onDayCheckBox = (CheckBox) findViewById(R.id.onDayCheckBoxUpdate);

        current = Calendar.getInstance(TimeZone.getDefault());
        setDate = Calendar.getInstance(TimeZone.getDefault());
        setInitial(position);

        findViewById(R.id.updateEventLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                eventTitleEditText.clearFocus();
                return false;
            }
        });

        sinceRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dayBeforeCheckBox.setChecked(false);
                onDayCheckBox.setChecked(false);
                eventDateEditText.setText("");
            }
        });

        untilRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dayBeforeCheckBox.setEnabled(true);
                    onDayCheckBox.setEnabled(true);
                }
                else {
                    dayBeforeCheckBox.setEnabled(false);
                    onDayCheckBox.setEnabled(false);
                }
                eventDateEditText.setText("");
            }
        });

        eventDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDateEditText.setText("");
                Bundle args = new Bundle();
                args.putInt("day", mDay);
                args.putInt("month", mMonth);
                args.putInt("year", mYear);
                args.putLong("current", current.getTimeInMillis());

                int radioId = radioGroup.getCheckedRadioButtonId();
                if (radioId == R.id.radioButtonSinceUpdate) {
                    args.putString("type", "since");
                }
                else if (radioId == R.id.radioButtonUntilUpdate) {
                    args.putString("type", "until");
                }
                else {
                    args.putString("type", "null");
                }
                myDatePickerDialogFragment d3 = new myDatePickerDialogFragment();
                d3.setArguments(args);
                d3.setOnDateSetListener(datePickerListener);
                d3.show(getSupportFragmentManager(), "date");
            }
        });

        eventDescriptionEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.eventDescriptionEditTextUpdate) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }


    /*
     *
     *          methods called from onCreate
     *
     */


    public void setInitial(Long l) {
        DaysDatabase db = new DaysDatabase(this);
        db.open();
        data = db.getRow(l);
        db.close();

        eventTitleEditText.setText(data.get(3));
        eventDescriptionEditText.setText(data.get(4));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);

        if("since".equals(data.get(0))) {
            type = "since";
            sinceRadioButton.setChecked(true);
            try {
                eventDate = format.parse(data.get(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            setDate.setTime(eventDate);
            eventDateEditText.setText(df.format(eventDate));

            dayBeforeCheckBox.setEnabled(false);
            onDayCheckBox.setEnabled(false);
        }
        else if ("until".equals(data.get(0))) {
            type = "until";
            untilRadioButton.setChecked(true);
            try {
                eventDate = format.parse(data.get(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setDate.setTime(eventDate);

            if ("1".equals(data.get(7))) {
                isDayBefore = true;
            }
            else {
                isDayBefore = false;
            }

            if ("1".equals(data.get(8))) {
                isOnDay = true;
            }
            else {
                isOnDay = false;
            }
            dayBeforeCheckBox.setChecked(isDayBefore);
            onDayCheckBox.setChecked(isOnDay);
            eventDateEditText.setText(df.format(eventDate));
        }

        mYear = setDate.get(Calendar.YEAR);
        mMonth = setDate.get(Calendar.MONTH);
        mDay = setDate.get(Calendar.DAY_OF_MONTH);

        currentDay = current.get(Calendar.DAY_OF_MONTH);
        currentMonth = current.get(Calendar.MONTH);
        currentYear = current.get(Calendar.YEAR);
    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            isDateChanged = true;

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            setDate = Calendar.getInstance();
            setDate.set(Calendar.YEAR, year);
            setDate.set(Calendar.MONTH, monthOfYear);
            setDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);

            eventDateEditText.setText(df.format(setDate.getTime()));
        }
    };


    /*
     *
     *          Below methods are after save button is clicked
     *
     */


    public void prepareEventData() {

        int radioId = radioGroup.getCheckedRadioButtonId();
        mMonth++;
        currentMonth++;

        if (radioId == R.id.radioButtonSinceUpdate) {

            if ("until".equals(type)) {
                isTypeChanged = true;
            }

            type = "since";
            fromDate = mYear + "-" + mMonth + "-" + mDay;
            untilDate = currentYear + "-" + currentMonth + "-" + currentDay;
        }
        else if (radioId == R.id.radioButtonUntilUpdate){

            if ("since".equals(type)) {
                isTypeChanged = true;
            }

            type = "until";
            untilDate = mYear + "-" + mMonth + "-" + mDay;
            fromDate = currentYear + "-" + currentMonth + "-" + currentDay;
        }

        title = eventTitleEditText.getText().toString();
        description = eventDescriptionEditText.getText().toString();

        mMonth--;
        currentMonth--;
    }

    public void checkReminders() {
        if (dayBeforeCheckBox.isChecked()) {
            isDayBefore = true;
        }
        else {
            isDayBefore = false;
        }
        if (onDayCheckBox.isChecked()) {
            isOnDay = true;
        }
        else {
            isOnDay = false;
        }
    }

    public void prepareOrders() {

        if (isTypeChanged) {
            if ("since".equals(type)) {
                DaysDatabase db = new DaysDatabase(this);
                db.open();
                ArrayList<String> results = db.getLastOrders();
                db.close();

                since = Integer.parseInt(results.get(1));
                since++;
            }
            else if ("until".equals(type)) {
                since = -1;
            }
        }
        else {
            since = Integer.parseInt(data.get(6));
        }
        all = Integer.parseInt(data.get(5));
    }

    public void addReminderAsNotification() {

        long RowId = position;
        int requestRowId = (int) RowId;

        if ( (isDateChanged) || (isTypeChanged) ) {
            Intent cancelIntent1 = new Intent(this, OnDayAlarmBroadcastReceiver.class);
            cancelIntent1.putExtra("itemPosition", RowId);

            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, requestRowId, cancelIntent1, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            am1.cancel(pendingIntent1);


            Intent cancelIntent2 = new Intent(this, DayBeforeAlarmBroadcastReceiver.class);
            cancelIntent2.putExtra("itemPosition", RowId);

            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, requestRowId, cancelIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am2 = (AlarmManager) getSystemService(ALARM_SERVICE);
            am2.cancel(pendingIntent2);
        }
        else {
            if (!isDayBefore) {
                Intent cancelIntent2 = new Intent(this, DayBeforeAlarmBroadcastReceiver.class);
                cancelIntent2.putExtra("itemPosition", RowId);

                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, requestRowId, cancelIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                am2.cancel(pendingIntent2);
            }
            if (!isOnDay) {
                Intent cancelIntent1 = new Intent(this, OnDayAlarmBroadcastReceiver.class);
                cancelIntent1.putExtra("itemPosition", RowId);

                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, requestRowId, cancelIntent1, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                am1.cancel(pendingIntent1);
            }
        }

        setDate.set(Calendar.HOUR_OF_DAY, 0);
        setDate.set(Calendar.MINUTE, 0);
        setDate.set(Calendar.SECOND, 0);
        setDate.set(Calendar.MILLISECOND, 0);

        if (isOnDay) {

            Intent intent1 = new Intent(this, OnDayAlarmBroadcastReceiver.class);
            intent1.putExtra("itemPosition", RowId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestRowId, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
        }

        if (isDayBefore) {

            Intent intent2 = new Intent(this, DayBeforeAlarmBroadcastReceiver.class);
            intent2.putExtra("itemPosition", RowId);

            setDate.add(Calendar.DATE, -1);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestRowId, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_edited) {

            String temp1 = eventTitleEditText.getText().toString();
            String temp2 = eventDateEditText.getText().toString();

            if ((temp1 == null || "".equals(temp1)) || (temp2 == null || "".equals(temp2))) {
                //Toast.makeText(EditSavedEventActivity.this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                Snackbar.make(this.findViewById(R.id.updateEventLayout), "Please Fill All the Fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else {

                prepareEventData();
                checkReminders();

                prepareOrders();
                DaysDatabase entry = new DaysDatabase(EditSavedEventActivity.this);
                entry.open();
                entry.updateEntry(position, type, fromDate, untilDate, title, description, all, since, isDayBefore, isOnDay);
                entry.close();
                finish();
            }
            addReminderAsNotification();
        }

        return super.onOptionsItemSelected(item);
    }
}