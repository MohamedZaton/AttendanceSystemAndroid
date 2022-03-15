package com.pclink.attendance.system.languages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.Activities.SplashScreen;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.TabRequest.RequestFragment;
import com.pclink.attendance.system.TabRequest.VacationRequestFragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Locale;

public class LanguagesActivity extends AppCompatActivity {

    Button arabicButton ;
    Button englishButton ;
    LinearLayout linearLayoutReq ;
    SharedPrefData sharedPrefData;
    String namefileSp = DataConstant.promoterDataNameSpFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        sharedPrefData =new SharedPrefData(this);

         arabicButton =findViewById(R.id.arabic_request_button);
         englishButton =findViewById(R.id.english_request_button);
        linearLayoutReq=findViewById(R.id.language_selectLayout);
        arabicButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLanguage(DataConstant.kArabic) ;
            }

        }));

           englishButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLanguage(DataConstant.kEnglish) ;
            }

        }));


    }

    private void setLanguage(String lang) {
        Locale myLocale = new Locale(lang);
        sharedPrefData.putElement(namefileSp,DataConstant.kLanguage,lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf,dm);
        Intent refresh = new Intent(this, SplashScreen.class);
        finish();
        startActivity(refresh);
    }
}