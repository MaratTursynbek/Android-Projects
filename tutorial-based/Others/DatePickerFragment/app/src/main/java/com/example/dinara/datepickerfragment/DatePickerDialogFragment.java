package com.example.dinara.datepickerfragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Dinara on 15.12.2015.
 */
public class DatePickerDialogFragment extends DialogFragment implements View.OnClickListener {

    Button ok, cancel;
    DatePicker datePicker;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_picker_fragment, null);
        ok = (Button) view.findViewById(R.id.ok);
        cancel = (Button) view.findViewById(R.id.cancel);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ok){
            communicator.onDatePicked(datePicker);
            dismiss();
        }
        else {
            communicator.onDialogMessage("Cancelled!");
            dismiss();
        }
    }

    interface Communicator {
        public void onDialogMessage(String message);
        public void onDatePicked(DatePicker datePicker);
    }
}
