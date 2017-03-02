package com.marat.apps.android.pro3.MenuSections;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.CarWashersAllRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.OnToolbarTitleChangeListener;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private Cursor cursor;
    private CWStationsDatabase db;

    private RecyclerView recyclerView;
    private TextView emptyText;
    private CarWashersAllRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_car_washers, container, false);
        context = getContext();

        OnToolbarTitleChangeListener listener = (OnToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_favorite_stations));

        recyclerView = (RecyclerView) v.findViewById(R.id.carWashersRecyclerView);
        emptyText = (TextView) v.findViewById(R.id.emptyTextView);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.carWashersSwipeToRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        db = new CWStationsDatabase(getContext());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupDataAndViews();
    }

    private void setupDataAndViews() {
        ArrayList<Integer> cityIds = new ArrayList<>();

        db.open();
        Cursor cityCursor = db.getAllCities();
        for (cityCursor.moveToFirst(); !cityCursor.isAfterLast(); cityCursor.moveToNext()) {
            cityIds.add(cityCursor.getInt(cityCursor.getColumnIndex(CWStationsDatabase.KEY_CITY_ID)));
        }
        cityCursor.close();

        cursor = db.getFavoriteStations(cityIds);

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
            adapter = new CarWashersAllRecyclerViewAdapter(cursor, getContext(), db, "FavoriteStations");
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateCursor(cursor);
        }
    }

    private void stopRefreshImage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        stopRefreshImage();
    }
}
