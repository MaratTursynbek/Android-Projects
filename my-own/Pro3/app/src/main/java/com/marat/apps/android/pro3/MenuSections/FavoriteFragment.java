package com.marat.apps.android.pro3.MenuSections;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.WashingStationsRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.OnToolbarTitleChangeListener;
import com.marat.apps.android.pro3.R;

public class FavoriteFragment extends Fragment {

    private Context context;
    private Cursor data;
    private CWStationsDatabase db;

    private RecyclerView recyclerView;
    private TextView emptyText;

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
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        db = new CWStationsDatabase(context);
        db.open();
        data = db.getFavoriteStations();

        if (data.getCount() <= 0) {
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
        WashingStationsRecyclerViewAdapter adapter = new WashingStationsRecyclerViewAdapter(data, context, db);
        recyclerView.setAdapter(adapter);
    }
}
