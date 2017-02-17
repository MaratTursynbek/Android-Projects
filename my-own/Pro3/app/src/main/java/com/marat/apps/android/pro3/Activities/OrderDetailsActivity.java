package com.marat.apps.android.pro3.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.R;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView carWashName;
    private TextView carWashAddress;
    private TextView orderServices;
    private TextView orderDate;
    private TextView orderPrice;
    private TextView orderStatus;
    private Button cancelOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carWashName = (TextView) findViewById(R.id.odStationNameTextView);
        carWashAddress = (TextView) findViewById(R.id.odStationAddressTextView);
        orderServices = (TextView) findViewById(R.id.odOrderServicesTextView);
        orderDate = (TextView) findViewById(R.id.odOrderDateTextView);
        orderPrice = (TextView) findViewById(R.id.odPriceTextView);
        orderStatus = (TextView) findViewById(R.id.odStatusTextView);
        cancelOrderButton = (Button) findViewById(R.id.odCancelOrderButton);

        long rowId = getIntent().getLongExtra("row_id", 0);
        setTextData(rowId);
    }

    private void setTextData(long rowId) {
        CWStationsDatabase db = new CWStationsDatabase(this);
        db.open();
        Cursor cursor = db.getOrderAt(rowId);
        cursor.moveToFirst();
        carWashName.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_NAME)));
        carWashAddress.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_ADDRESS)));
        orderServices.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_SERVICES)));
        orderDate.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_DATE)));
        orderPrice.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_PRICE)) + " тг.");
        orderStatus.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_STATUS)));

        if ("Активный".equals(orderStatus.getText().toString())) {
            orderStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
        else {
            orderStatus.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            cancelOrderButton.setEnabled(false);
            cancelOrderButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDisabledCancelButton));
        }
        db.close();
    }

    public void cancelOrder(View v) {
        Log.v("OrderDetailsActivity", "order is canceled");
        Toast.makeText(this, "order is canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
