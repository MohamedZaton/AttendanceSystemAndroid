package com.pclink.attendance.system.TabBreak;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.pclink.attendance.system.Activities.OfflineUploadActivity;
import com.pclink.attendance.system.Camera.CameraApi;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.LocationFind.GpsTracker;
import com.pclink.attendance.system.Permission.PermissionCheck;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.DateAndTime.TimeHelper;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class BreakFragment extends Fragment {
    Button breakInButton;
    Button breakOutButton;
    Chronometer breakChronometer;
    boolean isFackLoction;


    int h,m,s;
    private String offlineFile = DataConstant.offlineModeFile;
    private DialogAll dialogAll ;
    String stageWheel;
    MainAsynctask mainAsynctask;

    // Sharedpref file data
    String promoterNameFileSp=DataConstant.promoterDataNameSpFile,
            promoterImgeSp=DataConstant.imageSP;


    private com.pclink.attendance.system.DataBase.SharedPrefData SharedPrefData;

    private  String breakTypeEnter = "checkTypeEnter";
    /* permission */


    // lists for permissions
    PermissionCheck permissionCheck;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    public BreakFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_break, container, false);

        SharedPrefData =new SharedPrefData(getActivity());

        breakInButton=view.findViewById(R.id.break_in_button);
        breakOutButton=view.findViewById(R.id.break_out_button);
        breakChronometer=view.findViewById(R.id.break_chronometer);
        mainAsynctask = new MainAsynctask(getActivity(), 22);
        dialogAll = new DialogAll(getContext());

        breakChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged)
            {
                breakChronometer = chronometerChanged;
                stageWheel=SharedPrefData.stageWheelNow();
                Long arraiveAtCikin= SharedPrefData.getElementLongValue(promoterNameFileSp,DataConstant.CheckInTimeSp);
                Long breakInTime = SharedPrefData.getElementLongValue(promoterNameFileSp,DataConstant.pausebreakInWheelKey);
                Long breakOutTime= SharedPrefData.getElementLongValue(promoterNameFileSp,DataConstant.resumebreakInWheelKey);

                // --------------------------------New permission

                //checkDrawOverPermission();

                permissionCheck = new PermissionCheck(getActivity(),ALL_PERMISSIONS_RESULT);
                permissions  =permissionCheck.permissionListCheckBreak();       // break permission needed
                permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);


                long time = System.currentTimeMillis()  - breakChronometer.getBase();


                h   = (int)(time /3600000);
                m = (int)(time - h*3600000)/60000;
                s= (int)(time - h*3600000 - m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                breakChronometer.setText(t);
            }
        });

        breakProgressWheel();


        breakInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isFackLoction = SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey);

                if(isFackLoction)
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_fake,"Location",getResources().getString(R.string.fake_message),"ok",null);
                    GpsTracker gpsTracker = new GpsTracker(getContext());
                    gpsTracker.fakeLocRequestServer(DataConstant.breakInType); // break in 3

                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (permissionsToRequest!= null )
                    {
                        if( permissionsToRequest.size() > 0)
                        {
                            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                        }
                    } else
                        {
                            if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                                Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                                startActivity(i);
                            }

                        Intent i = new Intent(getActivity(), CameraApi.class);
                        i.putExtra(breakTypeEnter, "Break in");
                        startActivity(i);
                    }
                }
                else
                 {
                     if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                         Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                         startActivity(i);
                     }

                    Intent i = new Intent(getActivity(), CameraApi.class);
                    i.putExtra(breakTypeEnter, "Break in");
                    startActivity(i);
                 }
            }
        });

        breakOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFackLoction = SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey);

                if(isFackLoction)
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_fake,"Location",getResources().getString(R.string.fake_message),"ok",null);
                    GpsTracker gpsTracker = new GpsTracker(getContext());
                    gpsTracker.fakeLocRequestServer(DataConstant.breakOutType); // break out 4

                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionsToRequest!= null )
                    {

                         int a = permissionsToRequest.size();

                        if (permissionsToRequest.size() > 0)
                        {
                            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                        } else {
                            if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                                Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                                startActivity(i);
                            }

                            Intent i = new Intent(getActivity(), CameraApi.class);
                            i.putExtra(breakTypeEnter, "Break out");
                            startActivity(i);
                        }
                    } else {
                        Intent i = new Intent(getActivity(), CameraApi.class);
                        i.putExtra(breakTypeEnter, "Break out");
                        startActivity(i);
                    }
                }else
                     {
                         if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                             Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                             startActivity(i);
                         }


                        Intent i = new Intent(getActivity(), CameraApi.class);
                        i.putExtra(breakTypeEnter, "Break out");

                        startActivity(i);
                    }
            }
        });


        return view;


    }


    public void  breakProgressWheel()
    {
        TimeHelper timeHelper=new TimeHelper(getContext());


        stageWheel=SharedPrefData.stageWheelNow();
        Long arraiveAtCikin = SharedPrefData.getElementLongValue(promoterNameFileSp,DataConstant.CheckInTimeSp);
        Long breakInTime    = SharedPrefData.getElementLongValue(promoterNameFileSp,DataConstant.pausebreakInWheelKey);
        Long breakOutTime   = SharedPrefData.getElementLongValue(promoterNameFileSp,DataConstant.resumebreakInWheelKey);

        switch (stageWheel)
        {
            case DataConstant.startCheckWheelSp :
                breakChronometer.stop();
                breakChronometer.setText("00:00");


                break;
            case DataConstant.resumeCheckWheelSp :

                breakChronometer.stop();
                breakChronometer.setText("00:00");
                break;

            case DataConstant.pauseCheckWheelSp :
                Long timeBs =breakInTime;

                breakChronometer.setBase(timeBs);

                breakChronometer.start();

                break;

            case DataConstant.stopCheckWheelSp :

                breakChronometer.stop();
                breakChronometer.setText("00:00");

                break;


        }









        long getworkHours= 1; //  get workHours from  AsyncTask  request;

        final double workHours= TimeUnit.HOURS.toMillis( getworkHours);
        final double oneHour= TimeUnit.HOURS.toMillis( 1);
        final double oneMinute =TimeUnit.MINUTES.toMillis(1) ;

        //"one hour"





    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart()
    {
        super.onStart();

        onStartBreakSwitchButton();   //switch between break in , out

    }

      public void onStartBreakSwitchButton()
      {

          String switchButton=SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.breakToggleButtonSP);

          if(switchButton.equals(""))
          {

              breakInButton.setEnabled(false);
              breakInButton.setBackgroundResource(R.drawable.btn_disenable_gray);
              breakOutButton.setVisibility(View.GONE);

          }

          else if( switchButton.equals(DataConstant.breakInType) )
          {
              breakInButton.setVisibility(View.VISIBLE);
              breakOutButton.setVisibility(View.GONE);

          }
          else if(switchButton.equals(DataConstant.breakOutType))
          {
              breakInButton.setVisibility(View.GONE);
              breakOutButton.setVisibility(View.VISIBLE);

          }
      }
}