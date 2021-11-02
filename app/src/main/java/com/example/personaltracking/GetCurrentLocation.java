package com.example.personaltracking;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GetCurrentLocation extends Service {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    ScheduledExecutorService executorService;
    SharedPreferences sharedPreferences;

    double newlatitude, newlongitude;

    public GetCurrentLocation() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        showLocation();

        return START_STICKY;
    }

    public void showLocation(){

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(getApplicationContext(), "Notification");

        Intent newIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(this,1, new Intent[]{newIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("Notification");
        builder.setContentText("Tracking Started");
        builder.setOngoing(true);
        builder.setSmallIcon(R.drawable.message);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String channelid = "Channel ID";
            NotificationChannel channel = new NotificationChannel(
                    channelid,"Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            managerCompat.createNotificationChannel(channel);
            builder.setChannelId(channelid);
        }
        managerCompat.notify(1,builder.build());


        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                }else{
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    LocationServices.getFusedLocationProviderClient(getApplicationContext())
                            .requestLocationUpdates(locationRequest, new LocationCallback(){
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0){
                                        int latestlocation = locationResult.getLocations().size() - 1;
                                        newlatitude = locationResult.getLocations().get(latestlocation).getLatitude();
                                        newlongitude = locationResult.getLocations().get(latestlocation).getLongitude();

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("Latitude", String.valueOf(newlatitude));
                                        editor.putString("Longitude", String.valueOf(newlongitude));
                                        editor.commit();

                                    }
                                }
                            }, getMainLooper());
                }
            }
        }, 3, 3, TimeUnit.SECONDS);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        Log.e("Ending: ", "Service Destoried");
    }
}