package com.example.marat.days;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class myDatePickerDialogFragment extends DialogFragment{

    int mDay, mMonth, mYear;
    OnDateSetListener onSetDate;

    public myDatePickerDialogFragment(){
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mDay = args.getInt("day");
        mMonth = args.getInt("month");
        mYear = args.getInt("year");
    }

    public void setOnDateSetListener(OnDateSetListener setDate){
        onSetDate = setDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(), onSetDate, mYear, mMonth, mDay);
    }
}