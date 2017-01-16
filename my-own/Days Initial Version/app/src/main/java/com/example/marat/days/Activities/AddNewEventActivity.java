package com.example.marat.days.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

import java.util.Calendar;
import java.util.TimeZone;

public class AddNewEventActivity extends AppCompatActivity {

    CheckBox fromCheckBox, untilCheckBox;
    EditText fromEditText, untilEditText, eventTitleEditText, eventDescriptionEditText;
    Button saveEventButton;

    String type, title, fromDate, untilDate, description;

    protected Calendar current;

    private int currentYear, currentMonth, currentDay;
    private int year1, month1, day1;
    private int year2, month2, day2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fromCheckBox = (CheckBox) findViewById(R.id.fromCheckBox);
        untilCheckBox = (CheckBox) findViewById(R.id.untilCheckBox);
        fromEditText = (EditText) findViewById(R.id.fromDate);
        untilEditText = (EditText) findViewById(R.id.toDate);
        saveEventButton = (Button) findViewById(R.id.saveEventButton);
        eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);
        eventDescriptionEditText = (EditText) findViewById(R.id.eventDescriptionEditText);

        current = Calendar.getInstance(TimeZone.getDefault());

        findViewById(R.id.addEventLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                eventTitleEditText.clearFocus();
                return false;
            }
        });

        setDateToDefault();

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

        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    //Toast.makeText(AddNewEventActivity.this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                    Snackbar.make(v, "Please Fill All the Fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    DaysDatabase entry = new DaysDatabase(AddNewEventActivity.this);
                    entry.open();
                    entry.addData(type, title, fromDate, untilDate, description);
                    entry.close();
                    finish();
                }
            }
        });
    }

    private void setDateToDefault() {

        year1 = current.get(Calendar.YEAR);
        month1 = current.get(Calendar.MONTH);
        day1 = current.get(Calendar.DAY_OF_MONTH);

        year2 = current.get(Calendar.YEAR);
        month2 = current.get(Calendar.MONTH);
        day2 = current.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Dialog onCreateDialog(int id) {
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
            case R.id.fromCheckBox:
                if (checked) {
                    untilCheckBox.setEnabled(false);
                    setEditTextToToday(fromEditText);
                    type = "until";
                } else {
                    untilCheckBox.setEnabled(true);
                    setEditTextToDefault(fromEditText);
                }
                break;
            case R.id.untilCheckBox:
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
        currentDay = current.get(Calendar.DAY_OF_MONTH);
        currentMonth = current.get(Calendar.MONTH) + 1;
        currentYear = current.get(Calendar.YEAR);

        editText.setText(currentDay + "/" + currentMonth + "/" + currentYear);
    }

    public void setEditTextToDefault(EditText editText) {
        editText.setText("");
    }
}