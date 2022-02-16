package com.pclink.attendance.system.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.RealFireBase;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;

import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import java.io.File;


public class SplashScreen extends AppCompatActivity {



    private com.pclink.attendance.system.DataBase.SharedPrefData SharedPrefData;
    private static String url="";
    private MainAsynctask mainAsynctask;
    private ProgressBar splashLoadingBar;
    private NetworkRequestPr networkRequestPr ;
    private TextView versionTxt ;
    private RealFireBase realFireBase ;


    //private final  static String url="https://api.myjson.com/bins/tbhh8" + "";

public DialogAll dialogAll;
public AlertDialog gpsAlertDg =null;
// sharedpef sp data
private  String promoterDataFileSp=DataConstant.promoterDataNameSpFile,
                promoterURLpathUserKeySp= DataConstant.promoterUrlPathKey;




@Override
    protected void onResume() {
        super.onResume();
        if(!MainAsynctask.isLocationEnabled(SplashScreen.this))
        {
            gpsAlertDg=dialogAll.showGPSDisabledAlertToUser();
            gpsAlertDg.show();
            splashLoadingBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        setContentView(R.layout.activity_splash_screen);
        RequestQueue queue = Volley.newRequestQueue(SplashScreen.this);
        dialogAll=new DialogAll(this);
        realFireBase = new RealFireBase(this);
        SharedPrefData =new SharedPrefData(this);
        mainAsynctask=new MainAsynctask(this,0);
        splashLoadingBar= findViewById(R.id.splash_progressBar);
        versionTxt= findViewById(R.id.name_version_txt);
        url= SharedPrefData.getElementValue(promoterDataFileSp,promoterURLpathUserKeySp);
        networkRequestPr = new NetworkRequestPr(SplashScreen.this,"from_splash");
        Log.i("url_log_in",url);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionTxt.setText(version);
            versionTxt.setVisibility(View.VISIBLE);
            String OldVersion = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.versionOldKey);
            if(!version.equals(OldVersion))
            {
                Log.i("delete_version ",OldVersion);

                deleteCache(this) ;
                SharedPrefData.removeFileSp(DataConstant.promoterDataNameSpFile);
                SharedPrefData.removeFileSp(DataConstant.offlineModeFile);
                SharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.versionOldKey,version);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            deleteCache(this) ;

            Intent i = new Intent(SplashScreen.this, SignIn.class);
            startActivity(i);

        } catch (Exception e)
        {
            deleteCache(this) ;

            Intent i = new Intent(SplashScreen.this, SignIn.class);
            startActivity(i);

        }

            if(!url.equals(""))
            {
                realFireBase.sendLogMsg("open app" , "pass splash screen");
            }



            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    // TODO Auto-generated method stub
                    Log.i("info", "xxxx" + response.toString());

                    if (MainAsynctask.isLocationEnabled(SplashScreen.this))
                    {
                        networkRequestPr.getVacationDaysBalance();
                        new Promoter(SplashScreen.this).autoLogInCik(response.toString());

                    } else
                        {
                        gpsAlertDg = dialogAll.showGPSDisabledAlertToUser();
                        gpsAlertDg.show();
                        splashLoadingBar.setVisibility(View.GONE);
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    if (SharedPrefData.getElementValue(promoterDataFileSp, DataConstant.agencyIDJsonKeyUpcase).equals("")) {



                                Intent i = new Intent(SplashScreen.this, SignIn.class);
                                startActivity(i);



                    } else

                     {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);




                    }

                }
            });
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);











    }
    @Override
    protected void onStop()
    {
        super.onStop();
        if(gpsAlertDg!= null){
            gpsAlertDg.dismiss();
            gpsAlertDg.cancel();
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(gpsAlertDg!= null){
            gpsAlertDg.dismiss();
            gpsAlertDg.cancel();
        }
    }




    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
