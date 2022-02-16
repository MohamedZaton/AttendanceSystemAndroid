package com.pclink.attendance.system.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pclink.attendance.system.Camera.CameraApi;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.InterfaceHelper.VolleyInterFaceHelper;
import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.Permission.PermissionCheck;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.Report.SettingReport;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class OfflineUploadActivity extends AppCompatActivity {
    public com.pclink.attendance.system.DataBase.SharedPrefData sharedPrefData;
    public String keysStr, keyListType[];
    public Button syncBtn  ;
    public ImageView backBtnImgVw;
    public  boolean  isSend = false;
    public  int  inComplete=0 , progressLoadingPercentage=1;
    public Handler handler;
    public Runnable runnable;
    public  ProgressBar loadingPgBar ;
    public  MainAsynctask mainAsynctask;
    public NetworkRequestPr networkRequestPr;
    private  String offlineFile= DataConstant.offlineModeFile;
    public   JsonPr jsonPr;
    public DialogAll dialogAll ;

    /* permission */

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    // lists for permissions
    PermissionCheck permissionCheck;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_upload);
        syncBtn = findViewById(R.id.sync_btn);
        loadingPgBar = findViewById(R.id.loading_offline_progressBar);
        backBtnImgVw = findViewById(R.id.back_arrow_btn_image);

        sharedPrefData =new SharedPrefData(OfflineUploadActivity.this);
        dialogAll = new DialogAll(this);
        keysStr = sharedPrefData.getElementValue(DataConstant.offlineModeFile,DataConstant.recordOfflineKeys);

        networkRequestPr= new NetworkRequestPr(OfflineUploadActivity.this,"offline loading ");

        // --------------------------------New permission

        //checkDrawOverPermission();

        permissionCheck = new PermissionCheck(OfflineUploadActivity.this, ALL_PERMISSIONS_RESULT);
        permissions = permissionCheck.permissionListCheckBreak();       // all permission needed
        permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);

            if (permissionsToRequest.size() > 0)
            {
                Intent i = new Intent(OfflineUploadActivity.this, MainActivity.class);
                startActivity(i);

            }
        }

        mainAsynctask = new MainAsynctask(this,2222);
        keyListType=keysStr.split(",");
        jsonPr = new JsonPr();
        final String urlUploading = DataConstant.serverUrl + DataConstant.offlineControl +DataConstant.offlineAction;
        final int indexStart = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile,DataConstant.indexUploadOffline);


//s   sharedPrefData.removeFileSp(offlineFile);





        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncBtn.setVisibility(View.GONE);
                loadingPgBar.setVisibility(View.VISIBLE);

                if(mainAsynctask.isConnected() )
                {


                    MyOffAsyncTask myOffAsyncTask = new MyOffAsyncTask(OfflineUploadActivity.this, urlUploading);
                    myOffAsyncTask.execute();
                }
                else
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_offline_nn,"Failed Upload","Please check your network","OK" ,OfflineUploadActivity.class);


                    syncBtn.setVisibility(View.VISIBLE);

                }
            }
        });


        backBtnImgVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OfflineUploadActivity.this,MainActivity.class);
                startActivity(i);
            }
        });



    }

    @Override
    protected void onStart() {




        super.onStart();
    }




    public class MyOffAsyncTask extends AsyncTask<String,Integer, String>
    {

        @Override
        public void onProgressUpdate(Integer... values)
        {


        }

        private Context contextTk;
        private String urlTK ,  urlSalesRep;
        private SettingReport  settingReport ;


        public MyOffAsyncTask(Context hostContext , String urlTK )
        {
            contextTk = hostContext;
            this.urlTK=urlTK;
            urlSalesRep = DataConstant.serverUrl+DataConstant.offlineSalesRepUrl;
            settingReport = new SettingReport(contextTk);
        }

        @Override
        protected String doInBackground(String... params)
        {
                settingReport.getReqSetting();

            final int indexStart = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile,DataConstant.indexUploadOffline);
            // Method runs on a separate thread, make all the network calls you need
            NetworkRequestPr networkRequestPrTT = new NetworkRequestPr(contextTk,"thread  fetch future Request");





                if (indexStart != -1) {


                    for (int i = 0; i < keyListType.length; i++)   //indexStart
                    {
                        final Map<String, String> uploadBodyMap = sharedPrefData.getLoadMap(DataConstant.offlineModeFile, keyListType[i]);
                        networkRequestPrTT.uploadingFutchRequestVolley(urlTK, uploadBodyMap, i);
                        publishProgress((int) (((i + 1) / (float) keyListType.length) * 100));
                    }

                }

            return "f";


        }


        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(String result)
        {
            String incompleted= result;

            if(incompleted.equals("f"))
            {

                inComplete=  sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile,DataConstant.indexUploadOffline);




                if(inComplete == -1)
                {

                    sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.indexUploadOffline,0);
                    sharedPrefData.removeFileSp(offlineFile);
                    syncBtn.setVisibility(View.GONE);
                    loadingPgBar.setVisibility(View.GONE);
                    Intent i = new Intent(OfflineUploadActivity.this,MainActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(contextTk, "Please contact with your support,You have problem uploading", Toast.LENGTH_SHORT).show();

                    syncBtn.setVisibility(View.VISIBLE);

                }



            }

            super.onPostExecute(result);
        }
    }











}


//////////// olde request

/*
                   networkRequestPr.uploadingOfflineRequestVolley(urlUploading, uploadBodyMap, "uploading " + keyListType[i], new VolleyInterFaceHelper() {
                         @Override
                         public boolean onSuccess(JSONObject result) {

                             if(uploadBodyMap.containsKey(DataConstant.checkTypeJsonKey))
                             {


                                 if (uploadBodyMap.get(DataConstant.checkTypeJsonKey).equals(DataConstant.checkInType))
                                 {

                                     String checkinId = (!result.has(DataConstant.checkInIDJsonKey)) ? "" : jsonPr.getValueObjtJson(result.toString(), DataConstant.checkInIDJsonKey, "s");


                                     sharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkInIDJsonKey, checkinId); // Save CheckID responce

                                     sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.isSendOfflineFlag, true);


                                 }

                             }




                             return true;
                         }
                     });*/