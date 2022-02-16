package com.pclink.attendance.system.FilesAttachmentUploading;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;

import com.pclink.attendance.system.Models.FileConstraintModels;
import com.pclink.attendance.system.R;
import com.google.gson.Gson;


import org.json.JSONObject;

public class FilesReportActivity extends AppCompatActivity {
    CardView imageUploadingBtnCV , videoUploadingBtnCV,docUploadingBtnCV;
    private RequestQueue queue;
    public SharedPrefData sharedPrefData ;
    public  ConstraintsTask constraintsTask ;
    public Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_report);
         queue = Volley.newRequestQueue(this);
        imageUploadingBtnCV = findViewById(R.id.image_files_btn);
        videoUploadingBtnCV = findViewById(R.id.video_files_btn);
        docUploadingBtnCV = findViewById(R.id.doc_files_btn);
        nextBtn = findViewById(R.id.next_btn);
        sharedPrefData = new SharedPrefData(this);
        constraintsTask = new ConstraintsTask();
        constraintsTask.execute();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isEndDay = sharedPrefData.getElementBooleanValue(DataConstant.promoterDataNameSpFile,DataConstant.isEndDayKey);

                startActivity(new Intent(FilesReportActivity.this, MainActivity.class));


            }
        });

        imageUploadingBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FilesReportActivity.this, ImagesUploadingActivity.class));
            }
        });
        videoUploadingBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FilesReportActivity.this, VideoUploadingActivity.class));

            }
        });
        docUploadingBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(FilesReportActivity.this, DocumentUploadingActivity.class));

            }
        });
    }

    public  class  ConstraintsTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            getConstractFiles();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
        public  void getConstractFiles()
        {
            String urlListRequest = DataConstant.serverUrl+DataConstant.getAgencyPackageByAgencyID + sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase) ;
            final Gson gson = new Gson();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, urlListRequest,null ,  new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.e("ok_file_Info", response.toString());
                    FileConstraintModels fileConstraintModels = gson.fromJson(response.toString(), FileConstraintModels.class);
                    sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.fileSizeKey,fileConstraintModels.getFilesize());
                    sharedPrefData.putIntElement(DataConstant.promoterDataNameSpFile,DataConstant.fileAmountKey,fileConstraintModels.getNumberOfFiles());
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("error_file_Info", error.toString());

                }
            });

            queue.add(jsObjRequest);
        }




    }

}
