package developer.marat.apps.days.Tabs;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import developer.marat.apps.days.Dialogs.myDatePickerDialogFragment;
import developer.marat.apps.days.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DaysTabView extends Fragment {

    private Button mDayButton;
    private EditText mFromDate, mToDate;
    private TextView numberOfDays;
    private CheckBox endDayCheckBox;

    private Calendar date1, date2;
    private DateFormat df;

    private int year1, month1, day1;
    private int year2, month2, day2;

    @Override
    public void onResume() {
        super.onResume();
        mFromDate.setText("");
        mToDate.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.days_tab_view, container, false);

        mFromDate = (EditText) v.findViewById(R.id.fromDate);
        mToDate = (EditText) v.findViewById(R.id.toDate);
        numberOfDays = (TextView) v.findViewById(R.id.numberOfDaysTextView);
        mDayButton = (Button) v.findViewById(R.id.calculateDaysButton);
        endDayCheckBox = (CheckBox) v.findViewById(R.id.plusOneDayCheckBox) ;

        /////////////////////////////////////////////////////////////////
        ///////////////// Here it begins ////////////////////////////////
        /////////////////////////////////////////////////////////////////

        SetDateToToday(1);
        SetDateToToday(2);
        df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);

        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFromDate.setText("");
                numberOfDays.setText("0");
                Bundle args = new Bundle();
                args.putInt("day", day1);
                args.putInt("month", month1);
                args.putInt("year", year1);
                myDatePickerDialogFragment d1 = new myDatePickerDialogFragment();
                d1.setArguments(args);
                d1.setOnDateSetListener(datePickerListener1);
                d1.show(getFragmentManager(), "days");
            }
        });


        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToDate.setText("");
                numberOfDays.setText("0");
                myDatePickerDialogFragment d2 = new myDatePickerDialogFragment();
                Bundle args = new Bundle();
                args.putInt("day", day2);
                args.putInt("month", month2);
                args.putInt("year", year2);
                d2.setArguments(args);
                d2.setOnDateSetListener(datePickerListener2);
                d2.show(getFragmentManager(), "days");
            }
        });

        mDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = mFromDate.getText().toString();
                String text2 = mToDate.getText().toString();
                if( (text1.equals("") || text1.isEmpty()) || (text2.equals("") || text2.isEmpty())){
                    Toast.makeText(getActivity(), "Please enter all dates!", Toast.LENGTH_SHORT).show();
                }
                else {
                    calculateNumberOfDays();
                }
            }
        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        endDayCheckBox.setChecked(false);
    }

    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////

    private void SetDateToToday(int ID) {
        if(ID == 1) {
            year1 = Calendar.getInstance().get(Calendar.YEAR);
            month1 = Calendar.getInstance().get(Calendar.MONTH);
            day1 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        else if (ID == 2) {
            year2 = Calendar.getInstance().get(Calendar.YEAR);
            month2 = Calendar.getInstance().get(Calendar.MONTH);
            day2 = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener1
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year1 = year;
            month1 = monthOfYear;
            day1 = dayOfMonth;

            date1 = Calendar.getInstance();

            date1.set(Calendar.YEAR, year);
            date1.set(Calendar.MONTH, monthOfYear);
            date1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date1.set(Calendar.HOUR_OF_DAY, 0);
            date1.set(Calendar.MINUTE, 0);
            date1.set(Calendar.SECOND, 0);
            date1.set(Calendar.MILLISECOND, 0);

            mFromDate.setText(df.format(date1.getTime()));
        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListener2
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year2 = year;
            month2 = monthOfYear;
            day2 = dayOfMonth;

            date2 = Calendar.getInstance();

            date2.set(Calendar.YEAR, year);
            date2.set(Calendar.MONTH, monthOfYear);
            date2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date2.set(Calendar.HOUR_OF_DAY, 0);
            date2.set(Calendar.MINUTE, 0);
            date2.set(Calendar.SECOND, 0);
            date2.set(Calendar.MILLISECOND, 0);

            mToDate.setText(df.format(date2.getTime()));
        }
    };

    private void calculateNumberOfDays() {
        numberOfDays.setText("" + daysBetween(date1.getTime(), date2.getTime()));
    }

    private int daysBetween(Date d1, Date d2){
        if (endDayCheckBox.isChecked())
            return (int)(  ( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24) ) + 1  );
        else
            return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24) );
    }
}