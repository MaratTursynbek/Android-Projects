package com.marat.apps.android.pro3.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.CarTypesRecyclerViewAdapter;
import com.marat.apps.android.pro3.R;

public class CWStationDetailsActivity extends AppCompatActivity {

    private RecyclerView carTypesRecyclerView;
    private EditText chosenServiceEditText;
    private TextView totalPriceTextView;
    private Button chooseTimeButton;

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
    }

}
