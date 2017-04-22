package com.marat.apps.android.pro3.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import com.marat.apps.android.pro3.Internet.UniversalPostRequest;
import com.marat.apps.android.pro3.Models.Box;
import com.marat.apps.android.pro3.Models.TimetableRow;
import com.marat.apps.android.pro3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;

public class DialogFragmentTimetable extends DialogFragment implements View.OnClickListener, RequestResponseListener {

    private static final String TAG = "logtag";

    private static final String CAR_WASH_SCHEDULES_URL = "https://propropro.herokuapp.com/api/v1/schedules/";

    private ViewPager viewPager;
    private TextView boxNumberTextView, cancelTextView, registerTextView, todayTextView, tomorrowTextView, emptyTextView;
    private ImageView toLeftImageView, toRightImageView;
    private RelativeLayout todayTextViewLayout, tomorrowTextViewLayout, containerLayout;
    private ProgressBar progressBar;

    private BoxesPagerAdapter adapter;

    private RegistrationSuccessfullyFinishedListener regFinDelegate;

    private UniversalGetRequest getRequest;
    private UniversalPostRequest postRequest;

    private boolean timetableForToday = true;
    private boolean requestIsGet = false;
    private boolean registrationTimeIsValid = false;
    private int duration, chosenBoxId;
    private String weekDay = "";
    private String chosenTime = "";

    private ArrayList<Box> boxesForToday = new ArrayList<>();
    private ArrayList<Box> boxesForTomorrow = new ArrayList<>();

    private SimpleDateFormat sdfForSending = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private SimpleDateFormat sdfWeek;

    private Calendar orderStartTime, slotStartTime;
    private Calendar orderEndTime, slotEndTime;

    public static DialogFragmentTimetable newInstance(int carWashId, int priceId, int duration) {
        DialogFragmentTimetable dialogFragmentTimetable = new DialogFragmentTimetable();
        Bundle args = new Bundle();
        args.putInt("car_wash_id", carWashId);
        args.putInt("duration", duration);
        args.putInt("price_id", priceId);
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
        emptyTextView = (TextView) v.findViewById(R.id.dialogTimeTableEmptyTextView);
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

        postRequest = new UniversalPostRequest(getContext());
        postRequest.delegate = this;

        sdfWeek = new SimpleDateFormat("EEEE", Locale.US);
        Date d = new Date();
        weekDay = (sdfWeek.format(d)).toLowerCase();
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
                    bookChosenTimeSlot();
                }
                break;
            case R.id.dialogTodayTextViewLayout:
                toggleButtonsBackground(todayTextView, tomorrowTextView);
                timetableForToday = true;
                adapter.updateBoxes();
                break;
            case R.id.dialogTomorrowTextViewLayout:
                toggleButtonsBackground(tomorrowTextView, todayTextView);
                timetableForToday = false;
                adapter.updateBoxes();
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

    public void updateRegistrationValidity(boolean validOrNot, int id, String time) {
        registrationTimeIsValid = validOrNot;
        chosenBoxId = id;
        chosenTime = time;
    }

    ////////////////////////////////////////////////////////////////////////
    //                 downloading data from internet                     //
    ////////////////////////////////////////////////////////////////////////

