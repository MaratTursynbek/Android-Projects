package com.marat.apps.android.pro3.MenuSections;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Databases.CarWashesDatabase;
import com.marat.apps.android.pro3.Databases.StoreToDatabaseHelper;
import com.marat.apps.android.pro3.Interfaces.ToolbarTitleChangeListener;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class AccountFragment extends Fragment implements RequestResponseListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "logtag";

    private static final String GET_USER_URL = "https://propropro.herokuapp.com/api/v1/users/";
    private int userId;

    private CarWashesDatabase db;
    private Cursor cursor;

    private GetRequest getRequest;

    private TextView userNameTextView;
    private TextView userPhoneNumberTextView;
    private TextView userCarTypeTextView;
    private TextView userCityTextView;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AccountFragment: " + "onCreateView");
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        userNameTextView = (TextView) v.findViewById(R.id.fhNameTextView);
        userPhoneNumberTextView = (TextView) v.findViewById(R.id.fhPhoneNumberTextView);
        userCarTypeTextView = (TextView) v.findViewById(R.id.fhCarTypeTextView);
        userCityTextView = (TextView) v.findViewById(R.id.fhCityTypeTextView);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fhSwipeToRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

        ToolbarTitleChangeListener listener = (ToolbarTitleChangeListener) getActivity();
        listener.onTitleChanged(getString(R.string.title_main_fragment_account));

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "AccountFragment: " + "onResume");
        setUserInformationOnLayout();
    }

    private void setUserInformationOnLayout() {
        db = new CarWashesDatabase(getContext());
        db.open();
        cursor = db.getUserInformation();
        cursor.moveToFirst();
        userId = cursor.getInt(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_ID));
        userNameTextView.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_NAME)));
        String phoneNumber = cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_PHONE_NUMBER));
        phoneNumber = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 8) + "-" + phoneNumber.substring(8);
        userPhoneNumberTextView.setText(phoneNumber);
        userCarTypeTextView.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_CAR_TYPE_NAME)));
        userCityTextView.setText(cursor.getString(cursor.getColumnIndex(CarWashesDatabase.KEY_USER_CITY_NAME)));
        db.close();
    }

    private void getUserInfoFromServer() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        if (getRequest.isNetworkAvailable()) {
            getRequest.getUserInfo(GET_USER_URL + userId, "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            stopRefreshImage();
        }
    }

    @Override
    public void onFailure(IOException e) {
        Log.d(TAG, "AccountFragment: " + "onFailure");
        showErrorToast(getString(R.string.error_could_not_load_data));
        e.printStackTrace();
        stopRefreshImage();
    }

    @Override
    public void onResponse(Response response) {
        Log.d(TAG, "AccountFragment: " + "onResponse");
        String responseMessage = response.message();
        Log.d(TAG, "AccountFragment: " + "GetUserMessage: " + responseMessage);

        if (getString(R.string.server_response_ok).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "AccountFragment: " + "GetUserResponse: " + res);
                JSONObject result = new JSONObject(res);
                JSONObject userObject = result.getJSONObject("user");
                saveNewUserData(userObject);
            } catch (IOException | JSONException e) {
                showErrorToast(getString(R.string.error_could_not_load_data));
                e.printStackTrace();
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        stopRefreshImage();
    }

    private void saveNewUserData(JSONObject userObject) {
        StoreToDatabaseHelper helper = new StoreToDatabaseHelper(getContext());
        if (helper.saveNewUserData(userObject)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUserInformationOnLayout();
                    Log.d(TAG, "AccountFragment: " + "information updated");
                }
            });
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
        Log.d(TAG, "AccountFragment: " + "onRefresh");
        getUserInfoFromServer();
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
        Log.d(TAG, "AccountFragment: " + "onPause");
    }
}
