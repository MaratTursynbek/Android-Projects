package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Models.City;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class DialogCityPickerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<City> cities;
    private int chosenCity;

    public DialogCityPickerAdapter(Context context, ArrayList<City> cities) {
        this.context = context;
        this.cities = cities;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_city_picker, null);
            holder = new ViewHolder();
            holder.radioButton = (RadioButton) convertView.findViewById(R.id.dialogCityPickerRadioButton);
            holder.cityName = (TextView) convertView.findViewById(R.id.dialogCityPickerTextView);
            holder.onTopOfRadioButton = (RelativeLayout) convertView.findViewById(R.id.dialogCityPickerOnTopOfRadioButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cityName.setText(cities.get(position).getCityName());
        holder.radioButton.setClickable(false);

        if (position == chosenCity) {
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }

        return convertView;
    }

    private static class ViewHolder {
        RadioButton radioButton;
        TextView cityName;
        RelativeLayout onTopOfRadioButton;
    }

    public void setChosenCity(int chosenCity) {
        this.chosenCity = chosenCity;
        notifyDataSetChanged();
    }
}
