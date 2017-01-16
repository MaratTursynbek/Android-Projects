package com.example.marat.days.MainTabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marat.days.Activities.EditSavedEventActivity;
import com.example.marat.days.DB.DaysDatabase;
import com.example.marat.days.DB.SinceAdapter;
import com.example.marat.days.DB.UntilAdapter;
import com.example.marat.days.R;

import java.util.Arrays;
import java.util.Comparator;

public class UntilTabView extends Fragment {

    Context ourContext;

    ListView mList;
    TextView mEmptyText;

    String mDays[][];
    String items[] = {"Edit", "Delete"};

    UntilAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v3 = inflater.inflate(R.layout.list_view_content, container, false);

        mList = (ListView) v3.findViewById(android.R.id.list);
        mEmptyText = (TextView) v3.findViewById(android.R.id.empty);
        ourContext = getActivity();

        return v3;
    }

    @Override
    public void onResume() {
        super.onResume();
        showList();
    }

    public void showList() {
        DaysDatabase info = new DaysDatabase(ourContext);
        info.open();
        mDays = info.getUntil();
        info.close();

        Arrays.sort(mDays, new Comparator<String[]>() {
            @Override
            public int compare(final String[] entry1, final String[] entry2) {
                final String time1 = entry1[3];
                final String time2 = entry2[3];
                return time1.compareTo(time2);
            }
        });

        adapter = new UntilAdapter(ourContext, mDays);
        mList.setAdapter(adapter);
        mList.setEmptyView(mEmptyText);
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(Long.parseLong(mDays[position][0]));
                return false;
            }
        });
    }

    public void showOptionsDialog(final long pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ourContext);
        builder.setCancelable(true);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ourContext, android.R.layout.simple_list_item_1, items);
        ListView lv = new ListView(ourContext);
        lv.setAdapter(adapter2);

        builder.setView(lv);
        final AlertDialog alert = builder.create();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(ourContext, EditSavedEventActivity.class);
                    intent.putExtra("EventPosition", pos);
                    ourContext.startActivity(intent);
                    alert.dismiss();
                }
                else if (position == 1) {
                    alert.dismiss();
                    showConfirmationDialog(pos);
                }
            }
        });

        alert.show();
    }

    private void showConfirmationDialog(final long p) {
        final DaysDatabase db = new DaysDatabase(ourContext);
        db.open();
        String[] row = db.getRow(p);
        db.close();

        AlertDialog.Builder builder2 = new AlertDialog.Builder(ourContext);
        builder2.setCancelable(true);

        builder2.setTitle(row[3]);
        builder2.setMessage("Are you sure that you want to delete this event?");
        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.open();
                db.deleteEvent(p);
                db.close();
                dialog.dismiss();
                showList();
            }
        });

        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder2.create();
        alertDialog.show();
    }
}
