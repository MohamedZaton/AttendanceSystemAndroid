package com.pclink.attendance.system.FilesAttachmentUploading;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.iceteck.silicompressorr.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImagesUploadingActivity extends AppCompatActivity implements  FileDetails {
    Button selectFileBtn  ;
    ImageView imgPicturePt , backBtnPt ;
    public TextView nameFileTxtVw , typeFileTxtVw , sizeFileTxtVw ;
    public DialogAll dialogAll ;
    public MainAsynctask mainAsynctask ;
    private static final int SELECT_IMG = 1 , COMPRESS_IMG =2, UPLOADING_IMG =3, FINISHED_IMG =4;
    public int stateImg = 0;
    public    Uri selectImg =null ;
    public String selectedImgPath;
    String scheme =null ;
    int sizefilesCt = 0 , amountFilesCt=0;

    private ProgressBar progressBar;
    public TextView progress_tv , title_tv;
    public CardView detailBoxCardView;
    public  String uploadCompressedFile = null ;
    public SharedPrefData sharedPrefData ;
    public 	int amountToSet = 50;  // compress 50%
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_uploading);
        selectFileBtn = findViewById(R.id.select_img_btn);

        imgPicturePt = findViewById(R.id.file_upload_img_vw);
        backBtnPt = findViewById(R.id.back_arrow_btn_image);

        nameFileTxtVw  = findViewById(R.id.name_img_textview);
        sizeFileTxtVw = findViewById(R.id.size_img_textview);
        typeFileTxtVw = findViewById(R.id.type_img_textView);

        dialogAll = new DialogAll(this);
        sharedPrefData = new SharedPrefData(this);
        mainAsynctask = new MainAsynctask(this,222);
        progressBar = findViewById(R.id.progressBar_uploading);
        this.progress_tv = findViewById(R.id.text_details_txt) ;
        this.title_tv = findViewById(R.id.title_file_state_txt);
        this.detailBoxCardView = findViewById(R.id.details_box_cardview);

          sizefilesCt = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile,DataConstant.fileSizeKey);
         amountFilesCt = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile,DataConstant.fileAmountKey);

        if(stateImg == 0 || stateImg == SELECT_IMG)
        {
            selectFileState();
        }else if(stateImg == COMPRESS_IMG)
        {
            compressFileState();
        }


        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stateImg == 0 || stateImg == SELECT_IMG) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

                }
                else if(stateImg== COMPRESS_IMG)
                {

                    //    compressFileState();
                    Toast.makeText(ImagesUploadingActivity.this, "Compressing", Toast.LENGTH_SHORT).show();
                    if(selectImg!=null) {
                        String filePath = FileUtils.getPath(ImagesUploadingActivity.this, selectImg);
                        String extType =setFileType(ImagesUploadingActivity.this,selectImg);
                        String newPath = filePath.replace("."+extType, "_Compressed."+extType);
                        new ImagesUploadingActivity.ImgCompressAsyncTask(ImagesUploadingActivity.this, amountToSet).execute(filePath, newPath);

                    }
                }else  if(stateImg == UPLOADING_IMG) {
                    if (uploadCompressedFile != null) {
                        uploadToServer(uploadCompressedFile, 110);

                    }
                }else  if (stateImg == FINISHED_IMG )
                {


                    finishFileState();

                    startActivity(new Intent(ImagesUploadingActivity.this,  FilesReportActivity.class));
                }


            }
        });

        backBtnPt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImagesUploadingActivity.this,  FilesReportActivity.class));
            }
        });

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
        progressBar.setVisibility(View.GONE);
        progress_tv.setVisibility(View.GONE);
        detailBoxCardView.setVisibility(View.GONE);


    }

    public  void compressFileState()
    {
        progressBar.setVisibility(View.GONE);
        progress_tv.setVisibility(View.VISIBLE);
        detailBoxCardView.setVisibility(View.VISIBLE);

        title_tv.setText("Compress Img");
        selectFileBtn.setText("Compress");
        selectFileBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_async)));
        selectFileBtn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_compress_file, 0, 0, 0);




    }

    public  void uploadFileState ()
    {
        progressBar.setVisibility(View.VISIBLE);
        progress_tv.setVisibility(View.VISIBLE);
        detailBoxCardView.setVisibility(View.VISIBLE);

        title_tv.setText("Upload Img");
        selectFileBtn.setText("Upload");
        imgPicturePt.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud));
        selectFileBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.category_family)));
        selectFileBtn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_upload_button, 0, 0, 0);



    }
    public  void finishFileState()
    {
        progress_tv.setText("");
        progressBar.setVisibility(View.GONE);
        title_tv.setText("Success Uploaded");
        selectFileBtn.setText("Finish");
        selectFileBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.btn_send)));

        selectFileBtn.setCompoundDrawablesWithIntrinsicBounds(  R.drawable.ic_finish_upload, 0, 0, 0);

    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            selectFileBtn.setVisibility(View.VISIBLE);
            selectImg = data.getData();
            scheme = selectImg.getScheme();

            if (requestCode == SELECT_IMG) {
                selectedImgPath =  FileUtil.getPath(selectImg,getBaseContext());
                try {
                    if(selectedImgPath == null) {
                        Log.e("error_img","selected img path = null!");
                    } else
                    {

                        Toast.makeText(this, "selected", Toast.LENGTH_SHORT).show();

                        String sizeImgBf =   setFileSize(scheme,Uri.fromFile(new File(selectedImgPath)));
                        sizeFileTxtVw.setText(sizeImgBf);

                        String nameImg= setFileName(Uri.fromFile(new File(selectedImgPath)));
                        nameFileTxtVw.setText(nameImg);

                        String typeImg =setFileType(getBaseContext(),Uri.fromFile(new File(selectedImgPath)));
                        typeFileTxtVw.setText(typeImg);
                        compressFileState();
                        stateImg = COMPRESS_IMG ;


                        //          new ImgCompressAsyncTask(this).execute(selectImg.toString(), f.getPath());
                      /*  runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String dir = new File(selectImg.getPath()).getParent() ;

                                File f = new File(dir,"temp.mp4");
                                String filePath = null;

                                    Log.i("f_path",f.getPath());
                                    Log.i("select_path",selectImg.getPath());
                                    Log.i("unit_path",selectedImgPath);

                                   // filePath = SiliCompressor.with(getBaseContext()).compressImg(selectImg.getPath(), f.getPath());




                            }
                        });
*/

                    }
                } catch (Exception e) {
                    //#debug
                    e.printStackTrace();
                }


            }
        }
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
            return (""+getFileSize(dataSize));
        }else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            File fileX = null;
            String path = selectFile.getPath();
            try {
                fileX = new File(path);
                long length =fileX.length();
                //  Toast.makeText(this, "File size in bytesLgth"+fileX.length(), Toast.LENGTH_SHORT).show();
                return(""+getFileSize(fileX.length()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "" ;
    }
    public boolean  isSizeFilesAllow(int SizeLinit , String pathName)
    {
        // Get file from file name
        File file = new File(pathName);

// Get length of file in bytes
        long fileSizeInBytes = file.length();
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        long remainder  = SizeLinit-fileSizeInMB ;
        if(remainder<0)
        {
            return  false ;

        }else
        {
            return  true ;
        }


    }
    public  String setFileType(Context context, Uri uri) {
        String typeFile ="";

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            typeFile = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
            return  typeFile;
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            typeFile = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            return  typeFile ;
        }
        // Toast.makeText(context, "type file : " +typeFile, Toast.LENGTH_SHORT).show();
        // typeFileTxtVw.setText(typeFile);

    }

    public String setFileName(Uri selectFile)
    {
        // nameFileTxtVw.setText(selectFile.getLastPathSegment());
        //        Toast.makeText(this, " "+selectFile.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        return selectFile.getLastPathSegment();
    }



    public static String getFileSize(long size) {

        size = size/1024;
        return size + " KB";
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
        final ProgressDialog dialogUp = new ProgressDialog(ImagesUploadingActivity.this);
        dialogUp.setMessage("File uploading... \nPlease wait");
        dialogUp.show();
        Call call = uploadAPIs.uploadFile(part, ResponseID);
        if(isSizeFilesAllow(sizefilesCt,filePath))
        {
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
                        stateImg=FINISHED_IMG;
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

        }else {
            dialogAll.infoMsgOneBtn("The upload failed \nBecause your file was too big \nThe maximum size is "+ sizefilesCt +" MB" ,"OK");
        }

    }
    class ImgCompressAsyncTask extends AsyncTask<String, Float, String> {

        Context mContext;
        int amountToCompressToLocal;

        public ImgCompressAsyncTask(Context context) {
            mContext = context;
            this.amountToCompressToLocal = 0;
        }

        public ImgCompressAsyncTask(Context context, int percentToCompressDownToLocal) {
            mContext = context;
            this.amountToCompressToLocal = percentToCompressDownToLocal;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onProgressUpdate(Float... values) {
            if(values == null){
                return;
            }
            if(values.length <= 0){
                return;
            }
            if(true) { //Flip to false to stop printing here
                progressBar.setIndeterminate(false);
                try {
                    if(values != null) {
                        if(values[0] != null) {
                            float flt = ((values[0]) * 100);
                            Log.d("SelectPictureActivity", "Progress Complete: " + (flt) + "%");
                            progressBar.setProgress((int) flt);
                            progress_tv.setText("Progress: " + ((int) flt) + "%");

                            if(flt > 40){
                                if(false) { //Flip this to true to foce manual cancel when progress hits 40%
                                    Log.d("1", "Attempting manual cancel of progress conversion @ >= 40%");
                                }
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                progressBar.setIndeterminate(true);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            Log.d("d", "Do in background, amount to compress local == " + amountToCompressToLocal);

            try {


                try {
                    if(paths[0]!=null)
                    {

                        Log.i("path_image_org" , paths[0]);
                        File compressedImage = new Compressor(ImagesUploadingActivity.this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(amountToCompressToLocal)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(new File(paths[0]));

                        filePath = compressedImage.getPath();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            return (filePath == null) ? "" : filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            String value;

            value = getFileSize(imageFile.length());
            sizeFileTxtVw.setTextColor(getResources().getColor(R.color.category_family));
            sizeFileTxtVw.setText("" +value);
            uploadFileState();
            uploadCompressedFile = compressedFilePath ;
            stateImg = UPLOADING_IMG;

        }
    }
    private void triggerEstimatedMillisecondsLeft(Long est){
        if(est == null){
            return;
        }
        Log.d("1", "Estimated Number of MilliSeconds left: " + est);
    }

}

/* private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/