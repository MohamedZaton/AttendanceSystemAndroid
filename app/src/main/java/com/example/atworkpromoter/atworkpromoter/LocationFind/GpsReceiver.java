package com.pclink.attendance.system.LocationFind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pclink.attendance.system.Activities.MainActivity;

public class GpsReceiver extends BroadcastReceiver
{
    GpsTracker gpsTracker ;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        gpsTracker = new GpsTracker(context);



    }


}
