package com.example.alex.bscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.renderscript.Sampler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BScanner";
    private HashMap<Integer, String> mDeviceList = new HashMap<Integer, String>();

    BluetoothAdapter mBluetoothAdapter;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // BT adapter actions listeners
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "Scanning started");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Scanning finished");
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    startScanning();
                }
            }

            //  BD device actions listeners
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE); // signal strength

                mDeviceList.put(rssi, "Name:" + deviceName + "\n" + "MAC: " + deviceHardwareAddress + "\n" + "Signal strength: " + rssi);
                renderDeviceList(mDeviceList);
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("mDeviceList", mDeviceList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mDeviceList = (HashMap<Integer, String>) savedInstanceState.getSerializable("mDeviceList");
        renderDeviceList(mDeviceList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_scan) {
            if (mBluetoothAdapter != null) {
                // Prepare for a new scan
                mDeviceList.clear();
                destroyReceivers();

                if (!isBluetoothEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);

                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mBroadcastReceiver, BTIntent);
                } else {
                    startScanning();
                }
            } else {
                // TODO: implement no BT adapter notification
            }
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        destroyReceivers();
        super.onDestroy();
    }

    protected boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    /*protected void enableBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setCancelable(true);
        builder.setTitle("Bluetooth is disabled");
        builder.setMessage("Enable bluetooth?");

        builder.setPositiveButton("Enable",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mBluetoothAdapter == null) {
                        Log.d(TAG, "Bluetooth adapter is not available");
                    } else if (!isBluetoothEnabled()) { // just to be sure
                        // enabling the adapter

                    }
                }
            });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    protected void startScanning() {
        IntentFilter discoveryStartedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mBroadcastReceiver, discoveryStartedFilter);

        IntentFilter discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver, discoveryFinishedFilter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);

        mBluetoothAdapter.startDiscovery();
    }

    protected void renderDeviceList(HashMap<Integer, String> mDeviceList) {
        ArrayList<String> mSortedDeviceArray = sortHashMapByKeyDesc(mDeviceList);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, mSortedDeviceArray);

        ListView listView = (ListView) findViewById(R.id.devices);
        listView.setAdapter(adapter);
    }

    protected ArrayList sortHashMapByKeyDesc(HashMap<Integer, String> hashMap) {
        Map<Integer, String> treeMap = new TreeMap<Integer, String>(
            new Comparator<Integer>() {
                @Override
                public int compare(Integer i1, Integer i2) {
                    if (i1 > i2) {
                        return -1;
                    } else if (i1 < i2) {
                        return 1;
                    }

                    return 0;
                }
            }
        );

        treeMap.putAll(mDeviceList);

        return new ArrayList(treeMap.values());
    }

    protected void destroyReceivers() {
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}