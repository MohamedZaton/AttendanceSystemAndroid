package com.pclink.attendance.system.Models;

import com.google.gson.annotations.SerializedName;

public class VacDaysModel {

    @SerializedName("daysleft")
    private  int daysleft ;

    public int getDaysleft()
    {
        return daysleft;
    }
}
