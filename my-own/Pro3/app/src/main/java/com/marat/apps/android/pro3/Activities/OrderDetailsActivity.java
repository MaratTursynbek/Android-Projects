package com.marat.apps.android.pro3.Activities;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.UpdateRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener, RequestResponseListener {

    private static final String TAG = "OrderDetailsActivity";

    private static final String CANCEL_ORDER_URL = "https://propropro.herokuapp.com/api/v1/orders/";

    private TextView carWashName;
    private TextView carWashAddress;
    private TextView orderServices;
    private TextView orderDate;
    private TextView orderPrice;
    private TextView orderStatus;
    private Button cancelOrderButton;

    private UpdateRequest updateRequest;

    private int orderId;

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

        cancelOrderButton.setOnClickListener(this);

        long rowId = getIntent().getLongExtra("row_id", 0);
        setTextData(rowId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRequest = new UpdateRequest(this);
        updateRequest.delegate = this;
    }

    private void setTextData(long rowId) {
        CarWashesDatabase db = new CarWashesDatabase(this);
        db.open();
        Cursor cursor = db.getOrderAt(rowId);
        cursor.moveToFirst();
        orderId = cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_ORDER_ID));
        carWashName.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_NAME)));
        carWashAddress.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_ADDRESS)));
        orderServices.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_ORDER_SERVICES)));
        orderDate.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_ORDER_DATE)));
        orderPrice.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_ORDER_PRICE)) + " тг.");
        orderStatus.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_ORDER_STATUS)));

        if ("Активный".equals(orderStatus.getText().toString())) {
            orderStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            orderStatus.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            cancelOrderButton.setEnabled(false);
            cancelOrderButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDisabledCancelButton));
        }
        db.close();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        updateRequest = new UpdateRequest(this);
        updateRequest.delegate = this;

        if (updateRequest.isNetworkAvailable()) {
            updateRequest.cancelOrder(CANCEL_ORDER_URL + orderId, "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(IOException e) {
        Log.d(TAG, "onFailure");
        showErrorToast(getString(R.string.error_could_not_load_data));
    }

    @Override
    public void onResponse(Response response) {
        Log.d(TAG, "onResponse");
        String responseMessage = response.message();
        Log.d(TAG, "response message - " + responseMessage);

        if (getString(R.string.server_response_ok).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "response body - " + res);
                if ("Dancelled".equals(res)) {
                    Log.d(TAG, "Successful");
                }
            } catch (IOException e) {
                showErrorToast(getString(R.string.error_could_not_load_data));
                e.printStackTrace();
            }
        }
    }

    private void showErrorToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OrderDetailsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
