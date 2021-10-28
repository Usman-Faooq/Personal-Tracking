package com.example.personaltracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Switch switchbtn;
    Button starttracking, stoptracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchbtn = findViewById(R.id.switchbtn);
        starttracking = findViewById(R.id.start_tracking);
        stoptracking = findViewById(R.id.stop_tracking);


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
                NotificationCompat.Builder builder = new
                        NotificationCompat.Builder(MainActivity.this, "Notification");
                builder.setContentTitle("Notification");
                builder.setContentText("Tracking Started");
                builder.setSmallIcon(R.drawable.message);
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                managerCompat.notify(1,builder.build());

            }
        });

        stoptracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}