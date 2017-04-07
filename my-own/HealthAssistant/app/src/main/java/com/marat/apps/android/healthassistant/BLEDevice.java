package com.marat.apps.android.healthassistant;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class BLEDevice implements Parcelable {

    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BLEDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }

    public int getRSSI() {
        return rssi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bluetoothDevice, flags);
        dest.writeInt(rssi);
    }

    protected BLEDevice(Parcel in) {
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        rssi = in.readInt();
    }

    public static final Creator<BLEDevice> CREATOR = new Creator<BLEDevice>() {
        @Override
        public BLEDevice createFromParcel(Parcel in) {
            return new BLEDevice(in);
        }

        @Override
        public BLEDevice[] newArray(int size) {
            return new BLEDevice[size];
        }
    };
}
