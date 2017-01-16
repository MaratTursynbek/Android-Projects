package com.example.marat.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marat.stormy.R;
import com.example.marat.stormy.weather.Hour;

/**
 * Created by Marat on 29.07.2015.
 */
public class HourAdapter extends BaseAdapter {

    private Context mContext;
    private Hour[] mHours;

    public HourAdapter(Context context, Hour[] hours) {
        mContext = context;
        mHours = hours;
    }

    @Override
    public int getCount() {
        return mHours.length;
    }

    @Override
    public Object getItem(int position) {
        return mHours[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;   //we aren't going to use this. Tag items for easy reference
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.hourly_list_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.summaryLabel = (TextView) convertView.findViewById(R.id.summaryLabel);
            holder.hourLabel = (TextView) convertView.findViewById(R.id.timeLabel);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Hour hour = mHours[position];

        holder.iconImageView.setImageResource(hour.getIconId());
        holder.temperatureLabel.setText(hour.getTemperature() + "");
        holder.summaryLabel.setText(hour.getSummary());
        holder.hourLabel.setText(hour.getHour());

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView; //public by default
        TextView temperatureLabel;
        TextView summaryLabel;
        TextView hourLabel;
    }
}





/*public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private Hour[] mHours;

    public HourAdapter(Hour[] hours) {
        this.mHours = hours;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hourly_list_item, viewGroup, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int i) {
        hourViewHolder.bindHour(mHours[i]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {

        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;
        public ImageView mIconImageView;

        public HourViewHolder(View itemView) {
            super(itemView);

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
        }

        public void bindHour(Hour hour) {

            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(hour.getTemperature());
            mIconImageView.setImageResource(hour.gerIconId());

        }

    }

}*/
