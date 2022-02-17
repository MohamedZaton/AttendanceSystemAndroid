package com.pclink.attendance.system.LocationFind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;

import static android.content.Context.ALARM_SERVICE;

public class GpsHelper {
    Context mContext;

    public GpsHelper(Context context) {
        this.mContext = context;
    }

    public  void startGpsRecever(Long MileSecound) {


        try {

            Intent intent = new Intent(mContext, GpsReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, DataConstant.GpsReceverId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),MileSecound, pendingIntent); // XXX
        }
        catch(IllegalArgumentException e)
        {
                stopGpsRecever();


            Intent intent = new Intent(mContext, GpsReceiver.class);
            intent.putExtra("Gps", "searching location ");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, DataConstant.GpsReceverId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),MileSecound, pendingIntent); // XXX

        }
       

    }
    public  void stopGpsRecever()
    {

        try
        {
            Intent intent = new Intent(mContext, GpsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, DataConstant.GpsReceverId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        }
        catch(IllegalArgumentException e)
        {

        }

    }

}
