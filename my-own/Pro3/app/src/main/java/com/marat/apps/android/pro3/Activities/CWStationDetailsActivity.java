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
import android.widget.EditText;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.CarTypesRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.RegistrationSuccessfullyFinishedListener;
import com.marat.apps.android.pro3.Interfaces.RegistrationTimeChosenListener;
import com.marat.apps.android.pro3.Dialogs.DialogFragmentTimetable;
import com.marat.apps.android.pro3.R;

public class CWStationDetailsActivity extends AppCompatActivity implements RegistrationTimeChosenListener, RegistrationSuccessfullyFinishedListener, View.OnClickListener {

    private RecyclerView carTypesRecyclerView;
    private EditText chosenServiceEditText;
    private TextView totalPriceTextView;
    private Button chooseTimeButton;

    private DialogFragmentTimetable dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cwstation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carTypesRecyclerView = (RecyclerView) findViewById(R.id.carTypesRecyclerView);
        chosenServiceEditText = (EditText) findViewById(R.id.chosenServiceEditText);
        totalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);
        chooseTimeButton = (Button) findViewById(R.id.chooseTimeButton);

        long rowId = getIntent().getExtras().getLong("rowId");
        String origin = getIntent().getExtras().getString("origin");
        setCarWashStationData(rowId, origin);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carTypesRecyclerView.setLayoutManager(layoutManager);
        CarTypesRecyclerViewAdapter adapter = new CarTypesRecyclerViewAdapter(this);
        carTypesRecyclerView.setAdapter(adapter);

        chooseTimeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseTimeButton:
                dialog = new DialogFragmentTimetable();
                dialog.show(getSupportFragmentManager(), "dialog");
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
