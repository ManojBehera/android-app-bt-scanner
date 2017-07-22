package com.example.alex.bscanner;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                enableBluetooth();
            }

            String[] devicesList = getDevicesList();

            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                    R.layout.activity_listview, devicesList);

            ListView listView = (ListView) findViewById(R.id.devices);
            listView.setAdapter(adapter);
        } else {
        }

        return true;
    }

    protected boolean isBluetoothEnabled() {
        return false;
    }

    protected void enableBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setCancelable(true);
        builder.setTitle("Bluetooth is disabled");
        builder.setMessage("Enable bluetooth?");

        builder.setPositiveButton("Enable",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // enable bluetooth
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
    }

    protected String[] getDevicesList() {
        // implement fetching logic
        String[] devicesList = {"Device 1","Device 2","Device 3","Device 4",
                "Device 5","Device 6","Device 7","Device 8","Device 9","Device 10","Device 11","Device 12",
                "Device 13","Device 14","Device 16","Device 17","Device18 ","Device 19","Device 20",
                "Device 21","Device 22","Device 23","Device 24"};

        // implement sorting logic

        return devicesList;
    }
}
