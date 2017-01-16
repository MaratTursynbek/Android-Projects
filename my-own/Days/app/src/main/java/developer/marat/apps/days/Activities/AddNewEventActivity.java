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

import developer.marat.apps.days.Alarms.DayBeforeAlarmBroadcastReceiver;
import developer.marat.apps.days.Alarms.OnDayAlarmBroadcastReceiver;
import developer.marat.apps.days.DB.DaysDatabase;
import developer.marat.apps.days.R;
import developer.marat.apps.days.Dialogs.myDatePickerDialogFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddNewEventActivity extends AppCompatActivity {

    EditText eventDateEditText, eventTitleEditText, eventDescriptionEditText;
    RadioGroup radioGroup;
    RadioButton sinceRadioButton, untilRadioButton;
    CheckBox dayBeforeCheckBox, onDayCheckBox;

    String type, title, fromDate, untilDate, description;

    private int currentYear, currentMonth, currentDay;
    private int mYear, mMonth, mDay;
    private boolean isDayBefore, isOnDay;

    protected Calendar current, setDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);
        eventDescriptionEditText = (EditText) findViewById(R.id.eventDescriptionEditText);
        eventDateEditText = (EditText) findViewById(R.id.eventDateEditText);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupEventType);
        sinceRadioButton = (RadioButton) findViewById(R.id.radioButtonSince);
        untilRadioButton = (RadioButton) findViewById(R.id.radioButtonUntil);
        dayBeforeCheckBox = (CheckBox) findViewById(R.id.dayBeforeCheckBox);
        onDayCheckBox = (CheckBox) findViewById(R.id.onDayCheckBox);

        dayBeforeCheckBox.setEnabled(false);
        onDayCheckBox.setEnabled(false);
        sinceRadioButton.setChecked(true);

        current = Calendar.getInstance(TimeZone.getDefault());
        setDateToDefault();

        findViewById(R.id.addEventLayout).setOnTouchListener(new View.OnTouchListener() {
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
                } else {
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
                if (radioId == R.id.radioButtonSince) {
                    args.putString("type", "since");
                }
                else if (radioId == R.id.radioButtonUntil) {
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
                if (v.getId() == R.id.eventDescriptionEditText) {
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


    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

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

    private void setDateToDefault() {
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH);
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        currentDay = current.get(Calendar.DAY_OF_MONTH);
        currentMonth = current.get(Calendar.MONTH);
        currentYear = current.get(Calendar.YEAR);
    }


    /*
     *
     *          Below methods are after save button is clicked
     *
     */


    public void prepareEventData() {
        int radioId = radioGroup.getCheckedRadioButtonId();
        mMonth++;
        currentMonth++;

        if (radioId == R.id.radioButtonSince) {
            type = "since";
            fromDate = mYear + "-" + mMonth + "-" + mDay;
            untilDate = currentYear + "-" + currentMonth + "-" + currentDay;
        } else if (radioId == R.id.radioButtonUntil) {
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
        } else {
            isDayBefore = false;
        }
        if (onDayCheckBox.isChecked()) {
            isOnDay = true;
        } else {
            isOnDay = false;
        }
    }

    public void addReminderAsNotification() {
        DaysDatabase data = new DaysDatabase(this);
        data.open();
        long RowId = data.getLastRowId();
        data.close();
        int requestRowId = (int) RowId;

        setDate.set(Calendar.HOUR_OF_DAY, 0);
        setDate.set(Calendar.MINUTE, 0);
        setDate.set(Calendar.SECOND, 0);
        setDate.set(Calendar.MILLISECOND, 0);

        if (isOnDay) {

            Intent intent2 = new Intent(this, OnDayAlarmBroadcastReceiver.class);
            intent2.putExtra("itemPosition", RowId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestRowId, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
        }

        if (isDayBefore) {

            Intent intent1 = new Intent(this, DayBeforeAlarmBroadcastReceiver.class);
            intent1.putExtra("itemPosition", RowId);

            setDate.add(Calendar.DATE, -1);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestRowId, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.save_new) {

            String temp1 = eventTitleEditText.getText().toString();
            String temp2 = eventDateEditText.getText().toString();

            if ((temp1 == null || "".equals(temp1)) || (temp2 == null || "".equals(temp2))) {
                //Toast.makeText(AddNewEventActivity.this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                Snackbar.make(this.findViewById(R.id.addEventLayout), "Please Fill All the Fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {

                prepareEventData();
                checkReminders();

                DaysDatabase entry = new DaysDatabase(AddNewEventActivity.this);
                entry.open();

                ArrayList<String> orders = entry.getLastOrders();
                int all = Integer.parseInt(orders.get(0));
                int since = Integer.parseInt(orders.get(1));

                all++;
                if (type.equals("since")) {
                    since++;
                    entry.addData(type, title, fromDate, untilDate, description, all, since, isDayBefore, isOnDay);
                } else if (type.equals("until")) {
                    entry.addData(type, title, fromDate, untilDate, description, all, -1, isDayBefore, isOnDay);
                }

                entry.close();

                addReminderAsNotification();

                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}