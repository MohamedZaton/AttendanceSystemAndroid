package com.pclink.attendance.system.NetworkServer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.Activities.SignIn;
import com.pclink.attendance.system.Activities.SplashScreen;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.InterfaceHelper.VolleyInterFaceHelper;
import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.Report.ReportFormActivity;
import com.google.gson.JsonObject;
import com.sendbird.android.shadow.okhttp3.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.content.Context.MODE_PRIVATE;

public class NetworkRequestPr {
    Context context;
    String tagLog;
    SharedPrefData sharedPrefData;
    DialogAll dialogAll;
    Promoter promoter ;
    String TAG  =" networlReqiestPr";
    public NetworkRequestPr(Context context , String tagLog) {
        this.context = context;
        this.tagLog=tagLog;
        sharedPrefData=new SharedPrefData(this.context);
        dialogAll=new DialogAll(this.context);
        promoter = new Promoter(this.context);
    }

    public  String getRequestVolleyItem(String url , final String key, final String typeGetValue)
    {
        final SharedPrefData sharedPrefData = new SharedPrefData(context);
        RequestQueue queue = Volley.newRequestQueue(context);
        final JsonPr jsonPr =new JsonPr();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,
                        "tempRequest",
                        jsonPr.getValueObjtJson(response.toString(),key,typeGetValue));


            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                Log.e(tagLog, error.toString());
            }
        });

        queue.add(jsObjRequest);
        return sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,"tempRequest");

    }











    public  void getStringRequestVolleyItem(String url , final String TAG)
    {

        final SharedPrefData sharedPrefData = new SharedPrefData(context);
        RequestQueue queue = Volley.newRequestQueue(context);
        final JsonPr jsonPr =new JsonPr() ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,response);





                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, error.toString());


            }
        });

        queue.add(stringRequest);


    }





    public void postStringRequestVolleyItem(String url , final Map<String, String>  stringBody , final String TAG)
    {
        final SharedPrefData sharedPrefData = new SharedPrefData(context);
        RequestQueue queue = Volley.newRequestQueue(context);
        final JsonPr jsonPr =new JsonPr() ;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e(TAG+" onResponse",response);
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                // error
                Log.i(TAG+" onErrorResponse", error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                return stringBody;
            }

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);

    }




    public boolean uploadingOfflineRequestVolley(String url , final Map<String,String> bodyJson , final String messageUploading)
    {

        final JSONObject[] responseHolder = new JSONObject[1];
        final Object[] responseHolderOO = new Object[1];

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final JsonPr jsonPr = new JsonPr();


        RequestQueue queue = Volley.newRequestQueue(context);

        Log.e("messageUploading "+ messageUploading ,bodyJson.toString());
        // Start Request uploading //
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest( Request.Method.POST, url, new JSONObject(bodyJson), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                responseHolder[0] = response;
                countDownLatch.countDown();

                //-------------------------------- else


            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                responseHolderOO[0] = error;

                countDownLatch.countDown();


                //else


                Log.e(messageUploading,error.toString());

                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.isSendOfflineFlag,false);


            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        queue.add(jsObjRequest);


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (responseHolderOO[0] instanceof VolleyError) {
            final VolleyError volleyError = (VolleyError) responseHolderOO[0];
            return  false;
        } else {

            JSONObject response =responseHolder[0];
            String checkTypeInBody=bodyJson.get(DataConstant.checkTypeJsonKey);
            if (checkTypeInBody.equals(DataConstant.checkInType))
            {

/*


                String checkinId = (!response.has(DataConstant.checkInIDJsonKey)) ? "" : jsonPr.getValueObjtJson(response.toString(), DataConstant.checkInIDJsonKey, "s");
                sharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkInIDJsonKey, checkinId); // Save CheckID responce
                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.isSendOfflineFlag, true);

*/


            }


            return true;

        }


    }



    public String uploadingFutchRequestVolley(String SIGNUP_URL , final Map<String,String> reqBody ,int indexL)
    {



        String response;
        JsonPr jsonPr = new JsonPr();
        JSONObject  jsReq = null;
        SharedPrefData sharedPrefData  = new SharedPrefData(context);

        RequestQueue volleyRequestQueue = Volley.newRequestQueue(context);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        String checkTypeInBody=reqBody.get(DataConstant.checkTypeJsonKey);

        if (!checkTypeInBody.equals(DataConstant.checkInType))
        {


            String getCheckInID = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.checkInIDJsonKey); // Save CheckID responce

            reqBody.put(DataConstant.checkInIDJsonKey, getCheckInID);


        }
        if (checkTypeInBody.equals(DataConstant.checkOutType))
        {
            String   urlSalesRep = DataConstant.serverUrl+DataConstant.offlineSalesRepUrl;
                       JSONObject mBodyReportJson =  sharedPrefData.getLoadJsonObj(DataConstant.offlineModeFile,DataConstant.reportStoreOffline);
                            String getCheckInID = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.checkInIDJsonKey); // Save CheckID responce
                               String getAgencyID = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase) ;
                                try {

                                    mBodyReportJson.put(DataConstant.checkInIDJsonKey, getCheckInID);
                                    mBodyReportJson.put(DataConstant.agencyIDJsonKeyUpcase,getAgencyID);

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                                postReportOffline( urlSalesRep, mBodyReportJson);

        }





        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, new JSONObject(reqBody), future, future);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        volleyRequestQueue.add(request);


        try {

            jsReq =   future.get();
            sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.indexUploadOffline,-1);

            response =  jsonPr.getValueObjtJson(jsReq.toString(),DataConstant.checkInIDJsonKey,"s");
            if (checkTypeInBody.equals(DataConstant.checkInType)) {
                sharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkInIDJsonKey, response);
            }

            return response;

        } catch (InterruptedException e) {
            sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.indexUploadOffline,indexL);
           Log.e("Offline_error",e.getMessage());
            response = "xExc";
            e.printStackTrace();
        }catch (ExecutionException e) {
            sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.indexUploadOffline,indexL);
            response = "xExc";
            Log.e("Offline_error",e.getMessage());
            if (e.getCause() instanceof ClientError) {
                ClientError error = (ClientError)e.getCause();
                switch (error.networkResponse.statusCode) {

                }
            }

            e.printStackTrace();
        }
        //  Log.i("jsReq",jsReq.toString());

        return  response;
    }



    private   void   postReportOffline(String urlOffline,JSONObject body)
    {

        RequestFuture<JSONObject> futureR = RequestFuture.newFuture();
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(context);
        JSONObject reportRsponse;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlOffline, body, futureR, futureR);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        volleyRequestQueue.add(request);

        try {
            reportRsponse =   futureR.get();

          //  String getSalesFormID =  JsonPr.getValueObjtJson(reportRsponse.toString(),DataConstant.salesFormIdKey,"s");
         //   sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.salesFormIdKey,getSalesFormID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }





    }



    public  void postRequestVolley(String url , Map<String,String> bodyJson, final int stageCheckType , final Class whereYouGo,String messageUploadingDialog)
    {


        //example bodyJson above this method ZZZ
        final int connectFlagOffline = 2 ;
        String bodyStr = new JSONObject(bodyJson).toString();

        if(bodyJson != null) {

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(bodyJson), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.e(tagLog, "rsponceCheck -->" + stageCheckType + " data to server : " + response.toString());
                    Log.i("postRequestVolley :", "onResponse: "+ "accept" );
                    sharedPrefData.checkTypeStageSP(stageCheckType, response, tagLog);
                    intentGoActivity(whereYouGo, stageCheckType);
                }

            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e(tagLog, "errorx_send_Req_ch:" + stageCheckType + " data to server : " + error.toString());
                    Log.e("postRequestVolley :", "onErrorResponse: "+ "reject"  );

                    promoter.saveOfflineData(connectFlagOffline, String.valueOf(stageCheckType));
                    intentGoActivity(whereYouGo, stageCheckType);

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
            dialogAll.infoMsgOneBtn("gps signal lost","try again ");

        }
    }


    public void getPromoterDataCheckOut()
    {
        String url = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.promoterUrlPathKey);
        final String namefileSp = DataConstant.promoterDataNameSpFile;
        // shared pref
        final String stillRepeatingkeySp = DataConstant.stillRepeatingJsonKey;
        final String workingHourskeySp = DataConstant.workingHoursJsonKey;
        final String stillDurationkeySp = DataConstant.stillDurationJsonKey;

        //final
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                SharedPreferences prefA= context.getSharedPreferences(DataConstant.promoterDataNameSpFile,MODE_PRIVATE);
                Map<String, ?> prefsMap = prefA.getAll();
                int stillRepeating=0,workingHours=0,stillDuration=0;

                for (Map.Entry<String, ?> entry: prefsMap.entrySet())
                {
                    Log.v("SharedPreferences", entry.getKey() + ":" +
                            entry.getValue().toString());
                }

                String getPromoterIDJson = JsonPr.getValueObjtJson(response.toString(), "agencyID","s");

                if(!getPromoterIDJson.equals(""))
                {
                    stillRepeating = Integer.parseInt(JsonPr.getValueObjtJson(response.toString(), stillRepeatingkeySp, "i"));
                    workingHours = Integer.parseInt(JsonPr.getValueObjtJson(response.toString(), workingHourskeySp, "i"));
                    stillDuration = Integer.parseInt(JsonPr.getValueObjtJson(response.toString(), stillDurationkeySp, "i"));

                    // time work  , stillThere Time
                    sharedPrefData.putIntElement(namefileSp,stillRepeatingkeySp,stillRepeating);
                    sharedPrefData.putIntElement(namefileSp,workingHourskeySp,workingHours);
                    sharedPrefData.putIntElement(namefileSp,stillDurationkeySp,stillDuration);
                }


            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        });

        queue.add(jsObjRequest);

    }

    public void getVacationDaysBalance() {
        RequestQueue queue = Volley.newRequestQueue(context);


        String url = DataConstant.serverUrl + DataConstant.getVacationDays+sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase) ;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.i("response",response.toString());
                int days =Integer.parseInt(JsonPr.getValueObjtJson(response.toString(),DataConstant.vacDaysCikKey , "i"));


                if(days<0)
                {
                    days = 0;
                }
                sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.vacDaysCikKey,days);
                Log.i("daysX"," "+days);


            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
                Log.e("errorG", error.toString());

            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        queue.add(jsObjRequest);


    }

    public  int   postExcVacRequestVolley(String url ,Map<String,String> body )
    {

        RequestQueue queue = Volley.newRequestQueue(context);
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading... Please wait");
        dialog.show();
        // Start Request uploading //
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(body), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.i("response",response.toString());
                String mystring = context.getResources().getString(R.string.upload_success);

                dialog.dismiss();
                dialog.cancel();
                dialogAll.txtImageMsgRequest(R.drawable.ic_checked,"Success",mystring,"Done" ,SplashScreen.class);

            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
                Log.e("errorG", error.toString());
                String mystring = context.getResources().getString(R.string.upload_failed );
                dialog.dismiss();
                dialog.cancel();
                dialogAll.txtImageMsgRequest(R.drawable.ic_cancel,"Failed",mystring,"ok" ,SplashScreen.class);
            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        queue.add(jsObjRequest);
        return 1;
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

            if(extraTabcheckType== Integer.parseInt(DataConstant.checkInType))     //start * still there after check in
            {
                 Log.i("still_extra","start_still");
                i.putExtra(DataConstant.stillThereRunKeysp,1);
            }
            if(extraTabcheckType== Integer.parseInt(DataConstant.checkOutType)) // stop still there after check out
            {
                Log.i("still_extra","stop_still");

                i.putExtra(DataConstant.stillThereRunKeysp,2);
            }

            if(extraTabcheckType== Integer.parseInt(DataConstant.breakInType))  // stop still there after break in
            {
                Log.i("still_extra","stop_still");

                i.putExtra(DataConstant.stillThereRunKeysp,2);
            }
            if(extraTabcheckType== Integer.parseInt(DataConstant.breakOutType))  // start *  still there after break out
            {
                Log.i("still_extra","start_still");

                i.putExtra(DataConstant.stillThereRunKeysp,1);
            }

            context.startActivity(i);
        }
    }

    public boolean isSiteBlocked(String urlServer ) {

        return false;
    }


}
