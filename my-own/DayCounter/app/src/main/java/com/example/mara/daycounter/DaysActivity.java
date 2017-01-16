package com.example.mara.daycounter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class DaysActivity extends AppCompatActivity {

    private Button mDayButton;
    private EditText mFromDate, mToDate;
    private TextView numberOfDays;

    protected Calendar calendar, date1, date2;

    private int year1, month1, day1;
    private int year2, month2, day2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);

        calendar = Calendar.getInstance();

        mFromDate = (EditText) findViewById(R.id.fromDate);
        mToDate = (EditText) findViewById(R.id.toDate);
        numberOfDays = (TextView) findViewById(R.id.numberOfDaysTextView);
        mDayButton = (Button) findViewById(R.id.calculateDaysButton);

        /////////////////////////////////////////////////////////////////
        ///////////////// Here it begins ////////////////////////////////
        /////////////////////////////////////////////////////////////////

        mFromDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFromDate.setText("");
                SetDateToDefault(1);
                showDialog(1);
                return false;
            }
        });

        mToDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mToDate.setText("");
                SetDateToDefault(2);
                showDialog(2);
                return false;
            }
        });

        mDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateNumberOfDays();
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////

    private void SetDateToDefault(int ID) {

        if(ID == 1) {
            year1 = calendar.get(Calendar.YEAR);
            month1 = calendar.get(Calendar.MONTH);
            day1 = calendar.get(Calendar.DAY_OF_MONTH);
        }
        else if (ID == 2) {
            year2 = calendar.get(Calendar.YEAR);
            month2 = calendar.get(Calendar.MONTH);
            day2 = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    @Override
    public Dialog onCreateDialog(int id){
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

            date1 = Calendar.getInstance();

            date1.set(Calendar.YEAR, year);
            date1.set(Calendar.MONTH, monthOfYear);
            date1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mFromDate.setText(day1 + "/" + month1 + "/" + year1);
        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListener2
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year2 = year;
            month2 = monthOfYear + 1;
            day2 = dayOfMonth;

            date2 = Calendar.getInstance();

            date2.set(Calendar.YEAR, year);
            date2.set(Calendar.MONTH, monthOfYear);
            date2.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mToDate.setText(day2 + "/" + month2 + "/" + year2);
        }
    };

    private void calculateNumberOfDays() {

        numberOfDays.setText("" + daysBetween(date1.getTime(), date2.getTime()));

        Toast.makeText(this, day1 + "/" + month1 + "/" + year1, Toast.LENGTH_LONG).show();
    }

    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24) );
    }

}
