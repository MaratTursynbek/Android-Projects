package com.marat.apps.android.pro3.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marat.apps.android.pro3.Interfaces.RegistrationSuccessfullyFinishedListener;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.UniversalGetRequest;
import com.marat.apps.android.pro3.Models.Box;
import com.marat.apps.android.pro3.Models.TimetableRow;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;

public class DialogFragmentTimetable extends DialogFragment implements View.OnClickListener, RequestResponseListener {

    private static final String TAG = "logtag";

    private static final String GET_CAR_WASH_SCHEDULES_URL = "https://propropro.herokuapp.com/api/v1/schedules/";

    private ViewPager viewPager;
    private TextView boxNumberTextView, cancelTextView, registerTextView, todayTextView, tomorrowTextView;
    private ImageView toLeftImageView, toRightImageView;
    private RelativeLayout todayTextViewLayout, tomorrowTextViewLayout, containerLayout;
    private ProgressBar progressBar;

    private RegistrationSuccessfullyFinishedListener regFinDelegate;

    private UniversalGetRequest getRequest;

    private String weekDay = "";
    private boolean registrationTimeIsValid = false;
    private int duration, chosenBoxId;
    private ArrayList<Box> boxes = new ArrayList<>();

    public static DialogFragmentTimetable newInstance(int carWashId, int duration) {
        DialogFragmentTimetable dialogFragmentTimetable = new DialogFragmentTimetable();
        Bundle args = new Bundle();
        args.putInt("car_wash_id", carWashId);
        args.putInt("duration", duration);
        dialogFragmentTimetable.setArguments(args);
        return dialogFragmentTimetable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_timetable, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.dialogTimeTableContainer);
        boxNumberTextView = (TextView) v.findViewById(R.id.dialogBoxNumberTextView);
        cancelTextView = (TextView) v.findViewById(R.id.dialogCancelRegistrationTextView);
        registerTextView = (TextView) v.findViewById(R.id.dialogRegisterTextView);
        todayTextView = (TextView) v.findViewById(R.id.dialogTodayTimeTextView);
        tomorrowTextView = (TextView) v.findViewById(R.id.dialogTomorrowTimeTextView);
        toLeftImageView = (ImageView) v.findViewById(R.id.dialogToLeftArrowImageView);
        toRightImageView = (ImageView) v.findViewById(R.id.dialogToRightArrowImageView);
        todayTextViewLayout = (RelativeLayout) v.findViewById(R.id.dialogTodayTextViewLayout);
        tomorrowTextViewLayout = (RelativeLayout) v.findViewById(R.id.dialogTomorrowTextViewLayout);
        containerLayout = (RelativeLayout) v.findViewById(R.id.dialogChooseTimeContainerLayout);
        progressBar = (ProgressBar) v.findViewById(R.id.dialogLoadingProgressBar);

        cancelTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        todayTextViewLayout.setOnClickListener(this);
        tomorrowTextViewLayout.setOnClickListener(this);
        toLeftImageView.setOnClickListener(this);
        toRightImageView.setOnClickListener(this);

        todayTextViewLayout.bringToFront();
        tomorrowTextViewLayout.bringToFront();

        toggleButtonsBackground(todayTextView, tomorrowTextView);

