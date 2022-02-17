package com.pclink.attendance.system.FilesAttachmentUploading;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.pclink.attendance.system.Activities.SplashScreen;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.Models.UploadFileModel;
import com.pclink.attendance.system.NetworkServer.NetworkClient;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DocumentUploadingActivity extends AppCompatActivity implements  FileDetails {
    Button selectFileBtn  ;
    ImageView imgPicturePt , backBtnPt ;
    public TextView nameFileTxtVw , typeFileTxtVw , sizeFileTxtVw ;
    public DialogAll dialogAll ;
    public MainAsynctask mainAsynctask ;
    private static final int SELECT_PDF = 1 ,   UPLOADING_PDF =3, FINISHED_PDF =4;

    public int statePdf = 0;
    public String selectedPdfPath;
    String scheme =null ;
    public TextView  title_tv;
    public SharedPrefData sharedPrefData ;
    public CardView detailBoxCardView;
    public  String uploadCompressedFile = null ;
    public    Uri selectPDF =null ;
    private String selectedVideoPath;
    File myFile = null ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_uploading);

        selectFileBtn = findViewById(R.id.select_pdf_btn);
        imgPicturePt = findViewById(R.id.file_upload_pdf_vw);
        backBtnPt = findViewById(R.id.back_arrow_btn_pdf);
        nameFileTxtVw  = findViewById(R.id.name_pdf_textview);
        sizeFileTxtVw  = findViewById(R.id.size_pdf_textview);
        typeFileTxtVw  = findViewById(R.id.type_pdf_textView);

             dialogAll = new DialogAll(this);
          sharedPrefData = new SharedPrefData(this);
           mainAsynctask = new MainAsynctask(this,222);
        this.detailBoxCardView = findViewById(R.id.details_box_cardview);
        this.title_tv = findViewById(R.id.title_file_state_txt);

        if (statePdf == 0 || statePdf == SELECT_PDF)
        {
         selectFileState();
        }
        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (statePdf == 0 || statePdf == SELECT_PDF)
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    //intent.putExtra("browseCoa", itemToBrowse);
                    //Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
                    try {
                        //startActivityForResult(chooser, FILE_SELECT_CODE);
                        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),SELECT_PDF);
                    } catch (Exception ex) {
                        System.out.println("browseClick :"+ex);//android.content.ActivityNotFoundException ex
                    }
                }
                else if(statePdf == UPLOADING_PDF)
                {
                    Log.i("select_path",selectPDF.toString());
                        String pdfPath =  PathUtil.getPath(getBaseContext(),selectPDF);
                        int responseValue = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile,DataConstant.ResponseIDKey);
                        Log.i("response_id",""+responseValue);
                        uploadToServer(pdfPath, responseValue);
                }else  if (statePdf == FINISHED_PDF )
                {
                    finishFileState();
                    startActivity(new Intent(DocumentUploadingActivity.this,  FilesReportActivity.class));
                }
            }
        });




        backBtnPt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DocumentUploadingActivity.this,  FilesReportActivity.class));
            }
        });




        /*uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectPDF!=null)
                {

                    uploadToServer(selectPDF,110);
                }
                else
                {
                    Toast.makeText(DocumentUploadingActivity.this, "You must select video first ", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //  dg.exitAppDialog();

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    public  void selectFileState()
    {

        detailBoxCardView.setVisibility(View.GONE);


    }
    public  void uploadFileState ()
    {

        detailBoxCardView.setVisibility(View.VISIBLE);

        title_tv.setText("Upload PDF");
        selectFileBtn.setText("Upload");
        imgPicturePt.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud));
        selectFileBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.category_family)));
        selectFileBtn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_upload_button, 0, 0, 0);



    }
    public  void finishFileState()
    {

        title_tv.setText("Success Uploaded");
        selectFileBtn.setText("Finish");
        selectFileBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.btn_send)));

        selectFileBtn.setCompoundDrawablesWithIntrinsicBounds(  R.drawable.ic_finish_upload, 0, 0, 0);

    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PDF:
                Log.i("select_pdf","ok");
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Log.i("select_pdf","ok");

                    selectPDF = data.getData();
                    String uriString = selectPDF.toString();
                     myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = "";
                    Toast.makeText(this, "it selected", Toast.LENGTH_SHORT).show();
                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(selectPDF, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));


                            }
                        } finally {
                            cursor.close();
                        }

                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }
                    scheme = selectPDF.getScheme();
                    nameFileTxtVw.setText(displayName);
                    sizeFileTxtVw.setText(setFileSize(scheme,selectPDF));
                    typeFileTxtVw.setText(setFileType(this,selectPDF));
                    uploadFileState();
                    statePdf=UPLOADING_PDF;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public  String setFileSize(String scheme, Uri selectFile)
    {
        long dataSize = 0 ;
        if(scheme.equals(ContentResolver.SCHEME_CONTENT))
        {
            try {
                InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(selectFile);
                dataSize = fileInputStream.available();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Toast.makeText(this, "File size in bytes"+dataSize, Toast.LENGTH_SHORT).show();
           // sizeFileTxtVw.setText(""+getFileSize(dataSize));
            return (""+getFileSize(dataSize));
        }else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            File fileX = null;
            String path = selectFile.getPath();
            try {
                fileX = new File(path);
                //  Toast.makeText(this, "File size in bytesLgth"+fileX.length(), Toast.LENGTH_SHORT).show();
               // sizeFileTxtVw.setText(""+getFileSize(fileX.length()));
                return  (""+getFileSize(fileX.length()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  "" ;
    }
    public  String setFileType(Context context, Uri uri) {
        String typeFile = "";

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            typeFile = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            typeFile = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        // Toast.makeText(context, "type file : " +typeFile, Toast.LENGTH_SHORT).show();
       // typeFileTxtVw.setText(typeFile);
         return typeFile;
    }

    public String setFileName(Uri selectFile)
    {
         //nameFileTxtVw.setText(selectFile.getLastPathSegment());
        //        Toast.makeText(this, " "+selectFile.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        return (selectFile.getLastPathSegment());
    }

    private void uploadToServer(String filePath,int ResponseIDValue) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadFileModel uploadAPIs = retrofit.create(UploadFileModel.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("Files", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        //int ResponseID = 110;
        RequestBody ResponseID = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ResponseIDValue));
        final ProgressDialog dialogUp = new ProgressDialog(DocumentUploadingActivity.this);
        dialogUp.setMessage("File uploading... \nPlease wait");
        dialogUp.show();
        Call call = uploadAPIs.uploadFile(part, ResponseID);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.e("success_upload",response.toString());
                dialogUp.dismiss();
                dialogUp.cancel();
                //   Toast.makeText(ImagesUploadingActivity.this, "ok _upload :"+ response.toString(), Toast.LENGTH_SHORT).show();
                if(response.code()==200)
                {
                    finishFileState();
                    statePdf=FINISHED_PDF;
                    dialogAll.txtImageMsgRequest(R.drawable.ic_checked,"Success","","Done" , SplashScreen.class);

                }else
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_cancel, "Failed", " " + response.message(), "ok", SplashScreen.class);
                }

            }
            @Override
            public void onFailure(Call call, Throwable t)
            {
                Log.e("error_upload","error_upload " + t.getMessage());
                dialogUp.dismiss();
                dialogUp.cancel();
                //Toast.makeText(ImagesUploadingActivity.this, "error_upload " + t.getMessage(), Toast.LENGTH_SHORT).show();
                if(mainAsynctask.isConnected())
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_error_network, "Failed", "Connect Network" + t.getMessage(), "ok", SplashScreen.class);

                }else
                {
                    dialogAll.txtImageMsgRequest(R.drawable.ic_cancel, "Failed", " " + t.getMessage(), "ok", SplashScreen.class);
                }

            }
        });
    }

    public static String getFileSize(long size) {

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];

    }


}