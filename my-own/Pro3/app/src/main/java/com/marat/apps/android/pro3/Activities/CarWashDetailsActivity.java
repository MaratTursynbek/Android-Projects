package com.marat.apps.android.pro3.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Adapters.CarTypesRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Dialogs.DialogFragmentTimetable;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Interfaces.ServiceCarTypeChosenListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.Internet.PostRequest;
import com.marat.apps.android.pro3.Internet.UpdateRequest;
import com.marat.apps.android.pro3.Models.CarTypeWithServices;
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

public class CarWashDetailsActivity extends AppCompatActivity implements View.OnClickListener, RequestResponseListener, ServiceCarTypeChosenListener {

    private static final String TAG = "CarWashDetailsActivity";

    private static final String GET_CAR_WASH_URL = "https://propropro.herokuapp.com/api/v1/carwashes/";
    private static final String FAVORITE_CAR_WASHES_URL = "https://propropro.herokuapp.com/api/v1/favorites/";

    private RecyclerView carTypesRecyclerView;
    private TextView totalPriceTextView, totalTimeTextView, carWashPhoneNumberTextView, carWashAddressTextView, errorTextView;
    private CardView cardView1, cardView2;
    private ProgressBar progressBar;
    private LinearLayout servicesLinearLayout;

    private DialogFragmentTimetable dialogTimetable;
    private ProgressDialog dialogFavorite;

    private ArrayList<CarTypeWithServices> arrayOfCarTypesWithServices = new ArrayList<>();
    private CarTypesRecyclerViewAdapter adapter;
    private CarTypeWithServices userCarTypeWithServices;

    private GetRequest getRequest;
    private PostRequest postRequest;
    private UpdateRequest updateRequest;

    private int userCarTypeID, carWashID, chosenServiceIndex = -1, chosenServicePriceID, carTypeIndex;
    private String carWashAddressText, carWashPhoneNumberText;
    private boolean requestIsGet = false;
    private boolean requestIsCreateFavorite = false;
    private boolean carWashIsFavorite = false;

    private Menu menu;

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

        carTypesRecyclerView = (RecyclerView) findViewById(R.id.cwdCarTypesRecyclerView);
        totalPriceTextView = (TextView) findViewById(R.id.cwdPriceTextView);
        totalTimeTextView = (TextView) findViewById(R.id.cwdTimeTextView);
        carWashPhoneNumberTextView = (TextView) findViewById(R.id.cwdPhoneNumberTextView);
        carWashAddressTextView = (TextView) findViewById(R.id.cwdCarWashAddressTextView);
        cardView1 = (CardView) findViewById(R.id.cwdCardView1);
        cardView2 = (CardView) findViewById(R.id.cwdCardView2);
        servicesLinearLayout = (LinearLayout) findViewById(R.id.cwdServicesLayout);
        progressBar = (ProgressBar) findViewById(R.id.cwdProgressBar);
        errorTextView = (TextView) findViewById(R.id.cwdErrorTextView);

