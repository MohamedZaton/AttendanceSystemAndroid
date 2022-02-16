package com.pclink.attendance.system.Json;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonPr {


    public static String getValueObjtJson(String jsonResponse, String key, String typeGetValue)

    {
        String value = null;
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }


        try {


            JSONObject rootJsonResponceOjt = new JSONObject(jsonResponse);

            switch (typeGetValue) {
                case "i": // int

                    value = String.valueOf(rootJsonResponceOjt.getInt(key));
                    break;
                case "s": // string
                    value = rootJsonResponceOjt.getString(key);
                    break;
                case "d":   //double
                    value = String.valueOf(rootJsonResponceOjt.getDouble(key));
                    break;
                case "b": //boolean
                    value = String.valueOf(rootJsonResponceOjt.getBoolean(key));
                    break;
                default:
                    value = rootJsonResponceOjt.getString(key);


            }
        } catch (JSONException e) {
            value = "";

        }

        //here will be  loop for another data --> review news app

        return value;
    }


    public static String getPromoterJsonElementValue(String jsonResponse, String key) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        String getId = null;
        try {
            JSONObject rootJsonResponceOjt = new JSONObject(jsonResponse);
            getId = String.valueOf(rootJsonResponceOjt.getInt(key));//"agencyID"

        } catch (JSONException e) {
            getId = null;

        }

        //here will be  loop for another data --> review news app

        return getId;
    }


    public static ArrayList<HashMap<String, String>> getValueObjtArrayJson(String jsonResponse, String[] arrayAttributesKeys)
    {
        ArrayList<HashMap<String, String>> jsonArrayList = new ArrayList<>();


        // tmp hash map for a single json
        HashMap<String, String> singleJsonUnit = new HashMap<>();

        JSONArray JsonResponceArray;
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }








            // add each child node to HashMap key => value

            try {
                JsonResponceArray = new JSONArray(jsonResponse);
                  int lenthResponce =   JsonResponceArray.length();
                for (int i = 0; i < lenthResponce; i++) {

                    JSONObject e = JsonResponceArray.getJSONObject(i);
                    singleJsonUnit=new HashMap<String, String>();

                    for (int j = 0; j < arrayAttributesKeys.length; j++)
                    {
                        String keyH = arrayAttributesKeys[j];
                        String valueH = e.getString(arrayAttributesKeys[j]);

                        singleJsonUnit.put(keyH,valueH );

                    }
                    jsonArrayList.add(i,singleJsonUnit);


                }
                return jsonArrayList;

            } catch (JSONException e) {

            }



        return jsonArrayList;
    }

    public static ArrayList<HashMap<String, String>> getValueObjtArrayJsonWitharrayKey(String jsonResponse,String mainkey ,String[] arrayAttributesKeys)
    {

        ArrayList<HashMap<String, String>> jsonArrayList = new ArrayList<>();

        JSONObject jsonObj = null;
        JSONArray getArrayJson = null;
           int lenthResponce = 0 ;
        // tmp hash map for a single json
        HashMap<String, String> singleJsonUnit = new HashMap<>();


        if (jsonResponse != null)
        {



            try {
                jsonObj = new JSONObject(jsonResponse);

                 getArrayJson = jsonObj.getJSONArray(mainkey);
                lenthResponce =getArrayJson.length();
                for (int i = 0; i < lenthResponce; i++)
                {

                    JSONObject e = getArrayJson.getJSONObject(i);
                    singleJsonUnit=new HashMap<String, String>();

                    for (int j = 0; j < arrayAttributesKeys.length; j++)
                    {
                        String keyH = arrayAttributesKeys[j];
                        String valueH = e.getString(arrayAttributesKeys[j]);

                        singleJsonUnit.put(keyH,valueH );

                    }
                    jsonArrayList.add(i,singleJsonUnit);


                }
                return jsonArrayList;

            } catch (JSONException e) {

            }

        }

        return jsonArrayList;

    }
}
