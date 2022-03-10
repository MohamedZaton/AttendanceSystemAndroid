package com.pclink.attendance.system.TabRequest;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;

import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MissionRequestFragment extends Fragment {

    NetworkRequestPr networkRequestPr;
    LinearLayout missionLinearLayout;
    TextView displayDateDayTxtView;
    ImageView exDateDayImgeView;
    EditText writeReasonEditTxt;
    String  dataDayMission = "";
    SharedPrefData sharedPrefData ;
    String pomoterIdSp ;
    String promoterFile= DataConstant.promoterDataNameSpFile;
    Button  submitMissionButton, backButton;
    private int mYear, mMonth, mDay;
    SimpleDateFormat formatDate;
    RequestQueue queue ;
    Promoter promoter ;
    ArrayList<HashMap<String, String>> getMissionList;
    String jsonListArrayKey = "";
    String unselectFromlist="-1999";

    public MissionRequestFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
        sharedPrefData = new SharedPrefData(getActivity()) ;
        pomoterIdSp =sharedPrefData.getElementValue(promoterFile,DataConstant.agencyIDJsonKeyUpcase) ;
        networkRequestPr = new NetworkRequestPr(getActivity() , "mission  ....");


        promoter = new Promoter(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_mission, container, false);
        missionLinearLayout = view.findViewById(R.id.mission_linerLayout);
        formatDate = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        exDateDayImgeView = view.findViewById(R.id.date_day_mission_imageView);
        displayDateDayTxtView = view.findViewById(R.id.display_mission_date_textView);
        writeReasonEditTxt = view.findViewById(R.id.input_mission_reason_Text);
        submitMissionButton = view.findViewById(R.id.submit_mission_button);
        backButton = view.findViewById(R.id.back_mission_button);
        missionLinearLayout.setVisibility(View.VISIBLE);



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
                                dataDayMission = (monthOfYear + 1) + "/" + dayOfMonth + "/" +  year;
                                displayDateDayTxtView.setVisibility(View.VISIBLE);
                                displayDateDayTxtView.setText(dataDayMission);
                                displayDateDayTxtView.setError(null);
                            }
                        }, mYear, mMonth, mDay);

                Calendar ca = Calendar.getInstance();
                ca.add(Calendar.YEAR ,-1); //before 5 years from now
                datePickerDialog.getDatePicker().setMinDate(ca.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        submitMissionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String reasonText = writeReasonEditTxt.getText().toString();


                clearAuthentication();





                 if (dataDayMission.equals(""))
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

                    uploadMissionReq();

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

        return view;
    }


    public void  backButtonAction()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                missionLinearLayout.setVisibility(View.GONE);
                RequestFragment secondFragment=new RequestFragment();
                FragmentTransaction fragmentTransaction =   getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.request_continer_fragment, secondFragment);
                fragmentTransaction.commit();
                fragmentTransaction.disallowAddToBackStack();
            }
        });

    }





    public void clearAuthentication()
    {

        displayDateDayTxtView.setError(null);
        writeReasonEditTxt.setError(null);

    }

    public  void uploadMissionReq()
    {

        String missionDay =dataDayMission;
        String missionReason= writeReasonEditTxt.getText().toString();

        String urlpostMission = DataConstant.serverUrl+DataConstant.missionGetControl+DataConstant.missionGetAction + "";
        Log.i("url_get_req" , urlpostMission);

        Map<String,String> bodyReq =  promoter.postMissionRequestBody(missionReason ,missionDay,sharedPrefData.getElementValue(promoterFile, DataConstant.agencyIDJsonKeyUpcase));

        int a   =    networkRequestPr.postExcVacRequestVolley(urlpostMission , bodyReq);

        if(a==1)
        {
            backButtonAction();
        }

    }

}

