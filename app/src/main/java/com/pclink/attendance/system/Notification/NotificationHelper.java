package com.pclink.attendance.system.Notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.RealFireBase;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Models.NotificationModel;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;


public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Promoter promoter;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    SharedPrefData sharedPrefData;
    private  String promoterDataFileSp=DataConstant.promoterDataNameSpFile;
    MainAsynctask mainAsynctask;
    RealFireBase realFireBase ;
    public NotificationHelper(Context context)
    {
        mContext = context;
        sharedPrefData=new SharedPrefData(mContext);
        mainAsynctask = new MainAsynctask(mContext,2345);
        realFireBase = new RealFireBase(mContext);
    }

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message , String extraKey,boolean extraValue , String nameGroup, int notifyId,boolean isAutoCanceled , boolean isProgressbarLoading,int colorTitle,int imageInt ,boolean isCancelAll)
    {

        checkLockScreen();

        promoter=new Promoter(mContext);
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext , MainActivity.class);
            if(extraValue) {
                resultIntent.putExtra(extraKey, extraValue);
            }
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        KeyguardManager km = (KeyguardManager) mContext.getSystemService(KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("IN");
        kl.disableKeyguard();

        PowerManager pm = (PowerManager) mContext.getSystemService(POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "SMOK Komunal");
        wl.acquire();

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setColor(ContextCompat.getColor(mContext,colorTitle))
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), imageInt))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setGroup(nameGroup)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setContentIntent(resultPendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                 .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // This is the answer to OP's question, set the visibility of notification to public.
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if(extraValue){
            mBuilder.setOngoing(true);
        }
        if(isProgressbarLoading)
        {
            mBuilder.setProgress(100, 30, true);
        }
        if(isCancelAll)
        {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();  // cancel all
        }

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;

        if(isAutoCanceled)
        {
            mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            mBuilder.setAutoCancel(true);

        }

        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        wl.release();
        mNotificationManager.notify(notifyId , mBuilder.build()); // id for every notification


    }

    public void stillNotifiAMr(final int  notifyId, final String title, final String message , final String missTitle, final String missMsg , final String extraKey, boolean extraValue)
    {
        realFireBase.sendLogMsg("still there" , "notification appear ");

        createNotification(title,message , extraKey ,extraValue,"Still There List ", notifyId,false,true, R.color.colorStillThere,R.mipmap.still_c,true);
        sharedPrefData.putElementBoolean(promoterDataFileSp,DataConstant.stillRunning,true);
        final Long timeAppear = System.currentTimeMillis();

        Log.i("duration_set"," " + promoter.getStillDurationTime());

        new CountDownTimer(promoter.getStillDurationTime(), 1000)
        {

            public void onTick(long millisUntilFinished)
            {

            }
            public void onFinish() {

                boolean isclked = sharedPrefData.getElementBooleanValue(promoterDataFileSp, DataConstant.StillThereisClked);
                sharedPrefData.putElementBoolean(promoterDataFileSp,DataConstant.stillRunning,false);

                if (isclked){
                    try {
                        sharedPrefData.putElementBoolean(promoterDataFileSp,DataConstant.StillThereisClked,false);
                        mNotificationManager.cancel(notifyId);

                    } catch(NullPointerException e) { }
                    catch(Exception e) { }
                } else {
                    String timeFinishFormat = new java.text.SimpleDateFormat("hh:mm a", new Locale("en")).format(System.currentTimeMillis());
                    String timeAppearFormt = new java.text.SimpleDateFormat("hh:mm a", new Locale("en")).format(timeAppear);
                    String missMsgWithTime = "You missed \"Still There\"  at " + timeAppearFormt ;
                    realFireBase.sendLogMsg("still there" , " missed click ");

                    createNotification(missTitle, missMsgWithTime, extraKey, false, "Still There List ", notifyId, false, false, R.color.red_button,R.mipmap.still_a,false);
                    if (checkStillThereNotificationRunningMr()){
                        Log.i("still_there_is_run", "true");
                        Log.i("still_there_ntf", "while stop and update start still ");
                        //try this
                    }

                    if (mainAsynctask.isConnected()){
                        missStillTherePost(mContext,1 );
                    }else
                    {
                        missStillTherePost(mContext,2 );

                    }

                }

            }

        }.start();

        stopStillThereNotificationRecever();
        startCalenderStillThereNotificationRecever(timeAppear);







    }


    public void missStillTherePost(Context context,int connectFlag)
    {
        NetworkRequestPr networkRequestPr = new NetworkRequestPr(context,"MissedStillNotif");
        String url = DataConstant.serverUrl + DataConstant.clockingControl + DataConstant.postOnlineClockingAction;

        int checkType = Integer.parseInt(DataConstant.stillThereType);
        if(connectFlag==1)
        {
            Map<String, String> StillMessedTherebody = promoter.postMissedStillThereDataBody(connectFlag);
            networkRequestPr.postRequestVolley(url, StillMessedTherebody, checkType, null, "upLoading .....");
        }
        else if(connectFlag == 2) //offline
        {
            promoter.saveOfflineData(connectFlag,DataConstant.stillThereType);
        }

    }

    public void startCalenderStillThereNotificationRecever(Long timeNow)
    {
        Promoter promoter = new Promoter(mContext);

        if(checkStillThereNotificationRunningMr())
        {
            stopStillThereNotificationRecever();
        }
    if(promoter.getIntervalMinutesInt()!=0)
    {
        sharedPrefData.putIntElement(promoterDataFileSp, DataConstant.idNotifyStillKey, -1);
        Intent intent = new Intent(mContext, NotificationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, DataConstant.NotificationThreadReceverId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        // Long timeNow = System.currentTimeMillis();
        calendar.setTimeInMillis(timeNow);
        calendar.add(Calendar.MINUTE, promoter.getIntervalMinutesInt());
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en")).format(calendar.getTimeInMillis());
        Log.e("still_set_time", time);

        // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), promoter.getStillRepeatingIntervalMinutes(), pendingIntent); // XXX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("version", " >= .M");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i("version==", " >= Include lollipop");

            Log.i("version", " >= .K");
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            Log.i("version==", " < .k lower than kitkat");
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    }

    public  void stopStillThereNotificationRecever()
        {
            Promoter promoter = new Promoter(mContext);

            if(promoter.getIntervalMinutesInt()!=0)
            {
                try
                {
                    sharedPrefData.putIntElement(promoterDataFileSp,DataConstant.idNotifyStillKey,-2);
                    Intent intent = new Intent(mContext, NotificationAlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, DataConstant.NotificationThreadReceverId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    Log.i("still_there","ok stopped");
                }
                catch(IllegalArgumentException e)
                {

                    Log.i("still_there","error stopped empty");
                    e.printStackTrace();
                }
            }


        }
        public  boolean checkStillThereNotificationRunningMr()
        {
            //checking if alarm is working with pendingIntent
            Intent intent = new Intent(mContext, NotificationAlarmReceiver.class);//the same as up
            boolean isWorking = (PendingIntent.getBroadcast(mContext, DataConstant.NotificationThreadReceverId, intent, PendingIntent.FLAG_UPDATE_CURRENT) != null);//just changed the flag
            Log.d("is_still_there", "alarm is " + (isWorking ? "" : "not") + " working...");
            return isWorking;
        }

        public void checkLockScreen()
        {
            // Logic to turn on the screen
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);

            if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MH24_SCREENLOCK");
                wl.acquire(10000);
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MH24_SCREENLOCK");
                wl_cpu.acquire(10000);
            }

        }

        public  Boolean isNotificationEnabled()
        {
            return  NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        }


    public JSONObject bodyJsOjReq(Integer fromAgencyID, Integer toAgencyID, String message)
    {
        NotificationModel notifiModel = new NotificationModel(fromAgencyID,toAgencyID,message);
        JSONObject jsonObjBody = new JSONObject();
        Gson gson = new Gson();
        String stringJsonBody = gson.toJson(notifiModel);
        Log.i("body_notif_chat" , stringJsonBody);
        try
        {
            jsonObjBody = new JSONObject(stringJsonBody);
        } catch(JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObjBody;
    }


    public void  sendMessageNotification(Integer fromID, Integer toID, String msg )
    {
        String url = DataConstant.serverUrl + DataConstant.postChatUrl ;
        JSONObject jsonObject = bodyJsOjReq(fromID,toID,msg);
        postMsgAPI(url,jsonObject) ;


    }

    public void   postMsgAPI(String url  , JSONObject body)
    {

        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("ok_send_Notification", response.toString());

            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error_send_Notification", error.toString());

            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }


    public boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {
                NotificationChannel channel = mNotificationManager.getNotificationChannel(channelId);
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

}

