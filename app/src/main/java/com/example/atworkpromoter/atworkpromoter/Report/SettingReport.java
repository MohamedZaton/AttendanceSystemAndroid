package com.pclink.attendance.system.Report;

import android.content.Context;
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
import com.pclink.attendance.system.Models.SettingReportModel;
import com.pclink.attendance.system.Wrapper.FormSettingWrapper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class SettingReport {
    public Context mContext ;
    public SharedPrefData sharedPrefData ;

    public SettingReport(Context context) {
        mContext = context;
        sharedPrefData = new SharedPrefData(mContext);
    }

    public void  getReqSetting()
    {
        String url = DataConstant.serverUrl + DataConstant.getSettingSalesUrl+sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);


            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("ok_setting_sales", " " + response.toString());

                    sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.saveSettingSalesFormKey,response.toString());


                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error_setting_sales", " " + error.toString());

                }
            });
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);


    }

    public HashMap<String ,Boolean> getSettingMap()
    {
        HashMap<String,Boolean> settingMap  = new HashMap<>();
        String responsStr =  sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.saveSettingSalesFormKey);
        Gson gson = new Gson();
        FormSettingWrapper settingWrapper = gson.fromJson(responsStr , FormSettingWrapper.class);
        try {
            List<SettingReportModel> settingReportModels = settingWrapper.getFromSettings();

            for (SettingReportModel item : settingReportModels) {

                Log.i("list_sales", item.getFieldID() + "= " + item.getStatus());
                boolean status;
                if (item.getStatus().equals("Enabled")) {
                    status = true;
                } else   // Disabled
                {
                    status = false;
                }
                settingMap.put(item.getFieldID(), status);

            }
        }
        catch (NullPointerException e)
        {
            return  new HashMap<String, Boolean>();
        }


        return  settingMap ;



    }
    public   HashMap<String ,String> getSettingName()
    {
        HashMap<String,String> nameMap  = new HashMap<>();
        String responsStr =  sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.saveSettingSalesFormKey);
        Gson gson = new Gson();
        FormSettingWrapper settingWrapper = gson.fromJson(responsStr , FormSettingWrapper.class);
        try {
            List<SettingReportModel> settingReportModels = settingWrapper.getFromSettings();

            for (SettingReportModel item : settingReportModels) {

                Log.i("list_sales_names", item.getFieldID() + "= " + item.getFieldName());

                nameMap.put(item.getFieldID(), item.getFieldName());

            }
        }
        catch (NullPointerException e)
        {
            return  new HashMap<String, String>();
        }


        return  nameMap ;


    }



}
