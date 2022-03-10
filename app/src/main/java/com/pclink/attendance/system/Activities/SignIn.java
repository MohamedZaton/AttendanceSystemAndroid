package com.pclink.attendance.system.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.RealFireBase;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.Models.AuthLogInModel;
import com.pclink.attendance.system.Models.SuperModel;
import com.pclink.attendance.system.NetworkServer.Connectivity;
import com.pclink.attendance.system.NetworkServer.HttpHandler;
import com.pclink.attendance.system.Permission.PermissionCheck;
import com.pclink.attendance.system.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SignIn extends AppCompatActivity  {

    private com.pclink.attendance.system.DataBase.SharedPrefData SharedPrefData;
    private DialogAll dialogAll;
        public AuthLogInModel authLogInModel ;
    EditText userPassword;
    EditText userID;
    HttpHandler httpHandler;
    JSONObject jsonObjBody ;
    String getPromoterIDJson,namePromoter;
    int stillRepeating,workingHours,stillDuration;
    String getPromoterId,getUserPwd;
    Button signInBtn;
    public  String TAG = "SignIn ";
    String versionAPP = "" ;
    private ProgressBar progressbarLoading;

    // shared pref
    String namefileSp = DataConstant.promoterDataNameSpFile;
    String stillRepeatingkeySp = DataConstant.stillRepeatingJsonKey;
    String workingHourskeySp = DataConstant.workingHoursJsonKey;
    String stillDurationkeySp = DataConstant.stillDurationJsonKey;
    String vacDaysCikKey= DataConstant.vacDaysCikKey;
    String storeName ;
    RealFireBase realFireBase ;

    int vacationDays;

    /* permission */

    // lists for permissions
    PermissionCheck permissionCheck;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        dialogAll=new DialogAll(this , SignIn.class);
        userPassword =findViewById(R.id.password_inp_editText);
        userID =findViewById(R.id.userId_inp_editText) ;
        signInBtn = findViewById(R.id.sign_in_button);
        httpHandler =new HttpHandler();
        SharedPrefData =new SharedPrefData(this);
         jsonObjBody = new JSONObject();
         realFireBase = new RealFireBase(this);
        progressbarLoading = findViewById(R.id.progressBar1);

        permissionCheck = new PermissionCheck(this,ALL_PERMISSIONS_RESULT);
        permissions  =permissionCheck.permissionList();       // all permission needed
        permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (permissionsToRequest.size() > 0)
            {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbarLoading.setVisibility(View.VISIBLE);

                getPromoterId= userID.getText().toString();
                getUserPwd= userPassword.getText().toString();

                if(getUserPwd.isEmpty() || getPromoterId.isEmpty())
                {
                    dialogAll.pwdEmpty();
                }
                else {
                    final  String  url = DataConstant.serverUrl+DataConstant.LoginAuthPostControl;
                       Log.i("sign_in_url : ",url);

                    if( URLUtil.isValidUrl(url))
                    {
                        PackageInfo pInfo = null;
                        try {
                            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            versionAPP = pInfo.versionName;

                        } catch (PackageManager.NameNotFoundException e) {

                            versionAPP = "NameNotFound";
                            e.printStackTrace();
                        }



                        String osVersion="" + Build.VERSION.RELEASE  ;

                        String OS = "Android";
                        String deviceName = android.os.Build.MODEL;
                        JSONObject jsonObjBody = new JSONObject();
                        NetworkInfo activeNetwork = Connectivity.getNetworkInfo(SignIn.this);
                            String netWorkState = "" ;
                            try {
                                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                                    // connected to wifi
                                    netWorkState = "Type : WIFI And Connected Speed :   "+ (Connectivity.isConnectedFast(SignIn.this)?"Fast":"Slow") ;

                                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                                    // connected to mobile data
                                    TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                    int mobileData = mTelephonyManager.getNetworkType();
                                    String mobileDataTypeName = "UnKown" ;
                                    switch (mobileData)
                                    {
                                        case TelephonyManager.NETWORK_TYPE_IDEN:
                                            mobileDataTypeName = "2G" ;
                                            break;
                                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                                            mobileDataTypeName = "3G" ;
                                            break;
                                        case TelephonyManager.NETWORK_TYPE_LTE:
                                            mobileDataTypeName = "4G" ;
                                            break;

                                    }



                                    netWorkState = "Type : "+mobileDataTypeName+" And Connected Speed :   "+ (Connectivity.isConnectedFast(SignIn.this)?"Fast":"Slow") ;

                                }
                            }catch (Exception e)
                            {
                                netWorkState="" ;
                            }

                        authLogInModel =new AuthLogInModel(getPromoterId,getUserPwd,versionAPP,OS,osVersion,deviceName,netWorkState );



                        Gson gson = new Gson();
                        final String StringJsonBody = gson.toJson(authLogInModel);
                        Log.i("logIn_body"," " +StringJsonBody);

                        try
                        {
                            jsonObjBody = new JSONObject(StringJsonBody);

                        } catch(JSONException e)
                        {
                            e.printStackTrace();
                        }

                        //final
                        RequestQueue queue = Volley.newRequestQueue(SignIn.this);
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjBody, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                SharedPreferences prefA= getSharedPreferences(DataConstant.promoterDataNameSpFile,MODE_PRIVATE);
                                Map<String, ?> prefsMap = prefA.getAll();
                                progressbarLoading.setVisibility(View.VISIBLE);

                                for (Map.Entry<String, ?> entry: prefsMap.entrySet())
                                {
                                    Log.v("SharedPreferences", entry.getKey() + ":" + entry.getValue().toString());
                                }

                                // TODO Auto-generated method stub

                                getPromoterIDJson = JsonPr.getValueObjtJson(response.toString(), "agencyID","s");

                                if(!getPromoterIDJson.equals("")) {

                                    namePromoter = JsonPr.getValueObjtJson(response.toString(), "name", "s");
                                    stillRepeating = Integer.parseInt(JsonPr.getValueObjtJson(response.toString(), stillRepeatingkeySp, "i"));
                                    workingHours = Integer.parseInt(JsonPr.getValueObjtJson(response.toString(), workingHourskeySp, "i"));
                                    stillDuration = Integer.parseInt(JsonPr.getValueObjtJson(response.toString(), stillDurationkeySp, "i"));
                                    vacationDays =  Integer.parseInt(JsonPr.getValueObjtJson(response.toString(),stillDurationkeySp ,"i"));
                                    storeName = JsonPr.getValueObjtJson(response.toString(), "store", "s");
                                }

                                if (getPromoterId.equals(getPromoterIDJson))
                                {

                                    SharedPrefData.putElement(namefileSp, DataConstant.agencyIDJsonKeyUpcase, getPromoterId);
                                    SharedPrefData.putElement(namefileSp, DataConstant.promoterUrlPathKey, url);
                                    SharedPrefData.putElement(namefileSp, DataConstant.promoterUserName, namePromoter);
                                    // time work  , stillThere Time
                                    SharedPrefData.putIntElement(namefileSp,stillRepeatingkeySp,stillRepeating);
                                    SharedPrefData.putIntElement(namefileSp,workingHourskeySp,workingHours);
                                    SharedPrefData.putIntElement(namefileSp,stillDurationkeySp,stillDuration);
                                    SharedPrefData.putIntElement(namefileSp,vacDaysCikKey,vacationDays);
                                    SharedPrefData.putElement(namefileSp,DataConstant.storeNameSp,storeName);
                                    realFireBase = new RealFireBase(SignIn.this);

                                    realFireBase.sendLogMsg("login success" , "response "+ response.toString());

                                    superInfoGet();
                                    Intent i = new Intent(SignIn.this, MainActivity.class);
                                    startActivity(i);

                                } else
                                {
                                    // password wrong
                                    DialogAll dialogAll = new DialogAll(SignIn.this);
                                 //   String errorMsg  = JsonPr.getValueObjtJson(response.toString(), DataConstant.wrongLoginMsgKey,"s");
                                    String title = "Login Failed";
                                    String errorMsg = "Invalid User ID or Password";

                                    // String title = "Failed to login";
                                    // String errorMsg  = "Incorrect user id or password";
                                    userPassword.getText().clear();
                                    progressbarLoading.setVisibility(View.GONE);
                                    dialogAll.loginCik(title,errorMsg);
                                }

                            }
                        }, new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                NetworkResponse networkResponse = error.networkResponse;
                                String statusCode = "" ;
                                 try{
                                     statusCode = String.valueOf(error.networkResponse.statusCode);

                                 }catch (Exception e)
                                 {
                                     statusCode = "400" ;
                                 }
                                //get response body and parse with appropriate encoding
                                realFireBase = new RealFireBase(SignIn.this);

                                realFireBase.sendLogMsg("login error" , "status : "+ statusCode+ " msgError :"+error.toString());

                                realFireBase.sendLogMsg("login error" , "body error :"+ StringJsonBody);

                                Log.i(TAG, "onErrorResponse: code " + statusCode);

                                Log.i(TAG, "onErrorResponse: "+error.toString());
                                // TODO Auto-generated method stub
                                dialogAll.dialogMsgConnect();

                            }
                        });

                        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                                5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(jsObjRequest);

                    }

                }




            }
        });
    }
    public void superInfoGet()
    {

        RequestQueue queue = Volley.newRequestQueue(SignIn.this);
        String  urlSuper = DataConstant.serverUrl+DataConstant.superUrl+SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);
        final Gson gson = new Gson();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, urlSuper, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.i("ok_super_id", response.toString());
                SuperModel superModel = gson.fromJson(response.toString(), SuperModel.class);
                String supervisorId = String.valueOf(superModel.getsAgencyID());
                SharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.supervisorID ,supervisorId);

            }
        }, new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error_super_id", error.toString());






            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);




    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            return false;
        }


        return super.onKeyDown(keyCode, event);
    }




    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        switch(requestCode) {
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
                        else
                        {


                        }

                    }
                }

                break;
        }
    }

    public void permissionAlertAllRequest()
    {
        new androidx.appcompat.app.AlertDialog.Builder(SignIn.this).
                setMessage("These permissions are mandatory to get your location. You need to allow them.").
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            requestPermissions(permissionsRejected.
                                    toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                        }
                    }
                }).setNegativeButton("Exit",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                        finish();
                        finishAffinity();
                        System.exit(0);
                    }
                }).setCancelable(false).create().show();

    }

}
