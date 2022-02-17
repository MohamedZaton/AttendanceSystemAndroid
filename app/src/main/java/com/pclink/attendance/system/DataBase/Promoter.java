package com.pclink.attendance.system.DataBase;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.Activities.SignIn;
import com.pclink.attendance.system.DateAndTime.TimeHelper;
import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.LocationFind.GpsTracker;
import com.pclink.attendance.system.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Promoter implements LocationListener  {
    public Context context;
    private  TimeHelper timeHelper;
    private static String promoterId;
    private static String url;
    private SharedPrefData SharedPrefData;
    private JsonPr jsonPrHelper;
    // shared pref
    private  String offlineFile= DataConstant.offlineModeFile;

    private String namefile = DataConstant.promoterDataNameSpFile;
    private String imageKeySp = DataConstant.imageSP;
    private String userIdKeySp = DataConstant.agencyIDJsonKeyUpcase;
    private String userCheckInIdSp = DataConstant.checkInIDJsonKey;

    // key json
    private String agencyIDKey = DataConstant.agencyIDJsonKeyUpcase;
    private String checkTypeKey = DataConstant.checkTypeJsonKey;
    private String checkInIDKey = DataConstant.checkInIDJsonKey;
    private String CheckTimeKey = DataConstant.checkTimeJsonKey;
    private String CheckDateKey = DataConstant.checkDateJsonKey;
    private  String imageValue = null;
    private String imageKey = DataConstant.imageJsonKey;
    private String latKey = DataConstant.latLocationJsonKey;
    private String lngKey = DataConstant.lngLocationJsonKey;
    private  String stillthereStateKey = DataConstant.stillThereStatus;
    private String stillRepeatingkeySp = DataConstant.stillRepeatingJsonKey;
    private String workingHourskeySp = DataConstant.workingHoursJsonKey;
    private String stillDurationkeySp = DataConstant.stillDurationJsonKey;
    private FusedLocationProviderClient fusedLocationClient;
    Location locationFromChanged;
    private  GpsTracker gpsTracker;
    private int navIcon[] = DataConstant.navIcons;
    // excuse Json & Shared Pref
    private String exMessageKey= DataConstant.exMessageKey , exDateKey =DataConstant.exDateKey, exIdKey = DataConstant.exId ;
 // vication json & Shared Pref
    private String vacDateFromKey = DataConstant.vacDateFromKey,vacDateToKey=DataConstant.vacDateToKey,vacIdKey= DataConstant.vacIdKey;
    private RealFireBase realFireBase ;


    public Promoter(Context context, String promoterId ) {
        this.context = context;
        this.promoterId = promoterId;
        // url="https://api.myjson.com/bins/tbhh8" + "";
        //  url="http://204.15.178.119:5555/EATAPI/api/Authorization/GetAccess/"+ promoterId+"/Password" + "";
        SharedPrefData = new SharedPrefData(context);
        gpsTracker = new GpsTracker(this.context);
        realFireBase = new RealFireBase(context);
    } public Promoter(Context context, String imageValue,String promoterId  ) {
        this.context = context;
        this.promoterId = promoterId;
        // url="https://api.myjson.com/bins/tbhh8" + "";
        //  url="http://204.15.178.119:5555/EATAPI/api/Authorization/GetAccess/"+ promoterId+"/Password" + "";
        SharedPrefData = new SharedPrefData(context);
        gpsTracker = new GpsTracker(this.context);
        this.imageValue =imageValue;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        realFireBase = new RealFireBase(context);

    }

    public Promoter(Context context) // used in  auto login()
    {
        this.context = context;
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);
        gpsTracker = new GpsTracker(this.context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        realFireBase = new RealFireBase(context);

    }



    public  Promoter(){}

    //----------------------------------------------------------Location Model


    public Location searchCurrentLocation() {

      return   gpsTracker.getLocation();

    }







    //--------------------------------------------------------
    public void autoLogInCik(String jsonStringCikLogin)
    {
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);

        String getPassword,passwordFromSP="";

        String cikID = JsonPr.getPromoterJsonElementValue(jsonStringCikLogin , "agencyID");

           if(SharedPrefData.isExistsKey(namefile,userIdKeySp))
           {
               passwordFromSP = SharedPrefData.getElementValue(namefile, userIdKeySp);

                getPassword = cikID;
           }
           else
           {
                getPassword = "x";
           }
        if(getPassword.equals(passwordFromSP))
        {

                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);

        }
        else
        {
            if(!passwordFromSP.equals(null))
            {
                Toast.makeText(context, "auto-login failed ", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "it cannot connect to the server", Toast.LENGTH_SHORT).show();
            }

            Intent i =new Intent(context,SignIn.class);
            context.startActivity(i);

        }
    }


    /// create  body request promoter

    public Map<String, String> postCheckInDataBody(int  connectOnline)
    {
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);



        // post check in body request


            // value json

        String agencyIdValue=SharedPrefData.getElementValue(namefile,userIdKeySp);
        String checkTypeValue=DataConstant.checkInType;
        String  checkInIdValue="";

        if(imageValue==null)
        {
            imageValue=SharedPrefData.getElementValue(namefile,imageKeySp);

        }





       String latValue,lngValue,timeValue="",dateVale="";

        if(searchCurrentLocation() != null)
       {
           Location loct = searchCurrentLocation();
            latValue = Double.toString(loct.getLatitude());
            lngValue = Double.toString(loct.getLongitude());
            Long timegps = loct.getTime();
           timeValue = timeHelper.convertToHHMM(timegps);
           dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(timegps);
           realFireBase.sendLogMsg("collectBodyNull",","+latValue+","+lngValue+","+timeValue+","+dateVale);

       }
       else
       {
           String oldLat = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey));
           String oldLng = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey));
           Long oldTime = SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps);
           Long timegps = oldTime;

               latValue = oldLat;
               lngValue = oldLng;
               timeValue = timeHelper.convertToHHMM(timegps);
               dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(timegps);



           realFireBase.sendLogMsg("collectBodyLoct",","+latValue+","+lngValue+","+timeValue+","+dateVale);

       }



        Map<String,String> jsonData = new  HashMap<String,String>();

        if(connectOnline == 1) {
            jsonData.put(agencyIDKey, agencyIdValue);
            jsonData.put(checkTypeKey, checkTypeValue);
            jsonData.put(checkInIDKey, checkInIdValue);
            jsonData.put(imageKey, imageValue);
            jsonData.put(latKey, latValue);
            jsonData.put(lngKey, lngValue);

            validationBody(agencyIdValue,imageValue,latValue,lngValue);
        }

        else if(connectOnline == 2)  //offline
        {
            jsonData.put(agencyIDKey, agencyIdValue);
            jsonData.put(CheckTimeKey , timeValue);
            jsonData.put(CheckDateKey , dateVale);

            jsonData.put(checkTypeKey, checkTypeValue);
            jsonData.put(checkInIDKey, checkInIdValue);
            jsonData.put(imageKey, imageValue);
            jsonData.put(latKey, latValue);
            jsonData.put(lngKey, lngValue);
            validationBody(agencyIdValue,imageValue,latValue,lngValue);


        }

        return jsonData;
    }
    public Map<String, String> postCheckOutDataBody(int  connectOnline)
    {

        Map<String, String> jsonData=null;
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);


        String timeValue="",dateVale="" ,latValue="" ,lngValue="";

        // post check out body request



        // value json

       // String salesFormIdVale = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.salesFormIdKey);
        String agencyIdValue=SharedPrefData.getElementValue(namefile,userIdKeySp);
        String checkTypeValue=DataConstant.checkOutType;
        String  checkInIdValue=SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.checkInIDJsonKey);
        String imageValue=SharedPrefData.getElementValue(namefile,imageKeySp);

        Location loct = searchCurrentLocation();

        if(loct ==null)
        {
            String oldLat = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey));
            String oldLng = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey));
            Long oldTime = SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps);
            Long timegps = oldTime;

            latValue = oldLat;
            lngValue = oldLng;
            timeValue = timeHelper.convertToHHMM(timegps);
            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(timegps);


        }
        else {
            jsonData = new HashMap<String, String>();
             latValue = Double.toString(loct.getLatitude());
             lngValue = Double.toString(loct.getLongitude());
            timeValue = timeHelper.convertToHHMM(loct.getTime());

            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd", new Locale("en")).format(loct.getTime());

        }
            if (connectOnline == 1)
            {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(checkTypeKey, checkTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);
                validationBody(agencyIdValue,imageValue,latValue,lngValue);


            } else if (connectOnline == 2)  //offline
            {

                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(CheckTimeKey, timeValue);
                jsonData.put(CheckDateKey, dateVale);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(checkTypeKey, checkTypeValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);
                validationBody(agencyIdValue,imageValue,latValue,lngValue);



            }


        return jsonData;
    }
    public Map<String,String> postBreakInDataBody(int  connectOnline)
    {
        String timeValue="",dateVale="" ,latValue="" ,lngValue="";
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);

        Map<String, String> jsonData=null;
        // post break in body request

        // value json

        String agencyIdValue=SharedPrefData.getElementValue(namefile,userIdKeySp);
        String breakInTypeValue=DataConstant.breakInType;
        String  checkInIdValue=SharedPrefData.getElementValue(namefile, DataConstant.checkInIDJsonKey);
        String imageValue=SharedPrefData.getElementValue(namefile,imageKeySp);
        Location loct = searchCurrentLocation();
        if(loct ==null)
        {


            String oldLat = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey));
            String oldLng = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey));
            Long oldTime = SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps);
            Long timegps = oldTime;
            latValue = oldLat;
            lngValue = oldLng;
            timeValue = timeHelper.convertToHHMM(timegps);
            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(timegps);

            realFireBase.sendLogMsg("collectBodyNull",","+latValue+","+lngValue+","+timeValue+","+dateVale);

        }
        else {

             latValue = Double.toString(loct.getLatitude());
             lngValue = Double.toString(loct.getLongitude());
            timeValue = timeHelper.convertToHHMM(loct.getTime());

            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd", new Locale("en")).format(loct.getTime());

            realFireBase.sendLogMsg("collectBodyLoct",","+latValue+","+lngValue+","+timeValue+","+dateVale);

        }
         jsonData = new HashMap<String, String>();

            if (connectOnline == 1) {

                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(checkTypeKey, breakInTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);
                validationBody(agencyIdValue,imageValue,latValue,lngValue);

            } else if (connectOnline == 2)  //offline
            {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(CheckTimeKey, timeValue);
                jsonData.put(CheckDateKey, dateVale);
                jsonData.put(checkTypeKey, breakInTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);

                validationBody(agencyIdValue,imageValue,latValue,lngValue);

            }


        return jsonData;
    }
    public Map<String,String> postBreakOutDataBody(int  connectOnline)
    {
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);
        Map<String,String> jsonData = new  HashMap<String,String>();
        String timeValue="",dateVale="" ,latValue="" ,lngValue="";

        // post break out body request
        // value json

        String agencyIdValue=SharedPrefData.getElementValue(namefile,userIdKeySp);

        String breakOutTypeValue=DataConstant.breakOutType;
        String  checkInIdValue=SharedPrefData.getElementValue(namefile, DataConstant.checkInIDJsonKey);
        String imageValue=SharedPrefData.getElementValue(namefile,imageKeySp);
        Location loct = searchCurrentLocation();
        if(loct ==null)
        {
            String oldLat = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey));
            String oldLng = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey));
            Long oldTime = SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps);
            Long timegps = oldTime;

            latValue = oldLat;
            lngValue = oldLng;
            timeValue = timeHelper.convertToHHMM(timegps);
            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(timegps);
        }
        else {
             latValue = Double.toString(loct.getLatitude());
             lngValue = Double.toString(loct.getLongitude());
            timeValue = timeHelper.convertToHHMM(loct.getTime());
            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd", new Locale("en")).format(loct.getTime());
        }
            jsonData = new HashMap<String, String>();

            if (connectOnline == 1) {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(checkTypeKey, breakOutTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);
                validationBody(agencyIdValue,imageValue,latValue,lngValue);

            } else if (connectOnline == 2)  //offline
            {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(CheckTimeKey, timeValue);
                jsonData.put(CheckDateKey, dateVale);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(checkTypeKey, breakOutTypeValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);
                validationBody(agencyIdValue,imageValue,latValue,lngValue);


            }

        return jsonData;


    }
    public Map<String,String> postStillThereDataBody(int  connectOnline) {
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);
        Map<String, String> jsonData=null;
        String timeValue="",dateVale="" ,latValue="" ,lngValue="";

        // post still there body request

        // value json

        String agencyIdValue=SharedPrefData.getElementValue(namefile,userIdKeySp);
        String stillThereTypeValue=DataConstant.stillThereType;
        String  checkInIdValue=SharedPrefData.getElementValue(namefile, DataConstant.checkInIDJsonKey);
        String imageValue=SharedPrefData.getElementValue(namefile,imageKeySp);

        Location loct = searchCurrentLocation();
        if(loct ==null)
        {
            String oldLat = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey));
            String oldLng = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey));
            Long oldTime = SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps);
            Long timegps = oldTime;

            latValue = oldLat;
            lngValue = oldLng;
            timeValue = timeHelper.convertToHHMM(timegps);
            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(timegps);
        }
        else {

             latValue = Double.toString(loct.getLatitude());
             lngValue = Double.toString(loct.getLongitude());
            timeValue = timeHelper.convertToHHMM(loct.getTime());
            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd", new Locale("en")).format(loct.getTime());

        }
           jsonData = new HashMap<>();

            if (connectOnline == 1) {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(checkTypeKey, stillThereTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);

            } else if (connectOnline == 2)  //offline
            {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(CheckTimeKey, timeValue);
                jsonData.put(CheckDateKey, dateVale);
                jsonData.put(checkTypeKey, stillThereTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, imageValue);
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);

            }

        return jsonData;


    }


    public Map<String,String> postMissedStillThereDataBody(int  connectOnline) {
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);
        Map<String, String> jsonData=null;
        String timeValue="",dateVale="",latValue = "" ,lngValue= "" ;

        // post still there body request

        // value json

        String agencyIdValue=SharedPrefData.getElementValue(namefile,userIdKeySp);
        String stillThereTypeValue=DataConstant.stillThereType;
        String  checkInIdValue=SharedPrefData.getElementValue(namefile, DataConstant.checkInIDJsonKey);

        Location loct = searchCurrentLocation();
        if(loct ==null)
        {
            String oldLat = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey));
            String oldLng = String.valueOf(SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey));
            Long oldTime = SharedPrefData.getElementLongValue(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps);
            Long timegps = oldTime;

             latValue = oldLat;
             lngValue =oldLng;
            timeValue = timeHelper.convertToHHMM(Long.valueOf(oldTime));
            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd", new Locale("en")).format(oldTime);

        }
        else {
             latValue = Double.toString(loct.getLatitude());
             lngValue = Double.toString(loct.getLongitude());

            timeValue = timeHelper.convertToHHMM(loct.getTime());

            dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd", new Locale("en")).format(loct.getTime());



        }
        jsonData = new HashMap<>();
            if (connectOnline == 1) {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(checkTypeKey, stillThereTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, "");  // missed  still there not need image
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);
                jsonData.put(stillthereStateKey, "Missed");

            } else if (connectOnline == 2)  //offline
            {
                jsonData.put(agencyIDKey, agencyIdValue);
                jsonData.put(CheckTimeKey, timeValue);
                jsonData.put(CheckDateKey, dateVale);
                jsonData.put(checkTypeKey, stillThereTypeValue);
                jsonData.put(checkInIDKey, checkInIdValue);
                jsonData.put(imageKey, "");  // missed  Still there not need image
                jsonData.put(latKey, latValue);
                jsonData.put(lngKey, lngValue);
                jsonData.put(stillthereStateKey, "Missed");

            }

        return jsonData;


    }
    private void validationBody(String agencyId, String img, String lat, String lng) {
        Log.i("validationBody: ", "enter");


        if (agencyId.equals("") )
        {
            Toast.makeText(context, "not found your agency id", Toast.LENGTH_SHORT).show();
        }
        if (img.equals(""))
        {
            Toast.makeText(context,"Camera not capture image", Toast.LENGTH_SHORT).show();
        }
        if (lat.equals("")||lng.equals(""))
        {
            Toast.makeText(context, " Not found your location , please move around building", Toast.LENGTH_SHORT).show();
        }

    }
    public Map<String,String> postExcuseRequestBody(String exMessageValue, String exDateValue,String agencyIdValue , String exIdValue)
    {
        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);


        String timeValue="",dateVale="";


        Map<String,String> jsonData = new  HashMap<String,String>();

        jsonData.put(exMessageKey,exMessageValue);
        jsonData.put(exDateKey,exDateValue);
        jsonData.put(DataConstant.agencyIDJsonKeyUpcase,agencyIdValue);
        jsonData.put(exIdKey,exIdValue);

        return jsonData;

    }

    public Map<String,String> postVicationRequestBody( String agencyIdValue ,String vicDateFromValue ,String vicDateToValue ,  String vicIdValue)
    {

        timeHelper = new TimeHelper(context)  ;
        SharedPrefData = new SharedPrefData(context);
        Map<String,String> jsonData = new  HashMap<String,String>();
        jsonData.put(DataConstant.agencyIDJsonKeyUpcase,agencyIdValue);
        jsonData.put(vacDateFromKey,vicDateFromValue);
        jsonData.put(vacDateToKey,vicDateToValue);
        jsonData.put(vacIdKey,vicIdValue);
        return jsonData;
    }






    //----------------
