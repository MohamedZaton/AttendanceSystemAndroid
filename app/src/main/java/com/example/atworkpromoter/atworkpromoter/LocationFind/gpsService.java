package com.pclink.attendance.system.LocationFind;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.pclink.attendance.system.Activities.SplashScreen;

import java.util.List;


public class gpsService extends Service implements LocationListener {
    protected LocationManager locationManager;
    Location location;
    public static Location LastLocation;
    public static double lonI, latI;
    //private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_DISTANCE_FOR_UPDATE = 0;
    //private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
    private static final long MIN_TIME_FOR_UPDATE = 1000;
    private static final String TAG = "location";
    Context mContext;

    public gpsService(Context context) {
        mContext = context;
        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public Location getGPSLocation() {
        Location locationgps = null;


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
            // locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            if (locationManager != null) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                locationgps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationManager.removeUpdates(this);
            }


        return locationgps;
    }

    public void StopListening() {
        locationManager.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(String provider) {

            locationManager.requestLocationUpdates(provider,
                    MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this, Looper.getMainLooper());
            //locationManager.requestSingleUpdate(provider, this, null);

            if (locationManager != null) {

                location = locationManager.getLastKnownLocation(provider);
                locationManager.removeUpdates(this);
                return location;
            }


        StopListening();
        return null;
    }

    public Location getLastKnownLocation() {

        locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission")
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location loc) {
        lonI = loc.getLongitude();
        latI= loc.getLatitude();
        String longitude = "Longitude:Mi =  " + loc.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude:Mi =   " + loc.getLatitude();
        Log.v(TAG, latitude);
        LastLocation=location;
        //locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
