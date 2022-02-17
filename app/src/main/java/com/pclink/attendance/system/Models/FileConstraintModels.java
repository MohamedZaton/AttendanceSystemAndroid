package com.pclink.attendance.system.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileConstraintModels {
    @SerializedName("filesize")
    @Expose
    private Integer filesize;
    @SerializedName("numberOfFiles")
    @Expose
    private Integer numberOfFiles;
    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    public Integer getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

}