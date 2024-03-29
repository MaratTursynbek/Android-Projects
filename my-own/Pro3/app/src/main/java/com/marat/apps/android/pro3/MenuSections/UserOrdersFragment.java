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

import com.marat.apps.android.pro3.Adapters.OrdersRecyclerViewAdapter;
import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Interfaces.ToolbarTitleChangeListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.Models.Order;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Response;

public class UserOrdersFragment extends Fragment implements RequestResponseListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "UserOrdersFragment";

    private static final String ALL_USER_ORDERS_URL = "https://propropro.herokuapp.com/api/v1/orders";

    private RecyclerView ordersRecyclerView;
    private TextView ordersErrorTextView;
    private TextView ordersEmptyTextView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;

    private OrdersRecyclerViewAdapter adapter;

    private GetRequest getRequest;

    private ArrayList<Order> ordersArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_orders, container, false);

        ToolbarTitleChangeListener listener = (ToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_user_orders));

        ordersRecyclerView = (RecyclerView) v.findViewById(R.id.uoRecyclerView);
        ordersErrorTextView = (TextView) v.findViewById(R.id.uoErrorTextView);
        ordersEmptyTextView = (TextView) v.findViewById(R.id.uoNoOrdersTextView);
        progressBar = (ProgressBar) v.findViewById(R.id.uoProgressBar);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.uoSwipeToRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        ordersRecyclerView.setItemAnimator(itemAnimator);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ordersRecyclerView.setLayoutManager(layoutManager);

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        setVisibilityOfViews(4);
        getAllUserOrdersFromServer();

        return v;
    }

    private void setVisibilityOfViews(int status) {
        switch (status) {
            case 1:
                ordersRecyclerView.setVisibility(View.VISIBLE);
                ordersErrorTextView.setVisibility(View.GONE);
                ordersEmptyTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                break;
            case 2:
                ordersRecyclerView.setVisibility(View.INVISIBLE);
                ordersErrorTextView.setVisibility(View.VISIBLE);
                ordersEmptyTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                break;
            case 3:
                ordersRecyclerView.setVisibility(View.INVISIBLE);
                ordersErrorTextView.setVisibility(View.GONE);
                ordersEmptyTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                break;
            case 4:
                ordersRecyclerView.setVisibility(View.INVISIBLE);
                ordersErrorTextView.setVisibility(View.GONE);
                ordersEmptyTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getAllUserOrdersFromServer() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        if (getRequest.isNetworkAvailable()) {
            Log.d(TAG, "trying to download data");
            getRequest.getAllUserOrders(ALL_USER_ORDERS_URL, "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            stopRefreshImage(2);
        }
    }

    @Override
    public void onFailure(IOException e) {
        showErrorToast(getString(R.string.error_could_not_load_data));
        Log.d(TAG, "onFailure");
        e.printStackTrace();
        stopRefreshImage(2);
    }

    @Override
    public void onResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "response message - " + responseMessage);

        if (getString(R.string.server_response_ok).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "response body - " + res);
                JSONArray ordersArray = new JSONArray(res);
                processAllOrders(ordersArray);
                stopRefreshImage(1);
            } catch (IOException | JSONException | ParseException e) {
                e.printStackTrace();
                stopRefreshImage(2);
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
            stopRefreshImage(2);
        }
    }

    private void processAllOrders(JSONArray ordersJSONArray) throws JSONException, ParseException {
        ordersArrayList = new ArrayList<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
        Calendar date = Calendar.getInstance();
        String time;
        JSONObject orderJSON;
        for (int i = 0; i < ordersJSONArray.length(); i++) {
            Order order = new Order();
            orderJSON = ordersJSONArray.getJSONObject(i);

            order.setOrderID(orderJSON.getInt("id"));
            order.setCarWashName(orderJSON.getString("carwash_name"));
            order.setOrderCarType(orderJSON.getString("car_type"));
            order.setOrderServices(orderJSON.getString("service_name"));
            order.setOrderPrice(orderJSON.getString("price") + " тг.");

            time = orderJSON.getString("start_time");
            date.setTime(sdf1.parse(time.substring(0, time.length() - 1)));
            order.setOrderTime(sdf2.format(date.getTime()) + " в " + sdf3.format(date.getTime()));
            time = orderJSON.getString("end_time");
            date.setTime(sdf1.parse(time.substring(0, time.length() - 1)));

            int status = orderJSON.getInt("status");

            if (status == 1 && date.compareTo(Calendar.getInstance()) > 0) {
                order.setOrderStatus("Активный");
            } else {
                if (status == 1 && date.compareTo(Calendar.getInstance()) <= 0) {
                    order.setOrderStatus("Завершен");
                } else if (status == 2) {
                    order.setOrderStatus("Отменен");
                } else if (status == 3) {
                    order.setOrderStatus("Отказано");
                }
            }

            ordersArrayList.add(order);
        }
    }

    private void stopRefreshImage(final int status) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == 1) {
                    setupDataAndViews();
                } else if (status == 2) {
                    setVisibilityOfViews(2);
                }
                Log.d(TAG, "information updated");
            }
        });
    }

    private void setupDataAndViews() {
        if (ordersArrayList.size() <= 0) {
            setVisibilityOfViews(3);
        } else {
            setVisibilityOfViews(1);
            setAdapterToRecyclerView();
        }
    }

    private void setAdapterToRecyclerView() {
        if (adapter == null) {
            adapter = new OrdersRecyclerViewAdapter(getContext(), ordersArrayList);
            ordersRecyclerView.setAdapter(adapter);
        } else {
            adapter.updateCursor(ordersArrayList);
        }
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
        getAllUserOrdersFromServer();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        getRequest.cancelCall();
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.destroyDrawingCache();
            refreshLayout.clearAnimation();
        }
    }
}
