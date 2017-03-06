package com.marat.apps.android.pro3.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.CarTypesRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.RegistrationSuccessfullyFinishedListener;
import com.marat.apps.android.pro3.Interfaces.RegistrationTimeChosenListener;
import com.marat.apps.android.pro3.Dialogs.DialogFragmentTimetable;
import com.marat.apps.android.pro3.Models.CarType;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class CWStationDetailsActivity extends AppCompatActivity implements RegistrationTimeChosenListener, RegistrationSuccessfullyFinishedListener, View.OnClickListener {

    private RecyclerView carTypesRecyclerView;
    private TextView totalPriceTextView;
    private Button chooseTimeButton;
    private RelativeLayout service1Layout, service2Layout, service3Layout;
    private RadioButton service1RadioButton, service2RadioButton, service3RadioButton;
    private TextView service1TextView, service2TextView, service3TextView;

    private DialogFragmentTimetable dialog;

    private ArrayList<CarType> carTypes = new ArrayList<>();
    private CarType userCarType;

    private CWStationsDatabase db;

    private int userCarTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cwstation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carTypesRecyclerView = (RecyclerView) findViewById(R.id.CWSDetailsCarTypesRecyclerView);
        totalPriceTextView = (TextView) findViewById(R.id.CWSDetailsPriceTextView);
        chooseTimeButton = (Button) findViewById(R.id.CWSDetailsChooseTimeButton);
        service1Layout = (RelativeLayout) findViewById(R.id.CWSDetailsService1Layout);
        service2Layout = (RelativeLayout) findViewById(R.id.CWSDetailsService2Layout);
        service3Layout = (RelativeLayout) findViewById(R.id.CWSDetailsService3Layout);
        service1RadioButton = (RadioButton) findViewById(R.id.CWSDetailsService1RadioButton);
        service2RadioButton = (RadioButton) findViewById(R.id.CWSDetailsService2RadioButton);
        service3RadioButton = (RadioButton) findViewById(R.id.CWSDetailsService3RadioButton);
        service1TextView = (TextView) findViewById(R.id.CWSDetailsService1TextView);
        service2TextView = (TextView) findViewById(R.id.CWSDetailsService2TextView);
        service3TextView = (TextView) findViewById(R.id.CWSDetailsService3TextView);

        service1Layout.setOnClickListener(this);
        service2Layout.setOnClickListener(this);
        service3Layout.setOnClickListener(this);

        long rowId = getIntent().getExtras().getLong("rowId");
        String origin = getIntent().getExtras().getString("origin");
        setCarWashStationData(rowId, origin);

        db = new CWStationsDatabase(this);
        db.open();
        Cursor userCursor = db.getUserInformation();
        userCursor.moveToFirst();
        userCarTypeId = userCursor.getInt(userCursor.getColumnIndex(CWStationsDatabase.KEY_USER_CAR_TYPE_ID));
        setCarTypesArray();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPosition(carTypes.indexOf(userCarType));
        carTypesRecyclerView.setLayoutManager(layoutManager);
        CarTypesRecyclerViewAdapter adapter = new CarTypesRecyclerViewAdapter(this, carTypes, userCarTypeId);
        carTypesRecyclerView.setAdapter(adapter);

        chooseTimeButton.setOnClickListener(this);
    }

    private void setCarTypesArray() {
        db.open();
        Cursor cursor = db.getAllCarTypes();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            CarType carType = new CarType();
            carType.setRowID(cursor.getLong(cursor.getColumnIndex(CWStationsDatabase.ROW_ID)));
            carType.setCarTypeID(cursor.getInt(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_TYPE_ID)));
            carType.setCarTypeName(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_TYPE_NAME)));
            switch (carType.getCarTypeName()) {
                case "Малолитраж":
                    carType.setCarTypeIconId(R.drawable.ic_car_type_small);
                    break;
                case "Седан":
                    carType.setCarTypeIconId(R.drawable.ic_car_type_sedan);
                    break;
                case "Представительская":
                    carType.setCarTypeIconId(R.drawable.ic_car_type_premium);
                    break;
                case "Кроссовер":
                    carType.setCarTypeIconId(R.drawable.ic_car_type_crossover);
                    break;
                case "Внедорожник":
                    carType.setCarTypeIconId(R.drawable.ic_car_type_suv);
                    break;
            }
            if (userCarTypeId == carType.getCarTypeID()) {
                userCarType = carType;
            }
            carTypes.add(carType);
        }

        db.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CWSDetailsChooseTimeButton:
                dialog = new DialogFragmentTimetable();
                dialog.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.CWSDetailsService1Layout:
                service1RadioButton.setChecked(true);
                service2RadioButton.setChecked(false);
                service3RadioButton.setChecked(false);
                break;
            case R.id.CWSDetailsService2Layout:
                service1RadioButton.setChecked(false);
                service2RadioButton.setChecked(true);
                service3RadioButton.setChecked(false);
                break;
            case R.id.CWSDetailsService3Layout:
                service1RadioButton.setChecked(false);
                service2RadioButton.setChecked(false);
                service3RadioButton.setChecked(true);
                break;
        }
    }

    private void setCarWashStationData(long rowId, String origin) {
        CWStationsDatabase db = new CWStationsDatabase(this);
        db.open();
        if ("AllStations".equals(origin)) {
            Cursor c = db.getStationAt(rowId);
            c.moveToFirst();
            getSupportActionBar().setTitle(c.getString(c.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_NAME)));
        } else if ("FavoriteStations".equals(origin)) {
            Cursor c = db.getFavoriteStationAt(rowId);
            c.moveToFirst();
            getSupportActionBar().setTitle(c.getString(c.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_NAME)));
        }
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void registrationTimeIsChosen(boolean validOrNot, int boxId) {
        dialog.updateRegistrationValidity(validOrNot, boxId);
    }

    @Override
    public void registrationIsDone() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("startPage", "MyOrders");
        startActivity(intent);
        finish();
    }
}
