package com.pclink.attendance.system.DateAndTime;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    public DateHelper()
    {

    }

    public Calendar getDayCalender(int day , int month, int year)
{
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_MONTH,day);
    calendar.set(Calendar.MONTH,month);
    calendar.set(Calendar.YEAR, year);

    return calendar;

}

public  int  subtractTwoDate(int fDay , int fMonth, int fYear , int sDay , int sMonth, int sYear)

                {
                    int days = 0;
                    Long date1,date2;
                    date1= getDayCalender(fDay,fMonth,fYear).getTimeInMillis();
                    date2= getDayCalender(sDay,sMonth,sYear).getTimeInMillis();


                    long difference = date1 - date2;
                     days = (int) (difference/ (1000*60*60*24));




                    return  days;
                }



}




