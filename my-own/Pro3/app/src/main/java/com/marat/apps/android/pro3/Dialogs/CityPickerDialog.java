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

import com.marat.apps.android.pro3.Adapters.DialogCityPickerAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.CityChosenListener;
import com.marat.apps.android.pro3.Models.City;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class CityPickerDialog extends DialogFragment {

    private ArrayList<City> cities;
    private int chosenCity;
    private CityChosenListener delegate;

    RelativeLayout titleLayout, buttonsLayout;

    public static CityPickerDialog newInstance(int chosenCity) {
        CityPickerDialog dialog = new CityPickerDialog();
        Bundle args = new Bundle();
        args.putInt("chosen_city", chosenCity);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_city_picker, container, false);

        final ListView listView = (ListView) v.findViewById(R.id.dialogCityPickerListView);
        TextView okButton = (TextView) v.findViewById(R.id.dialogCityPositiveButton);
        TextView cancelButton = (TextView) v.findViewById(R.id.dialogCityNegativeButton);
        titleLayout = (RelativeLayout) v.findViewById(R.id.dialogCityPickerTitleLayout);
        buttonsLayout = (RelativeLayout) v.findViewById(R.id.dialogCityPickerButtonLayout);

        chosenCity = getArguments().getInt("chosen_city");

        cities = new ArrayList<>();

        CWStationsDatabase db = new CWStationsDatabase(getActivity());
        db.open();
        Cursor cursor = db.getAllCities();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            City city = new City();
            city.setRowID(cursor.getLong(cursor.getColumnIndex(CWStationsDatabase.ROW_ID)));
            city.setCityID(cursor.getInt(cursor.getColumnIndex(CWStationsDatabase.KEY_CITY_ID)));
            city.setCityName(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CITY_NAME)));
            cities.add(city);
        }

        db.close();

        final DialogCityPickerAdapter adapter = new DialogCityPickerAdapter(getActivity(), cities);
        adapter.setChosenCity(chosenCity);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenCity = position;
                adapter.setChosenCity(position);
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
                delegate.cityChosen(cities.get(chosenCity), chosenCity);
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
        delegate = (CityChosenListener) context;
    }
}
