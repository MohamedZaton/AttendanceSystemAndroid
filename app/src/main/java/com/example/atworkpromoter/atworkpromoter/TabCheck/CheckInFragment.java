package com.pclink.attendance.system.TabCheck;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.Activities.OfflineUploadActivity;
import com.pclink.attendance.system.Camera.CameraApi;
import com.pclink.attendance.system.Chat.MessageListActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.FilesAttachmentUploading.FilesReportActivity;
import com.pclink.attendance.system.LocationFind.GpsTracker;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.Notification.NotificationHelper;
import com.pclink.attendance.system.Permission.PermissionCheck;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.DateAndTime.TimeHelper;
import com.pclink.attendance.system.Report.GoogleFormActivity;
import com.pclink.attendance.system.Report.ReportFormActivity;
import com.pclink.attendance.system.Report.SettingReport;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.sendbird.android.ConnectionManager;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class CheckInFragment extends Fragment {
    Handler handler;
    View view;
    Promoter promoter;
    android.support.v7.app.AlertDialog exitDialog;
    MainAsynctask mainAsynctask;
    Bundle extrasClKin;
    double iprogressSeconds = 1000.0;
    private com.pclink.attendance.system.DataBase.SharedPrefData SharedPrefData;
    private DialogAll dialogAll;
    Chronometer timeChronometer;
    TextView usernameTxT, timeStageTxT,storeNameTxT;
    Button checkInButton;
    FloatingActionButton messageChatBtn , uploadFltBtn ;
    Button checkOutButton;
    String urlProListGet;
    String urlCompListGet;
    NetworkRequestPr networkRequestPr;
    ImageView imageUploadR;
    RelativeLayout loadingRlLyt;
    RelativeLayout buttonsRlLyt;
    NotificationHelper notificationHelper;
    String stageWheel;
    public static int MY_IGNORE_OPTIMIZATION_REQUEST = 7589;
    int h, m, s, newTimeWheel;
    CircularProgressIndicator circularProgress;
    private String checkTypeEnter = "checkTypeEnter";
    private String hasReport = "hasReport";
    private String offlineFile = DataConstant.offlineModeFile;
    ImageView profileimageView;
    double workHours;
    double oldSecond;
    boolean isFackLoction;
    TimeHelper timeHelper;
    // Sharedpref file data
    String promoterNameFileSp = DataConstant.promoterDataNameSpFile,
            promoterImgeSp = DataConstant.imageSP;




    /* permission */

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    // lists for permissions
    PermissionCheck permissionCheck;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    public SettingReport settingReport ;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exitDialog != null) {
            exitDialog.dismiss();
            exitDialog.cancel();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (exitDialog != null) {
            exitDialog.dismiss();
            exitDialog.cancel();
        }

    }
    public CheckInFragment() {


    }

    public FragmentActivity getActivityFragment() {
        return getActivity();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationHelper = new NotificationHelper(getContext());
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onStart() {
        super.onStart();



        onStartSwitchCheckButtton();


    }

    private void onStartSwitchCheckButtton() {
        String switchButton = "", stchBreckButton = "", timeStage = "";

        timeStage = SharedPrefData.getElementValue(promoterNameFileSp, DataConstant.timeStageKeySp);

        switchButton = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.checkToggleButtonSP);


        stchBreckButton = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.breakToggleButtonSP);


        if (switchButton.equals(DataConstant.checkInType)) {

            timeStageTxT.setText("leave at :" + timeStage);
            checkInButton.setVisibility(View.VISIBLE);
            checkOutButton.setVisibility(View.GONE);

                notificationHelper.stopStillThereNotificationRecever();    // add this to make sure still there Stopped


        } else if (!stchBreckButton.equals(DataConstant.breakOutType) && switchButton.equals(DataConstant.checkOutType)) {

            timeStageTxT.setText("arrive at :" + timeStage);
            checkInButton.setVisibility(View.GONE);
            checkOutButton.setVisibility(View.VISIBLE);

        } else if (stchBreckButton.equals(DataConstant.breakOutType)) {
            checkOutButton.setEnabled(false);
            checkOutButton.setBackgroundResource(R.drawable.btn_disenable_gray);
            checkOutButton.setVisibility(View.VISIBLE);
            checkInButton.setVisibility(View.GONE);


        } else {

            timeStageTxT.setText("leave at :" + timeStage);
            SharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.LogOutClk,true);
            checkInButton.setVisibility(View.VISIBLE);
            checkOutButton.setVisibility(View.GONE);



                notificationHelper.stopStillThereNotificationRecever();    // add this to make sure still there Stopped



        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_check_in, container, false);
        timeChronometer = view.findViewById(R.id.chronometer);
        timeHelper = new TimeHelper(getContext());
        timeStageTxT = view.findViewById(R.id.current_time_user_txtvw);
        checkInButton = view.findViewById(R.id.check_in_button);
        checkOutButton = view.findViewById(R.id.check_out_button);
        messageChatBtn = view.findViewById(R.id.chat_floatingActionButton);
        uploadFltBtn = view.findViewById(R.id.upload_file_floating_ac_btn);
        usernameTxT = view.findViewById(R.id.name_account_user_txtvw);
        profileimageView = view.findViewById(R.id.profile_image_imageview);
        circularProgress = view.findViewById(R.id.circular_progress);
        loadingRlLyt = view.findViewById(R.id.wait_loading_rlyt);
        buttonsRlLyt = view.findViewById(R.id.checks_buttons_rlyt);
        storeNameTxT = view.findViewById(R.id.store_name);
        extrasClKin = getActivity().getIntent().getExtras();
        SharedPrefData = new SharedPrefData(getActivity());
        mainAsynctask = new MainAsynctask(getActivity(), 22);
        networkRequestPr = new NetworkRequestPr(getActivity(),"check in ");
        promoter = new Promoter(getActivity());
        settingReport = new SettingReport(getContext());
        isFackLoction = SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey);

        String getstoreName = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.storeNameSp);
         String getname = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.promoterUserName);
        usernameTxT.setText(getname);
        storeNameTxT.setText(getstoreName);
        String encodeImage = SharedPrefData.getElementValue(promoterNameFileSp, promoterImgeSp);
        Bitmap imageProfile = getEncodeImageProfileToDecodeBitmap(encodeImage);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageProfile);

        //setting radius

        roundedBitmapDrawable.setCornerRadius(200.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        profileimageView.setImageDrawable(roundedBitmapDrawable);

        // end radius

        // --------------------------------New permission

        //checkDrawOverPermission();

        permissionCheck = new PermissionCheck(getActivity(), ALL_PERMISSIONS_RESULT);
        permissions = permissionCheck.permissionListCheckBreak();       // all permission needed
        permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);
        dialogAll = new DialogAll(getActivity());
        String mystring = getResources().getString(R.string.exit_ask_app);

        // sku url list pro and comp

         urlProListGet = DataConstant.serverUrl + DataConstant.ptGetControl + DataConstant.ptGetAction + SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase)+DataConstant.prodSkuId;
         urlCompListGet = DataConstant.serverUrl + DataConstant.ptGetControl + DataConstant.ptGetAction + SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase)+DataConstant.compSkuId;




        if(SharedPrefData.isExistsKey( DataConstant.promoterDataNameSpFile,DataConstant.locationFlag)&&!SharedPrefData.getElementBooleanValue( DataConstant.promoterDataNameSpFile,DataConstant.locationFlag))
         {
             networkRequestPr.getPromoterDataCheckOut();
         }
        timeChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                timeChronometer = chronometerChanged;
                stageWheel = SharedPrefData.stageWheelNow();

                long time = System.currentTimeMillis() - timeChronometer.getBase();

                h = (int) (time / 3600000);
                m = (int) (time - h * 3600000) / 60000;
                s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                timeChronometer.setText(t);
            }
        });


        checkInProgressWheel();


        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // white list permission
                whiteListAppPermissionRequest();
                settingReport.getReqSetting();
                boolean isLateFillForm=SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.hasLateFormKey);
                String bodyFormLate= SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.bodyFormLate);
                isFackLoction = SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey);

                if(isFackLoction)
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_fake,"Location",getResources().getString(R.string.fake_message),"ok",null);
                    GpsTracker gpsTracker = new GpsTracker(getContext());
                        gpsTracker.fakeLocRequestServer(DataConstant.checkInType); // check in 1


                }
               else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);

                    if (permissionsToRequest.size() > 0)
                    {
                        buttonsRlLyt.setVisibility(View.GONE);
                        loadingRlLyt.setVisibility(View.VISIBLE);
                        Log.e("permissionWit!", "wait permission XXXXXX1");

                        for (String perm : permissionsToRequest.toArray(new String[permissionsToRequest.size()]))
                            {
                                if (!shouldShowRequestPermissionRationale(perm))
                                {
                                    Log.i("NotShowPerm",perm);
                                     dialogAll.notShowPerm();
                                    break;
                                }
                            }

                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                        buttonsRlLyt.setVisibility(View.VISIBLE);
                    }
                    if (permissionsToRequest.size() <= 0)
                    {
                        loadingRlLyt.setVisibility(View.GONE);
                        buttonsRlLyt.setVisibility(View.VISIBLE);
                        Log.e("finish", "wait permission FFFF");
                        if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                            Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                            startActivity(i);
                        }else if(isLateFillForm && !bodyFormLate.equals("")){
                            Intent i  = new Intent(getActivity(), GoogleFormActivity.class);
                            i.putExtra(DataConstant.bodyFormLate,bodyFormLate);
                            startActivity(i);
                        } else if (!SharedPrefData.isExistsKey(offlineFile, DataConstant.checkInOffline) || !SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys)) {
                            Intent i = new Intent(getActivity(), CameraApi.class);
                            i.putExtra(checkTypeEnter, "check in");
                            startActivity(i);

                        } else {
                            Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                            startActivity(i);
                        }
                    }
                } else
                    {
                        if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                            Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                            startActivity(i);
                        }else if(isLateFillForm && !bodyFormLate.equals("")){
                            Intent i  = new Intent(getActivity(), GoogleFormActivity.class);
                            i.putExtra(DataConstant.bodyFormLate,bodyFormLate);
                            startActivity(i);
                        } else if(!SharedPrefData.isExistsKey(offlineFile, DataConstant.checkInOffline) || !SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys)) {
                            Intent i = new Intent(getActivity(), CameraApi.class);
                            i.putExtra(checkTypeEnter, "check in");
                            startActivity(i);

                        }  else
                            {
                            Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                            startActivity(i);
                        }

                    }






            }
        });

        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                boolean isReport=SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.hasReportKey);
                boolean isLateFillForm=SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.hasLateFormKey);

                if (extrasClKin != null)
                {
                    isReport=extrasClKin.getBoolean(hasReport);
                }

                isFackLoction = SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey);

                if(isFackLoction)
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_fake,"Location",getResources().getString(R.string.fake_message),"ok",null);
                    GpsTracker gpsTracker = new GpsTracker(getContext());
                    gpsTracker.fakeLocRequestServer(DataConstant.checkOutType); // check out 2

                }
               else  if(SharedPrefData.isExistsKey(DataConstant.offlineModeFile,DataConstant.reportStoreOffline) &&SharedPrefData.isExistsKey(offlineFile, DataConstant.checkOutOffline))  // report +check out
                {

                    Intent x = new Intent(getActivity(), OfflineUploadActivity.class);
                    startActivity(x);

                }
                else if(isLateFillForm)
                {
                    Toast.makeText(getContext(), "Don't forget fill sales form when you are connecting network .", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), CameraApi.class);
                    i.putExtra(checkTypeEnter, "check out");
                    startActivity(i);
                }
                else if(!isReport)
                {
                    Toast.makeText(getActivity(), "Please complete sales report first ", Toast.LENGTH_SHORT).show();

                    Intent x = new Intent(getActivity(), GoogleFormActivity.class);
                    startActivity(x);

                }   // report check offline
                else {
                    Toast.makeText(getActivity(), "Now you can check out", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionsToRequest.size() > 0) {
                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                    } else {
                        if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                            Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                            startActivity(i);
                        } else if (!SharedPrefData.isExistsKey(offlineFile, DataConstant.checkOutOffline) || !SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys)) {
                            Intent i = new Intent(getActivity(), CameraApi.class);
                            i.putExtra(checkTypeEnter, "check out");
                            startActivity(i);

                        } else {
                             Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                            startActivity(i);
                        }

                    }
                } else {
                    if (SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys) && mainAsynctask.isConnected()) {
                        Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                        startActivity(i);
                    } else if (!SharedPrefData.isExistsKey(offlineFile, DataConstant.checkOutOffline) || !SharedPrefData.isExistsKey(offlineFile, DataConstant.recordOfflineKeys)) {
                        Intent i = new Intent(getActivity(), CameraApi.class);
                        i.putExtra(checkTypeEnter, "check out");
                        startActivity(i);

                    } else {
                        Intent i = new Intent(getActivity(), OfflineUploadActivity.class);
                        startActivity(i);
                    }

                }

                }

            }
        });

        messageChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String agancyId = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);
                String supervisorId =  SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.supervisorID );
                if(!supervisorId.equals(""))
                {
                    List<String> usersIDs = new ArrayList<>();
                    usersIDs.add(agancyId);
                    usersIDs.add(supervisorId);
                    connectToSendBird(agancyId, usernameTxT.getText().toString());
                    createGroupChannel(usersIDs, true);
                }
                else
                {
                    Toast.makeText(getContext(), "Not Found Your Supervisor", Toast.LENGTH_SHORT).show();
                }
            }
        });
        uploadFltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FilesReportActivity.class));
            }
        });

        skuGetReq(urlProListGet,DataConstant.skuProListJsonKey);
        skuGetReq(urlCompListGet,DataConstant.skuCompListJsonKey);

        return view;
    }


    private void connectToSendBird(final String userId , String userName){

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading... Please wait");
        dialog.show();

        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (handler != null) {
                      Log.i("connected_bird","success_connected") ;
                    dialog.dismiss();

                  /*  Intent i = new Intent(getContext(),MessageListActivity.class);
                    startActivity(i);*/
                }
                else
                {
                    Log.i("connected_bird","filed_connected") ;

                    if (!mainAsynctask.isConnected())
                    {
                        dialog.dismiss();

                        dialogAll.dialogMsgConnect();
                    }
                }
            }
        });

        SendBird.updateCurrentUserInfo(userName, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!

                    return;
                }

            }
        });
    }

    private void createGroupChannel(List<String> userIds, boolean distinct) {

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Open Channel...");
        dialog.show();
        GroupChannel.createChannelWithUserIds(userIds, distinct, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {


                if (mainAsynctask.isConnected() && handler != null) {
                    Log.i("channel_bird","success_open") ;

                    dialog.dismiss();
                    try
                    {
                        Log.i("error_msg ", e.getMessage());
                    }
                    catch(NullPointerException errMsg)
                    {

                    }

                    Intent i = new Intent(getContext(),MessageListActivity.class);
                    i.putExtra("groupChannelUrl",groupChannel.getUrl());
                    startActivity(i);
                }
                else
                {
                    Log.i("channel_bird","field_open") ;

                    if (!mainAsynctask.isConnected())
                    {
                        dialog.dismiss();

                        Toast.makeText(getActivity(), "no network ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }



    private void skuGetReq(String urlProOrComp , final String saveJsonKey ) {
            final SharedPrefData sharedPrefData = new SharedPrefData(getActivity());
         RequestQueue queue;
        queue = Volley.newRequestQueue(getActivity());


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, urlProOrComp, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("ok_list_sku",response.toString()) ;
                sharedPrefData.putElement(DataConstant.skuFileSp, saveJsonKey, response.toString());


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error_list_sku",error.toString()) ;


            }
        });

        queue.add(jsObjRequest);

    }


    /*private void parseGJSON(String jsonString) {
        SharedPrefData sharedPrefData = new SharedPrefData(getActivity());
        String urlListGet = DataConstant.serverTestUrl + DataConstant.ptGetControl + DataConstant.ptGetAction + sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase);

        Gson gson = new Gson();
        Type type = new TypeToken<List<ProductModel>>() {
        }.getType();
        List<ProductModel> productList = gson.fromJson(jsonString, type);
        int sizelistCount = -1;

        if (sharedPrefData.isExistsKey(DataConstant.skuFileSp, DataConstant.itemsCodeSku)) {

            sharedPrefData.removeFileSp(DataConstant.skuFileSp);
        }

        for (ProductModel pdt : productList) {
            sizelistCount++;
            Log.i("product_Details", pdt.id + " - " + pdt.productName + " - " + pdt.sku);
            if (sizelistCount == 0) {
                sharedPrefData.putElement(DataConstant.skuFileSp, DataConstant.itemsIdSku, pdt.id + ",");
                sharedPrefData.putElement(DataConstant.skuFileSp, DataConstant.itemsNameSku, pdt.productName + ",");
                sharedPrefData.putElement(DataConstant.skuFileSp, DataConstant.itemsCodeSku, pdt.sku + ",");
            }
            else {
                String oldIds = sharedPrefData.getElementValue(DataConstant.skuFileSp, DataConstant.itemsIdSku);
                String oldNames = sharedPrefData.getElementValue(DataConstant.skuFileSp, DataConstant.itemsNameSku);
                String oldCodes = sharedPrefData.getElementValue(DataConstant.skuFileSp, DataConstant.itemsCodeSku);
                sharedPrefData.putElement(DataConstant.skuFileSp, DataConstant.itemsIdSku, oldIds + pdt.id + ",");
                sharedPrefData.putElement(DataConstant.skuFileSp, DataConstant.itemsNameSku, oldNames + pdt.productName + ",");
                sharedPrefData.putElement(DataConstant.skuFileSp, DataConstant.itemsCodeSku, oldCodes + pdt.sku + ",");

            }
        }
        sharedPrefData.putIntElement(DataConstant.skuFileSp, DataConstant.sizeSkuList, sizelistCount);


    }
*/

    public void checkInProgressWheel() {

        double getworkHours = promoter.getWorkingHoursTime();

        workHours = (getworkHours * 60);

        circularProgress.setMaxProgress(workHours);
        circularProgress.setCurrentProgress(0);

        circularProgress.setProgress(0, workHours); // 60 * 1000

        stageWheel = SharedPrefData.stageWheelNow();

        switch (stageWheel) {
            case DataConstant.startCheckWheelSp:
                Long timeW = timeHelper.getArrivedAtLong();
                timeChronometer.setBase(timeW);
                timeChronometer.start();
                break;
            case DataConstant.resumeCheckWheelSp:
                Long timer = timeHelper.getArrivedAtLong();
                timeChronometer.setBase(timer);
                timeChronometer.start();
                break;

            case DataConstant.pauseCheckWheelSp:
                Long timep = timeHelper.getArrivedAtLong();
                timeChronometer.setBase(timep);
                timeChronometer.start();
                break;


            case DataConstant.stopCheckWheelSp:

                timeChronometer.stop();
                timeChronometer.setText("00:00");

                break;


        }


        // you can get progress values using following getters
        circularProgress.getProgress(); // returns 5000
        circularProgress.getMaxProgress(); // returns 10000

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {


                //                iprogressSeconds=timeChronometer.getBase();

                long time = System.currentTimeMillis() - timeChronometer.getBase();
                handler.postDelayed(this, 500); // 1 secound = 1000
                iprogressSeconds = (double) ((h * 60) + m + (s / 60));

                /* long l =  (iprogressSeconds/workHours);*/
            /*    int k  k
                newTimeWheel =.intValue();;*/

                circularProgress.setCurrentProgress(iprogressSeconds);


                oldSecond = iprogressSeconds;
            }
        };

        handler.postDelayed(r, 1000);


    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(getActivity())) {
                // You don't have permission

            } else {


            }

        }

        if (requestCode == MY_IGNORE_OPTIMIZATION_REQUEST) {
            PowerManager pm = (PowerManager)getContext().getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getContext().getPackageName());
            if(isIgnoringBatteryOptimizations){
                // Ignoring battery optimization
                Log.i("battery_req","Add white list apps ");
            }else{
                // Not ignoring battery optimization
                Log.e("battery_req"," No Add white list apps ");
                dialogAll.backgroundRequest();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!permissionCheck.hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            permissionAlertAllRequest();
                            return;
                        }
                    }
                }

                break;
        }
    }

    public void permissionAlertAllRequest() {
        new android.support.v7.app.AlertDialog.Builder(getActivity()).
                setMessage("These permissions are mandatory to get your location. You need to allow them.").
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissionsRejected.
                                    toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                        }
                    }
                }).setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        getActivity().finish();
                        getActivity().finishAffinity();
                        System.exit(0);
                    }
                }).create().show();

    }


    @Override
    public void onResume()
    {
        super.onResume();

        String encodeImage = SharedPrefData.getElementValue(promoterNameFileSp, promoterImgeSp);
        Bitmap imageProfile = getEncodeImageProfileToDecodeBitmap(encodeImage);

        if (imageProfile != null) {
            profileimageView.setImageBitmap(imageProfile);
        }
    }

    public Bitmap getEncodeImageProfileToDecodeBitmap(String encodedImage)
    {
        if (encodedImage == null)
        {
            return null;
        }
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    public  void whiteListAppPermissionRequest()
    {

        PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);

        boolean isIgnoringBatteryOptimizations = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getContext().getPackageName());
            if(!isIgnoringBatteryOptimizations){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getContext().getPackageName());
            if(!isIgnoringBatteryOptimizations){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


}
