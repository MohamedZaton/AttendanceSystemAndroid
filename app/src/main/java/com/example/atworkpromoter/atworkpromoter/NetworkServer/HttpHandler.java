package com.pclink.attendance.system.NetworkServer;

/**
 * Created by vickytu on 7/24/17.
 */

import android.text.TextUtils;
import android.util.Log;

import com.pclink.attendance.system.DataBase.Promoter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpHandler {
        String promoterUsername="";
        int promoterId=-1;


    public HttpHandler() {

    }

    public String createHttpRequest(String Domin,String control,String action , String other )
    {
        String url = Domin+control+action+other+"";

        return  url ;
    }




    public String createLoginHttpRequestURL(String Domin,String control,String action ,String id, String password )
  {
       String url = createHttpRequest(Domin,control,action,id + "/"+ password);

       return  url ;
  }



    public List<Promoter> fatchParameterData(String newurl)
    {
        URL url =createUrl(newurl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();

        }
        List<Promoter> promoterList = extractFeatureFromJson(jsonResponse);

        return promoterList;
    }

    private List<Promoter> extractFeatureFromJson(String jsonResponse)
        {
            if (TextUtils.isEmpty(jsonResponse))
            {
                return null;
            }

            List<Promoter> promotersList = new ArrayList<>();
            try
            {
                    JSONObject rootJsonResponceOjt=new JSONObject(jsonResponse);
                    promoterUsername=rootJsonResponceOjt.getString("name");
                    promoterId=rootJsonResponceOjt.getInt("agencyID");

            } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("info error","errror json file data ");

            }

                //here will be  loop for another data --> review news app

           return  promotersList;
        }


    public String cikPromoterData(String jsonResponse)
    {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        String  getId=null;
        try {
            JSONObject rootJsonResponceOjt=new JSONObject(jsonResponse);
            getId= String.valueOf(rootJsonResponceOjt.getInt("agencyID"));

        } catch (JSONException e) {
            getId=null;

        }

        //here will be  loop for another data --> review news app

        return  getId;
    }


    public String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = convertStreamToString(inputStream);

        } catch (IOException e) {
            //handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return jsonResponse;
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
                sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }
}

