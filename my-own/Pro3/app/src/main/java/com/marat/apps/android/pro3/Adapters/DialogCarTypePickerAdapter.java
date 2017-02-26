package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Models.CarType;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class DialogCarTypePickerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CarType> carTypes;
    private int chosenCarType;

    public DialogCarTypePickerAdapter(Context context, ArrayList<CarType> carTypes) {
        this.context = context;
        this.carTypes = carTypes;
    }

    @Override
    public int getCount() {
        return carTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return carTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_car_type_picker, null);
            holder = new ViewHolder();
            holder.radioButton = (RadioButton) convertView.findViewById(R.id.dialogCarTypePickerRadioButton);
            holder.carTypeName = (TextView) convertView.findViewById(R.id.dialogCarTypePickerTextView);
            holder.onTopOfRadioButton = (RelativeLayout) convertView.findViewById(R.id.dialogCarTypePickerOnTopOfRadioButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.carTypeName.setText(carTypes.get(position).getCarTypeName());
        holder.radioButton.setClickable(false);

        if (position == chosenCarType) {
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }

        return convertView;
    }

    private static class ViewHolder {
        RadioButton radioButton;
        TextView carTypeName;
        RelativeLayout onTopOfRadioButton;
    }

    public void setChosenCarType(int chosenCarType) {
        this.chosenCarType = chosenCarType;
        notifyDataSetChanged();
    }
}
