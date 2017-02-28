package com.marat.apps.android.pro3.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Interfaces.RegistrationSuccessfullyFinishedListener;
import com.marat.apps.android.pro3.R;

public class DialogFragmentTimetable extends DialogFragment implements View.OnClickListener{

    private static String[][] box1 = new String[][]{{"9:00", "true"}, {"9:15", "true"}, {"9:30", "false"}, {"9:45", "false"}, {"10:00", "true"}, {"10:15", "true"}, {"10:30", "false"}, {"10:45", "false"}, {"11:00", "true"}, {"11:15", "true"}, {"11:30", "true"},};
    private static String[][] box2 = new String[][]{{"9:00", "false"}, {"9:15", "false"}, {"9:30", "false"}, {"9:45", "true"}, {"10:00", "true"}, {"10:15", "true"}, {"10:30", "true"}, {"10:45", "true"}, {"11:00", "false"}, {"11:15", "false"}, {"11:30", "true"},};

    private int duration = 26;
    private boolean registrationTimeIsValid = false;
    private int chosenBoxNumber;

    private ViewPager viewPager;
    private TextView boxNumberTextView, cancelTextView, registerTextView, todayTextView, tomorrowTextView;
    private ImageView toLeftImageView, toRightImageView;
    private RelativeLayout todayTextViewLayout, tomorrowTextViewLayout;

    private RegistrationSuccessfullyFinishedListener regFinDelegate;

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

        BoxesPagerAdapter adapter = new BoxesPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new OnPageChangeListener());

        cancelTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        todayTextViewLayout.setOnClickListener(this);
        tomorrowTextViewLayout.setOnClickListener(this);
        toLeftImageView.setOnClickListener(this);
        toRightImageView.setOnClickListener(this);

        todayTextViewLayout.bringToFront();
        tomorrowTextViewLayout.bringToFront();

        toggleButtonsBackground(todayTextView, tomorrowTextView);

        return v;
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

    public void updateRegistrationValidity(boolean validOrNot, int n) {
        registrationTimeIsValid = validOrNot;
        chosenBoxNumber = n;
    }

    private String[][] getTimetableBox(int i) {
        if (i == 0) {
            return box1;
        }
        return box2;
    }

    public class BoxesPagerAdapter extends FragmentPagerAdapter {

        BoxesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DialogFragmentTimetableContent.newInstance(position + 1, getTimetableBox(position), duration);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public class OnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            boxNumberTextView.setText("Box " + (position + 1));
        }
    }
}
