package com.marat.apps.android.pro3.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Activities.MainActivity;
import com.marat.apps.android.pro3.Adapters.TimetableListViewAdapter;
import com.marat.apps.android.pro3.Interfaces.RequestResponseListener;
import com.marat.apps.android.pro3.Internet.GetRequest;
import com.marat.apps.android.pro3.Internet.PostRequest;
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
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;

public class DialogFragmentTimetable extends DialogFragment implements View.OnClickListener, RequestResponseListener, AdapterView.OnItemClickListener {

    private static final String TAG = "logtag";

    private static final String GET_CAR_WASH_SCHEDULES_URL = "https://propropro.herokuapp.com/api/v1/schedules/";
    private static final String BOOK_TIME_SLOT_URL = "https://propropro.herokuapp.com/api/v1/orders/";

    private ListView timetableListView;
    private TextView todayTextView, tomorrowTextView, errorTextView, emptyTextView;
    private RelativeLayout containerLayout;
    private ProgressBar progressBar;
    private View containerView;

    private TimetableListViewAdapter adapter;

    private GetRequest getRequest;
    private PostRequest postRequest;

    private boolean timetableForToday = true;
    private boolean requestIsGet = false;
    private boolean registrationTimeIsValid = false;
    private int duration, chosenBoxId;
    private String weekDay = "";
    private String chosenTime = "";

    private ArrayList<TimetableRow> timetableRowsForToday = new ArrayList<>();
    private ArrayList<TimetableRow> timetableRowsForTomorrow = new ArrayList<>();

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
        containerView = inflater.inflate(R.layout.dialog_fragment_timetable, container, false);

        timetableListView = (ListView) containerView.findViewById(R.id.dialogTimetableListView);
        todayTextView = (TextView) containerView.findViewById(R.id.dialogTimetableTodayTimeTextView);
        tomorrowTextView = (TextView) containerView.findViewById(R.id.dialogTimetableTomorrowTimeTextView);
        containerLayout = (RelativeLayout) containerView.findViewById(R.id.dialogTimetableChooseTimeContainerLayout);
        progressBar = (ProgressBar) containerView.findViewById(R.id.dialogTimetableLoadingProgressBar);
        errorTextView = (TextView) containerView.findViewById(R.id.dialogTimetableErrorTextView);
        emptyTextView = (TextView) containerView.findViewById(R.id.dialogTimetableEmptyTextView);

        TextView cancelTextView = (TextView) containerView.findViewById(R.id.dialogTimetableCancelRegistrationTextView);
        TextView registerTextView = (TextView) containerView.findViewById(R.id.dialogTimetableRegisterTextView);
        RelativeLayout todayTextViewLayout = (RelativeLayout) containerView.findViewById(R.id.dialogTimetableTodayTextViewLayout);
        RelativeLayout tomorrowTextViewLayout = (RelativeLayout) containerView.findViewById(R.id.dialogTimetableTomorrowTextViewLayout);

        cancelTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        todayTextViewLayout.setOnClickListener(this);
        tomorrowTextViewLayout.setOnClickListener(this);

        todayTextViewLayout.bringToFront();
        tomorrowTextViewLayout.bringToFront();

        // initial setup
        toggleButtonsBackground(todayTextView, tomorrowTextView);

        getRequest = new GetRequest(getContext());
        getRequest.delegate = this;

        postRequest = new PostRequest(getContext());
        postRequest.delegate = this;

        sdfWeek = new SimpleDateFormat("EEEE", Locale.US);
        Date d = new Date();
        weekDay = (sdfWeek.format(d)).toLowerCase();
        Log.d(TAG, "ChooseTimeDialog: " + weekDay);

