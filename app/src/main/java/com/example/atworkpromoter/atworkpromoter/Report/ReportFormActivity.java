package com.pclink.attendance.system.Report;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.Json.JsonPr;
import com.pclink.attendance.system.Models.ProductCategoryModel;
import com.pclink.attendance.system.Models.ProductFamilyModel;
import com.pclink.attendance.system.Models.ProductModel;
import com.pclink.attendance.system.Models.ReportModel;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.TabAttendanceLog.itemCardViewAttendance;
import com.pclink.attendance.system.TabCheck.CheckInFragment;
import com.pclink.attendance.system.TabRequest.itemCardViewExcuseList;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.pclink.attendance.system.Wrapper.CategoryWrapper;
import com.pclink.attendance.system.Wrapper.FamilyWrapper;
import com.pclink.attendance.system.Wrapper.ProductWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class ReportFormActivity extends AppCompatActivity {
    public SharedPrefData sharedPrefData;
    public RequestQueue queue;
    public  TextView totalSoldTVw, numBuyerTVw, mainCommentTVw , numShopperTVw , storeTrafficTVw;
    public EditText totalSold, numBuyer, mainComment , numShopper;
    public Spinner storeTraffic;
    public LinearLayout pageOne, pageTwo, pageThree,pageFour ;
    public  CardView imageCapCardVw ;
    public RelativeLayout prodRlyt,compRlyts ;
    public Button nextOnePageBtn, backOnePageBtn, nextTwoProdtPageBtn, backTwoProdtPageBtn, nextThreeProdtPageBtn, backThreeProdtPageBtn,nextFourProdtPageBtn,backFourProdtPageBtn;
    public String totalSoldTxt, numBuyerTxt, mainCommentTxt , numShopperTxt;
    public JSONObject bodyMainjsonObt  , competitiveOnejObjt;
    public  JSONArray productJsOjt,compJsOjt;
    public  Context globalContext ;
    public Promoter promoter;
    //product comp popup var;
    public Dialog prodCompDialogPt;
    public EditText countItemEtPt, priceItemEtPt, commentItemEtPt;
    public TextView countItemEtPtTVw, priceItemEtPtTVw, commentItemEtPtTVw, imageTitleTvw;
    public CompetitiveAdapter competitiveAdapter;
    public  ProductsAdapter productsAdapter;
    public Spinner selectCatogrySpinnerPt,selectFamilySpinnerPt,selectProductSpinnerPt;
    public LinearLayout familyLayout , productLayout;
    public FloatingActionButton addProductPopupBtn, addcompPopupBtn;
    public CardView  addBtnPt,cancelBtnPt;
    public Button cameraIntCardvPt, selectGalaryCardvPt;
    public ImageView imagePicturePt , imageJsonSend;
    public FloatingActionButton closeFBtnPt;
    public String countItemTxt, priceItemTxt, commentItemPtTxt;
    public GetListProduct getListProduct;
    public  JsonPr jsonPr;
    public TextView selectProductTxt , selectCategoryTxt , selectFamilyTxt;
    public String hasReport="hasReport";
    public  Gson gson;
    public  String  imageItems = null;
    public  SettingReport settingReport ;
    public  HashMap<String,Boolean> settingSalesFormMaps ;
    public  HashMap<String,String> settingNamesLableMaps ;

    String[] jsonKeyArrayList = DataConstant.arrayListReport;
    public MainAsynctask mainAsynctask;
    public         ReportModel mRprt;

    public String arrayIdSku[], arrayNameSku[], arrayCodeSku[];
    public List<String>   categoryNames=new ArrayList<>(),familyName=new ArrayList<>() ,productNames=new ArrayList<>(),productSkus=new ArrayList<>();
    public List<Integer> categoryIds=new ArrayList<>(),familyIds=new ArrayList<>() , productIds=new ArrayList<>(),familyCtgIds=new ArrayList<>(),productFmlyIds=new ArrayList<>(),productCtgIds=new ArrayList<>();

    JSONObject productOnejObjt = new JSONObject();
    JSONObject compOnejObjt = new JSONObject();


    public String idskuAllSp, nameskuAllSp, codeskuAllSp;

public DialogAll dialogAll ;
    ArrayList<HashMap<String, String>> getProdCompListList;



    private RecyclerView prodtRecyclerView, compRecyclerView;
    private List<itemCardReport> mProdList, mCompList;
    public boolean isEtTxtAllFull = false;

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_form);
        queue = Volley.newRequestQueue(this);
        mainAsynctask = new MainAsynctask(this, 2222);
        globalContext = this;
        promoter= new Promoter(this);
        sharedPrefData = new SharedPrefData(this);
        dialogAll = new DialogAll(this);
        gson = new Gson();
        pageOne = findViewById(R.id.page_one_linearLyt);
        pageTwo = findViewById(R.id.page_Two_my_products_linearLyt);
        pageThree = findViewById(R.id.page_Three_my_comp_linearLyt);
        pageFour = findViewById(R.id.page_four_uploading_lyt);

        prodRlyt = findViewById(R.id.add_poduct_rlyt);
        compRlyts = findViewById(R.id.add_comp_rlyt);

        bodyMainjsonObt =  new JSONObject();
        productJsOjt = new JSONArray();
        compJsOjt = new JSONArray();
        settingReport=  new SettingReport(this);
        settingSalesFormMaps = settingReport.getSettingMap();
        settingNamesLableMaps = settingReport.getSettingName();



        prodCompPopup();


        pageOneLyt();

        // product and comp details


        pageTwoProductLyt();
        pageThreeComptLyt();


        getListProduct = new GetListProduct();
        getListProduct.execute();

        settingGate(settingSalesFormMaps,settingNamesLableMaps);


    }


    public  void settingGate(HashMap<String,Boolean> settingMap,HashMap<String,String>nameMaps) {
        // setting gate
        if (settingMap != null) {
            if (settingMap.size() > 1) {
                if (!settingMap.get("TotalSold")) {
                    totalSoldTVw.setVisibility(View.GONE);
                    totalSold.setVisibility(View.GONE);
                    totalSold.setText("0");
                    totalSoldTxt="0";
                }else
                {
                    totalSoldTVw.setText(nameMaps.get("TotalSold"));
                }
                if (!settingMap.get("numberOFShopper")) {
                    numShopperTVw.setVisibility(View.GONE);
                    numShopper.setVisibility(View.GONE);
                    numShopper.setText("0");
                    numShopperTxt = "0";

                }else
                {
                    numBuyerTVw.setText(nameMaps.get("numberOFShopper"));
                }
                if (!settingMap.get("NumberBuyer")) {
                    numBuyerTVw.setVisibility(View.GONE);
                    numBuyer.setVisibility(View.GONE);
                    numBuyer.setText("0");
                    numBuyerTxt = "0";

                }else
                {
                    numBuyerTVw.setText(nameMaps.get("NumberBuyer"));
                }
                if (!settingMap.get("StoreTraffic")) {
                    storeTrafficTVw.setVisibility(View.GONE);
                    storeTraffic.setVisibility(View.GONE);
                    //see  down handle null field

                }else
                {
                    storeTrafficTVw.setText(nameMaps.get("StoreTraffic"));

                }
                if (!settingMap.get("MainComments"))
                {
                    mainCommentTVw.setVisibility(View.GONE);
                    mainComment.setVisibility(View.GONE);
                }else
                {
                   mainCommentTVw.setText(nameMaps.get("MainComments"));
                }


            }
        }

    }

    public void prodSettingGate(HashMap<String,Boolean> prodMap,HashMap<String,String>nameMap)
    {
        // setting gate
        if (prodMap != null){
            if (prodMap.size() > 1) {
                if (!prodMap.get("ProductsItems")){
                    countItemEtPtTVw.setVisibility(View.GONE);
                    countItemEtPt.setVisibility(View.GONE);
                    countItemEtPt.setText("0");
                    countItemTxt = "0";
                   sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.countProdtHdn,true);
                } else {
                    countItemEtPtTVw.setVisibility(View.VISIBLE);
                    countItemEtPtTVw.setText(nameMap.get("ProductsItems"));
                    countItemEtPt.setVisibility(View.VISIBLE);
                    countItemEtPt.setText("");
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.countProdtHdn,false);


                }
                if (!prodMap.get("ProductsPrice")) {
                    priceItemEtPtTVw.setVisibility(View.GONE);
                    priceItemEtPt.setVisibility(View.GONE);
                    priceItemEtPt.setText("0");
                    priceItemTxt ="0" ;
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.priceProdtHdn,true);

                } else {
                    priceItemEtPtTVw.setVisibility(View.VISIBLE);
                    priceItemEtPtTVw.setText(nameMap.get("ProductsPrice"));
                    priceItemEtPt.setVisibility(View.VISIBLE);
                    priceItemEtPt.setText("");
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.priceProdtHdn,false);

                }
                if (!prodMap.get("ProductsImage"))
                {
                    imageCapCardVw.setVisibility(View.GONE);
                    imageTitleTvw.setText("Product");
                }else {
                    imageCapCardVw.setVisibility(View.VISIBLE);
                    imageTitleTvw.setText(nameMap.get("ProductsImage"));
                }

                if (!prodMap.get("ProductsComment")) {
                    commentItemEtPtTVw.setVisibility(View.GONE);
                    commentItemEtPt.setVisibility(View.GONE);
                    commentItemEtPt.setText("0");
                    commentItemPtTxt = "0";
                } else {
                    commentItemEtPtTVw.setVisibility(View.VISIBLE);
                    commentItemEtPtTVw.setText(nameMap.get("ProductsComment"));
                    commentItemEtPt.setVisibility(View.VISIBLE);
                    commentItemEtPt.setText("");

                }

            }
        }

    }

    public void compSettingGate(HashMap<String,Boolean> compMap ,HashMap<String,String> nameMaps)
    {
        if (compMap != null){
            // setting gate
            if (compMap.size() > 1){
                if (!compMap.get("CompItems")){
                    countItemEtPtTVw.setVisibility(View.GONE);
                    countItemEtPt.setVisibility(View.GONE);
                    countItemEtPt.setText("0");
                    countItemTxt = "0";
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.countCompHdn,true);

                }else{
                    countItemEtPtTVw.setVisibility(View.VISIBLE);
                    countItemEtPt.setVisibility(View.VISIBLE);
                    countItemEtPtTVw.setText(nameMaps.get("CompItems"));

                    countItemEtPt.setText("");
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.countCompHdn,false);

                }
                if(!compMap.get("CompPrice")){
                    priceItemEtPtTVw.setVisibility(View.GONE);
                    priceItemEtPt.setVisibility(View.GONE);
                    priceItemEtPt.setText("0");
                    priceItemTxt ="0" ;
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.priceCompHdn,true);

                }else{
                    priceItemEtPtTVw.setVisibility(View.VISIBLE);
                    priceItemEtPt.setVisibility(View.VISIBLE);
                    priceItemEtPt.setText("");
                    priceItemEtPtTVw.setText(nameMaps.get("CompPrice"));
                    sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.priceCompHdn,false);

                }
                if(!compMap.get("CompImage")){
                    imageCapCardVw.setVisibility(View.GONE);
                    imageTitleTvw.setText("Product");
                }else{
                    imageCapCardVw.setVisibility(View.VISIBLE);
                    imageTitleTvw.setText(nameMaps.get("CompImage"));
                }
                if(!compMap.get("CompComment"))
                {
                    commentItemEtPtTVw.setVisibility(View.GONE);
                    commentItemEtPt.setVisibility(View.GONE);
                    commentItemEtPt.setText("0");
                    commentItemPtTxt = "0";
                } else {
                    commentItemEtPtTVw.setVisibility(View.VISIBLE);
                    commentItemEtPt.setVisibility(View.VISIBLE);
                    commentItemEtPt.setText("");
                    commentItemEtPtTVw.setText(nameMaps.get("CompComment"));
                 }

            }
        }

    }

    private void pageOneLyt() {
        nextOnePageBtn = findViewById(R.id.next_page_one);
        backOnePageBtn = findViewById(R.id.back_page_one);

        totalSold = findViewById(R.id.total_amount_sold_edittext);
        numBuyer = findViewById(R.id.number_buyer_edittxt);
        numShopper = findViewById(R.id.number_shopper_edittxt);

        mainComment = findViewById(R.id.main_comment);
        storeTraffic = findViewById(R.id.traffic_spinner); // spinner

        totalSoldTVw = findViewById(R.id.total_sold_amount_txt);
        mainCommentTVw = findViewById(R.id.main_comment_txt);
        numBuyerTVw = findViewById(R.id.number_buyer_txt);
        numShopperTVw = findViewById(R.id.number_shopper_txt);
        storeTrafficTVw = findViewById(R.id.traffic_txt);

        spinnerTraffic();

        nextOnePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalSoldTxt = totalSold.getText().toString();
                numBuyerTxt = numBuyer.getText().toString();
                mainCommentTxt = mainComment.getText().toString();
                numShopperTxt = numShopper.getText().toString();
                isEtTxtAllFull = is_Empty_R(totalSold, totalSoldTxt) && is_Empty_R(numBuyer, numBuyerTxt) && is_Empty_R(numShopper, numShopperTxt) && is_Empty_R(mainComment, mainCommentTxt);

                if (isEtTxtAllFull){
                    try {
                        String checkinIdValue = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.checkInIDJsonKey);
                        String agencyIdValue = sharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile,DataConstant.agencyIDJsonKeyUpcase);
                        bodyMainjsonObt.put(DataConstant.checkInIDJsonKey,checkinIdValue);
                        bodyMainjsonObt.put(DataConstant.agencyIDJsonKeyUpcase,agencyIdValue);
                        bodyMainjsonObt.put(DataConstant.TotalSoldRp, totalSoldTxt);
                        bodyMainjsonObt.put(DataConstant.NumberBuyerRp, numBuyerTxt);
                        bodyMainjsonObt.put(DataConstant.MainCommentsRp, mainCommentTxt);
                        bodyMainjsonObt.put(DataConstant.NumberShopperRp,numShopperTxt);
                        if(storeTraffic.isShown()) {
                            bodyMainjsonObt.put(DataConstant.StoreTrafficRp, storeTraffic.getSelectedItem().toString());
                        }
                        else
                        {
                            bodyMainjsonObt.put(DataConstant.StoreTrafficRp, "ThisFieldDisappear"); // disappear For This Promoter
                        }
                        } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    pageOne.setVisibility(View.GONE);
                    pageTwo.setVisibility(View.VISIBLE);

                    // *  if product hidden and comp hidden  then upload sales form

                    if(settingSalesFormMaps.size()>1 &&!settingSalesFormMaps.get("ProductsForm")&&!settingSalesFormMaps.get("CompProducts"))
                    {
                        pageOne.setVisibility(View.GONE);
                        pageTwo.setVisibility(View.GONE);
                        pageThree.setVisibility(View.GONE);
                        pageFour.setVisibility(View.VISIBLE);

                        if (mainAsynctask.isConnected())
                        {
                            try {
                                bodyMainjsonObt.put(DataConstant.myProductsRp, productJsOjt);
                                bodyMainjsonObt.put(DataConstant.compProductRp, compJsOjt);

                                sendReportPostReq(bodyMainjsonObt);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else
                        {
                            pageThree.setVisibility(View.GONE);
                            pageFour.setVisibility(View.VISIBLE);
                            OfflineSalesReq();


                        }


                    }
                        // * product hidden and comp show  then jump to comp

                    else if(settingSalesFormMaps.size()>1 &&!settingSalesFormMaps.get("ProductsForm")&&settingSalesFormMaps.get("CompProducts"))
                    {
                        prodRlyt.setVisibility(View.GONE);
                        pageOne.setVisibility(View.GONE);
                        pageTwo.setVisibility(View.GONE);
                        pageThree.setVisibility(View.VISIBLE);
                    }

                }


            }
        });
        backOnePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(ReportFormActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

    }


    private void spinnerTraffic() {

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Medium");
        categories.add("Low");
        categories.add("High");

        ArrayAdapter<String> storeAdapterSp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        storeAdapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeTraffic.setAdapter(storeAdapterSp);
    }

    private void spinnerSku(int prodOrComp)
    {
      String saveSkuListType ="" ;

        switch(prodOrComp)
        {
            case  1 :  // prod
                saveSkuListType = DataConstant.skuProListJsonKey;
                break;

            case  2 :   //comp
               saveSkuListType = DataConstant.skuCompListJsonKey;
                break;
        }
        String responceJson = sharedPrefData.getElementValue(DataConstant.skuFileSp,saveSkuListType);

        CategoryWrapper categoryWrapper  =gson.fromJson(responceJson,CategoryWrapper.class);
        FamilyWrapper familyWrapper = gson.fromJson(responceJson,FamilyWrapper.class);
        ProductWrapper productWrapper = gson.fromJson(responceJson,ProductWrapper.class);

        List<ProductCategoryModel> categoryModelList = categoryWrapper.getProductCategories();
        final List<ProductFamilyModel> familyModelList = familyWrapper.getProductFamilies();
        final List<ProductModel> productModelList =productWrapper.getProducts();

        categoryNames.add( "Select category");
        categoryIds.add(-1);

        int i=1;
        for (ProductCategoryModel item :categoryModelList)
        {
            categoryNames.add(item.getCategoryName());
            categoryIds.add(item.getId());
            i++;
        }

        // display spr
        displaySpinnerData(categoryNames,selectCatogrySpinnerPt);
            selectCatogrySpinnerPt.setSelection(0);
       selectCatogrySpinnerPt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    int selectedIdCtg =categoryIds.get(position);
                    familyName=new ArrayList<>();
                    familyIds=new ArrayList<>();
                    familyCtgIds=new ArrayList<>();

                    familyName.add("Select family");
                    familyIds.add(-1);
                    familyCtgIds.add(-1);

                    for (ProductFamilyModel item :familyModelList)
                    {
                        if(selectedIdCtg==item.getCategoryID())
                        {
                            familyName.add(item.getFamilyName());
                            familyIds.add(item.getId());
                            familyCtgIds.add(item.getCategoryID());
                        }

                    }

                     displaySpinnerData(familyName,selectFamilySpinnerPt);

                    familyLayout.setVisibility(View.VISIBLE);

                }
                else
                {
                    selectFamilySpinnerPt.setSelection(0);
                    selectProductSpinnerPt.setSelection(0);
                    familyLayout.setVisibility(View.GONE);
                    productLayout.setVisibility(View.GONE);

                }


           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
        selectFamilySpinnerPt.setSelection(0);
        selectFamilySpinnerPt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    productNames=new ArrayList<>();
                    productIds=new ArrayList<>();
                    productSkus=new ArrayList<>();
                    productFmlyIds=new ArrayList<>();
                    productNames=new ArrayList<>();
                    int selectedIdFmly = familyIds.get(position);
                    int selectedIdFmlyCtg = familyCtgIds.get(position);
                    productNames.add("");
                    productIds.add(-1);
                    productSkus.add("select product");
                    productFmlyIds.add(-1);
                    productCtgIds.add(-1);


                    for (ProductModel item :productModelList)
                    {
                        if(selectedIdFmly == item.getFamilyProductID() && selectedIdFmlyCtg==item.getCategoryID())
                        {
                            productNames.add(item.getProductName());
                            productIds.add(item.getId());
                            productSkus.add(item.getSku());
                            productFmlyIds.add(item.getFamilyProductID());
                            productCtgIds.add(item.getCategoryID());
                        }

                    }
                    displaySpinnerData(productSkus,selectProductSpinnerPt);
                    productLayout.setVisibility(View.VISIBLE);

                }
                else
                {
                    selectProductSpinnerPt.setSelection(0);
                    productLayout.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });











    }

    private void pageTwoProductLyt()
    {
        mProdList = new ArrayList<>();
        nextTwoProdtPageBtn = findViewById(R.id.next_page_two);
        backTwoProdtPageBtn = findViewById(R.id.back_page_two);
        addProductPopupBtn = findViewById(R.id.add_item_pt_floatAButton);
       // mProdList.add(new itemCardReport("5", "Dell ", "1200", "2", "654697146"));
        prodtRecyclerView = findViewById(R.id.products_list_recyclerView);
        prodtRecyclerView.setHasFixedSize(true);
        prodtRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        productsAdapter = new ProductsAdapter(this, mProdList);

        prodtRecyclerView.setAdapter(productsAdapter);

        productsAdapter.notifyDataSetChanged();

        addProductPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllData();
                clearErrorData();

                prodCompActionPopup(1 ,globalContext);
                prodSettingGate(settingSalesFormMaps,settingNamesLableMaps) ;

                prodCompDialogPt.show();


            }
        });



        nextTwoProdtPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



               if(prodRlyt.isShown())
               {
                   if(mProdList.size() > 0)
                   {
                       pageTwo.setVisibility(View.GONE);
                       pageThree.setVisibility(View.VISIBLE);

                        // if comp hidden then jump to uploading
                       if(settingSalesFormMaps.size()>1 &&!settingSalesFormMaps.get("CompProducts"))
                       {
                           pageThree.setVisibility(View.GONE);
                           pageFour.setVisibility(View.VISIBLE);

                           if (mainAsynctask.isConnected())
                           {
                               try {

                                   bodyMainjsonObt.put(DataConstant.myProductsRp, productJsOjt);
                                   bodyMainjsonObt.put(DataConstant.compProductRp, compJsOjt);

                                   sendReportPostReq(bodyMainjsonObt);


                               } catch (JSONException e) {

                                   e.printStackTrace();

                               }

                           } else
                           {
                               pageThree.setVisibility(View.GONE);
                               pageFour.setVisibility(View.VISIBLE);

                               OfflineSalesReq();


                           }
                       }

                   }
                   else
                   {
                       dialogAll.infoMsgOneBtn("Please , Add one product","OK");

                   }

               }
               else
               {
                   pageTwo.setVisibility(View.GONE);
                   pageThree.setVisibility(View.VISIBLE);



               }










            }
        });
        backTwoProdtPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTwo.setVisibility(View.GONE);
                pageOne.setVisibility(View.VISIBLE);
            }
        });
    }


    public void prodCompPopup() {
        prodCompDialogPt = new Dialog(this);
        prodCompDialogPt.setContentView(R.layout.product_comp_pop_2);
        //img
        imagePicturePt = prodCompDialogPt.findViewById(R.id.product_imgView);
        imageTitleTvw = prodCompDialogPt.findViewById(R.id.product_title_txtview);
        //btn img open camera capture

        imageCapCardVw = prodCompDialogPt.findViewById(R.id.image_option_cardview);
        cameraIntCardvPt = prodCompDialogPt.findViewById(R.id.capture_camera_cardview);
        selectGalaryCardvPt = prodCompDialogPt.findViewById(R.id.upload_camera_cardview);
        // input field
        countItemEtPtTVw = prodCompDialogPt.findViewById(R.id.number_items_txt);
        countItemEtPt = prodCompDialogPt.findViewById(R.id.number_items_edittxt);

        priceItemEtPtTVw= prodCompDialogPt.findViewById(R.id.unit_price_txt);
        priceItemEtPt = prodCompDialogPt.findViewById(R.id.unit_price);

        commentItemEtPtTVw = prodCompDialogPt.findViewById(R.id.product_single_comment_txt);
        commentItemEtPt = prodCompDialogPt.findViewById(R.id.product_single_comment);

        selectCatogrySpinnerPt = prodCompDialogPt.findViewById(R.id.sku_categries_spinner);
        selectFamilySpinnerPt =  prodCompDialogPt.findViewById(R.id.sku_families_spinner);
        selectProductSpinnerPt = prodCompDialogPt.findViewById(R.id.sku_name_code_spinner);
        familyLayout =  prodCompDialogPt.findViewById(R.id.family_LinearLayout);
        productLayout =  prodCompDialogPt.findViewById(R.id.product_LinearLayout);

        selectProductTxt =  prodCompDialogPt.findViewById(R.id.select_list_title_testview);
        selectCategoryTxt =prodCompDialogPt.findViewById(R.id.category_list_title_testview);
        selectFamilyTxt =prodCompDialogPt.findViewById(R.id.family_list_title_testview);
        // btn submit

        addBtnPt = prodCompDialogPt.findViewById(R.id.add_prod_btn_cardview);
        cancelBtnPt = prodCompDialogPt.findViewById(R.id.cancel_prod_btn_cardview);


        prodCompDialogPt.setCanceledOnTouchOutside(false);
        prodCompDialogPt.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }

    public void prodCompActionPopup(final int prodOrComp , final Context mContext){
        spinnerSku(prodOrComp);

        cameraIntCardvPt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null)
                {

                    startActivityForResult(intent, 0);

                }
            }
        });

        selectGalaryCardvPt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

            }
        });


        addBtnPt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                clearErrorData();
                priceItemTxt = priceItemEtPt.getText().toString();
                countItemTxt = countItemEtPt.getText().toString();
                commentItemPtTxt = commentItemEtPt.getText().toString();

                isEtTxtAllFull = is_Empty_R(priceItemEtPt, priceItemTxt) && is_Empty_R(countItemEtPt, countItemTxt) && is_Empty_R(commentItemEtPt, commentItemPtTxt);

                int  idProductPosition=selectProductSpinnerPt.getSelectedItemPosition();
                int  idCatgPostion=selectCatogrySpinnerPt.getSelectedItemPosition();
                int  idFmlyPostion=selectFamilySpinnerPt.getSelectedItemPosition();


                if(idCatgPostion==0)
                {
                     selectCategoryTxt.setError("you must select  Category");
                }
                else if(idFmlyPostion==0)
                {
                    selectFamilyTxt.setError("you must select  Family");
                }
                else if(idProductPosition==0)
                {

                    selectProductTxt.setError("you must select  product");
                }

                else if(isEtTxtAllFull){


                switch (prodOrComp)
                {
                    case 1 :      //prod
                        //save hash map body json
                        String productIdxp = String.valueOf(productIds.get(idProductPosition));
                        mProdList.add(new itemCardReport(productIdxp, productNames.get(idProductPosition), priceItemTxt, countItemTxt, productSkus.get(idProductPosition)));
                        productsAdapter.notifyItemInserted(mProdList.size()-1);
                        try {
                            productOnejObjt.put(DataConstant.ItemsRp,countItemTxt);
                            productOnejObjt.put(DataConstant.ProductIDRp, productIdxp);
                            productOnejObjt.put(DataConstant.ImageRp,imageItems);
                            productOnejObjt.put(DataConstant.commentRp,commentItemPtTxt);
                            productOnejObjt.put(DataConstant.priceRp,priceItemTxt);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        productJsOjt.put(productOnejObjt);
                        prodCompDialogPt.dismiss();
                        prodCompDialogPt.cancel();
                        break;

                    case 2 :  //comp

                        //save hash map body json
                        String productIdxc = String.valueOf(productIds.get(idProductPosition));
                        mCompList.add(new itemCardReport(productIdxc, productNames.get(idProductPosition), priceItemTxt, countItemTxt, productSkus.get(idProductPosition)));
                         competitiveAdapter.notifyItemInserted(mCompList.size()-1);
                        try {
                            compOnejObjt.put(DataConstant.ItemsRp,countItemTxt);
                            compOnejObjt.put(DataConstant.ProductIDRp, productIdxc);
                            compOnejObjt.put(DataConstant.ImageRp,imageItems);
                            compOnejObjt.put(DataConstant.commentRp,commentItemPtTxt);
                            compOnejObjt.put(DataConstant.priceRp,priceItemTxt);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        compJsOjt.put(compOnejObjt);
                        prodCompDialogPt.dismiss();
                        prodCompDialogPt.cancel();
                        break;
                }
                }



            }
        });
        cancelBtnPt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                prodCompDialogPt.dismiss();
                prodCompDialogPt.cancel();
                clearAllData();
                clearErrorData();
            }
        });
    }


    private void pageThreeComptLyt() {
        nextThreeProdtPageBtn = findViewById(R.id.next_page_three_comp);
        backThreeProdtPageBtn = findViewById(R.id.back_page_three_comp);
        addcompPopupBtn = findViewById(R.id.add_item_compt_floatAButton);
        mCompList = new ArrayList<itemCardReport>();
        //mCompList.add(new itemCardReport("5", "Hp ", "1999", "2", "65400006"));


        compRecyclerView = findViewById(R.id.compt_list_recyclerView);
        compRecyclerView.setHasFixedSize(true);
        compRecyclerView.setLayoutManager(new LinearLayoutManager(this));




        competitiveAdapter = new CompetitiveAdapter(this, mCompList);


        compRecyclerView.setAdapter(competitiveAdapter);

        addcompPopupBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               clearAllData();
                prodCompActionPopup(2,globalContext);
                compSettingGate(settingSalesFormMaps,settingNamesLableMaps);

                prodCompDialogPt.show();
            }
        });
        nextThreeProdtPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean startUpload = false;
                if (compRlyts.isShown()) {
                    if (mCompList.size() > 0) {

                        startUpload = true;
                    } else {
                        dialogAll.infoMsgOneBtn("please , add one   comp ", "ok");

                    }
                }
                        boolean compR = compRlyts.isShown() ;
                if((compR &&startUpload)||(!compR&&!startUpload))
                {
                    pageThree.setVisibility(View.GONE);
                    pageFour.setVisibility(View.VISIBLE);

                    if (mainAsynctask.isConnected())
                    {
                        try {

                            bodyMainjsonObt.put(DataConstant.myProductsRp, productJsOjt);
                            bodyMainjsonObt.put(DataConstant.compProductRp, compJsOjt);

                            sendReportPostReq(bodyMainjsonObt);


                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                    } else
                        {
                        pageThree.setVisibility(View.GONE);
                        pageFour.setVisibility(View.VISIBLE);

                        OfflineSalesReq();


                    }

                }



            }

        });
        backThreeProdtPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageThree.setVisibility(View.GONE);
                pageTwo.setVisibility(View.VISIBLE);


            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {

                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");

                    imagePicturePt.setImageBitmap(photo);
                    imageItems = convertToBaseString(photo);

                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    int scale = 1;

                    imagePicturePt.setImageURI(selectedImage);
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    Bitmap bitmapPhoto = null;

                    try {
                        bitmapPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        imageItems = convertToBaseString(bitmapPhoto);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }


    public void sendReportPostReq(JSONObject bodyJObjt)
    {
        String urlPost = DataConstant.serverUrl + DataConstant.salesRepUrl;

        RequestQueue queue = Volley.newRequestQueue(ReportFormActivity.this);
        String bodyStr =  new Gson().toJson(bodyJObjt );
            Log.i("body_report= ",bodyStr);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlPost,bodyJObjt, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("ok_sales_reportX" , response.toString());

             //  String salesFormID= JsonPr.getValueObjtJson(response.toString(),DataConstant.salesFormIdKey,"s");

               // sharedPrefData.putElement(DataConstant.promoterDataNameSpFile,DataConstant.salesFormIdKey,salesFormID);

                Intent i = new Intent(ReportFormActivity.this, MainActivity.class);
                  i.putExtra(hasReport,true)  ;
                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasReportKey,true);

                startActivity(i);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error_sales_reportCX" , error.toString());
                OfflineSalesReq();
            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void OfflineSalesReq() {
        new CountDownTimer(1000, 500)
        {


            public void onTick(long millisUntilFinished)
            {


            }


            public void onFinish()
            {
                try {
                    Location loct = promoter.searchCurrentLocation();

                    String dateVale = new java.text.SimpleDateFormat("yyyy-MM-dd" , new Locale("en")).format(loct.getTime());

                    bodyMainjsonObt.put(DataConstant.reportDateKeyOffline,dateVale);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sharedPrefData.putSaveJsonObj(DataConstant.offlineModeFile,DataConstant.reportStoreOffline,bodyMainjsonObt);

                Intent i = new Intent(ReportFormActivity.this, MainActivity.class);
                i.putExtra(hasReport,true)  ;
                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.hasReportKey,true);

                startActivity(i);

            }

        }.start();


    }

    public void clearAllData() {
        priceItemEtPt.getText().clear();
        countItemEtPt.getText().clear();
        commentItemEtPt.getText().clear();
        selectProductSpinnerPt.setSelection(0);
        selectFamilySpinnerPt.setSelection(0);
        selectCatogrySpinnerPt.setSelection(0);
        familyLayout.setVisibility(View.GONE);
        productLayout.setVisibility(View.GONE);
        imagePicturePt.setImageResource(R.drawable.ic_count_items_1);
        categoryNames = new ArrayList<>();
        categoryIds = new ArrayList<>() ;

    }
    public  void clearErrorData()
    {
        selectFamilyTxt.setError(null);
        selectCategoryTxt.setError(null);
        selectProductTxt.setError(null);
    }


    public boolean is_Empty_R(EditText et, String etS) {
        if(et.isShown()) {
            if (etS.isEmpty()) {
                et.setError("is Empty");
                return false;
            }
        }
        return true;
    }

    private String convertToBaseString(Bitmap photo)
    {
        if(photo.getHeight()>480 && photo.getWidth()>640) {

            Bitmap resized = Bitmap.createScaledBitmap(photo, 640, 480, true);
            photo = resized ;
        }
        //base64
        String baseSixFour;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] bytes = baos.toByteArray();
        baseSixFour= Base64.encodeToString(bytes, Base64.DEFAULT);
        return baseSixFour;

    }


    public  void displaySpinnerData(List<String> categories , Spinner spinnerCategory)
    {
        ArrayAdapter<String> storeAdapterSp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        storeAdapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(storeAdapterSp);
    }

    private class GetListProduct extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (mainAsynctask.isConnected()) {
                sharedPrefData.putElementBoolean(DataConstant.skuFileSp, DataConstant.foundsku, true);
            } else {
                if (sharedPrefData.isExistsKey(DataConstant.skuFileSp, DataConstant.foundsku))
                {
                }
            }

            super.onPostExecute(aVoid);
        }


    }
}
