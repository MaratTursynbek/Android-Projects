package com.marat.apps.android.healthassistant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothStateChangedReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceiver");
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "STATE_OFF");
                    context.stopService(new Intent(context, BackgroundBLEService.class));
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "STATE_TURNING_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "STATE_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d(TAG, "STATE_TURNING_ON");
                    break;
            }
        }
    }
}