public int getIntervalMinutesInt()
{
    int interval = SharedPrefData.getElementIntValue(namefile,stillRepeatingkeySp);

    //xxx
    //interval =6;

    return  interval;

}

    public long getStillRepeatingIntervalMinutes()
    {
        timeHelper = new TimeHelper(context);
        SharedPrefData = new SharedPrefData(context);

        //note you will  XXX here  get request  interval time
       //default
        int stillRepeating =SharedPrefData.getElementIntValue(namefile,stillRepeatingkeySp);

        //xxx
      // stillRepeating = 6;


        if (stillRepeating!=0)
        {
            Long timeRepeating =TimeUnit.MINUTES.toMillis(stillRepeating);
            return timeRepeating;
        }

        return 0; //still there time
    }

    public long getStillDurationTime()
    {
        timeHelper = new TimeHelper(context) ;
        SharedPrefData = new SharedPrefData(context);
        int stillDuration = SharedPrefData.getElementIntValue(namefile,stillDurationkeySp);
        //note you will  XXX here  get request  interval time

        //default
      //  stillDuration = 3;

        if (stillDuration!=0)
        {
            return TimeUnit.MINUTES.toMillis(stillDuration) ;
        }

        return 0; //still there time gap

    }

    public double getWorkingHoursTime()
    {
        timeHelper = new TimeHelper(context);
        SharedPrefData = new SharedPrefData(context);
        int workingHours= SharedPrefData.getElementIntValue(namefile,workingHourskeySp);

         //note you will  XXX here  get request  interval time

        //default
        if (workingHours!=0)
        {
            return (double) (workingHours) ;
        }


        return  (double)(2); //still there time gap


    }

    public int geticonImgeCheckType(int typeCheck)
    {
        switch (typeCheck)
        {
            case 1: // check in

                return navIcon[0];
            case 2: // check out

                return navIcon[0];

            case 3: // break in

                return navIcon[1];

            case 4: // break out
                return navIcon[1];

            case 5: // still there
                return R.drawable.ic_location_black_16dp;

            default:
                return navIcon[0];
        }
    }

    public String  getTitleCheckTypeLog(int typeCheck)
    {
        switch (typeCheck)
        {

            case 1: // check in
                return "check in";

            case 2: // check out
                return "check out";

            case 3: // break in
                return "break in";

            case 4: // break out
                return " break out";


            case 5: // still there
                return "still there";


            default:
                return " ";


        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            locationFromChanged = location;
            locationFromChanged.getLatitude();
            locationFromChanged.getLongitude();

        }
        assert location != null;
        boolean isMockLocation = location.isFromMockProvider();
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


    public void saveOfflineData( int connectFlag ,String checkType)
    {
        SharedPrefData = new SharedPrefData(context);
        Map<String, String> checkBody =null;
        String keySaveOffline = null;
        Boolean haveCount =true;
        String offlineCount = null;


        switch (checkType)
        {
            case DataConstant.checkInType :
                checkBody =  postCheckInDataBody(connectFlag);
                keySaveOffline = DataConstant.checkInOffline;
                haveCount=false;
                break;

            case DataConstant.checkOutType :
                checkBody= postCheckOutDataBody(connectFlag);
                keySaveOffline = DataConstant.checkOutOffline;

                haveCount=false;
                break;

            case DataConstant.breakInType :
                checkBody = postBreakInDataBody(connectFlag);
                keySaveOffline = DataConstant.breakInOffline;
                haveCount =true;
                offlineCount =DataConstant.breakInOfflineCount;
                break;


            case DataConstant.breakOutType :
                checkBody = postBreakOutDataBody(connectFlag);
                keySaveOffline = DataConstant.breakOutOffline;
                haveCount =true;
                offlineCount =DataConstant.breakOutOfflineCount;
                break;


            case DataConstant.stillThereType :
                checkBody  = postStillThereDataBody(connectFlag);
                keySaveOffline = DataConstant.stillOffline;
                haveCount =true;
                offlineCount =DataConstant.stillOfflineCount;
                break;

        }

        if(!haveCount)
        {
            if(!SharedPrefData.isExistsKey(offlineFile,keySaveOffline))
            {

                SharedPrefData.putSaveMap(offlineFile, keySaveOffline, checkBody);

                if(!SharedPrefData.isExistsKey(offlineFile,DataConstant.recordOfflineKeys))
                {
                    SharedPrefData.putElement(offlineFile, DataConstant.recordOfflineKeys, keySaveOffline);
                }
                else
                {
                    String oldRecord = SharedPrefData.getElementValue(offlineFile, DataConstant.recordOfflineKeys);
                    SharedPrefData.putElement(offlineFile, DataConstant.recordOfflineKeys,oldRecord + ","+ keySaveOffline);

                }



            }
        }
        else
        {


            if(!SharedPrefData.isExistsKey(offlineFile,keySaveOffline))
            {

                SharedPrefData.putIntElement(offlineFile ,offlineCount , 1);

                SharedPrefData.putSaveMap(offlineFile,keySaveOffline, checkBody);
                SharedPrefData.putElement(offlineFile, DataConstant.recordOfflineKeys, keySaveOffline);


            }
            else
            {

                int temp =SharedPrefData.getElementIntValue(offlineFile,offlineCount);

                String KeyName = keySaveOffline+String.valueOf(temp+1);

                SharedPrefData.putIntElement(offlineFile , offlineCount , temp+1);   // exp breakCount : 2

                if(!SharedPrefData.isExistsKey(offlineFile,DataConstant.recordOfflineKeys))
                {
                    SharedPrefData.putElement(offlineFile, DataConstant.recordOfflineKeys, KeyName);
                }
                else
                {
                    String oldRecord = SharedPrefData.getElementValue(offlineFile, DataConstant.recordOfflineKeys);
                    SharedPrefData.putElement(offlineFile, DataConstant.recordOfflineKeys,oldRecord+","+ KeyName);
                }

                SharedPrefData.putSaveMap(offlineFile, KeyName, checkBody);      // example breakin2
            }



        }


        SharedPrefData.checkTypeStageSP(Integer.parseInt(checkType), null, checkType +  "   offline... ");


            intentGoActivity(MainActivity.class, Integer.parseInt(checkType));

    }


    public  void intentGoActivity (Class whereYouGo,int extraTabcheckType)
    {
        if (whereYouGo != null)
        {
            String jumpExtraTab=DataConstant.jumpTab;
            Intent i = new Intent(context, whereYouGo);
            if(extraTabcheckType !=  Integer.parseInt(DataConstant.stillThereType))
            {
                i.putExtra(jumpExtraTab, String.valueOf(extraTabcheckType));
            }
            context.startActivity(i);

        }
    }


}




//             end


//    public String putPromoterLocationJson()
//    {
//
//        HashMap data = new HashMap();
//        data.put("email","email");
//        data.put("password","password");
//
//        RequestQueue queue = Volley.newRequestQueue(context);
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//
//
//            }
//        });
//
//        queue.add(jsObjRequest);
//
//         return "";
//    }






