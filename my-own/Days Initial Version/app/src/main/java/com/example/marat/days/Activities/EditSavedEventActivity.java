package com.example.marat.days.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.marat.days.DB.DaysDatabase;
import com.example.marat.days.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EditSavedEventActivity extends AppCompatActivity {

    CheckBox fromCheckBox, untilCheckBox;
    EditText fromEditText, untilEditText, eventTitleEditText, eventDescriptionEditText;
    Button updateEventButton;

    String type, title, fromDate, untilDate, description;

    Calendar currentTime, calendarFrom, calendarUntil;
    Date dateFrom, dateUntil;

    int currentDay, currentMonth,currentYear;
    private int year1, month1, day1;
    private int year2, month2, day2;

    Boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final long position = extras.getLong("EventPosition");

        fromCheckBox = (CheckBox) findViewById(R.id.fromCheckBoxUpdate);
        untilCheckBox = (CheckBox) findViewById(R.id.untilCheckBoxUpdate);
        fromEditText = (EditText) findViewById(R.id.fromDateUpdate);
        untilEditText = (EditText) findViewById(R.id.toDateUpdate);
        eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditTextUpdate);
        eventDescriptionEditText = (EditText) findViewById(R.id.eventDescriptionEditTextUpdate);
        updateEventButton = (Button) findViewById(R.id.updateEventButton);

        findViewById(R.id.updateEventLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                eventTitleEditText.clearFocus();
                return false;
            }
        });

        currentTime = Calendar.getInstance(TimeZone.getDefault());
        setInitial(position);

        fromEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!fromCheckBox.isChecked()) {
                    fromEditText.setText("");
                    showDialog(1);
                }
                return false;
            }
        });

        untilEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!untilCheckBox.isChecked()) {
                    untilEditText.setText("");
                    showDialog(2);
                }
                return false;
            }
        });

        updateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isChanged) {
                    if ("since".equals(type)){
                        month1++;
                    }
                    else if ("until".equals(type)){
                        month2++;
                    }
                }

                if (fromCheckBox.isChecked())
                    fromDate = currentYear + "-" + currentMonth + "-" + currentDay;
                else
                    fromDate = year1 + "-" + month1 + "-" + day1;

                if (untilCheckBox.isChecked())
                    untilDate = currentYear + "-" + currentMonth + "-" + currentDay;
                else
                    untilDate = year2 + "-" + month2 + "-" + day2;

                title = eventTitleEditText.getText().toString();
                description = eventDescriptionEditText.getText().toString();

                if ( (type == null || "".equals(type)) || (title == null || "".equals(title)) || (fromDate == null || "".equals(fromDate)) || (untilDate == null || "".equals(untilDate)) ) {
                    //Toast.makeText(EditSavedEventActivity.this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                    Snackbar.make(v, "Please Fill All the Fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    DaysDatabase entry = new DaysDatabase(EditSavedEventActivity.this);
                    entry.open();
                    entry.updateEntry(position, type, title, fromDate, untilDate, description);
                    entry.close();
                    finish();
                }
            }
        });
    }

    public void setInitial(Long l) {
        DaysDatabase db = new DaysDatabase(this);
        db.open();
        String[] data = db.getRow(l);
        db.close();

        eventTitleEditText.setText(data[3]);
        eventDescriptionEditText.setText(data[4]);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        calendarFrom = Calendar.getInstance(TimeZone.getDefault());
        calendarUntil = Calendar.getInstance(TimeZone.getDefault());

        if("since".equals(data[0])) {
            type = "since";
            fromEditText.setText(data[1]);
            setEditTextToToday(untilEditText);
            untilCheckBox.setChecked(true);
            fromCheckBox.setEnabled(false);
            try {
                dateFrom = format.parse(data[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendarFrom.setTime(dateFrom);
        }
        else if ("until".equals(data[0])) {
            type = "until";
            untilEditText.setText(data[2]);
            setEditTextToToday(fromEditText);
            fromCheckBox.setChecked(true);
            untilCheckBox.setEnabled(false);
            try {
                dateUntil = format.parse(data[2]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendarUntil.setTime(dateUntil);
        }

        year1 = calendarFrom.get(Calendar.YEAR);
        month1 = calendarFrom.get(Calendar.MONTH);
        day1 = calendarFrom.get(Calendar.DAY_OF_MONTH);

        year2 = calendarUntil.get(Calendar.YEAR);
        month2 = calendarUntil.get(Calendar.MONTH);
        day2 = calendarUntil.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Dialog onCreateDialog(int id) {
        isChanged = true;
        if (id == 1)
            return new DatePickerDialog(this, datePickerListener1, year1, month1, day1);
        else if (id == 2)
            return new DatePickerDialog(this, datePickerListener2, year2, month2, day2);
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener1
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year1 = year;
            month1 = monthOfYear + 1;
            day1 = dayOfMonth;

            fromEditText.setText(day1 + "/" + month1 + "/" + year1);
        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListener2
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year2 = year;
            month2 = monthOfYear + 1;
            day2 = dayOfMonth;

            untilEditText.setText(day2 + "/" + month2 + "/" + year2);
        }
    };

    public void onCheckBoxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.fromCheckBoxUpdate:
                if (checked) {
                    untilCheckBox.setEnabled(false);
                    setEditTextToToday(fromEditText);
                    type = "until";
                } else {
                    untilCheckBox.setEnabled(true);
                    setEditTextToDefault(fromEditText);
                }
                break;
            case R.id.untilCheckBoxUpdate:
                if (checked) {
                    fromCheckBox.setEnabled(false);
                    setEditTextToToday(untilEditText);
                    type = "since";
                } else {
                    fromCheckBox.setEnabled(true);
                    setEditTextToDefault(untilEditText);
                }
                break;
        }
    }

    public void setEditTextToToday(EditText editText) {
        currentDay = currentTime.get(Calendar.DAY_OF_MONTH);
        currentMonth = currentTime.get(Calendar.MONTH) + 1;
        currentYear = currentTime.get(Calendar.YEAR);

        editText.setText(currentDay + "/" + currentMonth + "/" + currentYear);
    }

    public void setEditTextToDefault(EditText editText) {
        editText.setText("");
    }
}