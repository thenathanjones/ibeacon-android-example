package com.thenathanjones.ibeaconlibrary.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public class IBeaconService implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = IBeaconService.class.getName();

    private final Context mContext;
    private final Collection<IBeaconListener> mListeners = new ArrayList<IBeaconListener>();

    public IBeaconService(Context context) {
        mContext = context;
    }

    public void registerListener(IBeaconListener listener) {
        if (mListeners.isEmpty()) {
            startScanning();
        }

        mListeners.add(listener);
    }

    public void unregisterListener(IBeaconListener listener) {
        mListeners.remove(listener);

        if (mListeners.isEmpty()) {
           stopScanning();
        }
    }

    private void startScanning() {
        if (bluetoothAdapter().isEnabled()) {
            bluetoothAdapter().startLeScan(this);
        }
        else {
            Log.w(TAG + ":startScanning", "Bluetooth is disabled, unable to scan.");
        }
    }

    private void stopScanning() {
        bluetoothAdapter().stopLeScan(this);
    }

    private BluetoothAdapter mBluetoothAdapter;
    public BluetoothAdapter bluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        return mBluetoothAdapter;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        parseBeaconFrom(rssi, scanRecord);
    }

    private void parseBeaconFrom(int rssi, byte[] scanRecord) {
        if (isBeacon(scanRecord)) {
            Log.d(TAG + ":parseBeacon", "Congratulations, you have found an iBeacon!");
            IBeacon beacon = IBeacon.from(scanRecord);
        }
        else {
            Log.d(TAG + ":parseBeacon", "Record is not an iBeacon");
        }
    }


    private boolean isBeacon(byte[] scanRecord) {
        Integer[] headerBytes = new Integer[9];

        for (int i=0;i<headerBytes.length;i++) {
            headerBytes[i] = scanRecord[i] & 0xff;
        }

        return Collections.indexOfSubList(Arrays.asList(headerBytes), IBeacon.IBEACON_HEADER) == IBeacon.IBEACON_HEADER_INDEX;
    }
}
