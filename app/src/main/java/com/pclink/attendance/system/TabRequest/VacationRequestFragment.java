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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.DateAndTime.DateHelper;
import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;

import org.json.JSONArray;

import java.text.ParseException;
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
public class VacationRequestFragment extends Fragment {


    DateHelper dateHelper ;
    private int siYear, siMonth, siDay,eiYear, eiMonth, eiDay;
    ImageView startVacationDayImg,endVacationDayImg;
    TextView startVacationDayViewTxT,endVacationDayViewTxT , vacDayTxt , notesMsgTxt;
    LinearLayout vactionLinearLayout ,notesLyout;
    SharedPrefData sharedPrefData;
    Button backButton,submitVacButton ,reasonTypeButton , submitListButton;
    int daysVacCheck  = 0;
    int eDay,eMonth,eYear;
    int sDay,sMonth,sYear;
    String promoterFile= DataConstant.promoterDataNameSpFile;
    String vacDaysCikKey= DataConstant.vacDaysCikKey;
    NetworkRequestPr networkRequestPr;
    String dataDayExS = "",dataDayExE = "";
    Dialog reasonVacationDialog;
    GetListVacation getListVacation ;
    FloatingActionButton closeFloatingButton;
    ProgressBar loadingProgBar;
    String unselectFromlist="-1999";
    boolean isSelectFromDay = false ;

    DatePickerDialog datePickerDialogStart,datePickerDialogEnd;
    RecyclerView vactionListRecyclerView;
    VacationListAdapter vacationAdapter;
    SimpleDateFormat formatDate;
    private List<itemCardViewVacationList> vacationList;
    RequestQueue queue ;
    Promoter promoter ;
    String pomoterIdSp ;
    int countNotes = 0;

    ArrayList<HashMap<String, String>> getVacationList;
    // vacation Json & Shared Pref
    private String vacListNameKey = DataConstant.vacNameListKey,vacListIdKey = DataConstant.vacIdListKey,  vacStartDateKey =DataConstant.vacDateFromKey,vacEndDateKey =DataConstant.vacDateToKey, vacIdKey = DataConstant.vacIdKey ;

    String jsonListArrayKey = "";
    String[] jsonKeyArrayList = DataConstant.arrayVacationListjsonKey;

    MainAsynctask mainAsynctask = new MainAsynctask(getContext(),-1);
    int vacDaysReminder = 0 ;


    public VacationRequestFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
        promoter = new Promoter(getActivity());
        sharedPrefData = new SharedPrefData(getActivity()) ;
        pomoterIdSp =sharedPrefData.getElementValue(promoterFile,DataConstant.agencyIDJsonKeyUpcase) ;


        networkRequestPr = new NetworkRequestPr(getActivity() , "vic  ....");


        vacationList = new ArrayList<itemCardViewVacationList>();


