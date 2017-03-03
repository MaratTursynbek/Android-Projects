package com.marat.apps.android.pro3.MenuSections;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Adapters.CarWashersAllRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Databases.StoreToDatabaseHelper;
import com.marat.apps.android.pro3.Interfaces.OnToolbarTitleChangeListener;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.UniversalGetRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class AllCarWashersFragment extends Fragment implements RequestResponseListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "logtag";

    private static final String ALL_CAR_WASHERS_URL = "https://propropro.herokuapp.com/api/v1/carwashes";

    private Cursor cursor;
    private CWStationsDatabase db;

    private UniversalGetRequest getRequest;

    private OnToolbarTitleChangeListener listener;

    private RecyclerView recyclerView;
    private TextView emptyText;
    private CarWashersAllRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;

    private int currentCityId = 0, downloadedCityId;

    public static AllCarWashersFragment newInstance(int currentCityId, String currentCityName) {
        AllCarWashersFragment fragment = new AllCarWashersFragment();
        Bundle args = new Bundle();
        args.putInt("current_city_id", currentCityId);
        args.putString("current_city_name", currentCityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_car_washers, container, false);

        listener = (OnToolbarTitleChangeListener) getActivity();

        recyclerView = (RecyclerView) v.findViewById(R.id.carWashersRecyclerView);
        emptyText = (TextView) v.findViewById(R.id.carWashersEmptyTextView);
        progressBar = (ProgressBar) v.findViewById(R.id.carWashersProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.carWashersSwipeToRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        db = new CWStationsDatabase(getContext());

        getRequest = new UniversalGetRequest(getContext());
        getRequest.delegate = this;

        currentCityId = getArguments().getInt("current_city_id");
        setCityOnToolbar(getArguments().getString("current_city_name"));

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        downloadedCityId = sharedPreferences.getInt("downloaded_city_id", 0);

        Log.d(TAG, "AllCarWashersFragment: " + " current_city_id = " + currentCityId + " downloaded_city_id = " + downloadedCityId);

        if (currentCityId == downloadedCityId) {
            Log.d(TAG, "AllCarWashersFragment: " + "ids are equal");
            setupDataAndViews();
        } else {
            Log.d(TAG, "AllCarWashersFragment: " + "new data is downloading");
            db.open();
            db.deleteAllStations();
            db.close();
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            getAllCarWashesFromServer();
        }

        return v;
    }

    private void setupDataAndViews() {
        db.open();
        cursor = db.getAllStations();

        if (cursor.getCount() <= 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            setAdapterToRecyclerView();
        }
        db.close();
    }

    private void setAdapterToRecyclerView() {
        if (adapter == null) {
            adapter = new CarWashersAllRecyclerViewAdapter(cursor, getContext(), db, "AllStations");
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateCursor(cursor);
        }
    }

    private void getAllCarWashesFromServer() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if (getRequest.isNetworkAvailable()) {
            getRequest.getCarWashersForCityId(ALL_CAR_WASHERS_URL, "Token token=\"" + token + "\"", currentCityId);
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            stopRefreshImage();
        }
    }

    @Override
    public void onFailure(IOException e) {
        showErrorToast(getString(R.string.error_could_not_load_data));
        Log.d(TAG, "AllCarWashersFragment: " + "onFailure");
        e.printStackTrace();
        stopRefreshImage();
    }

    @Override
    public void onResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "AllCarWashersFragment: " + "response message - " + responseMessage);

        if (getString(R.string.server_response_all_car_washers_received).equals(responseMessage)) {
            downloadedCityId = currentCityId;
            try {
                String res = response.body().string();
                Log.d(TAG, "AllCarWashersFragment: " + "response body - " + res);
                JSONObject responseObject = new JSONObject(res);
                JSONArray allCarWashers = responseObject.getJSONArray("city_carwashes");
                saveUpdatedAllCarWashersList(allCarWashers);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        stopRefreshImage();
    }

    private void saveUpdatedAllCarWashersList(JSONArray allCarWashers) throws JSONException {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(getContext());
        helper.saveAllCarWashers(allCarWashers, currentCityId);
    }

    public void updateDataForCity(int cityId, String cityName) {
        Log.d(TAG, "AllCarWashersFragment: " + "updateDataForCity - " + cityId);
        currentCityId = cityId;
        setCityOnToolbar(cityName);
        db.open();
        db.deleteAllStations();
        db.close();
        recyclerView.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        getAllCarWashesFromServer();
    }

    private void showErrorToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCityOnToolbar(final String cityName) {
        listener.onTitleChanged(getString(R.string.title_main_fragment_all_stations_toolbar) + cityName);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "AllCarWashersFragment: " + "onRefresh");
        getAllCarWashesFromServer();
    }

    private void stopRefreshImage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                setupDataAndViews();
                Log.d(TAG, "AllCarWashersFragment: " + "information updated");
            }
        });
    }

    @Override
    public void onPause() {
        getRequest.cancelCall();
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.destroyDrawingCache();
            refreshLayout.clearAnimation();
        }
        super.onPause();
        Log.d(TAG, "AllCarWashersFragment: " + "onPause");
    }
}
