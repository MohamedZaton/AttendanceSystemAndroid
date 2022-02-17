package com.pclink.attendance.system.Notification;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Models.ContentsOneSignalModel;
import com.pclink.attendance.system.Models.DataOneSignalModel;
import com.pclink.attendance.system.Models.FilterOneSignalModel;
import com.pclink.attendance.system.Models.OneSignalNotifiModel;
import com.google.gson.Gson;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public  class OneSignalHelper {
  public    static   OneSignalNotifiModel oneSignalNotifiModel ;

 public  static  String oneSignal_id_to_super ="b7677aac-b8f1-4eae-806e-edf860b250c4";
 public static Context mContext ;

    public OneSignalHelper(Context context) {
        mContext = context ;
    }

    public   void pushNotification(String title , String message , String KeyTag , String ValueTag)
{
    List<FilterOneSignalModel> filterList = new ArrayList<>();
    filterList.add(new FilterOneSignalModel("tag",KeyTag,"=",ValueTag));
    oneSignalNotifiModel = new OneSignalNotifiModel(oneSignal_id_to_super ,filterList, new DataOneSignalModel("bar"), new ContentsOneSignalModel(message) ) ;
    Gson gson = new Gson();
    String stringJsonBody = gson.toJson(oneSignalNotifiModel);
    Log.i("body_notfi",stringJsonBody);
    JSONObject jsonObjBody = null;
    String url ="https://onesignal.com/api/v1/notifications" ;

    try {
        jsonObjBody = new JSONObject(stringJsonBody);

        postNotif(url , jsonObjBody);
    } catch (JSONException e) {
        e.printStackTrace();
    }



}

    public  void testNot()
    {
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "YWQxZDMzODEtMDY2OC00MTc4LTg1M2ItYjc5ZWQ0ZjhjNjA4");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"b7677aac-b8f1-4eae-806e-edf860b250c4\","
                    +   "\"filters\": [{\"field\": \"tag\", \"key\": \"ID\", \"relation\": \"=\", \"value\": \"243\"}],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"contents\": {\"en\": \"English Message\"}"
                    + "}";


            Log.i("strJsonBodyX:\n" , strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            Log.i("httpResponse: " ,""+ httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            Log.e("jsonResponse: " , jsonResponse);

        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

private static void postNotif(String url, final JSONObject body) {

 /*   OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
        @Override
        public void idsAvailable(String userId, String registrationId) {
            Log.d("debug", "UserId:" + userId);
            if (registrationId != null) {
                Log.d("debug", "registrationId:" + registrationId);
                try {
                    OneSignal.postNotification(body,
                            new OneSignal.PostNotificationResponseHandler() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Log.i("OneSignalExample", "postNotification Success: " + response.toString());

                                }

                                @Override
                                public void onFailure(JSONObject response) {
                                    Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });

*/


}




}
