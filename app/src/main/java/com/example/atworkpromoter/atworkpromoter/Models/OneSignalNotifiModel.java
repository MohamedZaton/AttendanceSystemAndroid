package com.pclink.attendance.system.Models;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class OneSignalNotifiModel {

    @SerializedName("app_id")
    @Expose
    public String appId;
    @SerializedName("include_player_ids")
    @Expose
    public List<String> includePlayerIds = null;

    @SerializedName("filters")
    @Expose
    public List<FilterOneSignalModel> filters = null;
    @SerializedName("data")
    @Expose
    public DataOneSignalModel data;
    @SerializedName("contents")
    @Expose
    public ContentsOneSignalModel contents;

    /**
     * No args constructor for use in serialization
     *
     */
    public OneSignalNotifiModel() {
    }

    public OneSignalNotifiModel(String appId, List<FilterOneSignalModel> filters, DataOneSignalModel data, ContentsOneSignalModel contents) {
        super();
        this.appId = appId;
        this.filters = filters;
        this.data = data;
        this.contents = contents;
    }

}

