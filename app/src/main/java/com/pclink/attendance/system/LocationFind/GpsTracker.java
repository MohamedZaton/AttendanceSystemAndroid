package com.pclink.attendance.system.LocationFind;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.content.ContextCompat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.Models.FakeLoctModel;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


public class GpsTracker implements LocationListener {

    public  Context context;
    SharedPrefData sharedPrefData;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location location;
    double latitude;
    double longitude;
    MainAsynctask mainAsynctask;
    DialogAll dialogAll;
    gpsService mGpsService;
    Activity mActivity;
    List<Address> addresses ;
    String locationName="" ;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 ;
    protected LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;


    public GpsTracker() {
    }

    public GpsTracker(Context context) {
        this.context = context;
        sharedPrefData = new SharedPrefData(context);
        mainAsynctask = new MainAsynctask(context,8888);
         dialogAll = new DialogAll(context);
         mGpsService = new gpsService(this.context);
             mActivity = getActivity(context);
        if(ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            Log.i("permLoct", "true");

            getLocation();
            fakeLoctMockCheck();



            if(mActivity!=null)
            {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);

            }

        }

    }


    @SuppressLint("MissingPermission")
    public Location getLocation() {



        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled  = mainAsynctask.isLocationEnabled(context);

            isNetworkEnabled = mainAsynctask.isConnected();



              if (!isGPSEnabled)
              {
                  try
                  {
                      dialogAll.showGPSDisabledAlertToUser().show();
                  }
                  catch (Exception e)
                  {
                      dialogAll.showGPSDisabledAlertToUser().dismiss();
                      dialogAll.showGPSDisabledAlertToUser().cancel();
                  }

              }
              else
                {


                    if (isGPSEnabled)
                    {

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                    }

                    if (isNetworkEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            if (locationManager != null)
                            {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                                if (location != null)
                                {

                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                }
                            }

                        }
                    }




                }




        } catch (Exception e) {
            e.printStackTrace();
        }

        if(location != null)
        {
            long latt = (long) location.getLatitude();
            long longg = (long) location.getLongitude();
            long time = location.getTime();
            String  timeForm = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss a" , new Locale("en")).format(location.getTime());
            Log.e("gps"," lat = " + latt+" ");
            Log.e("gps","lng  = "+ longg+" ");
            Log.e("gps","time = "+ timeForm+" ");
            Geocoder geocoder = new Geocoder(context);
            try {
              addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
              /*  String state = addresses.get(0).getAdminArea();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();*/
                Log.e("gps","location_Name = "+ address+" "+city+" "+country);
                locationName=address;

            } catch (IOException e) {
                e.printStackTrace();
            }



            sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey, latt);

            sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey, longg);
            sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.nameLocationJsonKey, locationName);
            sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps, time);

        }else
        {
            location= mGpsService.getLocation(LocationManager.GPS_PROVIDER);
            if (location==null)
                {
                    location= mGpsService.getLocation(LocationManager.NETWORK_PROVIDER);
                }
            if(location==null)
                {
                    location= mGpsService.getLastKnownLocation();
                }
        }


        if(location==null && mFusedLocationClient!=null)
        {
            Location fLocation ;
            // Permissions ok, we get last location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(context), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            // Got last known location. In some rare situations, this can be null.
                            if (location != null) {
                                Log.i("locationMYM", "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());

                                long latt = (long) location.getLatitude();
                                long longg = (long) location.getLongitude();
                                long time = location.getTime();
                                sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey, latt);

                                sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey, longg);
                                sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps, time);
                            }

                        }

                    });
        }


            return location;
    }
    public  void  fakeLocRequestServer( String checkTypeAction )
    {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm a", new Locale("en"));
        int agencyID = Integer.parseInt(sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase));
        String statusNet ="online" ;
        if(!mainAsynctask.isConnected())
        {
            statusNet ="offline";

        }
        FakeLoctModel fakeLoctModel  = new FakeLoctModel(agencyID,checkTypeAction,statusNet,df.format(getTimeNow()));
        Gson gson = new Gson();
        final String fakeStrJsonBody = gson.toJson(fakeLoctModel);
        Log.i("fake_body"," "+ fakeStrJsonBody);
        JSONObject jsonObjBody = new JSONObject();
        try
        {
            jsonObjBody = new JSONObject(fakeStrJsonBody);

        } catch(JSONException e)
        {
            e.printStackTrace();
        }

        String url = DataConstant.serverUrl+DataConstant.fakeLoctUrl ;
        if(statusNet.equals("online")) {

            RequestQueue queue = Volley.newRequestQueue(context);

            if(sharedPrefData.isExistsKey(DataConstant.otherDataFile,DataConstant.fakeLoctBodyOffline) )
            {
                String oldBody = sharedPrefData.getElementValue(DataConstant.otherDataFile,DataConstant.fakeLoctBodyOffline );

                try {
                    jsonObjBody = new JSONObject(oldBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sharedPrefData.removeFileSp(DataConstant.otherDataFile);

            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjBody, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    Log.i("fake_loct_ok", response.toString());


                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("fake_loct_error", error.toString());

                    sharedPrefData.putElement(DataConstant.otherDataFile,DataConstant.fakeLoctBodyOffline ,fakeStrJsonBody );

                }
            });
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);


        }
        else
        {
            // save fake location offline body
            Log.i("fake_loct_offline", fakeStrJsonBody);

            sharedPrefData.putElement(DataConstant.otherDataFile,DataConstant.fakeLoctBodyOffline ,fakeStrJsonBody );

        }





    }
    public long getTimeNow()
    {

        long time= System.currentTimeMillis();
        return  time;
    }

    public  void  fakeLoctMockCheck()
    {
        boolean isFackeLoction = isMockSettingsON(context) ;
        boolean isAppsUseMock = areThereMockPermissionApps(context) ;
        boolean isMock ;
       try {
           isMock = location.isFromMockProvider();

       }catch (NullPointerException e)
       {
           isMock = false ;
       }

        Log.i("isMockLocationThree", " "+ isMock); //working well
        Log.i("isMockLocationOne", " "+ isFackeLoction);
        Log.i("isMockLocationTwo", " "+ isAppsUseMock);

        if(isMock)
        {
            Log.i("isMockLocation", " "+ isMock);
            sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey,isMock); ; //this phone use mock location

        }else
        {
            Log.i("isMockLocation", " "+ isMock);
            sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.isUseMockFakeLoctkey,isMock); ; //this phone use mock location

        }
    }
    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }
    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }
    public void stopUsingGPS() {
        if(locationManager != null) {
            locationManager.removeUpdates(GpsTracker.this);
        }
    }




    @Override
    public void onLocationChanged(Location arg0) {
      Location loct = arg0;
      if(loct != null)
      {
        long latt = (long) loct.getLatitude();
          long longg = (long) loct.getLongitude();
            long time = loct.getTime();
            String  timeForm = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss a" , new Locale("en")).format(loct.getTime());
          Log.e("gps","Changed : lat = " + latt+" ");
          Log.e("gps","Changed :lng  = "+ longg+" ");
          Log.e("gps","Changed : time = "+ timeForm+" ");

          sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.latLocationJsonKey, latt);
          sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.lngLocationJsonKey, longg);
          sharedPrefData.putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.timeMilsGps, time);

      }


    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

}