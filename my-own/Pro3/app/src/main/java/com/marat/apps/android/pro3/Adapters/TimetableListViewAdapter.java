package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.marat.apps.android.pro3.Models.TimetableRow;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;
import java.util.Arrays;

public class TimetableListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TimetableRow> timetable;
    private int[] chosenSlots;

    private int startTimePosition;

    public TimetableListViewAdapter(Context c, ArrayList<TimetableRow> data) {
        context = c;
        timetable = data;
        chosenSlots = new int[timetable.size()];
    }

    public void clearSlots() {
        if (chosenSlots != null) {
            Arrays.fill(chosenSlots, 0);
            notifyDataSetChanged();
        }
    }

    public String getStartTime() {
        return timetable.get(startTimePosition).getTime();
    }

    public boolean setStartTimePosition(int pos) {
        startTimePosition = pos;
        Arrays.fill(chosenSlots, 0);
        calculateDuration();
        notifyDataSetChanged();
        return checkChosenTimeSlots();
    }

    private void calculateDuration() {
        int remaining = 0;
        int slotsNeeded = 1;

        if (remaining > 0) {
            slotsNeeded++;
        }

        if (timetable.size() >= startTimePosition + slotsNeeded) {
            for (int i = startTimePosition; i < startTimePosition + slotsNeeded; i++) {
                if (timetable.get(i).isAvailable()) {
                    chosenSlots[i] = 1;
                } else {
                    chosenSlots[i] = 2;
                }
            }
        } else {
            for (int i = startTimePosition; i < timetable.size(); i++) {
                chosenSlots[i] = 2;
            }
        }
    }

    private boolean checkChosenTimeSlots() {
        for (int i = startTimePosition; i < chosenSlots.length; i++) {
            if (chosenSlots[i] == 2) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCount() {
        return timetable.size();
    }

    @Override
    public Object getItem(int position) {
        return timetable.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_timetable_row, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.timeTextView);
            holder.availability = (TextView) convertView.findViewById(R.id.availableOrNotTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.time.setText(timetable.get(position).getTime());

        if (chosenSlots[position] == 0) {
            if (timetable.get(position).isAvailable()) {
                holder.availability.setBackgroundResource(R.drawable.bg_timetable_row_slot_column);
            } else {
                holder.availability.setBackgroundResource(R.drawable.bg_timetable_row_4);
            }
        } else if (chosenSlots[position] == 1) {
            holder.availability.setBackgroundResource(R.drawable.bg_timetable_row_2);
        } else if (chosenSlots[position] == 2) {
            holder.availability.setBackgroundResource(R.drawable.bg_timetable_row_3);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView time;
        TextView availability;
    }
}
