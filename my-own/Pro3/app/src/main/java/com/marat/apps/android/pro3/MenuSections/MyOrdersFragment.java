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

import com.marat.apps.android.pro3.Adapters.OrdersRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.Interfaces.ToolbarTitleChangeListener;
import com.marat.apps.android.pro3.R;

public class MyOrdersFragment extends Fragment {

    private Context context;
    private Cursor data;
    private CWStationsDatabase db;

    private RecyclerView ordersRecyclerView;
    private TextView ordersEmptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_orders, container, false);
        context = getContext();

        ToolbarTitleChangeListener listener = (ToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_user_orders));

        ordersRecyclerView = (RecyclerView) v.findViewById(R.id.moMyOrdersRecyclerView);
        ordersEmptyTextView = (TextView) v.findViewById(R.id.moEmptyTextView);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        ordersRecyclerView.setItemAnimator(itemAnimator);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        ordersRecyclerView.setLayoutManager(layoutManager);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        db = new CWStationsDatabase(context);
        db.open();
        data = db.getMyOrders();

        if (data.getCount() <= 0) {
            ordersRecyclerView.setVisibility(View.INVISIBLE);
            ordersEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            ordersRecyclerView.setVisibility(View.VISIBLE);
            ordersEmptyTextView.setVisibility(View.INVISIBLE);
            setAdapterToRecyclerView();
        }

        db.close();
    }

    private void setAdapterToRecyclerView() {
        OrdersRecyclerViewAdapter adapter = new OrdersRecyclerViewAdapter(data, context, db);
        ordersRecyclerView.setAdapter(adapter);
    }
}
