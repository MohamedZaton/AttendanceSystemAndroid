package com.pclink.attendance.system.Report;

public class DataProductList {
   private String mName,mId,mCodeSku;

    public DataProductList(String mName, String mId, String mCodeSku) {
        this.mName = mName;
        this.mId = mId;
        this.mCodeSku = mCodeSku;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmCodeSku() {
        return mCodeSku;
    }

    public void setmCodeSku(String mCodeSku) {
        this.mCodeSku = mCodeSku;
    }
}
