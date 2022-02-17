package com.pclink.attendance.system.Models;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadFileModel {
    @Multipart
    @POST("Files/UploadReportFiles")
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part file, @Part("ResponseID")  RequestBody  ResponseID);
}
