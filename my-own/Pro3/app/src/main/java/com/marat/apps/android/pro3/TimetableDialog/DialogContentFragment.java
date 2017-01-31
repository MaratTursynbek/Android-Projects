package com.marat.apps.android.pro3.TimetableDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.marat.apps.android.pro3.Activities.CWStationDetailsActivity;
import com.marat.apps.android.pro3.Adapters.TimetableListViewAdapter;
import com.marat.apps.android.pro3.R;

public class DialogContentFragment extends Fragment {

    private static final String ARG_BOX_NUMBER = "box_number";
    private static final String ARG_BOX_TIME = "box_time";
    private static final String ARG_DURATION = "box_duration";

    private TimetableListViewAdapter adapter;
    private RegisterEventListener delegate;

    private ListView timetableListView;

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
        View v = inflater.inflate(R.layout.fragment_timetable_dialog_content, container, false);
        Log.v("DialogContentFragment", "onCreateView");
        timetableListView = (ListView) v.findViewById(R.id.timetableListView);
        adapter = new TimetableListViewAdapter(
                getContext(),
                (String[][]) getArguments().getSerializable(ARG_BOX_TIME),
                getArguments().getInt(ARG_DURATION)
        );
        timetableListView.setAdapter(adapter);
        timetableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("DialogContentFragment", "onItemClick");
                boolean isOk = adapter.setStartTime(position);
                delegate.registrationTimeIsChosen(isOk, getArguments().getInt(ARG_BOX_NUMBER));
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        delegate = (RegisterEventListener) context;
    }
}
