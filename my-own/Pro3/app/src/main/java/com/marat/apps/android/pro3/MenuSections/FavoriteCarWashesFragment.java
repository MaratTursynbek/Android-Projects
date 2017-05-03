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
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Adapters.FavoriteCarWashesRecyclerViewAdapter;
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

public class FavoriteCarWashesFragment extends Fragment implements RequestResponseListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FavoriteFragment";

    private static final String FAVORITE_CAR_WASHERS_URL = "https://propropro.herokuapp.com/api/v1/favorites/";

    private Cursor cursor;
    private CarWashesDatabase db;

    private GetRequest getRequest;

    private RecyclerView recyclerView;
    private TextView emptyText;
    private SwipeRefreshLayout refreshLayout;

    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_car_washers, container, false);

        ToolbarTitleChangeListener listener = (ToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_favorite_stations));

        recyclerView = (RecyclerView) v.findViewById(R.id.carWashersRecyclerView);
        emptyText = (TextView) v.findViewById(R.id.carWashersEmptyTextView);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.carWashersSwipeToRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        db = new CarWashesDatabase(getContext());

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        setupDataAndViews();

        return v;
    }

    private void setupDataAndViews() {
        db.open();
        Cursor userCursor = db.getUserInformation();
        userCursor.moveToFirst();
        userId = userCursor.getInt(userCursor.getColumnIndex(CarWashesDatabase.KEY_USER_ID));
        cursor = db.getFavoriteStations();

        if (cursor.getCount() <= 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            if (!db.userHasFavoriteStations()) {
                emptyText.setText(getString(R.string.text_no_favorite_car_washes));
            } else {
                emptyText.setText(getString(R.string.error_could_not_load_data));
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
            setAdapterToRecyclerView();
        }
        db.close();
    }

    private void setAdapterToRecyclerView() {
        FavoriteCarWashesRecyclerViewAdapter adapter = new FavoriteCarWashesRecyclerViewAdapter(cursor, getContext(), db, "favorite_car_washes");
        recyclerView.setAdapter(adapter);
    }

    private void getFavoriteCarWashesFromServer() {
        refreshLayout.setRefreshing(true);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        if (getRequest.isNetworkAvailable()) {
            getRequest.getFavoriteCarWashersForUserId(FAVORITE_CAR_WASHERS_URL + userId, "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
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
                JSONArray favoriteCarWashers = responseObject.getJSONArray("favorite_carwashes");
                saveUpdatedFavoriteCarWashersList(favoriteCarWashers);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        stopRefreshImage();
    }

    private void saveUpdatedFavoriteCarWashersList(JSONArray favoriteCarWashers) throws JSONException {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(getContext());
        helper.saveFavoriteCarWashers(favoriteCarWashers);
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
        Log.d(TAG, "onRefresh");
        getFavoriteCarWashesFromServer();
    }

    private void stopRefreshImage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
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
