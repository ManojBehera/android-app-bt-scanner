package com.example.alex.bscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BScanner";
    BluetoothAdapter mBluetoothAdapter;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, deviceName);
                Log.d(TAG, deviceHardwareAddress);
            } else if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(mBluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    Log.d(TAG, "Bluetooth has been turned on");
                }
            }
        }
    };


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
            if (!isBluetoothEnabled()) {
                Log.d(TAG, "BT is disabled trying to enable");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);

                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(mBroadcastReceiver, BTIntent);
            } else {
                getDevicesList();
            }

            /*String[] devicesList = getDevicesList();

            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                    R.layout.activity_listview, devicesList);

            ListView listView = (ListView) findViewById(R.id.devices);
            listView.setAdapter(adapter);*/
        } else {
            getDevicesList();
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
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

    protected void getDevicesList() {
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);
        /*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            s.add(bt.getName());

        //setListAdapter(new ArrayAdapter<String>(this, R.layout.list, s));
        // implement fetching logic
        String[] devicesList = {"Device 1","Device 2","Device 3","Device 4",
                "Device 5","Device 6","Device 7","Device 8","Device 9","Device 10","Device 11","Device 12",
                "Device 13","Device 14","Device 16","Device 17","Device18 ","Device 19","Device 20",
                "Device 21","Device 22","Device 23","Device 24"};

        // implement sorting logic

        Log.d(TAG, s.get(0));

        return devicesList;*/
    }
}