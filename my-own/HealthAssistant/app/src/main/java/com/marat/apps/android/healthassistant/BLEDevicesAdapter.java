package com.marat.apps.android.healthassistant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BLEDevicesAdapter extends ArrayAdapter<BLEDevice> {

    private Activity activity;
    private int layoutResourceID;
    private ArrayList<BLEDevice> devices;

    public BLEDevicesAdapter(Activity activity, int resource, ArrayList<BLEDevice> objects) {
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        layoutResourceID = resource;
        devices = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID, parent, false);
        }

        BLEDevice device = devices.get(position);
        String name = device.getName();
        String address = device.getAddress();
        int rssi = device.getRSSI();

        TextView tv = null;

        tv = (TextView) convertView.findViewById(R.id.btdNameTextView);
        if (name != null && name.length() > 0) {
            tv.setText(device.getName());
        } else {
            tv.setText("No Name");
        }

        tv = (TextView) convertView.findViewById(R.id.btdRSSITextView);
        tv.setText("RSSI: " + Integer.toString(rssi));

        tv = (TextView) convertView.findViewById(R.id.btdMacAddressTextView);
        if (address != null && address.length() > 0) {
            tv.setText(device.getAddress());
        } else {
            tv.setText("No Address");
        }

        return convertView;
    }
}
