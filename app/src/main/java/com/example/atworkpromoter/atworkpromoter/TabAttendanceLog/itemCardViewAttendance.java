package com.pclink.attendance.system.TabAttendanceLog;

import com.pclink.attendance.system.DataBase.DataConstant;

public class itemCardViewAttendance {
    private     int miconAtt;
    private  String titleAtt;
    private String dateAtt;
    private String locationAtt;

    private String timeAtt;

    public itemCardViewAttendance(){}

    public itemCardViewAttendance(int miconAtt, String titleAtt, String dateAtt,String timeAtt, String locationAtt) {
        this.miconAtt = miconAtt;
        this.titleAtt = titleAtt;
        this.timeAtt=timeAtt;
        this.dateAtt = dateAtt;
        this.locationAtt = locationAtt;
    }

    public void setMiconAtt(int miconAtt) {
        this.miconAtt = miconAtt;
    }

    public void setTitleAtt(String titleAtt) {
        this.titleAtt = titleAtt;
    }

    public void setDateAtt(String dateAtt) {
        this.dateAtt = dateAtt;
    }

    public void setLocationAtt(String locationAtt) {
        this.locationAtt = locationAtt;
    }


    public void setTimeAtt(String timeAtt) {
        this.timeAtt = timeAtt;
    }



    public int getMiconAtt() {
        return miconAtt;

    }

    public String getTitleAtt() {
        return titleAtt;
    }

    public String getDateAtt() {
        return dateAtt;
    }

    public String getLocationAtt() {
        return locationAtt;
    }

    public String getTimeAtt() {
        return timeAtt;
    }

}
