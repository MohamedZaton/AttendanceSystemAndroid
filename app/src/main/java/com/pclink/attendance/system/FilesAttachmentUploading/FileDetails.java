package com.pclink.attendance.system.FilesAttachmentUploading;

import android.content.Context;
import android.net.Uri;

public interface FileDetails {
    public  String setFileSize(String scheme, Uri selectFile);
    public  String setFileType(Context context, Uri uri);
    public String setFileName(Uri selectFile);
}
