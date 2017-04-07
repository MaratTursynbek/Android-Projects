package com.marat.apps.android.healthassistant;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private BackgroundBLEService mBackgroundBLEService;

    private int REQUEST_ENABLE_BT = 1;
    private int mInterval = 2000;
    private boolean activityBoundToService = false;

    private Handler mHandler;

    private TextView deviceNameTextView;
    private TextView deviceAddressTextView;
    private TextView deviceRSSITextView;

    private TextView hearRateValue;
    private TextView temperatureValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startService(new Intent(MainActivity.this, BackgroundBLEService.class));
        mHandler = new Handler();

        deviceNameTextView = (TextView) findViewById(R.id.deviceName);
        deviceAddressTextView = (TextView) findViewById(R.id.deviceAddress);
        deviceRSSITextView = (TextView) findViewById(R.id.deviceRSSI);

        hearRateValue = (TextView) findViewById(R.id.heartRate);
        temperatureValue = (TextView) findViewById(R.id.bodyTemperature);

        Button startServiceButton = (Button) findViewById(R.id.startServiceButton);
        Button stopServiceButton = (Button) findViewById(R.id.stopServiceButton);
        Button bindServiceButton = (Button) findViewById(R.id.bindServiceButton);
        Button unbindServiceButton = (Button) findViewById(R.id.unbindServiceButton);
        startServiceButton.setOnClickListener(this);
        stopServiceButton.setOnClickListener(this);
        bindServiceButton.setOnClickListener(this);
        unbindServiceButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (!activityBoundToService) {
            Intent backgroundBLEService = new Intent(this, BackgroundBLEService.class);
            bindService(backgroundBLEService, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    Runnable RSSIChecker = new Runnable() {
        @Override
        public void run() {
            try {
                mBackgroundBLEService.readDeviceRSSI();
            } finally {
                mHandler.postDelayed(RSSIChecker, mInterval);
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.startServiceButton:
                Log.d(TAG, "START service button clicked");
                startService(new Intent(MainActivity.this, BackgroundBLEService.class));
                break;
            case R.id.stopServiceButton:
                Log.d(TAG, "STOP service button clicked");
                if (activityBoundToService) {
                    unbindService(mServiceConnection);
                    activityBoundToService = false;
                }
                stopService(new Intent(MainActivity.this, BackgroundBLEService.class));
                break;
            case R.id.bindServiceButton:
                Log.d(TAG, "BIND service button clicked");
                Log.d(TAG, "Bind status: " + activityBoundToService);
                if (!activityBoundToService) {
                    Log.d(TAG, "Binding service");
                    Intent backgroundBLEService = new Intent(this, BackgroundBLEService.class);
                    bindService(backgroundBLEService, mServiceConnection, BIND_AUTO_CREATE);
                }
                break;
            case R.id.unbindServiceButton:
                Log.d(TAG, "UNBIND service button clicked");
                Log.d(TAG, "Bind status: " + activityBoundToService);
                if (activityBoundToService) {
                    Log.d(TAG, "Unbinding service");
                    unbindService(mServiceConnection);
                    activityBoundToService = false;
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        unregisterReceiver(mGattUpdateReceiver);
        mHandler.removeCallbacks(RSSIChecker);

        if (activityBoundToService) {
            unbindService(mServiceConnection);
            activityBoundToService = false;
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            BackgroundBLEService.LocalBinder binder = (BackgroundBLEService.LocalBinder) service;
            mBackgroundBLEService = binder.getService();
            activityBoundToService = true;
            if (mBackgroundBLEService != null) {
                if (mBackgroundBLEService.connectedToBleDevice()) {
                    deviceNameTextView.setText(mBackgroundBLEService.getDeviceName());
                    deviceAddressTextView.setText(mBackgroundBLEService.getDeviceAddress());
                    RSSIChecker.run();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");

            clearTextViews();

            mBackgroundBLEService = null;
            activityBoundToService = false;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BackgroundBLEService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "BroadcastReceiver: " + "ACTION_GATT_CONNECTED");

                if (!activityBoundToService) {
                    Intent backgroundBLEService = new Intent(MainActivity.this, BackgroundBLEService.class);
                    bindService(backgroundBLEService, mServiceConnection, BIND_AUTO_CREATE);
                }

                deviceNameTextView.setText(mBackgroundBLEService.getDeviceName());
                deviceAddressTextView.setText(mBackgroundBLEService.getDeviceAddress());
                RSSIChecker.run();

            } else if (BackgroundBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "BroadcastReceiver: " + "ACTION_GATT_DISCONNECTED");

                clearTextViews();

            } else if (BackgroundBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "BroadcastReceiver: " + "ACTION_DATA_AVAILABLE");

                displayData(intent.getByteArrayExtra(BackgroundBLEService.EXTRA_DATA));

            } else if (BackgroundBLEService.ACTION_GATT_RSSI.equals(action)) {
                Log.d(TAG, "BroadcastReceiver: " + "ACTION_GATT_RSSI");

                deviceRSSITextView.setText(intent.getExtras().getString(BackgroundBLEService.EXTRA_DATA));

            } else if (BackgroundBLEService.ACTION_UNBOUND_FROM_SERVICE.equals(action)) {
                Log.d(TAG, "BroadcastReceiver: " + "ACTION_UNBOUND_FROM_SERVICE");

                clearTextViews();

                mBackgroundBLEService = null;
                activityBoundToService = false;
            }
        }
    };

    private void displayData(byte[] byteArray) {
        if (byteArray != null) {
            String data = new String(byteArray);
            Log.d(TAG, "displayData: " + data);

            temperatureValue.setText(data.substring(1, 5));
            hearRateValue.setText(data.substring(7, 10));
        }
    }

    private void clearTextViews() {
        mHandler.removeCallbacks(RSSIChecker);
        deviceNameTextView.setText("NOT Connected!");
        deviceAddressTextView.setText("...............");
        deviceRSSITextView.setText("-XX");
        hearRateValue.setText("---");
        temperatureValue.setText("--,-");
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BackgroundBLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BackgroundBLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BackgroundBLEService.ACTION_GATT_RSSI);
        intentFilter.addAction(BackgroundBLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BackgroundBLEService.ACTION_UNBOUND_FROM_SERVICE);

        return intentFilter;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_disconnect) {
            SharedPreferences sharedPreferences = getSharedPreferences("ble_device_info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, BLEScanActivity.class);
            startActivity(intent);
            stopService(new Intent(MainActivity.this, BackgroundBLEService.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
