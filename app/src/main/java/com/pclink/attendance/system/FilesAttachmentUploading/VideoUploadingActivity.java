package com.pclink.attendance.system.FilesAttachmentUploading;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.Models.UploadFileModel;
import com.pclink.attendance.system.NetworkServer.NetworkClient;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.iceteck.silicompressorr.CompressionException;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.iceteck.silicompressorr.VideoConversionProgressListener;


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

public class VideoUploadingActivity extends AppCompatActivity implements  FileDetails {
    Button selectFileBtn  ;
    ImageView videoPicturePt , backBtnPt ;
    public TextView nameFileTxtVw , typeFileTxtVw , sizeFileTxtVw ;
    public DialogAll dialogAll ;
    public MainAsynctask mainAsynctask ;
    private static final int SELECT_VIDEO = 1 , COMPRESS_VIDEO =2, UPLOADING_VIDEO =3, FINISHED_VIDEO =4;
    public int stateVideo = 0;
    public    Uri selectVideo =null ;
    public String selectedVideoPath;
    String scheme =null ;
    private ProgressBar progressBar;
    public TextView progress_tv , title_tv;
    public CardView detailBoxCardView;
    public  String uploadCompressedFile = null ;
    public 	Float amountToSet = 0.7F;  // compress 70%

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_uploading);
        selectFileBtn = findViewById(R.id.select_video_btn);

        videoPicturePt = findViewById(R.id.file_upload_img_vw);
        backBtnPt = findViewById(R.id.back_arrow_btn_image);
        nameFileTxtVw  = findViewById(R.id.name_video_textview);
        sizeFileTxtVw = findViewById(R.id.size_video_textview);
        typeFileTxtVw = findViewById(R.id.type_video_textView);
        dialogAll = new DialogAll(this);
        mainAsynctask = new MainAsynctask(this,222);
        progressBar = findViewById(R.id.progressBar_uploading);
        this.progress_tv = findViewById(R.id.text_details_txt) ;
        this.title_tv = findViewById(R.id.title_file_state_txt);
        this.detailBoxCardView = findViewById(R.id.details_box_cardview);


        if(stateVideo == 0 || stateVideo == SELECT_VIDEO)
        {
            selectFileState();
        }else if(stateVideo == COMPRESS_VIDEO)
        {
            compressFileState();
        }


        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateVideo == 0 || stateVideo == SELECT_VIDEO) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECT_VIDEO);


                }
                else if(stateVideo== COMPRESS_VIDEO)
                {

                    //    compressFileState();
                    Toast.makeText(VideoUploadingActivity.this, "Compressing", Toast.LENGTH_SHORT).show();
                    if(selectVideo!=null) {
                        String filePath = FileUtils.getPath(VideoUploadingActivity.this, selectVideo);
                           String extType =setFileType(VideoUploadingActivity.this,selectVideo);
                        String newPath = filePath.replace("."+extType, "_Compressed."+extType);

                        new VideoCompressAsyncTask(VideoUploadingActivity.this, amountToSet).execute(filePath, newPath);

                    }
                }else  if(stateVideo == UPLOADING_VIDEO) {
                    if (uploadCompressedFile != null) {
                        uploadToServer(uploadCompressedFile, 110);

                    }
                }else  if (stateVideo == FINISHED_VIDEO )
                {


                    finishFileState();

                    startActivity(new Intent(VideoUploadingActivity.this, FilesReportActivity.class));
                }

            }
        });

     backBtnPt.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             startActivity(new Intent(VideoUploadingActivity.this, FilesReportActivity.class));
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
        progressBar.setVisibility(View.VISIBLE);
        progress_tv.setVisibility(View.VISIBLE);
        detailBoxCardView.setVisibility(View.VISIBLE);
        title_tv.setText("Compress Video");
        selectFileBtn.setText("Compress");
        selectFileBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_async)));
        selectFileBtn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_compress_file, 0, 0, 0);
    }


    public  void uploadFileState ()
    {
        progressBar.setVisibility(View.VISIBLE);
        progress_tv.setVisibility(View.VISIBLE);
        detailBoxCardView.setVisibility(View.VISIBLE);

        title_tv.setText("Upload Video");
        selectFileBtn.setText("Upload");
        videoPicturePt.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud));
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
             selectVideo = data.getData();
             scheme = selectVideo.getScheme();

            if (requestCode == SELECT_VIDEO) {
                selectedVideoPath =  FileUtil.getPath(selectVideo,getBaseContext());
                try {
                    if(selectedVideoPath == null) {
                        Log.e("error_video","selected video path = null!");
                    } else
                    {



                        String sizeVideoBf =   setFileSize(scheme,Uri.fromFile(new File(selectedVideoPath)));
                        sizeFileTxtVw.setText(sizeVideoBf);

                        String nameVideo= setFileName(Uri.fromFile(new File(selectedVideoPath)));
                        nameFileTxtVw.setText(nameVideo);

                        String typeVideo =setFileType(getBaseContext(),Uri.fromFile(new File(selectedVideoPath)));
                        typeFileTxtVw.setText(typeVideo);
                        compressFileState();
                        stateVideo = COMPRESS_VIDEO ;


                        //          new VideoCompressAsyncTask(this).execute(selectVideo.toString(), f.getPath());
                      /*  runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String dir = new File(selectVideo.getPath()).getParent() ;

                                File f = new File(dir,"temp.mp4");
                                String filePath = null;

                                    Log.i("f_path",f.getPath());
                                    Log.i("select_path",selectVideo.getPath());
                                    Log.i("unit_path",selectedVideoPath);

                                   // filePath = SiliCompressor.with(getBaseContext()).compressVideo(selectVideo.getPath(), f.getPath());




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
        float dataSize = 0 ;
        if(scheme.equals(ContentResolver.SCHEME_CONTENT))
        {
            try {
                InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(selectFile);
                dataSize = fileInputStream.available() / 1024f;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return (""+getFileSize(dataSize));
        }else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            File fileX = null;
            String path = selectFile.getPath();
            try {
                fileX = new File(path);
                //  Toast.makeText(this, "File size in bytesLgth"+fileX.length(), Toast.LENGTH_SHORT).show();
                float length = fileX.length() / 1024f; // Size in KB

                return(""+getFileSize(length));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "" ;
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



    public static String getFileSize(float size) {


        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
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
        final ProgressDialog dialogUp = new ProgressDialog(VideoUploadingActivity.this);
        dialogUp.setMessage("File uploading... \nPlease wait");
        dialogUp.show();
        Call call = uploadAPIs.uploadFile(part, ResponseID);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.e("success_upload",response.toString()+"--> "+response.code());
                dialogUp.dismiss();
                dialogUp.cancel();
                //   Toast.makeText(ImagesUploadingActivity.this, "ok _upload :"+ response.toString(), Toast.LENGTH_SHORT).show();
                if(response.code()==200)
                {
                    finishFileState();
                    stateVideo=FINISHED_VIDEO;
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
    class VideoCompressAsyncTask extends AsyncTask<String, Float, String> {

        Context mContext;
        Float amountToCompressToLocal;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
            this.amountToCompressToLocal = null;
        }

        public VideoCompressAsyncTask(Context context, float percentToCompressDownToLocal) {
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
                                    SiliCompressor.with(this.mContext).cancelVideoCompression();
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
            if(amountToCompressToLocal == null){
                amountToCompressToLocal = 0.5F;
            }
            if(amountToCompressToLocal < 0.0 || amountToCompressToLocal > 1.0){
                amountToCompressToLocal = 0.5F;
            }
            try {
                //Old Method
//                filePath = SiliCompressor.with(mContext).compressVideo(mContext, paths[0], paths[1]);

                //New Method
                try {
                    filePath = SiliCompressor.with(mContext, true).compressVideo(new VideoConversionProgressListener() {
                        @Override
                        public void videoConversionProgressed(float progressPercentage, Long estimatedNumberOfMillisecondsLeft) {
                            publishProgress(progressPercentage);
                            triggerEstimatedMillisecondsLeft(estimatedNumberOfMillisecondsLeft);
                        }
                    }, paths[0], paths[1], this.amountToCompressToLocal);
                } catch (CompressionException ce){
                    ce.printStackTrace();
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
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
                value = getFileSize(length);
            sizeFileTxtVw.setTextColor(getResources().getColor(R.color.category_family));
            sizeFileTxtVw.setText(value);
            uploadFileState();
            uploadCompressedFile = compressedFilePath ;
            stateVideo = UPLOADING_VIDEO;

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