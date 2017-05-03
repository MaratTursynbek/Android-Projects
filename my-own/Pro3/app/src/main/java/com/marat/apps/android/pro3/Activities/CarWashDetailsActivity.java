package com.marat.apps.android.pro3.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Adapters.CarTypesRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Dialogs.DialogFragmentTimetable;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Interfaces.ServiceCarTypeChosenListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.Models.CarType;
import com.marat.apps.android.pro3.Models.CarWashServices;
import com.marat.apps.android.pro3.Models.Service;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Response;

public class CarWashDetailsActivity extends AppCompatActivity implements RequestResponseListener, ServiceCarTypeChosenListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "CarWashDetailsActivity";

    private static final String GET_CAR_WASH_URL = "https://propropro.herokuapp.com/api/v1/carwashes/";

    private TextView totalPriceTextView, carWashPhoneNumberTextView, carWashAddressTextView, errorTextView;
    private RadioButton service1RadioButton, service2RadioButton, service3RadioButton;
    private TextView service1TextView, service2TextView, service3TextView;
    private CardView cardView1, cardView2;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;

    private DialogFragmentTimetable dialog;

    private ArrayList<CarWashServices> listOfCarWashServices = new ArrayList<>();
    private ArrayList<CarType> listOfCarTypes = new ArrayList<>();
    private CarType userCarType;
    private CarTypesRecyclerViewAdapter adapter;

    private GetRequest getRequest;

    private int userCarTypeId, carWashId, chosenService = -1, chosenServicePriceId;
    private String carWashAddress, carWashPhoneNumber;
    private boolean activityOpenedNow = true;

    private BroadcastReceiver finishActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("finish_car_wash_details_activity".equals(intent.getAction())) {
                LocalBroadcastManager.getInstance(CarWashDetailsActivity.this).unregisterReceiver(finishActivityReceiver);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(finishActivityReceiver, new IntentFilter("finish_car_wash_details_activity"));

        RecyclerView carTypesRecyclerView = (RecyclerView) findViewById(R.id.cwdCarTypesRecyclerView);
        totalPriceTextView = (TextView) findViewById(R.id.cwdPriceTextView);
        carWashPhoneNumberTextView = (TextView) findViewById(R.id.cwdPhoneNumberTextView);
        carWashAddressTextView = (TextView) findViewById(R.id.cwdCarWashAddressTextView);
        service1RadioButton = (RadioButton) findViewById(R.id.cwdService1RadioButton);
        service2RadioButton = (RadioButton) findViewById(R.id.cwdService2RadioButton);
        service3RadioButton = (RadioButton) findViewById(R.id.cwdService3RadioButton);
        service1TextView = (TextView) findViewById(R.id.cwdService1TextView);
        service2TextView = (TextView) findViewById(R.id.cwdService2TextView);
        service3TextView = (TextView) findViewById(R.id.cwdService3TextView);
        cardView1 = (CardView) findViewById(R.id.cwdCardView1);
        cardView2 = (CardView) findViewById(R.id.cwdCardView2);
        progressBar = (ProgressBar) findViewById(R.id.cwdProgressBar);
        errorTextView = (TextView) findViewById(R.id.cwdErrorTextView);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.cwdSwipeToRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        long rowId = getIntent().getExtras().getLong("row_id");
        String origin = getIntent().getExtras().getString("origin");
        getCarWashStationData(rowId, origin);

        setCarTypesArray();

        getRequest = new GetRequest(this);
        getRequest.delegate = this;

        showProgressBarVisible(1);
        getCarWashDataFromServer();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(listOfCarTypes.indexOf(userCarType), 70);
        carTypesRecyclerView.setLayoutManager(layoutManager);
        adapter = new CarTypesRecyclerViewAdapter(this, this, listOfCarTypes, userCarTypeId);
        carTypesRecyclerView.setAdapter(adapter);
    }

    private void getCarWashStationData(long rowId, String origin) {
        CarWashesDatabase db = new CarWashesDatabase(this);
        db.open();
        if ("all_car_washes".equals(origin)) {
            Cursor c = db.getStationAt(rowId);
            c.moveToFirst();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(c.getString(c.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_NAME)));
            }
            carWashId = c.getInt(c.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_ID));
        } else if ("favorite_car_washes".equals(origin)) {
            Cursor c = db.getFavoriteStationAt(rowId);
            c.moveToFirst();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(c.getString(c.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_NAME)));
            }
            carWashId = c.getInt(c.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_ID));
        }
        db.close();
    }

    private void setCarTypesArray() {
        CarWashesDatabase db = new CarWashesDatabase(this);
        db.open();
        Cursor carTypesCursor = db.getAllCarTypes();
        Cursor userCursor = db.getUserInformation();
        userCursor.moveToFirst();
        userCarTypeId = userCursor.getInt(userCursor.getColumnIndex(CarWashesDatabase.KEY_USER_CAR_TYPE_ID));

        for (carTypesCursor.moveToFirst(); !carTypesCursor.isAfterLast(); carTypesCursor.moveToNext()) {
            CarType carType = new CarType();
            carType.setRowID(carTypesCursor.getLong(carTypesCursor.getColumnIndex(CarWashesDatabase.ROW_ID)));
            carType.setCarTypeID(carTypesCursor.getInt(carTypesCursor.getColumnIndex(CarWashesDatabase.KEY_CAR_TYPE_ID)));
            carType.setCarTypeName(carTypesCursor.getString(carTypesCursor.getColumnIndex(CarWashesDatabase.KEY_CAR_TYPE_NAME)));
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
            listOfCarTypes.add(carType);
        }

        db.close();
    }

    private void getCarWashDataFromServer() {
        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        getRequest = new GetRequest(this);
        getRequest.delegate = this;

        if (getRequest.isNetworkAvailable()) {
            getRequest.getCarWash(GET_CAR_WASH_URL + carWashId, "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            showProgressBarVisible(3);
        }
    }

    private void showProgressBarVisible(int visible) {
        switch (visible) {
            case 1:
                cardView1.setVisibility(View.INVISIBLE);
                cardView2.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
                break;
            case 2:
                cardView1.setVisibility(View.VISIBLE);
                cardView2.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                if (activityOpenedNow) {
                    onClick(findViewById(R.id.cwdService2Layout));
                    activityOpenedNow = false;
                }
                break;
            case 3:
                cardView1.setVisibility(View.INVISIBLE);
                cardView2.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                break;
            case 4:
                cardView1.setVisibility(View.INVISIBLE);
                cardView2.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                refreshLayout.setRefreshing(true);
                break;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cwdChooseTimeButton:
                if (chosenService == -1) {
                    Toast.makeText(this, "Выберите Услугу", Toast.LENGTH_SHORT).show();
                    break;
                } else if (chosenService == 0) {
                    dialog = DialogFragmentTimetable.newInstance(carWashId, chosenServicePriceId, 30);
                } else {
                    dialog = DialogFragmentTimetable.newInstance(carWashId, chosenServicePriceId, 60);
                }
                dialog.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.cwdService1Layout:
                service1RadioButton.setChecked(true);
                service2RadioButton.setChecked(false);
                service3RadioButton.setChecked(false);
                chosenService = 0;
                totalPriceTextView.setText(getCheckedServicePrice());
                break;
            case R.id.cwdService2Layout:
                service1RadioButton.setChecked(false);
                service2RadioButton.setChecked(true);
                service3RadioButton.setChecked(false);
                chosenService = 1;
                totalPriceTextView.setText(getCheckedServicePrice());
                break;
            case R.id.cwdService3Layout:
                service1RadioButton.setChecked(false);
                service2RadioButton.setChecked(false);
                service3RadioButton.setChecked(true);
                chosenService = 2;
                totalPriceTextView.setText(getCheckedServicePrice());
                break;
            case R.id.cwdShowOnMapTextView:
                Intent intent = new Intent(this, LocationOnMapActivity.class);
                startActivity(intent);
                break;
        }
    }

    public String getCheckedServicePrice() {
        ArrayList<Service> listOfServices = listOfCarWashServices.get(chosenService).getServices();
        String price = "";
        for (int i = 0; i < listOfServices.size(); i++) {
            if (listOfServices.get(i).getCarTypeId() == adapter.selectedCar) {
                price = listOfServices.get(i).getServicePrice() + " тг";
                chosenServicePriceId = listOfServices.get(i).getServicePriceId();
                break;
            }
        }
        return price;
    }

    @Override
    public void onCarTypeChosen() {
        if (chosenService != -1) {
            totalPriceTextView.setText(getCheckedServicePrice());
        }
    }

    @Override
    public void onFailure(IOException e) {
        Log.d(TAG, "onFailure");
        showErrorToast(getString(R.string.error_could_not_load_data));
        e.printStackTrace();
        stopRefreshImage(3);
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
                JSONObject result = new JSONObject(res);
                JSONObject carWashObject = result.getJSONObject("carwash");
                processCarWashData(carWashObject);
                stopRefreshImage(2);
            } catch (IOException | JSONException e) {
                showErrorToast(getString(R.string.error_could_not_load_data));
                stopRefreshImage(3);
                e.printStackTrace();
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
            stopRefreshImage(3);
        }
    }

    private void processCarWashData(JSONObject carWashObject) throws JSONException {
        carWashAddress = carWashObject.getString("address");
        carWashPhoneNumber = carWashObject.getString("phone_number");
        JSONArray JSONServices = carWashObject.getJSONArray("prices");

        int currentServiceId = JSONServices.getJSONObject(0).getJSONObject("service").getInt("id");

        ArrayList<Service> servicesOfOneType = new ArrayList<>();
        Service service = new Service();

        for (int i = 0; i < JSONServices.length(); i++) {
            JSONObject JSONService = JSONServices.getJSONObject(i);
            service = new Service();
            service.setServiceId(JSONService.getJSONObject("service").getInt("id"));
            service.setServiceName(JSONService.getJSONObject("service").getString("name"));
            service.setCarTypeId(JSONService.getJSONObject("car_type").getInt("id"));
            service.setCarTypeName(JSONService.getJSONObject("car_type").getString("name"));
            service.setServicePriceId(JSONService.getInt("id"));
            service.setServicePrice(JSONService.getInt("price"));
            service.setServiceDescription(JSONService.getString("description"));

            Log.d(TAG, currentServiceId + " " + service.getServiceId());

            if (currentServiceId != service.getServiceId()) {
                CarWashServices temp1 = new CarWashServices();
                temp1.setServiceId(servicesOfOneType.get(4).getServiceId());
                temp1.setServiceName(servicesOfOneType.get(4).getServiceName());
                temp1.setServices(servicesOfOneType);
                listOfCarWashServices.add(temp1);
                servicesOfOneType = new ArrayList<>();
                Log.d(TAG, servicesOfOneType.size() + "");
                currentServiceId = service.getServiceId();
            }
            servicesOfOneType.add(service);
        }

        CarWashServices temp1 = new CarWashServices();
        temp1.setServiceId(service.getServiceId());
        temp1.setServiceName(service.getServiceName());
        temp1.setServices(servicesOfOneType);
        listOfCarWashServices.add(temp1);

        setUpCheckableServices();
    }

    private void setUpCheckableServices() {
        Collections.sort(listOfCarWashServices, new Comparator<CarWashServices>() {
            @Override
            public int compare(CarWashServices o1, CarWashServices o2) {
                return o1.getServiceName().compareTo(o2.getServiceName());
            }
        });
        Log.d(TAG, listOfCarWashServices + "");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                service1TextView.setText(listOfCarWashServices.get(0).getServiceName());
                service2TextView.setText(listOfCarWashServices.get(1).getServiceName());
                service3TextView.setText(listOfCarWashServices.get(2).getServiceName());
                carWashPhoneNumberTextView.setText(
                        "+7 (" + carWashPhoneNumber.substring(0, 3) + ") " +
                                carWashPhoneNumber.substring(3, 6) + "-" +
                                carWashPhoneNumber.substring(6, 8) + "-" +
                                carWashPhoneNumber.substring(8)
                );
                carWashAddressTextView.setText(carWashAddress);
            }
        });
    }

    private void stopRefreshImage(final int state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressBarVisible(state);
            }
        });
    }

    private void showErrorToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CarWashDetailsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        getCarWashDataFromServer();
        showProgressBarVisible(4);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onPause() {
        getRequest.cancelCall();
        super.onPause();
        Log.d(TAG, "onPause");
    }
}
