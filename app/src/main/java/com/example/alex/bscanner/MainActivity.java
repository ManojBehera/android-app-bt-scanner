package com.example.alex.bscanner;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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
        TextView placeholder = (TextView) findViewById(R.id.placeholder);

        if (id == R.id.action_scan) {
            if (!isBluetoothEnabled()) {
                enableBluetooth();
            }

            placeholder.setText("Sucess");
        } else {
            placeholder.setText("Fialure");
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
}
