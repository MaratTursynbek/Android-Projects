package developer.marat.apps.days.Dialogs;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class myDatePickerDialogFragment extends DialogFragment{

    int mDay, mMonth, mYear;
    long current;
    OnDateSetListener onSetDate;
    String type;

    public myDatePickerDialogFragment(){
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mDay = args.getInt("day");
        mMonth = args.getInt("month");
        mYear = args.getInt("year");
        type = args.getString("type");
        current = args.getLong("current");
    }

    public void setOnDateSetListener(OnDateSetListener setDate){
        onSetDate = setDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), onSetDate, mYear, mMonth, mDay);

        if ("since".equals(type)) {
            datePickerDialog.getDatePicker().setMaxDate(current);
        }
        else if ("until".equals(type)) {
            datePickerDialog.getDatePicker().setMinDate(current);
        }

        return datePickerDialog;
    }
}