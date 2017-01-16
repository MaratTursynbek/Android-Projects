package com.example.marat.days.Tabs;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marat.days.R;
import com.example.marat.days.myDatePickerDialogFragment;

import java.util.Calendar;

public class DateTabView extends Fragment {

    private Button mDateButton;
    private EditText mFromDate, mAfterDays;
    private TextView mResultingDate;

    protected Calendar date1, date2;

    private int year1, month1, day1;
    private int year2, month2, day2;
    private long afterSomeDays;

    @Override
    public void onResume() {
        super.onResume();
        mFromDate.setText("");
        mAfterDays.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.date_tab_view, container, false);

        mFromDate = (EditText) v.findViewById(R.id.fromDate2);
        mAfterDays = (EditText) v.findViewById(R.id.afterDays);
        mResultingDate = (TextView) v.findViewById(R.id.resultingDateTextView);
        mDateButton = (Button) v.findViewById(R.id.calculateDateButton);

        SetDateToDefault();

        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFromDate.setText("");
                Bundle args = new Bundle();
                args.putInt("day", day1);
                args.putInt("month", month1);
                args.putInt("year", year1);
                myDatePickerDialogFragment d3 = new myDatePickerDialogFragment();
                d3.setArguments(args);
                d3.setOnDateSetListener(datePickerListener3);
                d3.show(getFragmentManager(), "date");
            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = mFromDate.getText().toString();
                String text2 = mAfterDays.getText().toString();
                if( (text1.equals("") || text1.isEmpty()) || (text2.equals("") || text2.isEmpty())){
                    Toast.makeText(getActivity(), "Please enter all data!", Toast.LENGTH_SHORT).show();
                }
                else {
                    calculateTheFinalDate();
                }
            }
        });

        return v;
    }

    DatePickerDialog.OnDateSetListener datePickerListener3 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year1 = year;
            month1 = monthOfYear + 1;
            day1 = dayOfMonth;

            date1 = Calendar.getInstance();

            date1.set(Calendar.YEAR, year);
            date1.set(Calendar.MONTH, monthOfYear);
            date1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mFromDate.setText("" + day1 + "/" + month1 + "/" + year1);

            month1--;
        }
    };

    private void SetDateToDefault() {
        year1 = Calendar.getInstance().get(Calendar.YEAR);
        month1 = Calendar.getInstance().get(Calendar.MONTH);
        day1 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public void calculateTheFinalDate(){

        afterSomeDays = Integer.parseInt(mAfterDays.getText().toString());
        afterSomeDays = afterSomeDays*24*60*60*1000;
        long date_1 = (date1.getTime().getTime());
        long finalMillis = date_1 + afterSomeDays;

        date2 = Calendar.getInstance();
        date2.setTimeInMillis(finalMillis);

        year2 = date2.get(Calendar.YEAR);
        month2 = date2.get(Calendar.MONTH) + 1;
        day2 = date2.get(Calendar.DAY_OF_MONTH);

        mResultingDate.setText(day2 + "/" + month2 + "/" + year2);
    }
}