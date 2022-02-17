package com.pclink.attendance.system.Wrapper;

import com.pclink.attendance.system.Models.SettingReportModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FormSettingWrapper {
    @SerializedName("fromSettings")
    @Expose
    private List<SettingReportModel> fromSettings ;

    public List<SettingReportModel> getFromSettings() {
        return fromSettings;
    }

    public void setFromSettings(List<SettingReportModel> fromSettings) {
        this.fromSettings = fromSettings;
    }

}
