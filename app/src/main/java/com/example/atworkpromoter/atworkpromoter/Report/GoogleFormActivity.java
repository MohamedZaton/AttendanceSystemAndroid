package com.pclink.attendance.system.Report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.FilesAttachmentUploading.FilesReportActivity;
import com.pclink.attendance.system.Models.FileConstraintModels;
import com.pclink.attendance.system.Models.ReportGenerateModel;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.FormElement;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import me.riddhimanadib.formmaster.FormBuilder;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementPickerTime;
import me.riddhimanadib.formmaster.model.FormElementSwitch;
import me.riddhimanadib.formmaster.model.FormElementTextEmail;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextNumber;
import me.riddhimanadib.formmaster.model.FormElementTextPassword;
import me.riddhimanadib.formmaster.model.FormElementTextPhone;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormHeader;

public class GoogleFormActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FormBuilder mFormBuilder;
    private  WebView webView ;
    private static final int TAG_EMAIL = 12;
    private static final int TAG_PASSWORD = 2234;
    private SharedPrefData sharedPrefData ;
    public Button tryNetBtn , ckeckWithoutFormBtn , contNoFormBtn ;
    public ReportGenerateModel generateModel ;
    public ProgressBar loadingBar;
    public TextView loadingTxt ,orTxt;
    public LinearLayout formWebLyOut ;
    public LinearLayout loadingBoxLyOut ;
    public  boolean loadingFinished =true , errorLoading = false  ;
    public FormAsync  formAsync ;
    public  Bundle extraFormGoogle ;
    public  MainAsynctask mainAsynctask ;
    String noConnectMsg,errorServerMsg ;
    GenerateFormController generateController ;
    private static final String TAG = "GoogleFormActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_form);
        sharedPrefData = new SharedPrefData(this);
        mainAsynctask= new MainAsynctask(GoogleFormActivity.this,222);
        tryNetBtn = findViewById(R.id.try_btn) ;
        contNoFormBtn = findViewById(R.id.continue_btn);
        ckeckWithoutFormBtn = findViewById(R.id.check_out_later_form) ;
        orTxt = findViewById(R.id.or_txt);
        loadingBar = findViewById(R.id.loading_form_progressbar);
        loadingTxt = findViewById(R.id.loading_prnt_txt) ;
        formWebLyOut = findViewById(R.id.form_loading_lnlyt);
        loadingBoxLyOut = findViewById(R.id.loading_network_linearlyt);
        noConnectMsg = getResources().getString(R.string.no_network_msg);
        errorServerMsg = getResources().getString(R.string.server_down_msg);
        formAsync = new FormAsync();
        formAsync.execute();

        //hideKeyboard();

        tryNetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new FormAsync().execute();
            }
        });

        ckeckWithoutFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save this offline body

                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasReportKey,false);

                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasLateFormKey,true);
                Log.d(TAG, "body_google : "+ bodyRequest().toString());
                sharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.bodyFormLate, bodyRequest().toString());

                Intent i = new Intent(GoogleFormActivity.this, MainActivity.class);
                startActivity(i);
                //intent form later go to check out

            }
        });

        contNoFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save this offline body

                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasReportKey,true);
                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasLateFormKey,false);
                sharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.bodyFormLate, "");

                Intent i = new Intent(GoogleFormActivity.this, MainActivity.class);
                startActivity(i);
                //intent form later go to check out

            }
        });


        // webViewForm();

        //setupForm();

    }

    private void beforeLoadingState() {
        formWebLyOut.setVisibility(View.GONE);
        tryNetBtn.setVisibility(View.GONE);
        orTxt.setVisibility(View.GONE);
        ckeckWithoutFormBtn.setVisibility(View.GONE);
        loadingBar.setProgress(1);
        loadingBar.setMax(100);
        loadingTxt.setText( "1 % "+"Request your form link ...");

    }

    public  String postFormRequest()
    {
        JSONObject body = new JSONObject();
        String postUrl = DataConstant.serverUrl + DataConstant.postGenerateUrl ;
        String bodyStr = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.bodyFormLate);
        Log.i("url_form", ""+postUrl);
        Log.i("form_google",bodyStr);
        extraFormGoogle = getIntent().getExtras();

      if(extraFormGoogle!=null && !bodyStr.equals(""))
      {
                orTxt.setVisibility(View.GONE);
                ckeckWithoutFormBtn.setVisibility(View.GONE);
                loadingTxt.setTextColor(Color.RED);
                loadingTxt.setText("You must fill sales form online before start next day");
                extraFormGoogle.getString(DataConstant.bodyFormLate);
          try {
              body = new JSONObject(bodyStr);
          }catch (JSONException err){
              Log.e("Error_late_form", err.toString());
              body = bodyRequest();

          }
      }else
      {
          body = bodyRequest();
      }
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, postUrl, body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("ok_generte_req",response.toString());
                loadingTxt.setTextColor(Color.BLUE);
                loadingBar.setProgress(30);
                loadingTxt.setText( "30 % "+"\nRequest your form ...");
                String errorResMsg = null;
                try {
                    errorResMsg = response.getString("error");
                } catch (JSONException e) { e.printStackTrace(); }

                if(errorResMsg!=null)
                {
                    loadingBar.setVisibility(View.GONE);
                    loadingTxt.setText(errorResMsg);
                    contNoFormBtn.setVisibility(View.VISIBLE);

                    orTxt.setVisibility(View.GONE);
                    ckeckWithoutFormBtn.setVisibility(View.GONE);
                    tryNetBtn.setVisibility(View.GONE);

                }
                else
                {
                    try {
                        webActionJs(response.getString("formurl"));
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null || error.networkResponse == null) {
                    Log.i(TAG, "onErrorResponse: null ");

                    loadingBar.setVisibility(View.GONE);
                    loadingTxt.setTextColor(Color.RED);

                    if(mainAsynctask.isConnected())
                    {
                        loadingTxt.setText(noConnectMsg);
                    }else
                    {
                        loadingTxt.setText("Error get Your Sales Form.\nPlease try again later,contact  support if the error persists.");
                    }


                    orTxt.setVisibility(View.VISIBLE);
                    ckeckWithoutFormBtn.setVisibility(View.VISIBLE);


                    if(extraFormGoogle!=null)
                    {
                        loadingTxt.setText("You must fill sales form online before start the day");
                        ckeckWithoutFormBtn.setVisibility(View.GONE);
                        orTxt.setVisibility(View.GONE);

                    }

                    tryNetBtn.setVisibility(View.VISIBLE);
                }
                else {
                    final String statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding

                    Log.i(TAG, "onErrorResponse: code " + statusCode);

                    String errorResMsg = "you don't have form " ;
                    if(statusCode.equals("500") && errorResMsg.equals(errorResMsg))
                    {
                        loadingBar.setVisibility(View.GONE);
                        loadingTxt.setText(errorResMsg);
                        contNoFormBtn.setVisibility(View.VISIBLE);
                        orTxt.setVisibility(View.GONE);
                        ckeckWithoutFormBtn.setVisibility(View.GONE);
                        tryNetBtn.setVisibility(View.GONE);
                    }
                }


            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);

        return  "" ;

    }
    private void webActionJs(String urlWebView )
    {
        //urlWebView = "https://forms.gle/KQN6UZEuGjKUQx5o9";

        Log.i(TAG, "webActionJs url : " + urlWebView);
        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        // webView.loadUrl("file:///android_asset/testform.html");
        webView.loadUrl(urlWebView);

        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "android");

        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                Log.i(TAG, "onReceivedError: method "+ request.getRequestHeaders());
                Log.i(TAG, "onReceivedError: error "+""+ error.getErrorCode());
                loadingFinished =false ;
                loadingBar.setVisibility(View.GONE);
                loadingTxt.setTextColor(Color.RED);
                loadingTxt.setText(errorServerMsg);
                tryNetBtn.setVisibility(View.VISIBLE);
                orTxt.setVisibility(View.VISIBLE);
                ckeckWithoutFormBtn.setVisibility(View.VISIBLE);
                super.onReceivedError(view, request, error);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                loadingFinished =false ;
                loadingBar.setVisibility(View.GONE);
                loadingTxt.setTextColor(Color.RED);
                loadingTxt.setText(errorServerMsg);
                tryNetBtn.setVisibility(View.VISIBLE);
                orTxt.setVisibility(View.VISIBLE);
                ckeckWithoutFormBtn.setVisibility(View.VISIBLE);
                return super.shouldOverrideUrlLoading(view, request);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingTxt.setTextColor(Color.BLUE);
                loadingBar.setProgress(60);
              loadingTxt.setText("60 %" +"\nLoading form ...");

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


                if(loadingFinished && Patterns.WEB_URL.matcher(url).matches()){
                    //HIDE LOADING IT HAS FINISHED
                        loadingTxt.setVisibility(View.VISIBLE);
                        loadingBar.setProgress(100);
                        loadingTxt.setTextColor(Color.BLUE);
                        loadingTxt.setText(" 100 %"+"Loading is completed");
                        Log.i("complete_form","form_completed");
                    new CountDownTimer(1000, 1000)
                    {
                        public void onTick(long millisUntilFinished)
                        {


                        }
                        public void onFinish()
                        {
                            loadingBoxLyOut.setVisibility(View.GONE);
                            formWebLyOut.setVisibility(View.VISIBLE);
                            fullScreencall();
                        }
                    }.start();

                } else{
                    loadingBar.setVisibility(View.GONE);
                    loadingTxt.setTextColor(Color.RED);
                    loadingTxt.setText(errorServerMsg);
                    tryNetBtn.setVisibility(View.VISIBLE);
                    orTxt.setVisibility(View.VISIBLE);
                    ckeckWithoutFormBtn.setVisibility(View.VISIBLE);
                }

            }
        });

     //   hideSoftInput();
    }
    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) (GoogleFormActivity.this).getSystemService(
                Context.INPUT_METHOD_SERVICE);
        /*
         * If no view is focused, an NPE will be thrown
         *
         * Maxim Dmitriev
         */
        if (webView != null) {
            inputManager.hideSoftInputFromWindow(webView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public boolean hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(webView.getWindowToken(), 0);
    }


    public JSONObject bodyRequest()
    {
        int agencyID = Integer.parseInt(sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase));
        String checkInID = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.checkInIDJsonKey) ;
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(System.currentTimeMillis());
        String formType = "sales" ;

        JSONObject jsonObjBody = new JSONObject();
        generateModel =new ReportGenerateModel(agencyID,checkInID,date,formType,"2");

        Gson gson = new Gson();

        String StringJsonBody = gson.toJson(generateModel);
            Log.i("google_body"," " +StringJsonBody);
        try
        {
            jsonObjBody = new JSONObject(StringJsonBody);

        } catch(JSONException e)
        {
            e.printStackTrace();
        }
        return  jsonObjBody ;

    }

    public void fullScreencall()
    {

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    public class WebViewJavaScriptInterface{

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context){
            this.context = context;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void reportResponseID(boolean isfull , String responseID){        // not use java but using javascript   response java

            sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.bodyFormLate,""); // clear any form saved
            Bundle extras = getIntent().getExtras();
            Log.i("response_id"," "+responseID);
            Log.i("is_full"," "+isfull);
            int responseIDValue = 0 ;


            if(isfull){
                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasReportKey,true);
                String fileStates =   sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.fileStatesKey);
                Log.i("states_file",fileStates);
                if(fileStates.equals("False"))
                {

                        sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.hasReportKey, true);
                        Intent i = new Intent(context, MainActivity.class);
                        context.startActivity(i);

                }
                else
                {
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.hasReportKey, true);
                    Intent i = new Intent(context, FilesReportActivity.class);
                    context.startActivity(i);
                }
            }
            else
            {
                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasReportKey,false);
                Toast.makeText(context, "please,Fill This Report", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public  class  FormAsync extends  AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            beforeLoadingState();

            getConstractFiles();
            postFormRequest();

        }
    }

    public  void getConstractFiles()
    {
        final SharedPrefData sharedPrefData = new SharedPrefData(this);
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
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
                sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.fileStatesKey,fileConstraintModels.getStatus());

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