        carWashID = getIntent().getExtras().getInt("car_wash_id");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getExtras().getString("car_wash_name"));
        }

        CarWashesDatabase db = new CarWashesDatabase(this);
        db.open();
        Cursor userCursor = db.getUserInformation();
        userCursor.moveToFirst();
        userCarTypeID = userCursor.getInt(userCursor.getColumnIndex(CarWashesDatabase.KEY_USER_CAR_TYPE_ID));
        db.close();

        getRequest = new GetRequest(this);
        getRequest.delegate = this;
        postRequest = new PostRequest(this);
        postRequest.delegate = this;
        updateRequest = new UpdateRequest(this);

        showProgressBarVisible(1);
        getCarWashDataFromServer();
    }

    private void getCarWashDataFromServer() {
        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        getRequest = new GetRequest(this);
        getRequest.delegate = this;

        if (getRequest.isNetworkAvailable()) {
            getRequest.getCarWash(GET_CAR_WASH_URL + carWashID, "Token token=\"" + token + "\"");
            requestIsGet = true;
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
                break;
            case 2:
                setUpDataAndViews();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        carTypesRecyclerView.findViewHolderForAdapterPosition(arrayOfCarTypesWithServices.indexOf(userCarTypeWithServices)).itemView.performClick();
                        cardView1.setVisibility(View.VISIBLE);
                        cardView2.setVisibility(View.VISIBLE);
                        errorTextView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 10);
                break;
            case 3:
                cardView1.setVisibility(View.INVISIBLE);
                cardView2.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void setUpDataAndViews() {
        if (carWashIsFavorite) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_white_36dp));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_border_white_36dp));
        }

        carWashPhoneNumberTextView.setText(
                "+7 (" + carWashPhoneNumberText.substring(0, 3) + ") " +
                        carWashPhoneNumberText.substring(3, 6) + "-" +
                        carWashPhoneNumberText.substring(6, 8) + "-" +
                        carWashPhoneNumberText.substring(8)
        );
        carWashAddressTextView.setText(carWashAddressText);

        adapter = new CarTypesRecyclerViewAdapter(this, arrayOfCarTypesWithServices);
        carTypesRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CarWashDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(arrayOfCarTypesWithServices.indexOf(userCarTypeWithServices), 70);
        carTypesRecyclerView.setLayoutManager(layoutManager);
        carTypesRecyclerView.setHasFixedSize(true);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cwdChooseTimeButton:
                if (chosenServiceIndex == -1) {
                    Toast.makeText(this, "Выберите Услугу", Toast.LENGTH_SHORT).show();
                    break;
                } else if (chosenServiceIndex == 0) {
                    dialogTimetable = DialogFragmentTimetable.newInstance(carWashID, chosenServicePriceID, 30);
                } else {
                    dialogTimetable = DialogFragmentTimetable.newInstance(carWashID, chosenServicePriceID, 60);
                }
                dialogTimetable.show(getSupportFragmentManager(), "dialogTimetable");
                break;
            case R.id.cwdShowOnMapTextView:
                Intent intent = new Intent(this, LocationOnMapActivity.class);
                startActivity(intent);
                break;
            default:
                switch (v.getTag().toString()) {
                    case "cwdService1Layout":
                        chosenServiceIndex = 0;
                        totalPriceTextView.setText(getCheckedServicePrice());
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService1Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(true);
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService2Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(false);
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService3Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(false);
                        break;
                    case "cwdService2Layout":
                        chosenServiceIndex = 1;
                        totalPriceTextView.setText(getCheckedServicePrice());
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService1Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(false);
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService2Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(true);
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService3Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(false);
                        break;
                    case "cwdService3Layout":
                        chosenServiceIndex = 2;
                        totalPriceTextView.setText(getCheckedServicePrice());
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService1Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(false);
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService2Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(false);
                        ((RadioButton) servicesLinearLayout.findViewWithTag("cwdService3Layout").findViewById(R.id.cwdServiceRadioButton)).setChecked(true);
                        break;
                }
        }
    }

    public String getCheckedServicePrice() {
        if (arrayOfCarTypesWithServices.get(carTypeIndex).getServices().get(chosenServiceIndex).getServiceName().equals("Кузов")) {
            totalTimeTextView.setText(R.string.text_30_minutes);
        } else {
            totalTimeTextView.setText(R.string.text_60_minutes);
        }
        chosenServicePriceID = arrayOfCarTypesWithServices.get(carTypeIndex).getServices().get(chosenServiceIndex).getServicePriceId();
        return arrayOfCarTypesWithServices.get(carTypeIndex).getServices().get(chosenServiceIndex).getServicePrice() + " тг.";
    }

    @Override
    public void onCarTypeChosen(int carTypeIndex) {
        Log.d(TAG, "ocCarTypeChosen");
        this.carTypeIndex = carTypeIndex;
        servicesLinearLayout.removeAllViewsInLayout();
        int serviceLayoutIDIterator = 1;
        for (int i = 0; i < arrayOfCarTypesWithServices.get(carTypeIndex).getServices().size(); i++) {
            RelativeLayout serviceLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.list_item_service_layout, null, false);
            serviceLayout.setTag("cwdService" + serviceLayoutIDIterator + "Layout");
            serviceLayout.setOnClickListener(this);
            ((TextView) serviceLayout.findViewById(R.id.cwdServiceTextView)).setText(arrayOfCarTypesWithServices.get(carTypeIndex).getServices().get(i).getServiceName());
            servicesLinearLayout.addView(serviceLayout);
            serviceLayoutIDIterator++;
        }
        if (chosenServiceIndex != -1) {
            onClick(servicesLinearLayout.findViewWithTag("cwdService" + (chosenServiceIndex + 1) + "Layout"));
            return;
        }
        boolean clickedServiceFound = false;
        for (int i = 0; i < arrayOfCarTypesWithServices.get(carTypeIndex).getServices().size(); i++) {
            if (arrayOfCarTypesWithServices.get(carTypeIndex).getServices().get(i).getServiceName().equals("Кузов + Салон")) {
                onClick(servicesLinearLayout.findViewWithTag("cwdService" + (i + 1) + "Layout"));
                clickedServiceFound = true;
                break;
            }
        }
        if (!clickedServiceFound) {
            totalPriceTextView.setText("");
        }
    }

    @Override
    public void onFailure(IOException e) {
        Log.d(TAG, "onFailure");
        e.printStackTrace();
        if (requestIsGet) {
            stopRefreshImage(3);
        } else {
            if (requestIsCreateFavorite) {
                showErrorToast(getString(R.string.error_could_not_make_favorite));
            } else {
                showErrorToast(getString(R.string.error_could_not_cancel_favorite));
            }
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
                    String result = response.body().string();
                    Log.d(TAG, "response body - " + result);
                    JSONObject responseObject = new JSONObject(result);
                    processCarWashData(responseObject);
                    stopRefreshImage(2);
                } catch (IOException | JSONException e) {
                    stopRefreshImage(3);
                    e.printStackTrace();
                }
            } else {
                if (requestIsCreateFavorite) {
                    showToastAndChangeMenuIcon(getString(R.string.success_car_wash_added_to_favorites), true, true);
                    carWashIsFavorite = true;
                } else {
                    showToastAndChangeMenuIcon(getString(R.string.success_car_wash_deleted_from_favorites), true, false);
                    carWashIsFavorite = false;
                }
            }
        } else {
            stopRefreshImage(3);
        }
    }

    private void processCarWashData(JSONObject responseObject) throws JSONException {
        JSONObject carWashObject = responseObject.getJSONObject("carwash");
        JSONArray servicesArray = responseObject.getJSONArray("prices");

        carWashIsFavorite = responseObject.getString("favorite?").equals("Yes");
        carWashAddressText = carWashObject.getString("address");
        carWashPhoneNumberText = carWashObject.getString("phone_number");

        if (servicesArray.length() > 0) {
            int currentCarTypeID = servicesArray.getJSONObject(0).getJSONObject("car_type").getInt("id");
            String currentCarTypeName = servicesArray.getJSONObject(0).getJSONObject("car_type").getString("name");

            ArrayList<Service> services = new ArrayList<>();

            for (int i = 0; i < servicesArray.length(); i++) {
                JSONObject serviceObject = servicesArray.getJSONObject(i);

                if (currentCarTypeID != serviceObject.getJSONObject("car_type").getInt("id")) {
                    createCarTypeWithServices(currentCarTypeID, currentCarTypeName, services);

                    currentCarTypeID = serviceObject.getJSONObject("car_type").getInt("id");
                    currentCarTypeName = serviceObject.getJSONObject("car_type").getString("name");
                    services = new ArrayList<>();
                }

                Service oneService = new Service();
                oneService.setServiceName(serviceObject.getJSONObject("service").getString("name"));
                oneService.setServiceDescription(serviceObject.getString("description"));
                oneService.setServicePriceId(serviceObject.getInt("id"));
                oneService.setServicePrice(serviceObject.getInt("price"));
                services.add(oneService);
            }

            // add last car type to array
            createCarTypeWithServices(currentCarTypeID, currentCarTypeName, services);
        }
    }

    private void createCarTypeWithServices(int id, String name, ArrayList<Service> services) {
        // filling car type with its services
        CarTypeWithServices carTypeWithServices = new CarTypeWithServices();
        carTypeWithServices.setCarTypeID(id);
        carTypeWithServices.setCarTypeName(name);
        switch (carTypeWithServices.getCarTypeName()) {
            case "Малолитраж":
                carTypeWithServices.setCarTypeIconID(R.drawable.ic_car_type_small);
                break;
            case "Седан":
                carTypeWithServices.setCarTypeIconID(R.drawable.ic_car_type_sedan);
                break;
            case "Представительская":
                carTypeWithServices.setCarTypeIconID(R.drawable.ic_car_type_premium);
                break;
            case "Кроссовер":
                carTypeWithServices.setCarTypeIconID(R.drawable.ic_car_type_crossover);
                break;
            case "Внедорожник":
                carTypeWithServices.setCarTypeIconID(R.drawable.ic_car_type_suv);
                break;
        }
        Collections.sort(services, new Comparator<Service>() {
            @Override
            public int compare(Service o1, Service o2) {
                return o1.getServiceName().compareTo(o2.getServiceName());
            }
        });
        carTypeWithServices.setServices(services);

        arrayOfCarTypesWithServices.add(carTypeWithServices);

        if (id == userCarTypeID) {
            userCarTypeWithServices = carTypeWithServices;
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

    private void showErrorToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogFavorite != null && dialogFavorite.isShowing()) {
                    dialogFavorite.dismiss();
                }
                Toast.makeText(CarWashDetailsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showToastAndChangeMenuIcon(final String message, final boolean successful, final boolean menuIconIsFavorite) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogFavorite != null && dialogFavorite.isShowing()) {
                    dialogFavorite.dismiss();
                }
                if (successful) {
                    if (menuIconIsFavorite) {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(CarWashDetailsActivity.this, R.drawable.ic_star_white_36dp));
                    } else {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(CarWashDetailsActivity.this, R.drawable.ic_star_border_white_36dp));
                    }
                }
                Toast.makeText(CarWashDetailsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car_wash_details, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.create_or_delete_favorite:
                if (!carWashIsFavorite) {
                    addCarWashToFavorites();
                } else {
                    deleteCarWashFromFavorites();
                }
                break;
        }
        return true;
    }

    private void addCarWashToFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        String json = "{\"favorite\": { \"carwash_id\" : \"" + carWashID + "\",\"status\" : \"t\"}}";

        postRequest = new PostRequest(this);
        postRequest.delegate = this;

        if (postRequest.isNetworkAvailable()) {
            postRequest.addCarWashToFavorites(FAVORITE_CAR_WASHES_URL, json, "Token token=\"" + token + "\"");
            showDialogForFavorite(getString(R.string.dialog_text_adding_to_favorites));
            requestIsGet = false;
            requestIsCreateFavorite = true;
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            showProgressBarVisible(3);
        }
    }

    private void deleteCarWashFromFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        updateRequest = new UpdateRequest(this);

        if (updateRequest.networkIsAvailable()) {
            updateRequest.deleteCarWashFromFavorites(FAVORITE_CAR_WASHES_URL + carWashID, "Token token=\"" + token + "\"");
            showDialogForFavorite(getString(R.string.dialog_text_deleting_from_favorites));
            requestIsGet = false;
            requestIsCreateFavorite = false;
        } else {
            Toast.makeText(this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            showProgressBarVisible(3);
        }
    }

    private void showDialogForFavorite(String process) {
        dialogFavorite = new ProgressDialog(this);
        dialogFavorite.setMessage(process);
        dialogFavorite.setCancelable(false);
        dialogFavorite.setCanceledOnTouchOutside(false);
        dialogFavorite.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        getRequest.cancelCall();
        postRequest.cancelCall();
        updateRequest.cancelCall();
        if (dialogFavorite != null && dialogFavorite.isShowing()) {
            dialogFavorite.dismiss();
        }
        if (dialogTimetable != null && dialogTimetable.getDialog() != null && dialogTimetable.getDialog().isShowing()) {
            dialogTimetable.dismiss();
        }
    }
}
