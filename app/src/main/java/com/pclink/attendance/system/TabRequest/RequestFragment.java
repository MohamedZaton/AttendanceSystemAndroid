package com.pclink.attendance.system.TabRequest;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;


public class RequestFragment extends Fragment {
LinearLayout linearLayoutReq;
    DialogAll dialogAll;
    MainAsynctask mainAsynctask;
    public RequestFragment() {
        // Required empty public constructor
    }
    public FragmentActivity getActivityFragment ()
    {
        return  getActivity();

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

      View view= inflater.inflate(R.layout.fragment_request, container, false);
        Button vacationButton =(Button)view.findViewById(R.id.vacation_requestm_button);
        Button excuseButton =(Button)view.findViewById(R.id.excuse_requestm_button);
        linearLayoutReq=view.findViewById(R.id.request_continerLayout);
        dialogAll=new DialogAll(getActivity());
        mainAsynctask = new MainAsynctask(getActivity(),22);
        vacationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainAsynctask.isConnected()) {
                    linearLayoutReq.setVisibility(View.GONE);
                    VacationRequestFragment secondFragment = new VacationRequestFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.request_continer_fragment, secondFragment);
                    fragmentTransaction.commit();
                    fragmentTransaction.disallowAddToBackStack();

                } else {
                    String mystring = getResources().getString(R.string.cik_conect_network);

                    dialogAll.txtImageMsgRequest(R.drawable.ic_error_network, "Error Network",mystring, "try again ", RequestFragment.class);

                }

            }

        });
        excuseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mainAsynctask.isConnected()) {
                    linearLayoutReq.setVisibility(View.GONE);

                    ExcuseRequestFragment secondFragment = new ExcuseRequestFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.request_continer_fragment, secondFragment);

                    fragmentTransaction.commit();

                    fragmentTransaction.disallowAddToBackStack();  //solve error back
                }
                else {
                    String mystring = getResources().getString(R.string.cik_conect_network);

                    dialogAll.txtImageMsgRequest(R.drawable.ic_error_network, "Error Network", mystring, "try again ", RequestFragment.class);

                }

            }
        });


    return view;
    }


    private  void swapFragment(){
        RequestFragment secondFragment=new RequestFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.request_continerLayout, secondFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void vacationButton(View v)
    {
        switch (v.getId())
        {
//            VacationRequestFragment secondFragment=new VacationRequestFragment();
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.request_continerLayout, secondFragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
        }


    }



}
