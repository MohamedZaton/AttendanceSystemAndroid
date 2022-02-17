package com.pclink.attendance.system.ThreadTasks;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.pclink.attendance.system.Activities.SplashScreen;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.NetworkServer.HttpHandler;
import com.pclink.attendance.system.Notification.NotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.content.Context.LOCATION_SERVICE;

public class MainAsynctask extends AsyncTask<Void,Void,Void> {

    Context context;
    int stage;
    NotificationHelper notificationHelper;

    Boolean loginFlag=true;
    String promoterIdSp;
    public   String err ;
    public  boolean notConnected=false,isNewVirsion=false;
    SharedPreferences sharedPreferences;
    public String linkDownloadVersion="www.google.com";
    String getnewVersion="";
    final   String WORK_PROMOTER_VERSION="EAT 1.0.0";


    public MainAsynctask(Context context,int stage)
    {
        this.context=context;
        this.stage=stage;

    }
    public MainAsynctask(){}



    public static boolean isLocationEnabled(Context context)
    {
        int locationMode = 0;
        String locationProviders;
        final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return  true;

        }
           return  false;

    }
    public boolean isConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo info=connectivityManager.getActiveNetworkInfo();

            if(info!=null)
            {
                if(info.getState()==NetworkInfo.State.CONNECTED)
                {
                    return true;
                }

            }
        }
        return  false;
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        notificationHelper=new NotificationHelper(context);
       DialogAll dialogAll =new DialogAll(context);
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");

        } catch (UnknownHostException e)
        {
                  notConnected=true;
        }
       // check version
        HttpHandler httpHandler = new HttpHandler();
        String url = "https://api.myjson.com/bins/penyg" ; // check version
        String jsonString = "";
        try {
            jsonString = httpHandler.makeHttpRequest(httpHandler.createUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("error json", "down json server version work  promoter ");
        }

        Log.i("json file ", "Response from url: " + jsonString);

        if (jsonString != null)
        {
            try {
                JSONObject jOt = new JSONObject(jsonString);
                getnewVersion = jOt.getString("version");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error json object", "json object not found fff");
            }

        } else {

        }
        if ( getnewVersion.equals(WORK_PROMOTER_VERSION))
        {

        }
        else
        {
            isNewVirsion=true;
        }

        return null;

    }



    @Override
    protected void onPostExecute(Void result ) {

        DialogAll dialogAll=new DialogAll(context);

          if(notConnected)
          {
              dialogAll.notConnectServer();

          }

          else if(isNewVirsion)
          {
              dialogAll.newVersion(getnewVersion,linkDownloadVersion);

          }










        else  if(!isConnected())
        {
            DialogAll dg=new DialogAll(context,SplashScreen.class);
            dg.dialogMsgConnect();


            err="No Network Please check your  internet connection and try again ";
            Toast.makeText(context, err,Toast.LENGTH_LONG).show();
        }

        if(!getnewVersion.equals("") && !getnewVersion.equals(WORK_PROMOTER_VERSION))
        {
            Log.e("new version","download new version from websites ");
           // Toast.makeText(context, "new version " + getnewVersion, Toast.LENGTH_SHORT).show();

            dialogAll.newVersion(getnewVersion,linkDownloadVersion);

        }


        super.onPostExecute(result);


    }


}
