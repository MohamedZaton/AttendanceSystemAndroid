package com.pclink.attendance.system.TabRequest;

public class itemCardViewExcuseList {
private  String ExuseTxt;


    private String ExId;
    public itemCardViewExcuseList(String exuseTxt ,String exId ) {

        ExuseTxt = exuseTxt;
        ExId=exId;
    }



    public String getExId() {
        return ExId;
    }

    public void setExId(String exId) {
        ExId = exId;
    }




    public String getExuseTxt() {
        return ExuseTxt;
    }

    public void setExuseTxt(String exuseTxt) {
        ExuseTxt = exuseTxt;
    }


}
