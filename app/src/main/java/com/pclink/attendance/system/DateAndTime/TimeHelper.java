package com.pclink.attendance.system.DateAndTime;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {
  Context context;
    SharedPrefData sharedPrefData;
    String promoterFile = DataConstant.promoterDataNameSpFile;
    public TimeHelper(Context context) {
        this.context = context;
         sharedPrefData =new SharedPrefData(context);

    }


        public void savebreakInTime()
        {

             sharedPrefData.putElementLong(promoterFile,DataConstant.breakInTimeSp,getTimeNow());

        }

        public  Long getbreakInTime()
        {

            return sharedPrefData.getElementLongValue(promoterFile,DataConstant.breakInTimeSp);
        }



    public void  saveLeaveAt()
    {
        long cikOutTime= getTimeNow();
        saveTimeSp(DataConstant.CheckOutTimeSp,cikOutTime);

    }

    public  Long getLeaveAtLong()
    {

        Long timeMs = getTimeSp(DataConstant.CheckOutTimeSp);
        return timeMs;

    }

    public void  saveArrivedAt()
        {

            long cikinTime= getTimeNow();
            saveTimeSp(DataConstant.CheckInTimeSp,cikinTime);

        }


        public long clearTime()
        {
               return Long.valueOf(0);

        }

    public void  clearArrivedAt()
    {

        long cikinTime= clearTime();
        saveTimeSp(DataConstant.CheckInTimeSp,cikinTime);

    }
        public  String getArrivedAtHHmm()
        {
            int clock[] ;
            Long timeMs = getArrivedAtLong();
            clock=clockHMSarray(timeMs);
            String arrivedAt= " "+clock[0]+ ":"+clock[1]+" ";
            return arrivedAt;

        }

    public  Long getArrivedAtLong()
    {

        Long timeMs = getTimeSp(DataConstant.CheckInTimeSp);
        return timeMs;

    }



            public Long  getWheelTimeNow()
            {
                Long oldArrivedTime=getArrivedAtLong();
                long nowTime = getTimeNow();


                    Long lastHours=subtractTimeHHmmss( nowTime, oldArrivedTime);
                String lH = convertToHHMMSS(lastHours);
                Log.e("llHHHHHHH",lH);

                        return lastHours;



            }



            public Long getWheelTimeDestroy( String key)
            {

                return  getTimeSp(key);
            }



        public long getTimeNow()
        {

            long time= System.currentTimeMillis();
            return  time;
        }

    public void saveTimeSp(String key ,Long timeMs)
    {
        sharedPrefData.putElementLong(promoterFile,key,timeMs);
    }
    public Long getTimeSp(String key )
    {
        Long time =sharedPrefData.getElementLongValue(promoterFile,key);

        return  time ;
    }




    public String convertToHHMMSS(Long timeMs)
    {
        int h   = (int)(timeMs /3600000);
        int m = (int)(timeMs - h*3600000)/60000;
        int s= (int)(timeMs - h*3600000- m*60000)/1000 ;
        String time = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s); // "hh:mm:ss"

        return  time;
    }

    public String convertToHHMM(Long timeMs)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", new Locale("en"));
        Date resultdate = new Date(timeMs);
        String hhmm =  sdf.format(resultdate);
        return hhmm;
    }

    public int[] clockHMSarray(Long timeMs)
    {
        int clock[] = new int[3];
        int h   = (int)(timeMs /3600000);
        int m = (int)(timeMs - h*3600000)/60000;
        int s= (int)(timeMs - h*3600000- m*60000)/1000 ;

        clock[0]=h;
        clock[1]=m;
        clock[2]=s;
        return  clock;
    }

    public long subtractTimeHHmmss(Long time1 ,Long time2)
    {

        Date date1=new Date(time1);
        Date date2 = new Date(time2);

        Long subDate= date1.getTime() - date2.getTime();
            int[] time1array= clockHMSarray(time1) ;
            int[] time2array= clockHMSarray(time2) ;
            int h = time1array[0] - time2array[0];

            int m = time1array[1] - time2array[1];
            int s = time1array[2] - time2array[2];

        h=Math.abs(h);
        m=Math.abs(m);
        s=Math.abs(s);


        String timeResult = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s); // "hh:mm:ss"

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", new Locale("en"));
        java.util.Date date11 = null;


        try {
            date11 = df.parse("1970-01-01 " +timeResult);


        } catch (ParseException e) {
            e.printStackTrace();
        }



        return subDate;
    }
}
