package com.example.alex.bscanner;

import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ListView;
import android.view.MenuItem;
import android.view.Menu;
import android.os.Bundle;
import android.util.Log;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private scanningStatuses scanStatus = null;
    private Menu storedMenu;
    private HashMap<Integer, String> mDeviceList = new HashMap<Integer, String>();
    private BluetoothAdapter mBluetoothAdapter;

    private enum scanningStatuses {
        SCAN_IN_PROGRESS,
        SCAN_STOPPED
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        storedMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (scanStatus == scanningStatuses.SCAN_IN_PROGRESS) {
            hideScanButton();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_scan) {
            if (mBluetoothAdapter != null) {
                // prepare to a new scan
                mDeviceList.clear();
                renderDeviceList(mDeviceList);
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
                showErrorMessage("Your device has no bluetooth adapter");
            }
        }

        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("mDeviceList", mDeviceList);
        savedInstanceState.putSerializable("scanStatus", scanStatus);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // restore device list and render it
        mDeviceList = (HashMap<Integer, String>) savedInstanceState.getSerializable("mDeviceList");
        renderDeviceList(mDeviceList);

        // restore scan status and run scanning if required
        scanStatus = (scanningStatuses) savedInstanceState.getSerializable("scanStatus");
        if (scanStatus == scanningStatuses.SCAN_IN_PROGRESS) {
            startScanning();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onDestroy() {
        destroyReceivers();
        super.onDestroy();
    }

    protected void startScanning() {
        IntentFilter discoveryStartedFilter = new IntentFilter(
            BluetoothAdapter.ACTION_DISCOVERY_STARTED
        );
        registerReceiver(mBroadcastReceiver, discoveryStartedFilter);

        IntentFilter discoveryFinishedFilter = new IntentFilter(
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED
        );
        registerReceiver(mBroadcastReceiver, discoveryFinishedFilter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);

        mBluetoothAdapter.startDiscovery();
        hideScanButton();
    }

    protected void renderDeviceList(HashMap<Integer, String> mDeviceList) {
        ArrayList<String> mSortedDeviceArray = sortHashMapByKeyDesc(mDeviceList);

        ArrayAdapter adapter = new ArrayAdapter<String>(
            this,
            R.layout.activity_listview,
            mSortedDeviceArray
        );

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

    protected boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    protected void showErrorMessage(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Error");
        builder.setMessage(errorMessage);

        builder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // No implementation required
                }
            });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    protected void hideScanButton()
    {
        if (storedMenu != null) {
            MenuItem item = storedMenu.findItem(R.id.action_scan);
            item.setVisible(false);
        }

        ProgressBar scan_indicator = (ProgressBar) findViewById(R.id.scan_indicator);
        scan_indicator.setVisibility(ProgressBar.VISIBLE);
    }

    protected void showScanButton()
    {
        if (storedMenu != null) {
            MenuItem item = storedMenu.findItem(R.id.action_scan);
            item.setVisible(true);
        }

        ProgressBar scan_indicator = (ProgressBar) findViewById(R.id.scan_indicator);
        scan_indicator.setVisibility(ProgressBar.INVISIBLE);
    }

    protected void destroyReceivers() {
        // quick workaround for preventing app crash, after device rotation
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.d("BScanner app", e.getMessage());
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // BT adapter actions listeners
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                scanStatus = scanningStatuses.SCAN_IN_PROGRESS;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scanStatus = scanningStatuses.SCAN_STOPPED;
                showScanButton();
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                );

                if (state == BluetoothAdapter.STATE_ON) {
                    startScanning();
                }
            }

            //  BD device actions listeners
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                int rssi = intent.getShortExtra(
                    BluetoothDevice.EXTRA_RSSI,
                    Short.MIN_VALUE
                ); // signal strength

                mDeviceList.put(
                    rssi,
                    "Name:" + deviceName + "\n"
                            + "MAC: " + deviceHardwareAddress
                            + "\n" + "Signal strength: " + rssi
                );
                renderDeviceList(mDeviceList);
            }
        }
    };
}