        vacationList.add(new itemCardViewVacationList(getResources().getString(R.string.default_list_selected ), unselectFromlist ));








    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_vication_request, container, false);
        sharedPrefData = new SharedPrefData(getContext());
        if(sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.kLanguage).contains(DataConstant.kArabic)){
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dateHelper= new DateHelper();
        startVacationDayImg=view.findViewById(R.id.date_start_vacation_imageView);
        endVacationDayImg=view.findViewById(R.id.date_end_vacation_imageView);
        vacDayTxt = view.findViewById(R.id.vac_days_txt);
        notesMsgTxt = view.findViewById(R.id.notes_message_textview);
        notesLyout = view.findViewById(R.id.note_balance_lyout);

        vacDaysReminder = sharedPrefData.getElementIntValue(DataConstant.promoterDataNameSpFile,DataConstant.vacDaysKey);
        notesLyout.setVisibility(View.GONE);

        getListVacation =new GetListVacation();
        startVacationDayViewTxT =view.findViewById(R.id.start_vacation_date_display_textView);
        endVacationDayViewTxT =view.findViewById(R.id.to_end_vacation_date_display_textView);

        vactionLinearLayout= view.findViewById(R.id.vacation_linearLayout);
        backButton = view.findViewById(R.id.back_vacation_button);
        submitVacButton = view.findViewById(R.id.submit_vication_button);
        daysVacCheck= sharedPrefData.getElementIntValue(promoterFile,vacDaysCikKey);
        formatDate = new SimpleDateFormat("MM/dd/yyyy", new Locale("en"));

        reasonTypeButton = view.findViewById(R.id.reason_vacation_Button);

        if(vacDaysReminder<0)
        {
            vacDaysReminder = 0;
        }
        vacDayTxt.setText(" "+vacDaysReminder);


        // Get Current Date
        final Calendar c = Calendar.getInstance();
        siYear = c.get(Calendar.YEAR);
        siMonth = c.get(Calendar.MONTH);

        siDay = c.get(Calendar.DAY_OF_MONTH);

        eiYear = c.get(Calendar.YEAR);
        eiMonth = c.get(Calendar.MONTH);
        eiDay = c.get(Calendar.DAY_OF_MONTH);




        vacationListPopUp();


        reasonTypeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                reasonVacationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reasonVacationDialog.show();


            }
        });



        closeFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                reasonVacationDialog.dismiss();
                reasonVacationDialog.cancel();

            }
        });


        submitListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String yourSelectid = sharedPrefData.getElementValue(promoterFile,DataConstant.saveVacationIdjson);
                String selectTextReason  = sharedPrefData.getElementValue(promoterFile,DataConstant.saveVacationTxtjson);
                if(yourSelectid.equals(unselectFromlist))
                {
                    Toast.makeText(getActivity(), " you do not  select your excuse type ", Toast.LENGTH_SHORT).show();
                    selectTextReason  = "Select Reason";

                }
                else
                {
                    reasonTypeButton.setError(null);
                }
                reasonTypeButton.setText(selectTextReason);
                reasonVacationDialog.dismiss();
                reasonVacationDialog.cancel();

            }
        });





        startVacationDayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.i("go","xdxx");



                datePickerDialogStart = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener()
                        {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {

                                dataDayExS =(monthOfYear + 1) + "/" + dayOfMonth + "/" +  year;
                                sDay = dayOfMonth;
                                sMonth=monthOfYear;
                                sYear=year;
                                siYear=year;
                                siMonth = monthOfYear;
                                siDay=dayOfMonth;
                                dataDayExS =(monthOfYear + 1) + "/" + dayOfMonth + "/" +  year;
                                startVacationDayViewTxT.setText(dataDayExS);
                                startVacationDayViewTxT.setError(null);
                                isSelectFromDay = true ;

                            }
                        }, siYear, siMonth, siDay);


                datePickerDialogStart.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialogStart.getDatePicker().updateDate(sYear,sMonth,sDay);
                datePickerDialogStart.show();
            }

        });


        endVacationDayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("go", "xdxx");


                if (!isSelectFromDay) {
                    startVacationDayViewTxT.setError("full start vacation day first");
                } else {

                    datePickerDialogEnd = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    eYear = year;
                                    eMonth = monthOfYear;
                                    eDay = dayOfMonth;

                                    eiYear = year;
                                    eiMonth = monthOfYear;
                                    eiDay = dayOfMonth;

                                    dataDayExE = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;

                                    endVacationDayViewTxT.setText(dataDayExE);

                                    endVacationDayViewTxT.setError(null);
                                }
                            }, eiYear, eiMonth, eiDay);
                    try {
                        datePickerDialogEnd.getDatePicker().setMinDate(formatDate.parse(startVacationDayViewTxT.getText().toString()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //  datePickerDialogStart.getDatePicker().updateDate(eYear,eMonth+ 1,eDay);

                    datePickerDialogEnd.show();
                }
            }
        });









        // submit button

        submitVacButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int inputDays=dateHelper.subtractTwoDate(sDay,sMonth,sYear,eDay,eMonth,eYear);
                Log.i("Sub_days " , " "+ inputDays) ;
                inputDays=inputDays*-1;
                clearAuthentication();
                String listbuttonText = reasonTypeButton.getText().toString();
                if((inputDays > vacDaysReminder) && countNotes==0 ) {
                    countNotes = 1 ;

                }



                clearAuthentication();

                if(listbuttonText.equals(getResources().getString(R.string.default_list_selected)))
                {
                    reasonTypeButton.setError(getResources().getString(R.string.error_select_list_type));
                }
                else if (dataDayExS.equals(""))
                {

                    startVacationDayViewTxT.setError(getResources().getString(R.string.error_date));
                }
                else if (dataDayExE.equals(""))
                {
                    endVacationDayViewTxT.setError(getResources().getString(R.string.error_date));
                }

                else if( inputDays < 0 )
                {
                    Toast.makeText(getActivity(), "Start Date must be less than End date ", Toast.LENGTH_SHORT).show();

                }
                else if((inputDays > vacDaysReminder) &&  countNotes==1){
                    int unpaidLeaves = inputDays - vacDaysReminder;
                    notesLyout.setVisibility(View.VISIBLE);
                    notesMsgTxt.setVisibility(View.VISIBLE);
                    notesMsgTxt.setText("If you submit this vacation again , These " + unpaidLeaves  + " days will be unpaid leaves ");
                    countNotes = 2 ;
                }
                else
                {
                    String startVacDay =dataDayExS;
                    String endVacDay =dataDayExE;
                    String vacId = sharedPrefData.getElementValue(promoterFile,DataConstant.saveVacationIdjson );

                    // save date before upload
                    sharedPrefData.putElement(promoterFile,vacStartDateKey,startVacDay);
                    sharedPrefData.putElement(promoterFile,vacEndDateKey,endVacDay);
                    sharedPrefData.putElement(promoterFile,vacIdKey,vacId);
                    Map<String,String> bodyReq =  promoter.postVicationRequestBody( sharedPrefData.getElementValue(promoterFile, DataConstant.agencyIDJsonKeyUpcase),startVacDay,endVacDay,vacId);

                    String urlPostVac =  DataConstant.serverUrl+ DataConstant.vacPostControl + DataConstant.vacPostAction;
                    int a   = networkRequestPr.postExcVacRequestVolley(urlPostVac , bodyReq);

                    // uploadVacReq();


                    if(a==1)
                    {
                        backButtonAction();
                    }

                }







            }
        });









        // back button


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                backButtonAction();
            }
        });






        String responceMenu = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.vacLisJsonKey);
        getVacationList= JsonPr.getValueObjtArrayJson(responceMenu,jsonKeyArrayList);

        for(int i=0;i<getVacationList.size();i++)
        {



            HashMap<String,String> singleRow = getVacationList.get(i);
            vacationList.add(new itemCardViewVacationList(singleRow.get(vacListNameKey),singleRow.get(vacListIdKey)));



        }

        loadingProgBar.setVisibility(View.GONE);
        vactionListRecyclerView.setVisibility(View.VISIBLE);


        return view;

    }

    public void  backButtonAction()
    {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vactionLinearLayout.setVisibility(View.GONE);

                RequestFragment secondFragment=new RequestFragment();
                FragmentTransaction fragmentTransaction =   getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.request_continer_fragment, secondFragment);
                fragmentTransaction.commit();
                fragmentTransaction.disallowAddToBackStack();
            }
        });


    }
    public void vacationListPopUp()
    {

        reasonVacationDialog = new Dialog(getActivity());
        reasonVacationDialog.setContentView(R.layout.excuse_request_menu_list_popup);
        closeFloatingButton = reasonVacationDialog.findViewById(R.id.close_list_excuse_floatAButton);
        submitListButton = reasonVacationDialog.findViewById(R.id.select_excuseList_button);
        vactionListRecyclerView = reasonVacationDialog.findViewById(R.id.exuse_menu_list_recyclerView);
        loadingProgBar = reasonVacationDialog.findViewById(R.id.loading_list_progressBar);
        vacationAdapter=new VacationListAdapter(getContext(),vacationList);
        vactionListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        vactionListRecyclerView.setAdapter(vacationAdapter);
        reasonVacationDialog.setCanceledOnTouchOutside(false);
        loadingProgBar.setVisibility(View.VISIBLE);
        vactionListRecyclerView.setVisibility(View.GONE);
    }





    public void clearAuthentication()
    {
        reasonTypeButton.setError(null);
        startVacationDayViewTxT.setError(null);
        endVacationDayViewTxT.setError(null);

    }

    public  void uploadVacReq()
    {

       /* String urlGetExc =  RoutesHelper.mainServer+ DataConstant.vacGetControl + DataConstant.vacGetAction;
        Map<String,String> bodyReq =  promoter.postVicationRequestBody();

        networkRequestPr.postExcVacRequestVolley(urlGetExc , bodyReq);
*/



    }





    private class  GetListVacation extends AsyncTask<Void, Void, Void>

    {
        String vacDominList,vacGetControl,vacGetAction;

        @Override
        protected Void doInBackground(Void... voids) {




            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {



            String urlListRequest  = DataConstant.serverUrl+DataConstant.vacGetControl+DataConstant.vacGetAction +sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase) ;


            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, urlListRequest,null ,  new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {


                    sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.vacLisJsonKey,response.toString());
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                    loadingProgBar.setVisibility(View.GONE);
                    vactionListRecyclerView.setVisibility(View.VISIBLE);

                }
            });

            queue.add(jsObjRequest);



            super.onPostExecute(aVoid);
        }
    }


}
