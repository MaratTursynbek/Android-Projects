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

import com.marat.apps.android.pro3.Adapters.AllCarWashesRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Databases.StoreToDatabaseHelper;
import com.marat.apps.android.pro3.Interfaces.ToolbarTitleChangeListener;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class AllCarWashesFragment extends Fragment implements RequestResponseListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "AllCarWashesFragment";

    private static final String ALL_CAR_WASHERS_URL = "https://propropro.herokuapp.com/api/v1/carwashes";

    private RecyclerView recyclerView;
    private TextView emptyText;
    private AllCarWashesRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;

    private Cursor cursor;
    private CarWashesDatabase db;

    private GetRequest getRequest;

    private ToolbarTitleChangeListener listener;

    private int currentCityId = 0, downloadedCityId;

    public static AllCarWashesFragment newInstance(int currentCityId, String currentCityName) {
        AllCarWashesFragment fragment = new AllCarWashesFragment();
        Bundle args = new Bundle();
        args.putInt("current_city_id", currentCityId);
        args.putString("current_city_name", currentCityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_car_washes_all, container, false);

        listener = (ToolbarTitleChangeListener) getActivity();

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

        db = new CarWashesDatabase(getContext());

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        currentCityId = getArguments().getInt("current_city_id");
        setCityOnToolbar(getArguments().getString("current_city_name"));

        db.open();
        cursor = db.getAllStations();
        cursor.moveToFirst();
        downloadedCityId = cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_CAR_WASH_CITY_ID));
        db.close();

        if (currentCityId == downloadedCityId) {
            Log.d(TAG, "ids are equal");
            setupDataAndViews();
        } else {
            Log.d(TAG, "new data is downloading");
            db.open();
            db.deleteAllStations();
            db.close();
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.GONE);
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
            progressBar.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            setAdapterToRecyclerView();
        }
        db.close();
    }

    private void setAdapterToRecyclerView() {
        if (adapter == null) {
            adapter = new AllCarWashesRecyclerViewAdapter(cursor, getContext(), db, "all_car_washes");
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateCursor(cursor);
        }
    }

    private void getAllCarWashesFromServer() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

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
        Log.d(TAG, "onFailure");
        e.printStackTrace();
        stopRefreshImage();
    }

    @Override
    public void onResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "response message - " + responseMessage);

        if (getString(R.string.server_response_ok).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "response body - " + res);
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
        helper.saveAllCarWashers(allCarWashers);
    }

    public void updateDataForCity(int cityId, String cityName) {
        Log.d(TAG, "updateDataForCity - " + cityId);
        currentCityId = cityId;
        setCityOnToolbar(cityName);
        db.open();
        db.deleteAllStations();
        db.close();
        recyclerView.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.GONE);
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
        Log.d(TAG, "onRefresh");
        getAllCarWashesFromServer();
    }

    private void stopRefreshImage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                setupDataAndViews();
                Log.d(TAG, "information updated");
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
        Log.d(TAG, "onPause");
    }
}
