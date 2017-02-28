package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Models.City;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class MenuCityPickerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<City> cities = new ArrayList<>();
    private int currentCityID;

    public MenuCityPickerAdapter(Context c, ArrayList<City> data, int selected) {
        context = c;
        cities = data;
        currentCityID = selected;
    }

    public void updateCurrentCity(int newSelected) {
        currentCityID = newSelected;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int i) {
        return cities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_menu_cities_listview, null);
            holder = new ViewHolder();
            holder.rowCityName = (TextView) view.findViewById(R.id.rowCityNameTextView);
            holder.layout = (RelativeLayout) view.findViewById(R.id.rowListItemLayout);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.rowCityName.setText(cities.get(i).getCityName());

        if (currentCityID == cities.get(i).getCityID()) {
            holder.rowCityName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.rowCityName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        return view;
    }

    private static class ViewHolder {
        TextView rowCityName;
        RelativeLayout layout;
    }
}