        return containerView;
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
            case R.id.dialogTimetableCancelRegistrationTextView:
                dismiss();
                break;
            case R.id.dialogTimetableRegisterTextView:
                if (registrationTimeIsValid) {
                    containerLayout.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    bookChosenTimeSlot();
                }
                break;
            case R.id.dialogTimetableTodayTextViewLayout:
                toggleButtonsBackground(todayTextView, tomorrowTextView);
                timetableForToday = true;
                registrationTimeIsValid = false;
                setupDataAndViews();
                break;
            case R.id.dialogTimetableTomorrowTextViewLayout:
                toggleButtonsBackground(tomorrowTextView, todayTextView);
                timetableForToday = false;
                registrationTimeIsValid = false;
                setupDataAndViews();
                break;
        }
    }

    private void toggleButtonsBackground(TextView textView1, TextView textView2) {
        textView1.setBackgroundResource(R.drawable.bg_chosen_day_text);
        textView1.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        textView2.setBackgroundResource(0);
        textView2.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    ////////////////////////////////////////////////////////////////////////
    //                 downloading data from internet                     //
    ////////////////////////////////////////////////////////////////////////

    private void getTimetableFromServer() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if (getRequest.isNetworkAvailable()) {
            requestIsGet = true;
            getRequest.getTimetableOfCarWash(GET_CAR_WASH_SCHEDULES_URL + getArguments().getInt("car_wash_id"), "Token token=\"" + token + "\"");
        } else {
            showErrorSnack(getString(R.string.error_no_internet_connection));
            setErrorTextViewVisible();
        }
    }

    private void bookChosenTimeSlot() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("carWashUserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("ACCESS_TOKEN", "");

        if (postRequest.isNetworkAvailable()) {
            requestIsGet = false;
            postRequest.bookTimeSlot(BOOK_TIME_SLOT_URL, getRegistrationDataAsJSON(), "Token token=\"" + token + "\"");
            Log.d(TAG, "ChooseTimeDialog: " + getRegistrationDataAsJSON());
        } else {
            showErrorSnack(getString(R.string.error_no_internet_connection));
            setErrorTextViewVisible();
            stopRefreshImageToRepeatRequest();
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

                "\"box_id\":" + "\"" + chosenBoxId + "\"" + "," +
                "\"price_id\":" + "\"" + getArguments().getInt("price_id") + "\"" + "," +
                "\"status\":" + "\"" + 1 + "\"" + "," +
                "\"start_time\":" + "\"" + sdf.format(s.getTime()) + "\"" + "," +
                "\"end_time\":" + "\"" + sdf.format(e.getTime()) + "\"" +

                "}}";
    }

    @Override
    public void onFailure(IOException e) {
        Log.d(TAG, "ChooseTimeDialog: " + "onFailure");
        e.printStackTrace();
        if (requestIsGet) {
            setErrorTextViewVisible();
        } else {
            stopRefreshImageToRepeatRequest();
        }
    }

    @Override
    public void onResponse(Response response) {
        String responseMessage = response.message();
        Log.d(TAG, "ChooseTimeDialog: " + "response message - " + responseMessage);

        if (getString(R.string.server_response_ok).equals(responseMessage) || getString(R.string.server_response_created).equals(responseMessage)) {
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
                            finishIntent = new Intent("finish_car_wash_details_activity");
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(finishIntent);

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra("start_page", "user_orders");
                            startActivity(intent);
                            dismiss();
                        }
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                setErrorTextViewVisible();
                return;
            }
        } else {
            if (requestIsGet) {
                setErrorTextViewVisible();
                return;
            } else {
                showErrorSnack(getString(R.string.error_could_not_load_data));
                stopRefreshImageToRepeatRequest();
                return;
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

        //preparing time slots for TODAY
        prepareTimeSlots(c, cEnd, timetableRowsForToday);

        // setting time slots for TODAY
        JSONArray boxesObjects = carWashObject.getJSONObject("today").getJSONArray("boxes");
        createTimetableForBoxes(boxesObjects, c, timetableRowsForToday);

        // setting time slots for TOMORROW
        weekDay = (sdfWeek.format(cStart.getTime())).toLowerCase();

        time = (schedulesObjects.getJSONObject(0).getString(weekDay));

        cStart.add(Calendar.DAY_OF_MONTH, 1);
        cStart.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, 2)));
        cStart.set(Calendar.MINUTE, Integer.valueOf(time.substring(2, 4)));
        cEnd = (Calendar) cStart.clone();

        if (Integer.valueOf(time.substring(0, 2)) >= Integer.valueOf(time.substring(5, 7))) {
            cEnd.add(Calendar.DAY_OF_MONTH, 1);
        }
        cEnd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(5, 7)));
        cEnd.set(Calendar.MINUTE, Integer.valueOf(time.substring(7, 9)));

        prepareTimeSlots(cStart, cEnd, timetableRowsForTomorrow);

        boxesObjects = carWashObject.getJSONObject("tomorrow").getJSONArray("boxes");
        createTimetableForBoxes(boxesObjects, cStart, timetableRowsForTomorrow);
    }

    private void prepareTimeSlots(Calendar cStart, Calendar cEnd, ArrayList<TimetableRow> timetableRows) {
        Calendar start = (Calendar) cStart.clone();

        TimetableRow timetableRow;

        while (start.compareTo(cEnd) < 0) {
            timetableRow = new TimetableRow();
            timetableRow.setTime(start.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", start.get(Calendar.MINUTE)));
            timetableRows.add(timetableRow);

            if (duration == 30) {
                start.add(Calendar.MINUTE, 30);
            } else {
                start.add(Calendar.HOUR_OF_DAY, 1);
            }
        }
    }

    private void createTimetableForBoxes(JSONArray boxesObjects, Calendar cStart, ArrayList<TimetableRow> timetableRows) throws JSONException {
        int length = boxesObjects.length();
        for (int i = 0; i < length; i++) {
            slotStartTime = (Calendar) cStart.clone();
            JSONArray onlineOrders = boxesObjects.getJSONObject(i).getJSONArray("orders");
            JSONArray offlineOrders = boxesObjects.getJSONObject(i).getJSONArray("offorders");
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

            for (int j = 0; j < timetableRows.size(); j++) {
                if (timetableRows.get(j).isAvailable()) {
                    addDurationToSlotStartTime();
                    continue;
                }
                String time = slotStartTime.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", slotStartTime.get(Calendar.MINUTE));
                if (!time.equals(timetableRows.get(j).getTime())) {
                    addDurationToSlotStartTime();
                    continue;
                }

                boolean available = true;

                if (!"".equals(startTime1) && !"".equals(endTime1)) {
                    if (!slotIsAvailable(startTime1, endTime1)) {
                        available = false;
                    }
                    while (slotStartTime.compareTo(orderEndTime) >= 0) {
                        on++;
                        if (on < onlineOrders.length()) {
                            startTime1 = onlineOrders.getJSONObject(on).getString("start_time");
                            endTime1 = onlineOrders.getJSONObject(on).getString("end_time");
                            if (!slotIsAvailable(startTime1, endTime1)) {
                                available = false;
                            }
                        } else {
                            startTime1 = "";
                            endTime1 = "";
                            break;
                        }
                    }
                }

                if (!"".equals(startTime2) && !"".equals(endTime2)) {
                    if (!slotIsAvailable(startTime2, endTime2)) {
                        available = false;
                    }
                    while (slotStartTime.compareTo(orderEndTime) >= 0) {
                        off++;
                        if (off < offlineOrders.length()) {
                            startTime2 = offlineOrders.getJSONObject(off).getString("start_time");
                            endTime2 = offlineOrders.getJSONObject(off).getString("end_time");
                            if (!slotIsAvailable(startTime2, endTime2)) {
                                available = false;
                            }
                        } else {
                            startTime2 = "";
                            endTime2 = "";
                            break;
                        }
                    }
                }

                if (available) {
                    timetableRows.get(j).setBoxId(boxesObjects.getJSONObject(i).getInt("id"));
                }

                addDurationToSlotStartTime();
            }
        }
    }

    private void addDurationToSlotStartTime() {
        if (duration == 30) {
            slotStartTime.add(Calendar.MINUTE, 30);
        } else {
            slotStartTime.add(Calendar.HOUR_OF_DAY, 1);
        }
    }

    private boolean slotIsAvailable(String startTime, String endTime) {
        orderStartTime = Calendar.getInstance();
        orderEndTime = Calendar.getInstance();

        try {
            orderStartTime.setTime(sdf.parse(startTime.substring(0, startTime.length() - 1)));
            orderStartTime.set(Calendar.MILLISECOND, 0);
            orderEndTime.setTime(sdf.parse(endTime.substring(0, endTime.length() - 1)));
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

    private void showErrorSnack(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                Snackbar snackbar = Snackbar.make(containerView, message, Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
                snackbar.show();
            }
        });
    }

    private void setErrorTextViewVisible() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                containerLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                emptyTextView.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void stopRefreshImageToRepeatRequest() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                containerLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                emptyTextView.setVisibility(View.GONE);
                errorTextView.setVisibility(View.GONE);
            }
        });
    }

    private void stopRefreshImage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                containerLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                errorTextView.setVisibility(View.GONE);
                setupDataAndViews();
            }
        });
    }

    private void setupDataAndViews() {
        if (timetableForToday) {
            adapter = new TimetableListViewAdapter(getContext(), timetableRowsForToday);
            if (timetableRowsForToday.size() == 0) {
                emptyTextView.setText(R.string.text_no_time_slots_for_today);
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                emptyTextView.setVisibility(View.GONE);
            }
        } else {
            adapter = new TimetableListViewAdapter(getContext(), timetableRowsForTomorrow);
            if (timetableRowsForTomorrow.size() == 0) {
                emptyTextView.setText(R.string.text_no_time_slots_for_tomorrow);
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                emptyTextView.setVisibility(View.GONE);
            }
        }
        timetableListView.setAdapter(adapter);
        timetableListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        registrationTimeIsValid = adapter.setAndCheckTimeSlot(position);
        if (registrationTimeIsValid) {
            if (timetableForToday) {
                chosenBoxId = timetableRowsForToday.get(position).getBoxId();
            } else {
                chosenBoxId = timetableRowsForTomorrow.get(position).getBoxId();
            }
            chosenTime = adapter.getChosenTimeSlot();
        }
    }
}
