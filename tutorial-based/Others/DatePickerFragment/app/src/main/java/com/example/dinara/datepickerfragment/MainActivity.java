package com.example.dinara.datepickerfragment;

import android.app.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DatePickerDialogFragment.Communicator{

    TextView text;
    int year1, month1, day1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.textView);
    }


    public void showDialog(View v){
        FragmentManager manager = getFragmentManager();
        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.show(manager, "DatePickerDialog");
    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDatePicked(DatePicker date) {
        year1 = date.getYear();
        month1 = date.getMonth()+1;
        day1 = date.getDayOfMonth();

        text.setText(day1 + " / " + month1 + " / " + year1);
    }
}
