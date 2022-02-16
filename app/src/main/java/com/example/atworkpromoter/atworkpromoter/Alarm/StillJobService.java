package com.pclink.attendance.system.Alarm;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.CountDownTimer;
import android.util.Log;

import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Notification.NotificationHelper;
import com.pclink.attendance.system.R;

import java.util.Locale;

public class StillJobService extends JobService {

    NotificationHelper notificationHelper ;
     Long timeAppear ;
   Promoter promoter;
    SharedPrefData sharedPrefData;

    @Override
    public boolean onStartJob(JobParameters params) {
        promoter=new Promoter(getApplicationContext());
        sharedPrefData=new SharedPrefData(getApplicationContext());
        notificationHelper = new NotificationHelper(getApplicationContext());
        timeAppear = System.currentTimeMillis();
        int idNotifyStill = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile, DataConstant.idNotifyStillKey);
        startStillNotifi(idNotifyStill, "still There", "click  still There","stillThereNotif",true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        promoter=new Promoter(getApplicationContext());
        sharedPrefData=new SharedPrefData(getApplicationContext());
        notificationHelper = new NotificationHelper(getApplicationContext());
        int idNotifyStill = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile, DataConstant.idNotifyStillKey);

        stopStillNotifi(idNotifyStill, "still There  !", "still there time finished ","stillThereNotif",true);

        sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.idNotifyStillKey,idNotifyStill+1);  // id++;


        return true;
    }

    public void startStillNotifi(final int  notifyId, final String title, final String message  , final String extraKey, boolean extraValue)
    {
        /*"still N"*/
        //notificationHelper.createNotification(title,message , extraKey ,extraValue,"Still There List ", notifyId,true,true, R.color.red_button,R.mipmap.still_c);


    }

    public void stopStillNotifi(final int  notifyId, final String missTitle, final String missMsg , final String extraKey, boolean extraValue)
    {
        String timeFinishFormat = new java.text.SimpleDateFormat("hh:mm a", new Locale("en")).format(System.currentTimeMillis());
        String timeAppearFormt = new java.text.SimpleDateFormat("hh:mm a", new Locale("en")).format(timeAppear);

        String missMsgWithTime = "still there appeared from : "+timeAppearFormt + "to "+ timeFinishFormat ;
      //  notificationHelper.createNotification(missTitle,missMsgWithTime, extraKey, false, "Still There List ", notifyId, false, false, R.color.colorStillThere);

    }
}
