package com.marat.apps.android.pro3.Dialogs;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.DialogCarTypePickerAdapter;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Interfaces.DialogCarTypeChosenListener;
import com.marat.apps.android.pro3.Models.CarType;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class CarTypePickerDialog extends DialogFragment {

    private ArrayList<CarType> carTypes;
    private int chosenCarType;
    private DialogCarTypeChosenListener delegate;

    RelativeLayout titleLayout, buttonsLayout;

    public static CarTypePickerDialog newInstance(int chosenCarType) {
        CarTypePickerDialog dialog = new CarTypePickerDialog();
        Bundle args = new Bundle();
        args.putInt("chosen_car_type", chosenCarType);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_car_type_picker, container, false);

        final ListView listView = (ListView) v.findViewById(R.id.dialogCarTypePickerListView);
        TextView okButton = (TextView) v.findViewById(R.id.dialogCarTypePositiveButton);
        TextView cancelButton = (TextView) v.findViewById(R.id.dialogCarTypeNegativeButton);
        titleLayout = (RelativeLayout) v.findViewById(R.id.dialogCarTypePickerTitleLayout);
        buttonsLayout = (RelativeLayout) v.findViewById(R.id.dialogCarTypePickerButtonLayout);

        chosenCarType = getArguments().getInt("chosen_car_type");

        carTypes = new ArrayList<>();

        CarWashesDatabase db = new CarWashesDatabase(getActivity());
        db.open();
        Cursor cursor = db.getAllCarTypes();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            CarType carType = new CarType();
            carType.setRowID(cursor.getLong(cursor.getColumnIndex(CarWashesDatabase.ROW_ID)));
            carType.setCarTypeID(cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_TYPE_ID)));
            carType.setCarTypeName(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_TYPE_NAME)));
            carTypes.add(carType);
        }

        db.close();

        final DialogCarTypePickerAdapter adapter = new DialogCarTypePickerAdapter(getActivity(), carTypes);
        adapter.setChosenCarType(chosenCarType);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenCarType = position;
                adapter.setChosenCarType(position);
                Log.v("Dialog", "" + position);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount != 0) {
                    if ((firstVisibleItem == 0) && (listView.getChildAt(0).getTop() == 0)) {
                        titleLayout.setBackgroundResource(0);
                    } else {
                        titleLayout.setBackgroundResource(R.drawable.bg_city_and_car_picker_title);
                    }

                    if ( ((firstVisibleItem + visibleItemCount) >= totalItemCount) && (listView.getChildAt(listView.getChildCount()-1).getBottom() == listView.getHeight()) ) {
                        buttonsLayout.setBackgroundResource(0);
                    } else {
                        buttonsLayout.setBackgroundResource(R.drawable.bg_city_and_car_picker_buttons);
                    }
                }
                Log.d("Dialog", "first - " + firstVisibleItem + " visible - " + visibleItemCount + " total - " + totalItemCount);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.carTypeChosen(carTypes.get(chosenCarType), chosenCarType);
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        delegate = (DialogCarTypeChosenListener) context;
    }
}
