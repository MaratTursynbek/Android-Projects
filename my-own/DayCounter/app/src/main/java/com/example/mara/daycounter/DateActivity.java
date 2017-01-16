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

public class DateActivity extends AppCompatActivity {

    private Button mDateButton;
    private EditText mFromDate, mAfterDays;
    private TextView mResultingDate;

    protected Calendar calendar, date1, date2;

    private int year_1, month_1, day_1;
    private int year_2, month_2, day_2;
    private long afterSomeDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        mFromDate = (EditText) findViewById(R.id.fromDate2);
        mAfterDays = (EditText) findViewById(R.id.afterDays);
        mResultingDate = (TextView) findViewById(R.id.resultingDateTextView);
        mDateButton = (Button) findViewById(R.id.calculateDateButton);

        calendar = Calendar.getInstance();

        mFromDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFromDate.setText("");
                SetDateToDefault();
                showDialog(1);
                return false;
            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTheFinalDate();
            }
        });
    }

    private void SetDateToDefault() {
        year_1 = calendar.get(Calendar.YEAR);
        month_1 = calendar.get(Calendar.MONTH);
        day_1 = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Dialog onCreateDialog(int id){
        if (id == 1)
            return new DatePickerDialog(this, datePickerListener3, year_1, month_1, day_1);
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener3
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_1 = year;
            month_1 = monthOfYear + 1;
            day_1 = dayOfMonth;

            date1 = Calendar.getInstance();

            date1.set(Calendar.YEAR, year);
            date1.set(Calendar.MONTH, monthOfYear);
            date1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mFromDate.setText(day_1 + "/" + month_1 + "/" + year_1);
        }
    };

    private void calculateTheFinalDate() {

        afterSomeDays = Integer.parseInt(mAfterDays.getText().toString());

        long dif = afterSomeDays*24*60*60*1000;
        long date11 = (date1.getTime().getTime());
        long sum = dif + date11;

        //Toast.makeText(this, dif + " | " + date11 + " | " + sum, Toast.LENGTH_LONG).show();

        date2 = Calendar.getInstance();

        date2.setTimeInMillis(sum);

        year_2 = date2.get(Calendar.YEAR);
        month_2 = date2.get(Calendar.MONTH) + 1;
        day_2 = date2.get(Calendar.DAY_OF_MONTH);

        mResultingDate.setText(day_2 + "/" + month_2 + "/" + year_2);
    }
}
