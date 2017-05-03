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

    private int timeSlotPosition = -1;

    public TimetableListViewAdapter(Context c, ArrayList<TimetableRow> data) {
        context = c;
        timetable = data;
        chosenSlots = new int[timetable.size()];
    }

    public String getChosenTimeSlot() {
        return timetable.get(timeSlotPosition).getTime();
    }

    public boolean setAndCheckTimeSlot(int pos) {
        Arrays.fill(chosenSlots, 0);
        if (timeSlotPosition == pos) {
            timeSlotPosition = -1;
            notifyDataSetChanged();
            return false;
        }
        timeSlotPosition = pos;
        if (timetable.get(timeSlotPosition).isAvailable()) {
            chosenSlots[timeSlotPosition] = 1;
        } else {
            chosenSlots[timeSlotPosition] = 2;
        }
        notifyDataSetChanged();
        return timetable.get(timeSlotPosition).isAvailable();
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