    private void getTimetableFromServer() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if (getRequest.isNetworkAvailable()) {
            requestIsGet = true;
            getRequest.getTimetableOfCarWash(CAR_WASH_SCHEDULES_URL + getArguments().getInt("car_wash_id"), "Token token=\"" + token + "\"");
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            stopRefreshImage();
        }
    }

    private void bookChosenTimeSlot() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if (postRequest.isNetworkAvailable()) {
            requestIsGet = false;
            postRequest.bookTimeSlot(CAR_WASH_SCHEDULES_URL, getRegistrationDataAsJSON(), "Token token=\"" + token + "\"");
            Log.d(TAG, "ChooseTimeDialog: " + getRegistrationDataAsJSON());
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
            stopRefreshImage();
        }
    }

    private String getRegistrationDataAsJSON() {
        Calendar s = Calendar.getInstance();
        s.set(Calendar.HOUR_OF_DAY, Integer.valueOf(chosenTime.substring(0, 2)));
        s.set(Calendar.MINUTE, Integer.valueOf(chosenTime.substring(3)));
        s.set(Calendar.SECOND, 0);
        s.set(Calendar.MILLISECOND, 0);
        if (!timetableForToday) {
            s.add(Calendar.DAY_OF_MONTH, 1);
        }
        Calendar e = (Calendar) s.clone();
        if (duration == 30) {
            e.add(Calendar.MINUTE, 30);
        } else {
            e.add(Calendar.HOUR_OF_DAY, 1);
        }

        return "{\"order\":{" +

                "\"box_id\":"           +    "\""    +    chosenBoxId                           +    "\""    +    ","    +
                "\"price_id\":"         +    "\""    +    getArguments().getInt("price_id")     +    "\""    +    ","    +
                "\"status\":"           +    "\""    +    1                                     +    "\""    +    ","    +
                "\"start_time\":"       +    "\""    +    sdfForSending.format(s.getTime())               +    "\""    +    ","    +
                "\"end_time\":"         +    "\""    +    sdfForSending.format(e.getTime())               +    "\""    +

                "}}";
    }

    @Override
    public void onFailure(IOException e) {
        //showErrorToast(getString(R.string.error_could_not_load_data));
        Log.d(TAG, "ChooseTimeDialog: " + "onFailure");
        e.printStackTrace();
        if (requestIsGet) {
            setEmptyTextViewVisible();
        } else {
            stopRefreshImage();
        }
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
                if (requestIsGet) {
                    processTimetableData(responseObject);
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent finishIntent = new Intent("finish_main_activity");
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(finishIntent);
                            regFinDelegate.registrationIsDone();
                            dismiss();
                        }
                    });
                }
                Log.d(TAG, "ChooseTimeDialog: " + "after processTimetableData");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (requestIsGet) {
                setEmptyTextViewVisible();
                return;
            } else {
                showErrorToast(getString(R.string.error_could_not_load_data));
            }
        }
        stopRefreshImage();
    }

    private void processTimetableData(JSONObject carWashObject) throws JSONException {
        JSONArray schedulesObjects = carWashObject.getJSONObject("week").getJSONArray("schedules");
        String time = (schedulesObjects.getJSONObject(0).getString(weekDay));

        Calendar c = Calendar.getInstance();
        Calendar cStart = (Calendar) c.clone();
        cStart.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)));
        cStart.set(Calendar.MINUTE, Integer.valueOf(time.substring(2, 4)));
        cStart.set(Calendar.SECOND, 0);
        cStart.set(Calendar.MILLISECOND, 0);

        if (c.compareTo(cStart) > 0) {
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

        Calendar cEnd = (Calendar) cStart.clone();
        if (Integer.valueOf(time.substring(0, 2)) > Integer.valueOf(time.substring(5, 7))) {
            cEnd.add(Calendar.DAY_OF_MONTH, 1);
        }
        cEnd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(5, 7)));
        cEnd.set(Calendar.MINUTE, Integer.valueOf(time.substring(7, 9)));

        Log.d(TAG, "ChooseTimeDialog: " + sdfForSending.format(c.getTime()));
        Log.d(TAG, "ChooseTimeDialog: " + sdfForSending.format(cEnd.getTime()));

        // setting boxes for TODAY
        JSONArray boxesObjects = carWashObject.getJSONObject("today").getJSONArray("boxes");
        createTimetableForBoxes(boxesObjects, c, cEnd, boxesForToday);

        // setting boxes for TOMORROW
        cStart.add(Calendar.DAY_OF_MONTH, 1);
        cEnd.add(Calendar.DAY_OF_MONTH, 1);
        weekDay = (sdfWeek.format(cStart.getTime())).toLowerCase();
        Log.d(TAG, "ChooseTimeDialog: " + weekDay);

        time = (schedulesObjects.getJSONObject(0).getString(weekDay));

        cStart.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)));
        cStart.set(Calendar.MINUTE, Integer.valueOf(time.substring(2, 4)));

        if (Integer.valueOf(time.substring(0, 2)) >= Integer.valueOf(time.substring(5, 7))) {
            cEnd.add(Calendar.DAY_OF_MONTH, 1);
        }
        cEnd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(5, 7)));
        cEnd.set(Calendar.MINUTE, Integer.valueOf(time.substring(7, 9)));

        boxesObjects = carWashObject.getJSONObject("tomorrow").getJSONArray("boxes");
        createTimetableForBoxes(boxesObjects, cStart, cEnd, boxesForTomorrow);

        Log.d(TAG, "ChooseTimeDialog: " + "processing of data is completed");
    }

    private void createTimetableForBoxes(JSONArray boxesObjects, Calendar cStart, Calendar cEnd, ArrayList<Box> boxes) throws JSONException {
        int length = boxesObjects.length();
        for (int i = 0; i < length; i++) {
            slotStartTime = (Calendar) cStart.clone();
            ArrayList<TimetableRow> timetableRows = new ArrayList<>();
            JSONArray onlineOrders = sortOrders(boxesObjects.getJSONObject(i).getJSONArray("orders"));
            JSONArray offlineOrders = sortOrders(boxesObjects.getJSONObject(i).getJSONArray("offorders"));
            int on = 0;
            int off = 0;

            String startTime1 = "", endTime1 = "", startTime2 = "", endTime2 = "";

            if (on < onlineOrders.length()) {
                startTime1 = onlineOrders.getJSONObject(on).getString("start_time");
                endTime1 = onlineOrders.getJSONObject(on).getString("end_time");
            }

            if (off < offlineOrders.length()) {
                startTime2 = offlineOrders.getJSONObject(off).getString("start_time");
                endTime2 = offlineOrders.getJSONObject(off).getString("end_time");
            }

            TimetableRow timetableRow;

            while (slotStartTime.compareTo(cEnd) < 0) {
                timetableRow = new TimetableRow();
                timetableRow.setTime(slotStartTime.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", slotStartTime.get(Calendar.MINUTE)));
                timetableRow.setAvailable(true);

                if (!"".equals(startTime1) && !"".equals(endTime1)) {
                    if (!slotIsAvailable(startTime1, endTime1)) {
                        timetableRow.setAvailable(false);
                    }
                    if (slotStartTime.compareTo(orderEndTime) >= 0) {
                        on++;
                        if (on < onlineOrders.length()) {
                            startTime1 = onlineOrders.getJSONObject(on).getString("start_time");
                            endTime1 = onlineOrders.getJSONObject(on).getString("end_time");
                            if (!slotIsAvailable(startTime1, endTime1)) {
                                timetableRow.setAvailable(false);
                            }
                        } else {
                            startTime1 = "";
                            endTime1 = "";
                        }
                    }
                }

                if (!"".equals(startTime2) && !"".equals(endTime2)) {
                    if (!slotIsAvailable(startTime2, endTime2)) {
                        timetableRow.setAvailable(false);
                    }
                    if (slotStartTime.compareTo(orderEndTime) >= 0) {
                        off++;
                        if (off < offlineOrders.length()) {
                            startTime2 = offlineOrders.getJSONObject(off).getString("start_time");
                            endTime2 = offlineOrders.getJSONObject(off).getString("end_time");
                            if (!slotIsAvailable(startTime2, endTime2)) {
                                timetableRow.setAvailable(false);
                            }
                        } else {
                            startTime2 = "";
                            endTime2 = "";
                        }
                    }
                }

                timetableRows.add(timetableRow);
                if (duration == 30) {
                    slotStartTime.add(Calendar.MINUTE, 30);
                } else {
                    slotStartTime.add(Calendar.HOUR_OF_DAY, 1);
                }
            }
            Box box = new Box();
            box.setBoxId(boxesObjects.getJSONObject(i).getInt("id"));
            box.setTimetableRows(timetableRows);
            boxes.add(box);
        }
    }

    private boolean slotIsAvailable(String startTime, String endTime) {
        orderStartTime = Calendar.getInstance();
        orderEndTime = Calendar.getInstance();

        Log.d(TAG, "ChooseTimeDialog: " + startTime);
        Log.d(TAG, "ChooseTimeDialog: " + endTime);

        try {
            orderStartTime.setTime(sdf.parse(startTime.substring(0, startTime.length()-1)));
            orderStartTime.set(Calendar.MILLISECOND, 0);
            orderEndTime.setTime(sdf.parse(endTime.substring(0, endTime.length()-1)));
            orderEndTime.set(Calendar.MILLISECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        slotEndTime = (Calendar) slotStartTime.clone();
        if (duration == 30) {
            slotEndTime.add(Calendar.MINUTE, 30);
        } else {
            slotEndTime.add(Calendar.HOUR_OF_DAY, 1);
        }

        if (orderStartTime.compareTo(slotStartTime) >= 0 && orderStartTime.compareTo(slotEndTime) < 0) {
            return false;
        } else if (orderEndTime.compareTo(slotStartTime) > 0 && orderEndTime.compareTo(slotEndTime) <= 0) {
            return false;
        } else if (slotStartTime.compareTo(orderStartTime) > 0 && slotEndTime.compareTo(orderEndTime) <= 0) {
            return false;
        }

        return true;
    }

    private JSONArray sortOrders(JSONArray orders) throws JSONException {
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < orders.length(); i++) {
            list.add(orders.getJSONObject(i));
        }

        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                String time1 = "";
                String time2 = "";

                try {
                    time1 = o1.getString("start_time");
                    time2 = o2.getString("start_time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return time1.compareTo(time2);
            }
        });

        JSONArray sortedOrders = new JSONArray();
        for (int i = 0; i< orders.length(); i++) {
            sortedOrders.put(list.get(i));
        }

        return sortedOrders;
    }

    private void showErrorToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setEmptyTextViewVisible() {
        Log.d(TAG, "ChooseTimeDialog: " + "setEmptyTextViewVisible");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                containerLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                emptyTextView.setVisibility(View.VISIBLE);
                Log.d(TAG, "ChooseTimeDialog: " + "information updated");
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
                emptyTextView.setVisibility(View.GONE);
                setupDataAndViews();
                Log.d(TAG, "ChooseTimeDialog: " + "information updated");
            }
        });
    }

    private void setupDataAndViews() {
        Log.d(TAG, "ChooseTimeDialog: " + "setupDataAndViews");
        if (timetableForToday) {
            adapter = new BoxesPagerAdapter(getChildFragmentManager(), boxesForToday);
        } else {
            adapter = new BoxesPagerAdapter(getChildFragmentManager(), boxesForTomorrow);
        }
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new OnPageChangeListener());
    }

    private class BoxesPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Box> boxes;

        BoxesPagerAdapter(FragmentManager fm, ArrayList<Box> boxes) {
            super(fm);
            this.boxes = boxes;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "ChooseTimeDialog: BoxesPagerAdapter - " + "getItem");
            return DialogFragmentTimetableContent.newInstance(boxes.get(position));
        }

        @Override
        public int getCount() {
            return boxes.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        void updateBoxes() {
            if (timetableForToday) {
                boxes = boxesForToday;
            } else {
                boxes = boxesForTomorrow;
            }
            notifyDataSetChanged();
        }
    }

    private class OnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            boxNumberTextView.setText("Box " + (position + 1));
        }
    }
}
