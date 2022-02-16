package com.pclink.attendance.system.DataBase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.DateAndTime.TimeHelper;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SharedPrefData {
    Context context;
    SharedPreferences sharedPreferences;


    public SharedPrefData(Context context) {
        this.context = context;


    }

    public String getElementValue(String nameFolder, String key) {
        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
            return sharedPreferences.getString(key, null);
        }

        return "";


    }
    public int getElementIntValue(String nameFolder, String key)
    {

        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(key, 0);
        }

        return 0;


    }

    public void putElement(String nameFolder, String key, String value) {

        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Long getElementLongValue(String nameFolder, String key) {
        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
            return sharedPreferences.getLong(key, 0);
        }

        return Long.valueOf(0);


    }


    public boolean getElementBooleanValue(String nameFolder, String key) {
        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(key, false);
        }

        return false;

    }


    public void putElementLong(String nameFolder, String key, Long value) {

        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }


    public void putElementBoolean(String nameFolder, String key, boolean value) {

        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putIntElement(String nameFolder, String key, int value) {

        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void removeElementSp(String nameFolder, String key) {

        SharedPreferences settings = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        settings.edit().remove(key).apply();
    }

    public void removeFileSp(String nameFolder) {

        SharedPreferences settings = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }


    public boolean isExistsKey(String nameFolder, String key) {
        sharedPreferences = context.getSharedPreferences(nameFolder, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            return true;
        }

        return false;


    }


    public void checkTypeStageSP(int stage, JSONObject response, String tagLog) {
        JsonPr jsonPr = new JsonPr();

        switch (stage) {
            case 1: // check in


                if(response != null)
                {
                    Log.e(tagLog, " success post request **checkIn** data to server : " + response.toString());

                    String checkinId = jsonPr.getValueObjtJson(response.toString(), DataConstant.checkInIDJsonKey, "s");
                    putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkInIDJsonKey, checkinId); // Save CheckID responce

                }



                putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkToggleButtonSP, DataConstant.checkOutType);
                putElement(DataConstant.promoterDataNameSpFile, DataConstant.breakToggleButtonSP, DataConstant.breakInType);

                putPromoterDayFlag(true,false,false,false,false,true); //* check in
                putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.LogOutClk,false);


                // check wheel
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.startCheckWheelSp, true); //*start
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.stopCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.pauseCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.resumeCheckWheelSp, false);

                 //save time arrive at wheel start


                long cikinTime = new TimeHelper(context).getTimeNow();
                putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.CheckInTimeSp,cikinTime);

                break;
            case 2: // check out

                if(response != null) {

                    Log.e(tagLog, " success post request  **checkOut** data to server : " + response.toString());
                }

                putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkToggleButtonSP, null);
                putElement(DataConstant.promoterDataNameSpFile, DataConstant.breakToggleButtonSP, null);

                putPromoterDayFlag(false,true,false,false,false,false); //* check out
                putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.LogOutClk,true);

                // check wheel
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.startCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.stopCheckWheelSp, true); //*stop
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.pauseCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.resumeCheckWheelSp, false);

                //clear  time
                long clearTime = new TimeHelper(context).clearTime();
                putElementLong(DataConstant.promoterDataNameSpFile, DataConstant.CheckInTimeSp, clearTime);
                putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.pausebreakInWheelKey,clearTime);
                putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.resumebreakInWheelKey,clearTime);
                break;


            case 3: // break in

                if(response != null) {

                    Log.e(tagLog, " success post request   **breakIn** data to server : " + response.toString());
                }

                putElement(DataConstant.promoterDataNameSpFile, DataConstant.breakToggleButtonSP, DataConstant.breakOutType);
                putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkToggleButtonSP, DataConstant.checkOutType);
                putPromoterDayFlag(true,false,true,false,false,false); //* break in


                // check wheel
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.startCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.stopCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.pauseCheckWheelSp, true); //*pause
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.resumeCheckWheelSp, false);


                // break in pause


                long brkinTime = new TimeHelper(context).getTimeNow();

                putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.pausebreakInWheelKey,brkinTime);

                break;

            case 4: // break out
                if(response != null) {
                    Log.e(tagLog, " success post request   **breakOut** data to server : " + response.toString());
                }
                putElement(DataConstant.promoterDataNameSpFile, DataConstant.breakToggleButtonSP, DataConstant.breakInType);
                putElement(DataConstant.promoterDataNameSpFile, DataConstant.checkToggleButtonSP, DataConstant.checkOutType);
                putPromoterDayFlag(false,false,false,true,false,true); //* break out

                // check wheel
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.startCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.stopCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.pauseCheckWheelSp, false);
                putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.resumeCheckWheelSp, true); //*resume
                long brkOutTime = new TimeHelper(context).getTimeNow();
                putElementLong(DataConstant.promoterDataNameSpFile,DataConstant.resumebreakInWheelKey,brkOutTime);

                break;

            case 5: // still there
                if(response != null)
                {

                    Log.e(tagLog, " success post request  ** StillThere **data to server : " + response.toString());

                }
                putPromoterDayFlag(false,false,false,false,true,true);  //* still there

                break;

        }

    }


    public String stageWheelNow()   //get
    {
        boolean startWheel = getElementBooleanValue(DataConstant.promoterDataNameSpFile, DataConstant.startCheckWheelSp);
        boolean stopWheel = getElementBooleanValue(DataConstant.promoterDataNameSpFile, DataConstant.stopCheckWheelSp);
        boolean pauseWheel = getElementBooleanValue(DataConstant.promoterDataNameSpFile, DataConstant.pauseCheckWheelSp);
        boolean resumeWheel = getElementBooleanValue(DataConstant.promoterDataNameSpFile, DataConstant.resumeCheckWheelSp);

        if (startWheel == true && stopWheel == false && pauseWheel == false && resumeWheel == false) {
            return DataConstant.startCheckWheelSp;

        } else if (startWheel == false && stopWheel == true && pauseWheel == false && resumeWheel == false) {
            return DataConstant.stopCheckWheelSp;

        } else if (startWheel == false && stopWheel == false && resumeWheel == true && pauseWheel == false) {
            return DataConstant.resumeCheckWheelSp;

        } else if (startWheel == false && stopWheel == false && resumeWheel == false && pauseWheel == true) {
            return DataConstant.pauseCheckWheelSp;
        }

        return DataConstant.stopCheckWheelSp;


    }


    public void putPromoterDayFlag(boolean chackInFlag,boolean checkOutFlag, boolean breakInFlag, boolean breakOutFlag , boolean stillThereFlag,boolean stillThereRun)

    {

        putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.chackInFlagSp,chackInFlag);      // check in

        putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.checkOutFlagSp,checkOutFlag);       //check out

        putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.breakInFlagSp,breakInFlag);      // break in

        putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.breakOutFlagSp,breakOutFlag);      //* break out

        putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.stillThereRunKeysp, stillThereRun);  // start still Thread


    }


    public void putSaveMap(String nameFile,String key, Map<String,String> inputMap)
    {
        SharedPreferences pSharedPref = context.getSharedPreferences(nameFile, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(key).apply();
            editor.putString(key, jsonString);
            editor.apply();
        }
    }

    public  void putSaveJsonObj(String nameFile,String key, JSONObject inputJsonOj)
    {
        SharedPreferences pSharedPref = context.getSharedPreferences(nameFile, Context.MODE_PRIVATE);
        if (pSharedPref != null)
         {
             String jsonString = inputJsonOj.toString();
             SharedPreferences.Editor editor = pSharedPref.edit();
             editor.remove(key).apply();
             editor.putString(key, jsonString);
             editor.apply();
        }

    }

    public   JSONObject getLoadJsonObj(String nameFile,String key)
    {
        JSONObject outputJson = new JSONObject();
        SharedPreferences pSharedPref =context.getSharedPreferences(nameFile, Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null)
            {

                String jsonString = pSharedPref.getString(key, (new JSONObject()).toString());
                 outputJson = new JSONObject(jsonString);

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return outputJson;


    }

    public Map<String,String> getLoadMap(String nameFile,String key)
    {
        Map<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref =context.getSharedPreferences(nameFile, Context.MODE_PRIVATE);
        try{
            if (pSharedPref!= null)
            {
                String jsonString = pSharedPref.getString(key, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext())
                {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }

}
