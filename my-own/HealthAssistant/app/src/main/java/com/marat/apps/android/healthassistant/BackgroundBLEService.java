package com.marat.apps.android.healthassistant;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackgroundBLEService extends Service {

    private static final String TAG = "BackgroundBLEService";

    private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static String BLE_SHIELD_TX = "713d0003-503e-4c75-ba94-3148f18d941e";
    private static String BLE_SHIELD_RX = "713d0002-503e-4c75-ba94-3148f18d941e";
    private static String BLE_SHIELD_SERVICE = "713d0000-503e-4c75-ba94-3148f18d941e";

    private final static UUID UUID_BLE_SHIELD_TX = UUID.fromString(BLE_SHIELD_TX);
    private final static UUID UUID_BLE_SHIELD_RX = UUID.fromString(BLE_SHIELD_RX);
    private final static UUID UUID_BLE_SHIELD_SERVICE = UUID.fromString(BLE_SHIELD_SERVICE);

    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_RSSI = "ACTION_GATT_RSSI";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "EXTRA_DATA";

    public final static String ACTION_UNBOUND_FROM_SERVICE = "ACTION_UNBOUND_FROM_SERVICE";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;

    private boolean activityBoundToService = false;
    private boolean bleConnected = false;

    private String jsonString = "{";
    private int counter = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand");

        if (!bleConnected) {
            if (!initialize()) {
                if (!initialize()) {
                    Log.e(TAG, "Could not initialize the Bluetooth Manager and Adapter");
                    stopSelf();
                }
            }

            SharedPreferences sharedPreferences = getSharedPreferences("ble_device_info", Context.MODE_PRIVATE);
            mBluetoothDeviceAddress = sharedPreferences.getString("device_address", null);
            if (!connect(mBluetoothDeviceAddress)) {
                bleConnected = false;
            }
        }

        return START_STICKY;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter
     *
     * @return Return true if the initialization is successful
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (mBluetoothDevice == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");

        return true;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (activityBoundToService) {
                    broadcastUpdate(ACTION_GATT_CONNECTED);
                }
                Log.i(TAG, "Connected to GATT server");
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                bleConnected = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                Log.i(TAG, "Disconnected from GATT server");
                close();
                bleConnected = true;
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_RSSI, rssi);
            } else {
                Log.w(TAG, "onReadRemoteRssi received: " + status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onServicesDiscovered successfully");
                getGattService(mBluetoothGatt.getService(UUID_BLE_SHIELD_SERVICE));
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                readReceivedCharacteristics(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged");
            readReceivedCharacteristics(characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, int rssi) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final byte[] data) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null) {
            return;
        }

        BluetoothGattCharacteristic characteristicRx = gattService.getCharacteristic(UUID_BLE_SHIELD_RX);

        if (characteristicRx != null && mBluetoothGatt != null) {
            mBluetoothGatt.setCharacteristicNotification(characteristicRx, true);

            if (UUID_BLE_SHIELD_RX.equals(characteristicRx.getUuid())) {
                BluetoothGattDescriptor descriptor = characteristicRx.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
            mBluetoothGatt.readCharacteristic(characteristicRx);
        }
    }

    private void readReceivedCharacteristics(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null) {
            Log.d(TAG, "readReceivedCharacteristics");
            Log.d(TAG, "UUID - " + characteristic.getUuid());
            byte[] data = characteristic.getValue();

            Log.d(TAG, "length = " + data.length);
            Log.d(TAG, "data = " + new String(data));

            if (data.length == 11) {
                String values = new String(data);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
                String time = format.format(c.getTime());

                jsonString = jsonString + "\"" + "health_param" + counter + "\"" + ":{" +
                        "\"temperature\"" + ":" + "\"" + values.substring(1, 5) + "\"" + "," +
                        "\"pulse\"" + ":" + "\"" + values.substring(7, 10) + "\"" + "," +
                        "\"time\"" + ":" + "\"" + time + "\"" + "}";

                if (counter + 1 > 30) {
                    jsonString = jsonString + "}";
                    sendDataToServer();
                    counter = 1;
                    jsonString = "{";
                } else {
                    jsonString = jsonString + ",";
                    counter++;
                }

                Log.d(TAG, jsonString);
                Log.d(TAG, "counter = " + counter);

                if (activityBoundToService) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, data);
                    return;
                }
            }
            Log.d(TAG, "readReceivedCharacteristics END");
        }
    }

    public String getDeviceName() {
        if (mBluetoothDevice != null) {
            return mBluetoothDevice.getName();
        }
        return "NOT Connected!";
    }

    public String getDeviceAddress() {
        if (mBluetoothDevice != null) {
            return mBluetoothDevice.getAddress();
        }
        return "...............";
    }

    public void readDeviceRSSI() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.readRemoteRssi();
        }
    }

    public boolean connectedToBleDevice() {
        return bleConnected;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    private void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        Log.w(TAG, "mBluetoothGatt disconnecting");
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    private void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        Log.w(TAG, "mBluetoothGatt closing");
    }

    //////////////////////////////////////////////////////////////////
    //
    //                      binders below
    //
    //////////////////////////////////////////////////////////////////

    public class LocalBinder extends Binder {
        BackgroundBLEService getService() {
            return BackgroundBLEService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        activityBoundToService = true;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        activityBoundToService = true;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        activityBoundToService = false;
        broadcastUpdate(ACTION_UNBOUND_FROM_SERVICE);
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        disconnect();
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////

    private void sendDataToServer() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = "https://nucapstone.herokuapp.com/api/v1/health_params";

        if (networkIsAvailable()) {
            Log.d(TAG, "Attempting to send data");

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    Log.d(TAG, "onResponse");
                    Log.d(TAG, "Message = " + response.message());
                    try {
                        Log.d(TAG, "Body = " + response.body().string());
                    } catch (IOException e) {
                        Log.d(TAG, "IOException");
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.d(TAG, "no Internet");
        }

    }

    private boolean networkIsAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
