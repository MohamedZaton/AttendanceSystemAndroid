package com.pclink.attendance.system.Notification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ScreenLockService extends Service  {


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
