package com.marat.apps.android.pro3.MenuSections;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    OnToolbarTitleChangeListener listener;

    private RecyclerView recyclerView;
    private TextView emptyText;
    private CarWashersAllRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    int currentCityId = 0;

    public static AllCarWashersFragment newInstance(int currentCityId, int userCityId, String userCityName) {
        AllCarWashersFragment fragment = new AllCarWashersFragment();
        Bundle args = new Bundle();
        args.putInt("current_city_id", currentCityId);
        args.putInt("user_city_id", userCityId);
        args.putString("user_city_name", userCityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AllCarWashersFragment: " + "onCreateView");
        View v = inflater.inflate(R.layout.fragment_car_washers, container, false);

        listener = (OnToolbarTitleChangeListener) getActivity();

        recyclerView = (RecyclerView) v.findViewById(R.id.carWashersRecyclerView);
        emptyText = (TextView) v.findViewById(R.id.emptyTextView);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.carWashersSwipeToRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        db = new CWStationsDatabase(getContext());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "AllCarWashersFragment: " + "onResume");
        currentCityId = getArguments().getInt("current_city_id");
        int userCityId = getArguments().getInt("user_city_id");
        listener.onTitleChanged(getString(R.string.title_main_fragment_all_stations_toolbar) + getArguments().getString("user_city_name"));
        if (currentCityId == userCityId) {
            Log.d(TAG, "AllCarWashersFragment: " + "ids are equal");
            setupDataAndViews();
        } else {
            Log.d(TAG, "AllCarWashersFragment: " + "new data is downloading");
            getAllCarWashesFromServer();
        }
    }

    private void setupDataAndViews() {
        db.open();
        cursor = db.getAllStations();

        if (cursor.getCount() <= 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
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
        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        UniversalGetRequest getRequest = new UniversalGetRequest(getContext());
        getRequest.delegate = this;
        if (getRequest.isNetworkAvailable()) {
            getRequest.getCarWashersForCityId(ALL_CAR_WASHERS_URL, "Token token=\"" + token + "\"", currentCityId);
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
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
            try {
                String res = response.body().string();
                Log.d(TAG, "AllCarWashersFragment: " + "response body - " + res);
                JSONObject responseObject = new JSONObject(res);
                JSONArray allCarWashers = responseObject.getJSONArray("city_carwashes");
                saveUpdatedCarWashersList(allCarWashers);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        stopRefreshImage();
    }

    private void saveUpdatedCarWashersList(JSONArray allCarWashers) throws JSONException {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(getContext());
        helper.saveAllCarWashers(allCarWashers);
    }

    public void updateDataForCity(int cityId, String cityName) {
        Log.d(TAG, "AllCarWashersFragment: " + "updateDataForCity - " + cityId);
        currentCityId = cityId;
        listener.onTitleChanged(getString(R.string.title_main_fragment_all_stations_toolbar) + cityName);
        getAllCarWashesFromServer();
    }

    private void stopRefreshImage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                setupDataAndViews();
                Log.d(TAG, "AllCarWashersFragment: " + "information updated");
            }
        });
    }

    private void showErrorToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "AllCarWashersFragment: " + "onRefresh");
        getAllCarWashesFromServer();
    }


    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// END /////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "AllCarWashersFragment: " + "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AllCarWashersFragment: " + "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "AllCarWashersFragment: " + "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "AllCarWashersFragment: " + "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "AllCarWashersFragment: " + "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "AllCarWashersFragment: " + "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "AllCarWashersFragment: " + "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AllCarWashersFragment: " + "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "AllCarWashersFragment: " + "onDetach");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "AllCarWashersFragment: " + "onSaveInstanceState");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "AllCarWashersFragment: " + "onSaveInstanceState");
    }
}
