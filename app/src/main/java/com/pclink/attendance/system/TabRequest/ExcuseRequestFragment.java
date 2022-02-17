package com.pclink.attendance.system.TabRequest;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class  ExcuseRequestFragment extends Fragment {

    NetworkRequestPr networkRequestPr;
    LinearLayout excuseLinearLayout;
    TextView displayDateDayTxtView;
    ImageView exDateDayImgeView;
    ProgressBar loadingProgBar;
    EditText writeReasonEditTxt;
    String  dataDayEx = "";
    FloatingActionButton closeFloatingButton;
    private List<itemCardViewExcuseList> excuseList;
    SharedPrefData sharedPrefData ;
    String pomoterIdSp ;
    String promoterFile= DataConstant.promoterDataNameSpFile;
    Dialog reasonExcuseDialog;
    Button submitListButton ,reasonTypeButton , submitExcuseButton, backButton;
    DialogAll dialogAll = new DialogAll(getActivity());
    MainAsynctask mainAsynctask = new MainAsynctask(getContext(),-1);
    ExuseListAdapter excuseAdapter;
    private int mYear, mMonth, mDay;
    GetListExcuse getListExcuse;
    SimpleDateFormat formatDate;
    RecyclerView exuseListRecyclerView;
    RequestQueue queue ;
    Promoter promoter ;

    ArrayList<HashMap<String, String>> getExcuseList;


    String jsonListArrayKey = "";
    String unselectFromlist="-1999";
    String[] jsonKeyArrayList = DataConstant.arrayListExcuse;

    // excuse Json & Shared Pref
    private String   exListNameKey = DataConstant.exNameListKey,exListIdKey = DataConstant.exIdListKey, exMessageKey= DataConstant.exMessageKey , exDateKey =DataConstant.exDateKey, exIdKey = DataConstant.exId ;


    public ExcuseRequestFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
        sharedPrefData = new SharedPrefData(getActivity()) ;
        pomoterIdSp =sharedPrefData.getElementValue(promoterFile,DataConstant.agencyIDJsonKeyUpcase) ;
        networkRequestPr = new NetworkRequestPr(getActivity() , "excuse  ....");
        excuseList = new ArrayList<>();

        promoter = new Promoter(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_excuse_request, container, false);
        excuseLinearLayout = view.findViewById(R.id.excuse_linerLayout);
        reasonTypeButton = view.findViewById(R.id.reason_excuse_Button);
        formatDate = new SimpleDateFormat("MM/dd/yyyy", new Locale("en"));
        getListExcuse  = new GetListExcuse() ;
        exDateDayImgeView = view.findViewById(R.id.date_day_excuse_imageView);
        displayDateDayTxtView = view.findViewById(R.id.display_date_textView);
        writeReasonEditTxt = view.findViewById(R.id.input_reason_editText);
        submitExcuseButton = view.findViewById(R.id.submit_excuse_button);
        backButton = view.findViewById(R.id.back_excuse_button);
        excuseLinearLayout.setVisibility(View.VISIBLE);

        exuseListPopUp();






        reasonTypeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                reasonTypeButton.setError(null);    //removes error
                reasonExcuseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reasonExcuseDialog.show();


            }
        });



        closeFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                reasonExcuseDialog.dismiss();
                reasonExcuseDialog.cancel();

            }
        });

        submitListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String yourSelectid = sharedPrefData.getElementValue(promoterFile,DataConstant.saveExcuseIdjson);
                String selectTextReason  = sharedPrefData.getElementValue(promoterFile,DataConstant.saveExcuseTxtjson);
                if(yourSelectid.equals(unselectFromlist))
                {
                    Toast.makeText(getActivity(), " you do not  select your excuse type ", Toast.LENGTH_SHORT).show();
                    selectTextReason  = "Select Reason";

                }
                reasonTypeButton.setText(selectTextReason);
                reasonExcuseDialog.dismiss();
                reasonExcuseDialog.cancel();

            }
        });


        // select day ex

        exDateDayImgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener()
                        {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {
                                mYear = year ;
                                mMonth = monthOfYear;
                                mDay=dayOfMonth;
                                dataDayEx = (monthOfYear + 1) + "/" + dayOfMonth + "/" +  year;
                                displayDateDayTxtView.setVisibility(View.VISIBLE);
                                displayDateDayTxtView.setText(dataDayEx);
                                displayDateDayTxtView.setError(null);
                            }
                        }, mYear, mMonth, mDay);

                Calendar ca = Calendar.getInstance();
                ca.add(Calendar.YEAR ,-1); //before 5 years from now
                datePickerDialog.getDatePicker().setMinDate(ca.getTimeInMillis());
                datePickerDialog.show();
            }
        });



        submitExcuseButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String reasonText = writeReasonEditTxt.getText().toString();
                String listbuttonText = reasonTypeButton.getText().toString();



                clearAuthentication();



                if(listbuttonText.equals(getResources().getString(R.string.default_list_selected)))
                {

                    reasonTypeButton.setError(getResources().getString(R.string.error_select_list_type));

                }



                else if (dataDayEx.equals(""))
                {
                    displayDateDayTxtView.setVisibility(View.VISIBLE);

                    displayDateDayTxtView.setError(getResources().getString(R.string.error_date));
                }

                else if(TextUtils.isEmpty(reasonText))
                {

                    writeReasonEditTxt.setError(getResources().getString(R.string.error_empty_reason));

                }
                else
                {



                    uploadExcReq();



                }

            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                backButtonAction();
            }
        });

        getListExcuse.execute();
        return view;
    }


    public void  backButtonAction()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                excuseLinearLayout.setVisibility(View.GONE);
                RequestFragment secondFragment=new RequestFragment();
                FragmentTransaction fragmentTransaction =   getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.request_continer_fragment, secondFragment);
                fragmentTransaction.commit();
                fragmentTransaction.disallowAddToBackStack();
            }
        });

    }


    public void exuseListPopUp()
    {

        reasonExcuseDialog = new Dialog(getActivity());
        reasonExcuseDialog.setContentView(R.layout.excuse_request_menu_list_popup);
        closeFloatingButton = reasonExcuseDialog.findViewById(R.id.close_list_excuse_floatAButton);
        submitListButton = reasonExcuseDialog.findViewById(R.id.select_excuseList_button);
        exuseListRecyclerView = reasonExcuseDialog.findViewById(R.id.exuse_menu_list_recyclerView);
        loadingProgBar = reasonExcuseDialog.findViewById(R.id.loading_list_progressBar);

        excuseAdapter=new ExuseListAdapter(getContext(),excuseList);
        exuseListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        exuseListRecyclerView.setAdapter(excuseAdapter);
        reasonExcuseDialog.setCanceledOnTouchOutside(false);
        loadingProgBar.setVisibility(View.VISIBLE);
        exuseListRecyclerView.setVisibility(View.GONE);
    }


    public void clearAuthentication()
    {

        reasonTypeButton.setError(null);    //removes error
        displayDateDayTxtView.setError(null);
        writeReasonEditTxt.setError(null);

    }

    public  void uploadExcReq()
    {
        String excDay =dataDayEx;
        String excReason= writeReasonEditTxt.getText().toString();
        String excId = sharedPrefData.getElementValue(promoterFile,DataConstant.saveExcuseIdjson );



        String urlGetExc = DataConstant.serverUrl+DataConstant.exPostControl+DataConstant.exPostAction + "";
        Log.i("url_get_req" , urlGetExc);

        Map<String,String> bodyReq =  promoter.postExcuseRequestBody(excReason ,excDay,sharedPrefData.getElementValue(promoterFile, DataConstant.agencyIDJsonKeyUpcase),excId);

        int a   =    networkRequestPr.postExcVacRequestVolley(urlGetExc , bodyReq);

        if(a==1)
        {
            backButtonAction();
        }


    }



    private class  GetListExcuse extends AsyncTask<Void, Void, Void>

    {
        @Override
        protected Void doInBackground(Void... voids)
        {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            String urlListRequest  = DataConstant.serverUrl+DataConstant.exGetControl+DataConstant.exGetAction + sharedPrefData.getElementValue(promoterFile, DataConstant.agencyIDJsonKeyUpcase) ;

            Log.i("url_get_exc",urlListRequest);
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, urlListRequest,null ,  new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response)
                {



                    Log.e("acccept", response.toString());
                    getExcuseList= JsonPr.getValueObjtArrayJson(response.toString(),jsonKeyArrayList);

                    excuseList.add(new itemCardViewExcuseList(getResources().getString(R.string.default_list_selected ), unselectFromlist ));

                    for(int i=0;i<getExcuseList.size();i++)
                    {
                        HashMap<String,String> singleRow = getExcuseList.get(i);
                        excuseList.add(new itemCardViewExcuseList(singleRow.get(exListNameKey),singleRow.get(exListIdKey)));

                    }

                    loadingProgBar.setVisibility(View.GONE);
                    exuseListRecyclerView.setVisibility(View.VISIBLE);

                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("reject", error.toString());
                    loadingProgBar.setVisibility(View.GONE);
                    exuseListRecyclerView.setVisibility(View.VISIBLE);

                }
            });

            queue.add(jsObjRequest);

            super.onPostExecute(aVoid);

        }


    }


}
