package com.pclink.attendance.system.NetworkServer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.pclink.attendance.system.Activities.MainActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            try {
                if (isOnline(context)) {
                    // show online icon
                    MainActivity.offlineCikItem(isOnline(context));
                } else {
                    // show offline icon
                    MainActivity.offlineCikItem(isOnline(context));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}