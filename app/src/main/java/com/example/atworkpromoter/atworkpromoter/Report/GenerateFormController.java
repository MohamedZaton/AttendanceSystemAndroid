package com.pclink.attendance.system.Report;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.DateAndTime.TimeHelper;
import com.pclink.attendance.system.Models.ReportGenerateModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class GenerateFormController{
    public  static String FormType="Sales";
    public SharedPrefData sharedPrefData ;
    public ReportGenerateModel generateModel ;
    public  Context  mContext  ;
    public  GenerateFormController(Context context)
    {

        mContext = context ;
        sharedPrefData = new SharedPrefData(context);

    }





}
