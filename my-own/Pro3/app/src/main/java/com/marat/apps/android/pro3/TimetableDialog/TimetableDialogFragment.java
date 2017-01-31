package com.marat.apps.android.pro3.TimetableDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.marat.apps.android.pro3.Adapters.TimetableListViewAdapter;
import com.marat.apps.android.pro3.R;

public class TimetableDialogFragment extends DialogFragment {

    private static String[][] box1 = new String[][]{ {"9:00", "true"}, {"9:15", "true"}, {"9:30", "false"}, {"9:45", "false"}, {"10:00", "false"}, {"10:15", "true"}, {"10:30", "true"}, {"10:45", "true"}, {"11:00", "true"}, {"11:15", "true"}, {"11:30", "true"}, };
    private static String[][] box2 = new String[][]{ {"9:00", "true"}, {"9:15", "true"}, {"9:30", "false"}, {"9:45", "false"}, {"10:00", "true"}};

    private int duration = 46;
    private boolean registrationTimeIsValid = false;
    private int chosenBoxNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timetable_dialog, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.container);
        TextView cancelTextView = (TextView) v.findViewById(R.id.cancelRegistrationTextView);
        TextView registerTextView = (TextView) v.findViewById(R.id.registerTextView);

        final BoxesPagerAdapter adapter = new BoxesPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registrationTimeIsValid) {
                    // register user for time here
                    dismiss();
                }
            }
        });
        return v;
    }

    public void updateRegistrationValidity(boolean validOrNot, int n) {
        registrationTimeIsValid = validOrNot;
        chosenBoxNumber = n;
    }

    public class BoxesPagerAdapter extends FragmentPagerAdapter {

        private Fragment contentFragment;

        BoxesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.v("TimetableDialogFragment", "getItem");
            contentFragment = DialogContentFragment.newInstance(position + 1, box1, duration);
            return contentFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
