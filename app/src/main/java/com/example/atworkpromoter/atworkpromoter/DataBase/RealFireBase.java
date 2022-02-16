package com.pclink.attendance.system.DataBase;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.pclink.attendance.system.Models.LogBugsModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RealFireBase {
    DatabaseReference rootRef,userChild;
    FirebaseDatabase databaseF ;
    Context mContext;
    private  String nameDataBase= "appDB";
    private  String agencyID ;
    public LogBugsModel logBugsModel;
    public  SharedPrefData sharedPrefData ;
    String version ;

    String[] arrayBodykeys = {DataConstant.agencyIDJsonKeyUpcase,
            DataConstant.checkTypeJsonKey,
            DataConstant.checkInIDJsonKey,
            DataConstant.imageJsonKey,
            "Lat",
            "Lng"};

    public RealFireBase(Context mContext  ) {
        this.mContext = mContext;
        databaseF = FirebaseDatabase.getInstance("https://attendance-system-8c349-default-rtdb.europe-west1.firebasedatabase.app/");

        rootRef =databaseF.getReference();
        databaseF = FirebaseDatabase.getInstance("https://attendance-system-8c349-default-rtdb.europe-west1.firebasedatabase.app/");
        rootRef =databaseF.getReference();
        PackageInfo pInfo = null;
         version = "Unknown" ;
        try {
            pInfo = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        version = version.replace(".","_");

        sharedPrefData= new SharedPrefData(this.mContext);
        this.agencyID = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase) ;

        userChild = rootRef.child(nameDataBase).child("version"+version);
    }

    public void sendLogFB( String title, String msg, String url, HashMap<String, String> body)
    {

        userChild=userChild.child("Logs").child(agencyID);
        logBugsModel = new LogBugsModel(  title,  msg,  url,
                body.get(arrayBodykeys[1]),
                body.get(arrayBodykeys[2]),
                body.get(arrayBodykeys[3]),
                body.get(arrayBodykeys[4]),
                body.get(arrayBodykeys[5]));
        userChild = userChild.child(logBugsModel.getDate()+"__"+title);

        userChild.setValue(logBugsModel);
    }
    public void sendLogMsg(String title,String msg)
    {
        Log.i( "sendLogMsg: ","title: " +title+" msg: "+msg);
        userChild=userChild.child("Logs").child(agencyID);
        logBugsModel = new LogBugsModel(  title,  msg, "", "","","","","" );
        userChild = userChild.child(logBugsModel.getDate()+"__"+title);

        userChild.setValue(logBugsModel);
    }

}
