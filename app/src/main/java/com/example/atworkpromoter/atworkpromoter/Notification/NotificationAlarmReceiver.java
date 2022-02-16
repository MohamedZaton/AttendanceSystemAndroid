package com.pclink.attendance.system.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import java.util.Map;

import io.hypertrack.smart_scheduler.SmartScheduler;

public class NotificationAlarmReceiver extends BroadcastReceiver {

 SharedPrefData sharedPrefData;
 Promoter promoter;
 MainAsynctask mainAsynctask ;
 private  String promoterDataFileSp=DataConstant.promoterDataNameSpFile,
            stillNotificationTimeKeySp=DataConstant.stillNotificationTimeKeySp;

    @Override
    public void onReceive(Context context, Intent intent)
    {


        promoter=new Promoter(context);
        sharedPrefData=new SharedPrefData(context);
         mainAsynctask = new MainAsynctask(context,456665);
        int idNotifyStill = sharedPrefData.getElementIntValue(promoterDataFileSp,DataConstant.idNotifyStillKey);

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.i("reboot_phone","After Reboot Rest Alarm Manger");

           Boolean logFlag = sharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.LogOutClk);
            if(!logFlag)
            {
                final Long timeAppear = System.currentTimeMillis();
                // after rest your phone
                new NotificationHelper(context).stopStillThereNotificationRecever();
                new NotificationHelper(context).startCalenderStillThereNotificationRecever(timeAppear);
            }
            else
            {
                new NotificationHelper(context).stopStillThereNotificationRecever();
            }
        }
        else
         {

             new NotificationHelper(context).stillNotifiAMr(idNotifyStill, "Still There", "Click  still there", "Miss still there", "", "stillThereNotif", true);
            sharedPrefData.putIntElement(promoterDataFileSp, DataConstant.idNotifyStillKey, idNotifyStill + 1);  // id++;

         }
    }



}