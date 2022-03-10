package com.pclink.attendance.system.TabAttendanceLog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Models.LogCheckdataModel;
import com.pclink.attendance.system.Models.LogExcusesModel;
import com.pclink.attendance.system.Models.LogVacationsModel;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.pclink.attendance.system.Wrapper.LogCheckdataWrapper;
import com.pclink.attendance.system.Wrapper.LogExcusesWrapper;
import com.pclink.attendance.system.Wrapper.LogVacationWrapper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;



public class AttendanceFragment extends Fragment {
    ProgressBar loadingProgBar;
    ImageView imageErrorNetowrk;
    Spinner menuLogSpr ;
    public  RecyclerView recyclerView ;
    SharedPrefData sharedPrefData;
    TextView emptyTextView ;
    checkTypeAsync chkAsync ;
    excusesAsync    excAsync ;
    vacationsAsync vacAsync;
    MainAsynctask mainAsynctask;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefData = new SharedPrefData(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_attendance, container, false);
        loadingProgBar = view.findViewById(R.id.loading_list_log_progressBar);
        recyclerView = view.findViewById(R.id.log_list_recyclerView);
        imageErrorNetowrk = view.findViewById(R.id.error_network_imgView);
        menuLogSpr = view.findViewById(R.id.log_menu_spinner);
        emptyTextView = view.findViewById(R.id.empty_log_data_TxtView);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        imageErrorNetowrk.setVisibility(View.GONE);
        mainAsynctask = new MainAsynctask(getContext(),223);
        spinnerSetMenuData();
        menuLogSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        chkAsync = new checkTypeAsync();
                        if(mainAsynctask.isConnected()) {
                            imageErrorNetowrk.setVisibility(View.GONE);


                            chkAsync.execute(getContext());
                        }
                        else
                        {
                            imageErrorNetowrk.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            loadingProgBar.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.GONE);

                        }
                        break;

                    case 1:
                        excAsync = new excusesAsync();

                        if(mainAsynctask.isConnected()) {
                            imageErrorNetowrk.setVisibility(View.GONE);

                            excAsync.execute(getContext());

                        }
                        else
                        {
                            imageErrorNetowrk.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            loadingProgBar.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.GONE);

                        }
                        break;
                    case 2:
                        vacAsync = new vacationsAsync();

                        if(mainAsynctask.isConnected()) {
                            imageErrorNetowrk.setVisibility(View.GONE);

                            vacAsync.execute(getContext());

                        }
                        else
                        {
                            imageErrorNetowrk.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            loadingProgBar.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.GONE);

                        }
                        break;
                    default:
                        chkAsync = new checkTypeAsync();

                        if(mainAsynctask.isConnected()) {
                            imageErrorNetowrk.setVisibility(View.GONE);

                            chkAsync.execute(getContext());
                        }
                        else
                        {
                            imageErrorNetowrk.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            loadingProgBar.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.GONE);

                        }
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return  view ;
    }


    private void spinnerSetMenuData()
    {
        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Check Log");
        categories.add("Excuses");
        categories.add("Vacations");
        displaySpinnerData(categories,menuLogSpr);
    }

    public  void displaySpinnerData(List<String> categories , Spinner spinnerCategory)
    {
        ArrayAdapter<String> storeAdapterSp = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        storeAdapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(storeAdapterSp);
    }


    class  checkTypeAsync extends AsyncTask<Context,Void, List<LogCheckdataModel>>
    {
        private Context mContext;
        private  String urlGetReqs;
        @Override
        protected List<LogCheckdataModel> doInBackground(Context... params){
            mContext = params[0];

            urlGetReqs = DataConstant.serverUrl + DataConstant.getLogAction + sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);

            return getReqsFromServer(urlGetReqs);

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            imageErrorNetowrk.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.GONE);
        }
        @Override
        protected void onPostExecute(List<LogCheckdataModel> result) {
            super.onPostExecute(result);
            if(result != null){
                LogCheckDateAdapter logCheckDateAdapter = new LogCheckDateAdapter(result,mContext);
                recyclerView.setAdapter(logCheckDateAdapter);
                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                imageErrorNetowrk.setVisibility(View.GONE);
            }
            else
            {
                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                imageErrorNetowrk.setVisibility(View.VISIBLE);
            }
        }

        private List<LogCheckdataModel> getReqsFromServer(String url ) {
            JSONObject response = new JSONObject() ;
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(mContext);


            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
            volleyRequestQueue.add(request);

            try {
                response = future.get();
            } catch (InterruptedException e) {
                Log.e("check_log_error",e.toString());
            } catch (ExecutionException e) {
                Log.e("check_log_error",e.toString());

            }


            return convertJsonToObject(response.toString());
        }




        private List<LogCheckdataModel> convertJsonToObject(String responseStr){
            //instantiate Gson
            final Gson gson = new Gson();
            List<LogCheckdataModel> reqslst =null ;

            LogCheckdataWrapper logCheckdataWrapper= gson.fromJson(responseStr, LogCheckdataWrapper.class);
            try {
                reqslst = logCheckdataWrapper.getCheckdata();
                Log.i("ExcStaffReqs", "number of coupons from json response after gson parsing "+reqslst.size());
            }
            catch (Exception e)
            {
                try {
                    loadingProgBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                    this.cancel(true);
                }catch (Exception x )
                {

                }

            }
            return reqslst;
        }

    }

    class  excusesAsync extends AsyncTask<Context,Void, List<LogExcusesModel>>
    {

        private Context mContext;
        private  String urlGetReqs;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            imageErrorNetowrk.setVisibility(View.GONE);
        }
        @Override
        protected List<LogExcusesModel> doInBackground(Context... params){
            mContext = params[0];

            urlGetReqs = DataConstant.serverUrl + DataConstant.getLogAction + sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);

            return getReqsFromServer(urlGetReqs);

        }
        @Override
        protected void onPostExecute(List<LogExcusesModel> result) {
            super.onPostExecute(result);
            if(result != null){

                LogExcusesAdapter logExcusesAdapter = new LogExcusesAdapter(result,mContext);
                recyclerView.setAdapter(logExcusesAdapter);
                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                imageErrorNetowrk.setVisibility(View.GONE);

            }
            else
            {

                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                imageErrorNetowrk.setVisibility(View.VISIBLE);

            }
        }


        private List<LogExcusesModel> getReqsFromServer(String url ) {
            JSONObject response = new JSONObject() ;
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(mContext);


            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
            volleyRequestQueue.add(request);

            try {
                response = future.get();
            } catch (InterruptedException e) {
                Log.e("check_log_error",e.toString());
            } catch (ExecutionException e) {
                Log.e("check_log_error",e.toString());

            }


            return convertJsonToObject(response.toString());
        }



        private List<LogExcusesModel> convertJsonToObject(String responseStr){
            //instantiate Gson
            final Gson gson = new Gson();
            List<LogExcusesModel> reqslst = null;


            LogExcusesWrapper logExcusesWrapper= gson.fromJson(responseStr, LogExcusesWrapper.class);


            try {
                reqslst = logExcusesWrapper.getExcuses();


                Log.i("ExcStaffReqs", "number of coupons from json response after gson parsing "+reqslst.size());

            }
            catch (Exception e)
            {
                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                this.cancel(true);
            }

            return reqslst;
        }
    }

    class  vacationsAsync extends AsyncTask<Context,Void, List<LogVacationsModel>>
    {
        private Context mContext;
        private  String urlGetReqs;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            imageErrorNetowrk.setVisibility(View.GONE);
        }
        @Override
        protected List<LogVacationsModel> doInBackground(Context... params) {
            mContext = params[0];
            urlGetReqs = DataConstant.serverUrl + DataConstant.getLogAction + sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);

            return getReqsFromServer(urlGetReqs);
        }
        @Override
        protected void onPostExecute(List<LogVacationsModel> result) {
            super.onPostExecute(result);
            if(result != null)
            {
                LogVacationsAdapter logAdapter = new LogVacationsAdapter(result,mContext);
                recyclerView.setAdapter(logAdapter);
                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                imageErrorNetowrk.setVisibility(View.GONE);
            }
            else
            {

                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                imageErrorNetowrk.setVisibility(View.VISIBLE);

            }
        }

        private List<LogVacationsModel> getReqsFromServer(String url ) {
            JSONObject response = new JSONObject() ;
            RequestQueue volleyRequestQueue = Volley.newRequestQueue(mContext);


            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
            volleyRequestQueue.add(request);

            try {
                response = future.get();
            } catch (InterruptedException e) {
                Log.e("vac_log_error",e.toString());
            } catch (ExecutionException e) {
                Log.e("vac_log_error",e.toString());

            }


            return convertJsonToObject(response.toString());
        }



        private List<LogVacationsModel> convertJsonToObject(String responseStr){
            //instantiate Gson
            final Gson gson = new Gson();
            List<LogVacationsModel> reqslst = null;
            LogVacationWrapper logVacationWrapper= gson.fromJson(responseStr, LogVacationWrapper.class);

            try {

                reqslst = logVacationWrapper.getVacations();
                Log.i("ExcStaffReqs", "number of coupons from json response after gson parsing "+reqslst.size());

            }
            catch (Exception e)
            {
                loadingProgBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);

                this.cancel(true);
            }

            return reqslst;
        }
    }


}
