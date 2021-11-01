package com.example.personaltracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
/*                NotificationCompat.Builder builder = new
                        NotificationCompat.Builder(MainActivity.this, "Notification");
                builder.setContentTitle("Notification");
                builder.setContentText("Tracking Started");
                builder.setOngoing(true);
                builder.setSmallIcon(R.drawable.message);
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    String channelid = "Channel ID";
                    NotificationChannel channel = new NotificationChannel(
                            channelid,"Notification Channel", NotificationManager.IMPORTANCE_HIGH);
                    managerCompat.createNotificationChannel(channel);
                    builder.setChannelId(channelid);
                }
                managerCompat.notify(1,builder.build());*/

                startService(new Intent(MainActivity.this, GetCurrentLocation.class));

            }
        });

        stoptracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                managerCompat.cancelAll();

                SharedPreferences sharedPreferences = getApplicationContext()
                        .getSharedPreferences("MyLocation", Context.MODE_PRIVATE);

                String km = sharedPreferences.getString("TotalKM", "");

                distanceview.setText(km);

            }
        });

    }

}