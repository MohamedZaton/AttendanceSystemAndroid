package com.pclink.attendance.system.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.pclink.attendance.system.Camera.CameraApi;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.LocationFind.GpsHelper;
import com.pclink.attendance.system.Models.SuperModel;
import com.pclink.attendance.system.Models.VacDaysModel;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.NetworkServer.NetworkUtil;
import com.pclink.attendance.system.Notification.NotificationHelper;
import com.pclink.attendance.system.Permission.PermissionCheck;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.TabsControl.categoryAdapter;
import com.pclink.attendance.system.LocationFind.GpsTracker;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.pclink.attendance.system.languages.LanguagesActivity;
import com.sendbird.android.SendBird;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, SmartScheduler.JobScheduledCallback {



    private TabLayout tabLayout;
    private DrawerLayout mDrLyt;
    private DialogAll dg;
    private NavigationView navigationView;
    private LatLng locationUser;
    private TextView spXName ,spXEmail,spXPhone;
    int notifid;
    boolean notifiTime = false;
    private  MenuItem offlineItem ;
    public  boolean isVacBalanceRequest = false ;

    private View viewHeaderLayout;
    private ActionBarDrawerToggle mToggle;
    private com.pclink.attendance.system.DataBase.SharedPrefData SharedPrefData;
    private FusedLocationProviderClient mFusedLocationClient;
    private NetworkRequestPr networkRequestPr;
    private Handler handler;
    public TextView tab_label, profHeadTimeStageTxT, nameHeadTxT,storeTxt;
    public ImageView tab_icon, profImage;
    public RelativeLayout tabLinerLayout;
    public MainAsynctask mainAsynctask;
    public  boolean stillopenAppeared = false ;
    public GpsHelper gpsHelper;
    private RequestQueue queue;

    public Long intvlGpsTime = TimeUnit.SECONDS.toMillis(1);
    Job jobLocation = null, jobStill = null;
    private NotificationHelper notificationHelper;
    private CheckBox dontShowAgain;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    public static int MY_IGNORE_OPTIMIZATION_REQUEST = 7589;

    final String skipMsgKey = DataConstant.skipMsgStillThereSp, promoterImgeSp = DataConstant.imageSP, stillThereOutAppkeySp = DataConstant.stillThereOutAppkey;
    private int JOB_ID_Still = DataConstant.still_there_thread_JOB_ID;
    private int JOB_ID_Loction = DataConstant.location_thread_JOB_ID;
    private  boolean stillAppear = false ;
    private  boolean askNotifPermission = false ;

        @Override
    protected void onStart() {
        super.onStart();
        Log.i("action_method","On_Start");
        SharedPrefData = new SharedPrefData(this);
        dg = new DialogAll(this, MainActivity.class);
        if(!askNotifPermission)
        {
            if(!notificationHelper.isNotificationEnabled())
            {
                Log.i("notification","Not Enabled");
                dg.notificationEnableMsg();
                askNotifPermission=true;
            }
        }
        if(!stillAppear ) {
            stillAppear = true ;
            processIntent(getIntent());
        }
        if(!isVacBalanceRequest){
            isVacBalanceRequest = true ;
            balanceGetReq();
            vacMenuGetReq();
        }
        // offline text appear nav bar
                invalidateOptionsMenu();
    }

    //new location
    private Location location;
    private final static String TAG = "Main_Activity";
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    private Promoter promoter;
    private AlertDialog gpsAlertDg = null;
    //----------------------------------------------------------------------------------------------------------

    // lists for permissions
    PermissionCheck permissionCheck;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    //  tabs icons and text labels

    private int[] navIconsActive = DataConstant.navIconsActive;
    private int[] navLabels = DataConstant.navLabels;
    private int[] navIcons = DataConstant.navIcons;

    // sp shared_pref data promoter

    private String promoterDataFileSp = DataConstant.promoterDataNameSpFile,
            stillThereKeySp = DataConstant.stillThereRunKeysp, agencyIDkeySp = DataConstant.agencyIDJsonKeyUpcase,
            notificationStillKeySp = DataConstant.stillNotificationKeySp;

        private  StillAync stillAync  = new StillAync();
    @Override
    protected void onStop()
    {

        super.onStop();
        // --------------------------------- alarmManger notification

        if (gpsAlertDg != null)
        {
            gpsAlertDg.dismiss();
            gpsAlertDg.cancel();

         }
         if(stillDialog!=null)
         {
             stillDialog.dismiss();
             stillDialog.cancel();
         }
         Log.e("stop","xxx xx ");

        gpsHelper.stopGpsRecever();

        NetworkUtil.stopConnectReciver(MainActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        String stageCheckOut = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.checkToggleButtonSP);
        if (stageCheckOut.equals(DataConstant.checkOutType)) {

            if (rippleBackground.isRippleAnimationRunning()) {
                rippleBackground.stopRippleAnimation();
            }
            if (stillDialog.isShowing()) {
                stillDialog.dismiss();
                stillDialog.cancel();
            }
        }

        if (gpsAlertDg != null) {
            gpsAlertDg.dismiss();
            gpsAlertDg.cancel();
        }

        gpsHelper.stopGpsRecever();


    }

    // -----------------------------------------------------------------------------------------------Maps
    LocationManager locationManager;
    GoogleMap mgoogleMap;
    FloatingActionButton closeTextview;
    Dialog mapDialog;
    Dialog stillDialog;
    Long intervillTimeGps = TimeUnit.SECONDS.toMillis(10);
    AlertDialog stillAlertDialog;
    boolean still_n = false;
    Button stillThereButton, backButton;
    FloatingActionButton stillThereCloseButton;
    private String stillTypeEnter = "checkTypeEnter";
    RippleBackground rippleBackground;
    static Menu menu ;
    public    double[] myList = {1.9, 2.9, 3.4, 3.5};

    // -------------------------------------------------------------------- layout
    LinearLayout homeLyt, aboutLyt;
    ScrollView helpLyt;

   //-------------------------------------------------------------super
    LinearLayout superAllLyt,superDataLyt;
    ProgressBar superLoadingPrgBar;
    private static final String ONESIGNAL_APP_ID ="b7677aac-b8f1-4eae-806e-edf860b250c4";




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        offlineItem = menu.findItem(R.id.offline_txt_item);
        this.menu = menu;

        // offline text appear nav bar
        offlineCikItem(mainAsynctask.isConnected());
        NetworkUtil.startConnectReciver(MainActivity.this);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        offlineItem = menu.findItem(R.id.offline_txt_item);
        // offline text appear nav bar
        offlineCikItem(mainAsynctask.isConnected());
        return super.onPrepareOptionsMenu(menu);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("action_method","on_create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        SendBird.init(DataConstant.APP_Msg_ID, getApplicationContext());

        homeLyt = findViewById(R.id.home_main_lyt);
        aboutLyt = findViewById(R.id.about_lyt);
        helpLyt = findViewById(R.id.help_lyt_Scroll);
        superAllLyt = findViewById(R.id.super_lyts_all);
        superDataLyt =findViewById(R.id.super_data_lyts);
            spXName = findViewById(R.id.name_super_txt);
            spXEmail = findViewById(R.id.super_email_txt);
            spXPhone = findViewById(R.id.super_phone_txt);
            superLoadingPrgBar = findViewById(R.id.loading_super);
        homeLyt.setVisibility(View.VISIBLE);
        aboutLyt.setVisibility(View.GONE);
        helpLyt.setVisibility(View.GONE);
        SharedPrefData = new SharedPrefData(this);
        networkRequestPr = new NetworkRequestPr(this, TAG);
        notificationHelper = new NotificationHelper(this);
        gpsHelper = new GpsHelper(this);
        dg = new DialogAll(this);
        queue = Volley.newRequestQueue(this);

        mainAsynctask = new MainAsynctask(this,23123);

        if(!askNotifPermission)
        {
            if(!notificationHelper.isNotificationEnabled())
            {
                Log.i("notification","Not Enabled");
                dg.notificationEnableMsg();
                askNotifPermission=true;
            }
        }
        if(!stillAppear)
        {
            Log.i("still_there","appear");
            stillAppear = true ;
            processIntent(getIntent());
        }





        //-----------------------
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tabsNavigationBar();
        setNavigationViewListner();
        sideBar();
        mapPopUp();
        stillTherePopUp();
        //-------------------------promoter to search location
       promoter = new Promoter(this, SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase));
        permissionCheck = new PermissionCheck(this, ALL_PERMISSIONS_RESULT);
        permissions = permissionCheck.permissionList();       // all permission needed
        permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);

        String getUserID = SharedPrefData.getElementValue(promoterDataFileSp, DataConstant.agencyIDJsonKeyUpcase);
        crashlytics.log(getUserID);
        crashlytics.setCustomKey("version_name", "1.02VBeta");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!=null) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                }
            }
        }


        // if work add flag sharedpref still flag

        int stillThereRunValue = -1;

        Bundle extrasS = getIntent().getExtras();
        if(extrasS!=null ){
            stillThereRunValue = extrasS.getInt(stillThereKeySp);

            if (stillThereRunValue==1){

                Log.i("start_still" , "start Still_there");
                startStillThereMission(promoter.getStillRepeatingIntervalMinutes());
                gpsHelper.startGpsRecever(intvlGpsTime);

            } else if (stillThereRunValue==2)
            {
                removePeriodicJob(JOB_ID_Still, "StillThereJob");
                Log.i("end_still" , "End Still_there");
                notificationHelper.stopStillThereNotificationRecever();

            }
        }


        // start Gps
        gpsHelper.startGpsRecever(intvlGpsTime);
         // white list permission
        whiteListAppPermissionRequest();



        // OneSignal Initialization
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        String agancyIdSp = SharedPrefData.getElementValue(promoterDataFileSp, agencyIDkeySp);

        OneSignal.sendTag("App", "PclinkAttendance");

        OneSignal.sendTag("ID", agancyIdSp);

        if(!isVacBalanceRequest )
        {
            isVacBalanceRequest = true ;
            balanceGetReq();
            vacMenuGetReq();
        }

        String urlTK = "https://www.google.com";
        if(networkRequestPr.isSiteBlocked(urlTK))
        {
                Log.i("error_code_web","c" );
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //  dg.exitAppDialog();

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        Log.i("still_there","inside_process_intent");
        Bundle extras = intent.getExtras();
        boolean isStillTimeRunning = SharedPrefData.getElementBooleanValue(promoterDataFileSp,DataConstant.stillRunning);

        if (extras != null) {
            if (extras.containsKey("stillThereNotif")) {
                Log.i("still_there","still_notif_x");

                still_n = extras.getBoolean("stillThereNotif");
                final String isCik = SharedPrefData.getElementValue(promoterDataFileSp, agencyIDkeySp);
                if (!isCik.equals("")) {
                    if (still_n) {
                        Log.i("still_there","still_notif_open");

                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.submit(new Runnable() {
                            @Override
                            public void run()
                            {
                                still_n = false;
                                    Log.i("still_there","still_notif_show");
                                    stillAync.execute();
                                SharedPrefData.putElementBoolean(promoterDataFileSp,DataConstant.stillRunning,false);
                            }
                        });
                    }
                }
                else {
                    Intent i = new Intent(MainActivity.this, SignIn.class);
                    startActivity(i);
                }

            }
        } else
        {

        }


    }

    public void checkDrawOverPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String skipMessage = SharedPrefData.getElementValue(promoterDataFileSp, skipMsgKey);
            if (!skipMessage.equals("checked")) {

                if (!Settings.canDrawOverlays(this)) {


                    SharedPrefData.putElement(promoterDataFileSp, DataConstant.OVERLAY_PERMISSION, DataConstant.denyValue);

                    overplayAskPermissionDialog();

                } else {
                    // accept permission
                    SharedPrefData.putElement(promoterDataFileSp, DataConstant.OVERLAY_PERMISSION, DataConstant.allowValue);

                }


            }

        } else {
            SharedPrefData.putElement(promoterDataFileSp, DataConstant.OVERLAY_PERMISSION, DataConstant.allowValue);

        }
    }


    public  void whiteListAppPermissionRequest()
    {

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        boolean isIgnoringBatteryOptimizations = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if(!isIgnoringBatteryOptimizations){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if(!isIgnoringBatteryOptimizations){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission

                checkDrawOverPermission();

            } else {

                // Do as per your logic

            }


        }

        if (requestCode == MY_IGNORE_OPTIMIZATION_REQUEST) {
            PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if(isIgnoringBatteryOptimizations){
                // Ignoring battery optimization
                Log.i("battery_req","Add white list apps ");
            }else{
                // Not ignoring battery optimization
                Log.e("battery_req"," No Add white list apps ");
                dg.backgroundRequest();
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
                if (permissionsToRequest!=null) {

                    if (permissionsRejected.size() > 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                                permissionAlertAllRequest();
                                return;
                            }
                        }
                    }
                }

                break;
        }
    }

    public void overplayAskPermissionDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.still_there_ask, null);
        dontShowAgain = eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        //adb.setIcon(R.drawable.yourimage);
        adb.setTitle("still there popup");
        adb.setMessage("Do you what this app popup still there ?");
        adb.setPositiveButton("Yes, Go setting  ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                    SharedPrefData.putElement(promoterDataFileSp, skipMsgKey, checkBoxResult);
                }
                SharedPrefData.putElement(promoterDataFileSp, skipMsgKey, checkBoxResult);

                // go setting

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);


            }
        });

        adb.setNegativeButton("No ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";
                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                    SharedPrefData.putElement(promoterDataFileSp, skipMsgKey, checkBoxResult);
                }
                SharedPrefData.putElement(promoterDataFileSp, skipMsgKey, checkBoxResult);
                SharedPrefData.putElement(promoterDataFileSp, DataConstant.OVERLAY_PERMISSION, DataConstant.denyValue);


            }

        });
        String skipMessage = SharedPrefData.getElementValue(promoterDataFileSp, skipMsgKey);
        if (!skipMessage.equals("checked")) {
            adb.show();
        }
    }

    public void mapPopUp() {
        checkGpsAndPermissionLoction();
        mapDialog = new Dialog(this);

        mapDialog.setContentView(R.layout.map_popup);

        closeTextview = mapDialog.findViewById(R.id.close_map_popup);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_p);
        mapFragment.getMapAsync(this);
        mapDialog.setCanceledOnTouchOutside(true);

    }

    public void stillTherePopUp() {

        stillDialog = new Dialog(this);

        stillDialog.setContentView(R.layout.still_there_popup);
        rippleBackground = stillDialog.findViewById(R.id.ripple_background);
        stillThereButton = stillDialog.findViewById(R.id.still_there_button);
        stillThereCloseButton = stillDialog.findViewById(R.id.close_still_floatAButton);

        stillDialog.setCanceledOnTouchOutside(false);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("action_method","on_resume");

        // start Gps
        gpsHelper.startGpsRecever(intvlGpsTime);
        if(!askNotifPermission)
        {
            if(!notificationHelper.isNotificationEnabled())
            {
                Log.i("notification","Not Enabled");
                dg.notificationEnableMsg();
                askNotifPermission=true;
            }
        }


        if(!stillAppear) {
            Log.i("still_there","appear");

            stillAppear = true ;
            processIntent(getIntent());
        }


        // offline text appear nav bar
        invalidateOptionsMenu();
    }




    public void sideBar() {

        mDrLyt = findViewById(R.id.continer_drawer_lyt);
        mToggle = new ActionBarDrawerToggle(this, mDrLyt, R.string.open, R.string.close);
        mDrLyt.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void tabsNavigationBar() {
        final ViewPager viewPager = findViewById(R.id.mein_viewpager);
        categoryAdapter categoryAdapter = new categoryAdapter(getSupportFragmentManager());
        viewPager.setAdapter(categoryAdapter);

        tabLayout = findViewById(R.id.sluding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLinerLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
            tab_label = tabLinerLayout.findViewById(R.id.nav_label);
            tab_icon = tabLinerLayout.findViewById(R.id.nav_icon);

            tab_label.setText(getResources().getString(navLabels[i]));


            //XXXXXXXC

            if (i == 0) {
                tab_label.setTextColor(getResources().getColor(R.color.darkgray));
                tab_icon.setImageResource(navIconsActive[i]);
            } else {
                tab_icon.setImageResource(navIcons[i]);
            }
            tabLayout.getTabAt(i).setCustomView(tabLinerLayout);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // 1. get the custom View you've added
                View tabView = tab.getCustomView();

                // get inflated children Views the icon and the label by their id
                TextView tab_label = tabView.findViewById(R.id.nav_label);
                ImageView tab_icon = tabView.findViewById(R.id.nav_icon);

                // change the label color, by getting the color resource value
                tab_label.setTextColor(getResources().getColor(R.color.tab_background_selected));
                // change the image Resource
                // i defined all icons in an array ordered in order of tabs appearances
                // call tab.getPosition() to get active tab index.
                tab_icon.setImageResource(navIconsActive[tab.getPosition()]);


//
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                View tabView = tab.getCustomView();
                TextView tab_label = tabView.findViewById(R.id.nav_label);
                ImageView tab_icon = tabView.findViewById(R.id.nav_icon);
                // back to the black color
                tab_label.setTextColor(getResources().getColor(R.color.darkgray));
                // and the icon resouce to the old black image
                // also via array that holds the icon resources in order
                // and get the one of this tab's position
                tab_icon.setImageResource(navIcons[tab.getPosition()]);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // while reselect the Tab

            }
        });

    }


    public void checkGpsAndPermissionLoction() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Gps on or off

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            //Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();

        } else {


            try {
                gpsAlertDg = dg.showGPSDisabledAlertToUser();

                gpsAlertDg.show();

            } catch (Exception e) {
                if (gpsAlertDg != null) {
                    gpsAlertDg.dismiss();
                    gpsAlertDg.cancel();
                }
            }

        }


    }

    public void permissionAlertAllRequest() {

        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this).
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

                        finish();
                        finishAffinity();
                        System.exit(0);
                    }
                }).create().show();


    }


    // item actionbar select
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {

            return true;
        }
        if (item.getItemId() == R.id.gps_actionbar_item) {

            closeTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mapDialog.dismiss();
                    mapDialog.cancel();

                }
            });
            mapDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mapDialog.show();

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mgoogleMap = googleMap;


        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            Location loct = promoter.searchCurrentLocation();
            String oldLat = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey));
            String oldLng = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey));
            String oldTime = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps));

            if (loct != null) {
                locationUser = new LatLng(loct.getLatitude(), loct.getLongitude());

                String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en")).format(loct.getTime());

                Log.e("time gps", time);

                float zoomLevel = 15.0f;


                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mgoogleMap.addMarker(new MarkerOptions().position(locationUser).title("me").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser, zoomLevel));

                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(locationUser));


            }
            else {
                if(!oldLat.equals("") && !oldLng.equals(""))
                {
                    locationUser = new LatLng(Double.parseDouble(oldLat), Double.parseDouble(oldLng));

                    String time =oldTime;

                    Log.e("time gps", time);

                    float zoomLevel = 15.0f;


                    mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    mgoogleMap.addMarker(new MarkerOptions().position(locationUser).title("me").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationUser, zoomLevel));

                    mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(locationUser));


                }

            }



        }
    }


    private void setNavigationViewListner() {
        navigationView = findViewById(R.id.nav_view);
        setheaderNav();

        navigationView.setNavigationItemSelectedListener(this);
    }


    private void setheaderNav() {
        viewHeaderLayout = navigationView.getHeaderView(0);
        profImage = viewHeaderLayout.findViewById(R.id.image_pf_drawer_header_imageView);

        profHeadTimeStageTxT = viewHeaderLayout.findViewById(R.id.arrived_time_cheakin_header_drawer_bar);
        nameHeadTxT = viewHeaderLayout.findViewById(R.id.name_account_user_drawer_bar);

        String encodeImage = SharedPrefData.getElementValue(promoterDataFileSp, promoterImgeSp);
        String timeStage = SharedPrefData.getElementValue(promoterDataFileSp, DataConstant.timeStageKeySp);
        String getname = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.promoterUserName);
        nameHeadTxT.setText(getname);
        profHeadTimeStageTxT.setText(timeStage);
        Bitmap imageProfile = getEncodeImageProfileToDecodeBitmap(encodeImage);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageProfile);
        roundedBitmapDrawable.setCornerRadius(200.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        profImage.setImageDrawable(roundedBitmapDrawable);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case  R.id.language_drawerbar:
            {



                    Intent n = new Intent(this, LanguagesActivity.class);
                    startActivity(n);




                break;
            }
            case R.id.log_out_drawerbar:       // < -- logOut log out
            {


                Log.i("NAV_VIEW", "NAV_VIEW");
              //  Toast.makeText(this, "Log Out   ", Toast.LENGTH_SHORT).show();

                if (SharedPrefData.getElementBooleanValue(promoterDataFileSp, DataConstant.LogOutClk)) {

                    SharedPrefData.putElement(promoterDataFileSp, agencyIDkeySp, "");
                    SharedPrefData.putElement(promoterDataFileSp, DataConstant.promoterUrlPathKey, "");
                    gpsHelper.stopGpsRecever();

                    Intent n = new Intent(this,SignIn.class);
                    startActivity(n);



                } else {

                    String msgLogOut = getResources().getString(R.string.log_out_msg);

                    dg.infoMsgOneBtn(msgLogOut, "ok");

                    homeLyt.setVisibility(View.VISIBLE);
                    aboutLyt.setVisibility(View.GONE);
                    helpLyt.setVisibility(View.GONE);
                    superAllLyt.setVisibility(View.GONE);

                }

                break;
            }
            case R.id.about_drawerbar: {
                homeLyt.setVisibility(View.GONE);
                aboutLyt.setVisibility(View.VISIBLE);
                helpLyt.setVisibility(View.GONE);
                superAllLyt.setVisibility(View.GONE);


            }
            break;
            case R.id.help_drawerbar: {
                homeLyt.setVisibility(View.GONE);
                aboutLyt.setVisibility(View.GONE);
                helpLyt.setVisibility(View.VISIBLE);
                superAllLyt.setVisibility(View.GONE);


            }
            break;

            case R.id.Attendance_log_drawerbar:
                {
                homeLyt.setVisibility(View.VISIBLE);
                aboutLyt.setVisibility(View.GONE);
                helpLyt.setVisibility(View.GONE);
                superAllLyt.setVisibility(View.GONE);
                }
            break;
            case  R.id.super_info_drawerbar :
             {
                superAllLyt.setVisibility(View.VISIBLE);
                homeLyt.setVisibility(View.GONE);
                aboutLyt.setVisibility(View.GONE);
                helpLyt.setVisibility(View.GONE);
                superInfoGet();

                }
            break;


        }
        //close navigation drawer
        mDrLyt.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
           this.location.getLatitude();
            this.location.getLongitude();

        }
        assert location != null;
        boolean isMockLocation = location.isFromMockProvider();
        Log.i("isMockLocation", " "+ "ask");

        if(isMockLocation)
        {
            Log.i("isMockLocation", " "+ isMockLocation);
            SharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey,true); ; //this phone use mock location
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest!=null) {

                    if (permissionsToRequest.size() > 0) {
                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                    }
                }
            }
            return;
        }

        // Permissions ok, we get last location
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            Log.i("locationMYM", "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
                            long latt = (long) location.getLatitude();
                            long longg = (long) location.getLongitude();
                            long time = location.getTime();

                            SharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey, latt);
                            SharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey, longg);
                            SharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps, time);
                        }

                    }
                });

        startLocationUpdates();
    }

    @SuppressLint("RestrictedApi")
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // ---------------------- create smart Scheduler ** still There   ----------------------


    private int getJobType(int jobTypeSelectedPos) //0
    {
        switch (jobTypeSelectedPos) {

            default:
            case 0:
                return Job.Type.JOB_TYPE_HANDLER;
            case 1:
                return Job.Type.JOB_TYPE_ALARM;
            case 2:
                return Job.Type.JOB_TYPE_PERIODIC_TASK;

        }
    }

    private int getNetworkTypeForJob(int networkTypeSelectedPos) //0
    {

        switch (networkTypeSelectedPos) {
            default:
            case 0:
                return Job.NetworkType.NETWORK_TYPE_ANY;
            case 1:
                return Job.NetworkType.NETWORK_TYPE_CONNECTED;
            case 2:
                return Job.NetworkType.NETWORK_TYPE_UNMETERED;
        }
    }

    private void removeSchedulerJob()   // remove
    {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(this);
        smartScheduler.removeJob(JOB_ID_Still);

    }


    private void removePeriodicJob(int job_id, String nameJobSch) {
        //smartJobButton.setText(getString(R.string.schedule_job_btn));

        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);
        if (!jobScheduler.contains(job_id)) {

            Log.e("SmartScheduler : ", "No job exists with  JobID: " + nameJobSch + "  " + job_id);
            return;
        }

        if (jobScheduler.removeJob(job_id)) {
            Log.e("SmartScheduler :", "Job successfully removed! with id :" + nameJobSch + "  " + job_id);

        }
    }

    private Job createJobXshc(int job_id, int jobTypeID, Long intervalInMillis)   //  for long time jobtype : 2
    {
        String JOB_PERIODIC_TASK_TAG = "io.hypertrack.android_scheduler_demo.JobPeriodicTask";

        int jobType = getJobType(jobTypeID); // 2
        int networkType = getNetworkTypeForJob(0);
        boolean requiresCharging = false;

        if (intervalInMillis == null) {
            return null;
        }

        Job.Builder builder = new Job.Builder(job_id, this, jobType, JOB_PERIODIC_TASK_TAG)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(requiresCharging)
                .setIntervalMillis(intervalInMillis);


        builder.setPeriodic(intervalInMillis);


        return builder.build();
    }


    public void startSearchLocationGps(long intervalMinuteseconds) {
        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);
        if (jobScheduler.contains(JOB_ID_Loction)) {

            return;
        } else {

            jobLocation = createJobXshc(JOB_ID_Loction, 2, intervalMinuteseconds); //Long intervalInMillis
            if (jobLocation == null) {
                Log.e("startStillThere: ", "Error create job ");
                return;
            }

            // Schedule current created job
            if (jobScheduler.addJob(jobLocation)) {
             //   Toast.makeText(MainActivity.this, " gps Job Sch successfully added!", Toast.LENGTH_SHORT).show();
            } else {
               // Toast.makeText(MainActivity.this, "gps Job failed  added!", Toast.LENGTH_SHORT).show();

            }
        }


    }


    public void startStillThereMission(long intervalMinuteseconds) {

        // Check if any periodic job is currently scheduled

        // Create a new job with specified params


            notificationHelper.stopStillThereNotificationRecever();
         Long timeNow = System.currentTimeMillis();
            notificationHelper.startCalenderStillThereNotificationRecever(timeNow);





    }

    @Override
    public void onJobScheduled(Context context, final Job job) {
        if (job != null) {
            Log.i(TAG, "Job: " + job.getJobId() + " scheduled!");

            if(!job.isPeriodic()) {

            }
        }
    }


    public Bitmap getEncodeImageProfileToDecodeBitmap(String encodedImage) {
        if (encodedImage == null) {
            return null;
        }
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;

    }

    public void superInfoGet()
    {

        superLoadingPrgBar.setVisibility(View.VISIBLE);
        superDataLyt.setVisibility(View.GONE);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String  urlSuper = DataConstant.serverUrl+DataConstant.superUrl+SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);
        final Gson gson = new Gson();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, urlSuper, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                SuperModel superModel = gson.fromJson(response.toString(), SuperModel.class);
                String sNameTxt = superModel.getsName();
                String supervisorId = String.valueOf(superModel.getsAgencyID());

                SharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.supervisorID ,supervisorId);

                spXName.setText(sNameTxt);
                spXEmail.setText(superModel.getsEmail());
                spXPhone.setText(superModel.getsPhone());
                superLoadingPrgBar.setVisibility(View.GONE);

                superDataLyt.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                spXName.setText("");
                spXEmail.setText("");
                spXPhone.setText("");
                superLoadingPrgBar.setVisibility(View.GONE);

                superDataLyt.setVisibility(View.VISIBLE);

            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);

        spXEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, spXEmail.getText());
                startActivity(Intent.createChooser(intent, "Send Email"));

            }
        });
        spXPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+spXPhone.getText()));
                startActivity(intent);
            }
        });



    }
    public static void offlineCikItem(boolean isconnect) {
        if (menu != null)
            if (isconnect) {
                menu.findItem(R.id.offline_txt_item).setVisible(false);
            } else {
                menu.findItem(R.id.offline_txt_item).setVisible(true);
                menu.findItem(R.id.offline_txt_item).setTitle(Html.fromHtml("<font color='#ff3824'>OFFlINE</font>"));
            }


    }
    void  vacMenuGetReq()
    {
        String urlListRequest  = DataConstant.serverUrl+DataConstant.vacGetControl+DataConstant.vacGetAction +SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);
                Log.i("vac_url : ",urlListRequest) ;
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, urlListRequest,null ,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("ok_vac_menu", response.toString());
                SharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.vacLisJsonKey,response.toString());
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("error_vac_menu", error.toString());

            }
        });

        queue.add(jsObjRequest);
    }
    private void balanceGetReq() {
        String urlListRequest = DataConstant.serverUrl+DataConstant.getVacationDaysUrl + SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase) ;
        final Gson gson = new Gson();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, urlListRequest,null ,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                VacDaysModel vacDaysModel = gson.fromJson(response.toString(),VacDaysModel.class);
                SharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.vacDaysKey,vacDaysModel.getDaysleft());
                Log.e("ok_vacDays", response.toString());

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("error_vacDays", error.toString());

            }
        });

        queue.add(jsObjRequest);
    }



    private  class   StillAync extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            stillThereShow();

        }
        public void stillThereShow() {
            Log.e("still_there", "show_still_sch");

            if (rippleBackground.isRippleAnimationRunning()) {
                rippleBackground.stopRippleAnimation();
            }
            if (stillDialog.isShowing()) {
                stillDialog.dismiss();
                stillDialog.cancel();
            }


            SharedPrefData.putElementBoolean(promoterDataFileSp, stillThereOutAppkeySp, false);

       /*
        notificationHelper.createNotification("still ppppp","vvvvvvvvv","",false,"stillthere",355555+notifid);
*/

            runOnUiThread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.M)
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    Log.e("runnnn_still_1", "job sch");

                    int LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(MainActivity.this)) {
                            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

                        } else {
                            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                        }

                    } else

                    {
                        LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;

                    }


                    Log.e("runnnn_still_2", "job sch");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(MainActivity.this)) {
                            stillDialog.getWindow().setType(LAYOUT_FLAG);
                        }
                    } else {
                        stillDialog.getWindow().setType(LAYOUT_FLAG);

                    }

                    stillDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rippleBackground.startRippleAnimation();
                    stillDialog.show();

                    stillThereButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            stillDialog.dismiss();
                            stillDialog.cancel();
                            rippleBackground.stopRippleAnimation();
                             boolean  isFackLoction = SharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey);
                                 DialogAll dialogAll = new DialogAll(MainActivity.this);
                            if(isFackLoction)
                            {
                                dialogAll.txtImageMsgRequest(R.drawable.ic_fake,"Location",getResources().getString(R.string.fake_message),"ok",null);
                                GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
                                gpsTracker.fakeLocRequestServer(DataConstant.stillThereType); // still there 5

                            }else
                            {
                                Intent i = new Intent(MainActivity.this, CameraApi.class);
                                i.putExtra(stillTypeEnter, "Still There");
                                startActivity(i);
                            }


                        }
                    });

                    stillThereCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rippleBackground.stopRippleAnimation();
                            SharedPrefData.putElementBoolean(promoterDataFileSp,DataConstant.stillRunning,false);

                            if (stillDialog!=null) {
                                stillDialog.dismiss();
                                stillDialog.cancel();

                            }
                        }
                    });

                    new CountDownTimer(promoter.getStillDurationTime(), 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFinish() {
                            // TODO Auto-generated method stub

                            if (rippleBackground.isRippleAnimationRunning()) {
                                rippleBackground.stopRippleAnimation();
                            }

                            stillDialog.cancel();
                        }
                    }.start();

                }
            });

        }


    }


}