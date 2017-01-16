package com.example.marat.days.DB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.marat.days.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UntilAdapter extends BaseAdapter {

    private Context mContext;
    private String mDays[][];

    public UntilAdapter(Context context, String days[][]) {
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        long result = 0;

        if (convertView == null) {
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.days_list_item, null);
            holder = new ViewHolder();
            holder.numberOfDays = (TextView) convertView.findViewById(R.id.eventDays);
            holder.sinceOrUntil = (TextView) convertView.findViewById(R.id.eventType);
            holder.eventTitle = (TextView) convertView.findViewById(R.id.eventTitle);
            holder.daysText = (TextView) convertView.findViewById(R.id.DaysText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String event[] = mDays[position];
        String type = event[1];
        String untilDate = event[3];

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
        Calendar current, mDate2;

        current = Calendar.getInstance(TimeZone.getDefault());
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        Date until = null;
        try {
            until = format.parse(untilDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mDate2 = Calendar.getInstance(TimeZone.getDefault());
        mDate2.setTime(until);

        result = (mDate2.getTime().getTime() - current.getTime().getTime()) / (1000 * 60 * 60 * 24);

        holder.numberOfDays.setText("" + result);
        holder.sinceOrUntil.setText(type);
        holder.eventTitle.setText(event[4]);

        return convertView;
    }

    private static class ViewHolder {
        TextView numberOfDays;
        TextView daysText;
        TextView sinceOrUntil;
        TextView eventTitle;
    }
}
