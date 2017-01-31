package com.marat.apps.android.pro3.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.CarTypesRecyclerViewAdapter;
import com.marat.apps.android.pro3.TimetableDialog.RegisterEventListener;
import com.marat.apps.android.pro3.TimetableDialog.TimetableDialogFragment;
import com.marat.apps.android.pro3.R;

public class CWStationDetailsActivity extends AppCompatActivity implements RegisterEventListener{

    private RecyclerView carTypesRecyclerView;
    private EditText chosenServiceEditText;
    private TextView totalPriceTextView;
    private Button chooseTimeButton;

    private TimetableDialogFragment dialog;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carTypesRecyclerView.setLayoutManager(layoutManager);
        CarTypesRecyclerViewAdapter adapter = new CarTypesRecyclerViewAdapter(this);
        carTypesRecyclerView.setAdapter(adapter);

        chooseTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog = new TimetableDialogFragment();
                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public void registrationTimeIsChosen(boolean validOrNot, int boxId) {
        dialog.updateRegistrationValidity(validOrNot, boxId);
    }
}
