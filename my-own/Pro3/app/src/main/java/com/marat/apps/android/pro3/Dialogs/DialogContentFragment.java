package com.marat.apps.android.pro3.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.marat.apps.android.pro3.Adapters.TimetableListViewAdapter;
import com.marat.apps.android.pro3.Interfaces.RegistrationTimeChosenListener;
import com.marat.apps.android.pro3.R;

public class DialogContentFragment extends Fragment {

    private static final String ARG_BOX_NUMBER = "box_number";
    private static final String ARG_BOX_TIME = "box_time";
    private static final String ARG_DURATION = "box_duration";

    private TimetableListViewAdapter adapter;
    private RegistrationTimeChosenListener regTimeChosenDelegate;

    private ListView timetableListView;
    private ProgressBar progressBar;

    public static DialogContentFragment newInstance(int n, String[][] data, int d) {
        DialogContentFragment fragment = new DialogContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BOX_NUMBER, n);
        args.putSerializable(ARG_BOX_TIME, data);
        args.putInt(ARG_DURATION, d);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("DialogContentFragment", "onCreateView");
        View v = inflater.inflate(R.layout.dialog_fragment_timetable_content, container, false);
        timetableListView = (ListView) v.findViewById(R.id.timetableListView);
        progressBar = (ProgressBar) v.findViewById(R.id.timetableLoadingProgressBar);

        timetableListView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timetableListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 1500);

        adapter = new TimetableListViewAdapter(
                getContext(),
                (String[][]) getArguments().getSerializable(ARG_BOX_TIME),
                getArguments().getInt(ARG_DURATION)
        );
        timetableListView.setAdapter(adapter);
        timetableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isOk = adapter.setStartTime(position);
                regTimeChosenDelegate.registrationTimeIsChosen(isOk, getArguments().getInt(ARG_BOX_NUMBER));
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        regTimeChosenDelegate = (RegistrationTimeChosenListener) context;
    }
}
