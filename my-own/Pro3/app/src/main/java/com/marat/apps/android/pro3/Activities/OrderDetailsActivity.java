package com.marat.apps.android.pro3.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.Internet.UpdateRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Response;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener, RequestResponseListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "OrderDetailsActivity";

    private static final String ORDERS_URL = "https://propropro.herokuapp.com/api/v1/orders/";

    private TextView carWashName;
    private TextView carWashAddress;
    private TextView orderTime;
    private TextView orderCreatedDate;
    private TextView orderCarType;
    private TextView orderServices;
    private TextView orderPrice;
    private TextView orderStatus;
    private Button cancelOrderButton;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout viewsLayout;
    private ProgressBar progressBar;
    private TextView errorTextView;

    private UpdateRequest updateRequest;
    private GetRequest getRequest;

    private int orderId;
    private boolean requestIsGet = false, cancelButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carWashName = (TextView) findViewById(R.id.odStationNameTextView);
        carWashAddress = (TextView) findViewById(R.id.odStationAddressTextView);
        orderTime = (TextView) findViewById(R.id.odOrderTimeTextView);
        orderCreatedDate = (TextView) findViewById(R.id.odOrderCreatedDateTextView);
        orderCarType = (TextView) findViewById(R.id.odCarTypeTextView);
        orderServices = (TextView) findViewById(R.id.odOrderServicesTextView);
        orderPrice = (TextView) findViewById(R.id.odPriceTextView);
        orderStatus = (TextView) findViewById(R.id.odStatusTextView);
        cancelOrderButton = (Button) findViewById(R.id.odCancelOrderButton);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.odSwipeRefreshLayout);
        viewsLayout = (RelativeLayout) findViewById(R.id.odViewsLayout);
        progressBar = (ProgressBar) findViewById(R.id.odProgressBar);
        errorTextView = (TextView) findViewById(R.id.odErrorTextView);

        cancelOrderButton.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);

        orderId = getIntent().getIntExtra("order_id", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRequest = new UpdateRequest(this);
        getRequest = new GetRequest(this);
        getRequest.delegate = this;
        getOrder(1);
    }

    private void showProgressBarVisible(int visible) {
        switch (visible) {
            case 1:
                viewsLayout.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case 2:
                viewsLayout.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                break;
            case 3:
                viewsLayout.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                break;
        }
    }

    private void getOrder(int origin) {
        String token = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE).getString("ACCESS_TOKEN", "");

        getRequest = new GetRequest(this);
        getRequest.delegate = this;

        if (getRequest.isNetworkAvailable()) {
            if (origin == 1) {
                showProgressBarVisible(1);
            }
            requestIsGet = true;
            getRequest.getUserOrder(ORDERS_URL + orderId, "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        String token = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE).getString("ACCESS_TOKEN", "");

        updateRequest = new UpdateRequest(this);

        cancelButtonPressed = true;

        if (updateRequest.networkIsAvailable()) {
            showProgressBarVisible(1);
            requestIsGet = false;
            updateRequest.cancelOrder(ORDERS_URL + orderId, "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(IOException e) {
        Log.d(TAG, "onFailure");
        if (requestIsGet) {
            stopRefreshImage(3);
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
            stopRefreshImage(1);
        }
    }

    @Override
    public void onResponse(Response response) {
        Log.d(TAG, "onResponse");
        String responseMessage = response.message();
        Log.d(TAG, "response message - " + responseMessage);

        if (getString(R.string.server_response_ok).equals(responseMessage)) {
            if (requestIsGet) {
                try {
                    String res = response.body().string();
                    Log.d(TAG, "response body - " + res);
                    stopRefreshingAndSetData(new JSONObject(res));
                } catch (IOException | JSONException e) {
                    stopRefreshImage(3);
                    e.printStackTrace();
                }
            } else {
                setDataForCancel();
                stopRefreshImage(2);
            }
        } else {
            if (requestIsGet) {
                stopRefreshImage(3);
            } else {
                showErrorToast(getString(R.string.error_could_not_load_data));
                stopRefreshImage(1);
            }
        }
    }

    private void stopRefreshImage(final int state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressBarVisible(state);
            }
        });
    }

    private void stopRefreshingAndSetData(final JSONObject order) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    setData(order);
                    showProgressBarVisible(2);
                } catch (ParseException | JSONException e) {
                    showProgressBarVisible(3);
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(JSONObject orderJSON) throws ParseException, JSONException {
        carWashName.setText(orderJSON.getString("carwash_name"));
        carWashAddress.setText(orderJSON.getString("carwash_address"));

        SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar date = Calendar.getInstance();

        String time = orderJSON.getString("created_at");
        date.setTime(sdf0.parse(time.substring(0, time.length() - 1)));
        orderCreatedDate.setText(sdf2.format(date.getTime()));

        time = orderJSON.getString("start_time");
        date.setTime(sdf1.parse(time.substring(0, time.length() - 1)));
        time = orderJSON.getString("end_time");
        orderTime.setText(sdf2.format(date.getTime()) + " - " + time.substring(11, 16));
        date.setTime(sdf1.parse(time.substring(0, time.length() - 1)));

        orderCarType.setText(orderJSON.getString("car_type"));
        orderServices.setText(orderJSON.getString("service_name"));
        orderPrice.setText(orderJSON.getString("price"));

        int status = orderJSON.getInt("status");

        if (status == 1 && date.compareTo(Calendar.getInstance()) > 0) {
            orderStatus.setText("Активный");
            orderStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            orderStatus.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            cancelOrderButton.setEnabled(false);
            cancelOrderButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDisabledCancelButton));
            if (status == 1 && date.compareTo(Calendar.getInstance()) <= 0) {
                orderStatus.setText("Завершен");
            } else if (status == 2) {
                orderStatus.setText("Отменен");
            } else if (status == 3) {
                orderStatus.setText("Отказано");
            }
        }
    }

    private void setDataForCancel() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orderStatus.setText("Отменен");
                orderStatus.setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, android.R.color.black));
                cancelOrderButton.setEnabled(false);
                cancelOrderButton.setBackgroundColor(ContextCompat.getColor(OrderDetailsActivity.this, R.color.colorDisabledCancelButton));
            }
        });
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

    @Override
    public void onBackPressed() {
        if (cancelButtonPressed) {

        }
        super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        getOrder(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        getRequest.cancelCall();
        updateRequest.cancelCall();
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.destroyDrawingCache();
            refreshLayout.clearAnimation();
        }
    }
}
