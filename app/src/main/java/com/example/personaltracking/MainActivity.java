package com.example.personaltracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Switch switchbtn;
    Button starttracking, stoptracking;
    TextView distanceview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchbtn = findViewById(R.id.switchbtn);
        starttracking = findViewById(R.id.start_tracking);
        stoptracking = findViewById(R.id.stop_tracking);

        distanceview = findViewById(R.id.distanceview);

        switchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true){
                    Toast.makeText(MainActivity.this, "Switched to wifi, now less battary is comsuming", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "GPS is ON, High Battary Consuming", Toast.LENGTH_SHORT).show();
                }
            }
        });

        starttracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, GetCurrentLocation.class));
            }
        });

        stoptracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                managerCompat.cancelAll();
                stopService(new Intent(MainActivity.this,GetCurrentLocation.class));

            }
        });
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("SEND_TO_UI"));

        super.onResume();
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double milesrecive = intent.getDoubleExtra("miles", 0.00);
            distanceview.setText("Miles: " + String.valueOf(milesrecive));
        }
    };

}