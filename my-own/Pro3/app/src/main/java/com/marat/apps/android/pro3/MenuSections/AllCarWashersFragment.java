package com.marat.apps.android.pro3.MenuSections;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Adapters.WashingStationsRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.OnToolbarTitleChangeListener;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.UniversalGetRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class AllCarWashersFragment extends Fragment implements RequestResponseListener {

    private static final String TAG = "logtag";

    private static final String CITIES_AND_CAR_TYPES_URL = "https://propropro.herokuapp.com/api/v1/carwashes";

    private Context context;
    private Cursor data;
    private CWStationsDatabase db;

    private RecyclerView recyclerView;
    private WashingStationsRecyclerViewAdapter adapter;
    private TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AllCarWashersFragment: " + "onCreateView");
        View v = inflater.inflate(R.layout.fragment_car_washers, container, false);
        context = getContext();

        OnToolbarTitleChangeListener listener = (OnToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_all_stations_toolbar) + "Астана");

        recyclerView = (RecyclerView) v.findViewById(R.id.carWashersRecyclerView);
        emptyText = (TextView) v.findViewById(R.id.emptyTextView);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "AllCarWashersFragment: " + "onResume");

        db = new CWStationsDatabase(context);
        db.open();
        data = db.getAllStations();

        if (data.getCount() <= 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
            setAdapterToRecyclerView();
        }
        db.close();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        UniversalGetRequest getRequest = new UniversalGetRequest(getContext());
        getRequest.delegate = this;
        if (getRequest.isNetworkAvailable()) {
            getRequest.getUsingToken(CITIES_AND_CAR_TYPES_URL, "Authorization", "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    private void setAdapterToRecyclerView() {
        adapter = new WashingStationsRecyclerViewAdapter(data, context, db, "AllStations");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFailure(IOException e) {
        Toast.makeText(getContext(), getString(R.string.error_could_not_load_data), Toast.LENGTH_LONG).show();
        Log.d(TAG, "AllCarWashersFragment: " + "onFailure");
        e.printStackTrace();
    }

    @Override
    public void onResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "AllCarWashersFragment: " + "response message - " + responseMessage);

        try {
            String res = response.body().string();
            Log.d(TAG, "AllCarWashersFragment: " + "response body - " + res);
            JSONObject result = new JSONObject(res);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

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
}
