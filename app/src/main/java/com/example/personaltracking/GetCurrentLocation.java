package com.example.personaltracking;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GetCurrentLocation extends Service {
    
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    ScheduledExecutorService executorService;
    SharedPreferences sharedPreferences;

    double newlat, newlong;
    double oldlat, oldlong;

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

        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {


                //location work
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }else{
                    if(ActivityCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);

                    }else{
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null){
                            newlat = location.getLatitude();
                            newlong = location.getLongitude();

                            double result = distance(oldlat, newlat, oldlong, newlong);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("TotalKM", String.valueOf(result));
                            editor.commit();

                            Log.e("Latitude : ", "showLatitude:" + String.valueOf(newlat));
                            Log.e("Longitude: ", "showLongitude:" + String.valueOf(newlong));
                            oldlat = newlat;
                            oldlong = newlong;
                        }
                    }
                }

            }
        }, 3, 3, TimeUnit.SECONDS);



    }


    //distance calculating farmula
    private double distance(double oldlat, double newlat, double oldlong, double newlong) {

        oldlat = Math.toRadians(oldlat);
        newlat = Math.toRadians(newlat);
        oldlong = Math.toRadians(oldlong);
        newlong = Math.toRadians(newlong);

        // Haversine formula
        double dlon = newlong - oldlong;
        double dlat = newlat - oldlat;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(oldlat) * Math.cos(newlat)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);

    }

}