        getRequest = new UniversalGetRequest(getContext());
        getRequest.delegate = this;

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        Date d = new Date();
        weekDay = (sdf.format(d)).toLowerCase();
        Log.d(TAG, "ChooseTimeDialog: " + weekDay);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        duration = getArguments().getInt("duration");
        containerLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        getTimetableFromServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogCancelRegistrationTextView:
                dismiss();
                break;
            case R.id.dialogRegisterTextView:
                if (registrationTimeIsValid) {
                    Intent finishIntent = new Intent("finish_main_activity");
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(finishIntent);
                    regFinDelegate.registrationIsDone();
                    dismiss();
                }
                break;
            case R.id.dialogTodayTextViewLayout:
                toggleButtonsBackground(todayTextView, tomorrowTextView);
                break;
            case R.id.dialogTomorrowTextViewLayout:
                toggleButtonsBackground(tomorrowTextView, todayTextView);
                break;
            case R.id.dialogToLeftArrowImageView:
                turnPage(R.id.dialogToLeftArrowImageView);
                break;
            case R.id.dialogToRightArrowImageView:
                turnPage(R.id.dialogToRightArrowImageView);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        regFinDelegate = (RegistrationSuccessfullyFinishedListener) context;
    }

    private void toggleButtonsBackground(TextView textView1, TextView textView2) {
        textView1.setBackgroundResource(R.drawable.bg_chosen_day_text);
        textView1.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        textView2.setBackgroundResource(0);
        textView2.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    public void turnPage(int itemId) {
        int currentPage = viewPager.getCurrentItem();
        if (itemId == R.id.dialogToLeftArrowImageView) {
            currentPage--;
        } else if (itemId == R.id.dialogToRightArrowImageView) {
            currentPage++;
        }
        viewPager.setCurrentItem(currentPage, true);
        boxNumberTextView.setText("Box " + (viewPager.getCurrentItem() + 1));
    }

    public void updateRegistrationValidity(boolean validOrNot, int id) {
        registrationTimeIsValid = validOrNot;
        chosenBoxId = id;
    }

    ////////////////////////////////////////////////////////////////////////
    //                 downloading data from internet                     //
    ////////////////////////////////////////////////////////////////////////

    private void getTimetableFromServer() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if (getRequest.isNetworkAvailable()) {
            getRequest.getTimetableOfCarWash(GET_CAR_WASH_SCHEDULES_URL + getArguments().getInt("car_wash_id"), "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            stopRefreshImage();
        }
    }

    @Override
    public void onFailure(IOException e) {
        showErrorToast(getString(R.string.error_could_not_load_data));
        Log.d(TAG, "ChooseTimeDialog: " + "onFailure");
        e.printStackTrace();
        stopRefreshImage();
    }

    @Override
    public void onResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "ChooseTimeDialog: " + "response message - " + responseMessage);

        if (getString(R.string.server_response_car_wash_timetable_received).equals(responseMessage)) {
            try {
                String res = response.body().string();
                Log.d(TAG, "ChooseTimeDialog: " + "response body - " + res);
                JSONObject responseObject = new JSONObject(res);
                processTimetableData(responseObject);
                Log.d(TAG, "ChooseTimeDialog: " + "after processTimetableData");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            showErrorToast(getString(R.string.error_could_not_load_data));
        }
        stopRefreshImage();
    }

    private void processTimetableData(JSONObject carWashObject) throws JSONException {
        JSONArray schedulesObjects = carWashObject.getJSONObject("week").getJSONArray("schedules");
        String time = (schedulesObjects.getJSONObject(0).getString(weekDay));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
        Calendar c = Calendar.getInstance();
        Calendar cStart = (Calendar) c.clone();
        cStart.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)));
        cStart.set(Calendar.MINUTE, Integer.valueOf(time.substring(2, 4)));
        cStart.set(Calendar.SECOND, 0);
        cStart.set(Calendar.MILLISECOND, 0);

        if (cStart.compareTo(c) == -1) {
            if (duration == 30) {
                if (c.get(Calendar.MINUTE) < 30) {
                    c.set(Calendar.MINUTE, 30);
                } else if (c.get(Calendar.MINUTE) >= 30) {
                    c.add(Calendar.HOUR_OF_DAY, 1);
                    c.set(Calendar.MINUTE, 0);
                }
            } else {
                c.add(Calendar.HOUR_OF_DAY, 1);
                c.set(Calendar.MINUTE, 0);
            }
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        } else {
            c = (Calendar) cStart.clone();
        }

        Calendar cEnd = Calendar.getInstance();
        cEnd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(5, 7)));
        cEnd.set(Calendar.MINUTE, Integer.valueOf(time.substring(7, 9)));
        cEnd.set(Calendar.SECOND, 0);
        cEnd.set(Calendar.MILLISECOND, 0);

        Log.d(TAG, "ChooseTimeDialog: " + format.format(c.getTime()));
        Log.d(TAG, "ChooseTimeDialog: " + format.format(cEnd.getTime()));

        JSONArray boxesObjects = carWashObject.getJSONObject("tomorrow").getJSONArray("boxes");

        for (int i = 0; i < boxesObjects.length(); i++) {
            Calendar temp = (Calendar) c.clone();
            ArrayList<TimetableRow> timetableRows = new ArrayList<>();
            JSONArray onlineOrders = boxesObjects.getJSONObject(i).getJSONArray("orders");
            JSONArray offlineOrders = boxesObjects.getJSONObject(i).getJSONArray("offorders");
            for (int j = 0; temp.compareTo(cEnd) == -1; j++) {
                TimetableRow timetableRow = new TimetableRow();
                timetableRow.setTime(temp.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", temp.get(Calendar.MINUTE)));
                timetableRow.setAvailable(true);
                timetableRows.add(timetableRow);
                if (duration == 30) {
                    temp.add(Calendar.MINUTE, 30);
                } else {
                    temp.add(Calendar.HOUR_OF_DAY, 1);
                }
            }
            Box box = new Box();
            box.setBoxId(boxesObjects.getJSONObject(i).getInt("id"));
            box.setTimetableRows(timetableRows);
            boxes.add(box);
        }

        Log.d(TAG, "ChooseTimeDialog: " + "processing of data is completed");
    }

    private void showErrorToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void stopRefreshImage() {
        Log.d(TAG, "ChooseTimeDialog: " + "stopRefreshImage");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                containerLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                setupDataAndViews();
                Log.d(TAG, "ChooseTimeDialog: " + "information updated");
            }
        });
    }

    private void setupDataAndViews() {
        BoxesPagerAdapter adapter = new BoxesPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new OnPageChangeListener());
    }

    private class BoxesPagerAdapter extends FragmentPagerAdapter {

        BoxesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "ChooseTimeDialog: BoxesPagerAdapter - " + "getItem");
            return DialogFragmentTimetableContent.newInstance(boxes.get(position), duration);
        }

        @Override
        public int getCount() {
            return boxes.size();
        }
    }

    private class OnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            boxNumberTextView.setText("Box " + (position + 1));
        }
    }
}
