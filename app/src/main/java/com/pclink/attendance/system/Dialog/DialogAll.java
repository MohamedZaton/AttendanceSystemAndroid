package com.pclink.attendance.system.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.Activities.SplashScreen;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.LocationFind.GpsHelper;
import com.pclink.attendance.system.R;


public class DialogAll {
    Context context;
    Class activity;
    public  DialogAll(Context context, Class activityClass)
    {
        this.context=context;
        this.activity=activityClass;
    }

    public  DialogAll(Context context)
    {
        this.context=context;

    }


    public void notShowPerm()
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Excuse me \n You denied some permissions  \n You must go to setting App then allow it " )
                .setCancelable(false)
                .setPositiveButton("Setting",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                                dialog.cancel();
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);

                            }
                        });
        alertDialogBuilder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ((Activity)context).finishAffinity();
                        System.exit(0);
                    }
                });


         alertDialogBuilder.create();
         alertDialogBuilder.show();

    }



    public void  LocationEnabledDialogAll()
    {
        Log.i("location ", "enable location ");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.locationEnable_message).setTitle(R.string.location_title).setPositiveButton(R.string.locationEnable_btn_dialog, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        }).show();


    }


    public void  dialogMsgConnect()
    {
        Log.i("info", "No network ");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.connect_message)
                .setTitle(R.string.connect_title).setPositiveButton(R.string.connect_btn_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, activity);
                context.startActivity(i);
            }
        }).show();


    }


    public AlertDialog showGPSDisabledAlertToUser(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                                dialog.cancel();
                                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                        });
        alertDialogBuilder.setNegativeButton("try again ",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                        dialog.cancel();
                        Intent i = new Intent(context,SplashScreen.class);
                        context.startActivity(i);
                    }
                });


        return alertDialogBuilder.create();


    }


    public void newVersion(String versionNumber, final String linkdownload){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Download new version : "+versionNumber)
                .setCancelable(false)
                .setPositiveButton("Downolad",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

//                                Intent i = new Intent(Intent.ACTION_VIEW,
//                                        Uri.parse(linkdownload));
//                                context.startActivity(i);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        ((Activity)context).finishAffinity();
                        System.exit(0);

                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void notConnectServer()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("The Connection to the Server Failed")
                .setCancelable(false)
                .setPositiveButton("try again ",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                Intent i = new Intent(context,SplashScreen.class);
                                context.startActivity(i);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        ((Activity)context).finishAffinity();
                        System.exit(0);

                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


    }

   /* public void overplayAskPermissionDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("do you what popup  still There  on  any apps in  your phone ?  ")
                .setCancelable(false)
                .setPositiveButton("yes,Go setting",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                            }
                        });
        alertDialogBuilder.setNegativeButton("No ,Thank you ",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        new SharedPrefData(context).putElement(DataConstant.promoterDataNameSpFile,);
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


    }*/


    public boolean loginCik(String title,String wrongMsgs)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(wrongMsgs)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("cancel ",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        return  true;
    }

    public  void pwdEmpty()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Please enter a valid User Id and Password")
                .setCancelable(false)
               .setNegativeButton("close",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void showAlertDialogOutApp()
    {


        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        builder .setMessage("where are you ,bra ??")
                .setCancelable(false)
                .setPositiveButton("still there ",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent i = new Intent(context,MainActivity.class);
                                context.startActivity(i);
                            }
                        });

        /** create dialog & set builder on it */
        Dialog dialog = builder.create();
        /** this required special permission but u can use aplication context */
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        /** show dialog */
        dialog.show();
    }


    public AlertDialog exitAppDialog()
    {
        final SharedPrefData sharedPrefData = new SharedPrefData(context);
        final GpsHelper gpsHelper = new GpsHelper(context);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(" Do you want exit from this  App ??")
                .setCancelable(false)
                .setPositiveButton("stay  ",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id)
                            {
                                sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.locationFlag,true);

                                dialog.dismiss();
                                dialog.cancel();

                            }
                        });
        alertDialogBuilder.setNegativeButton("Exit App",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                        dialog.cancel();

                        sharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile,DataConstant.locationFlag,true);
                        gpsHelper.stopGpsRecever();

                        ((Activity)context).finishAffinity();
                        System.exit(0);

                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
       return alert;

    }

    public void txtImageMsgRequest(int  imgCenter ,String title, String Msg  , String buttonTxt, final Class directGo )
    {
        final Dialog dg = new Dialog(context);
        dg.setContentView(R.layout.msg_img_btn_dialog_popup);
        Button btn = dg.findViewById(R.id.direct_button);
        ImageView imageView= dg.findViewById(R.id.img_dialog_imageView);
        TextView textMsg  = dg.findViewById(R.id.msg_textView);
        TextView textTitle  = dg.findViewById(R.id.title_textView);

        btn.setText(buttonTxt);
        textTitle.setText(title);
        textMsg.setText(Msg);

        imageView.setImageDrawable(context.getDrawable(imgCenter));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent i = new Intent(context,directGo);
                context.startActivity(i);*/

                dg.dismiss();
                dg.cancel();

            }
        });






        dg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dg.show();


    }

    public void infoMsgOneBtn(  String Msg  , String buttonName)
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(Msg)
                .setCancelable(true)
                .setPositiveButton(buttonName,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id)
                            {


                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public  void backgroundRequest()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Background Request");
        alertDialogBuilder.setMessage("You Must Accept Running on Background Device ")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id)
                            {

                                Intent i = new Intent(context,SplashScreen.class);
                                    context.startActivity(i);

                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    public void notificationEnableMsg()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
       alertDialogBuilder.setTitle("Notification Permission")
                .setMessage("Promoter app need to use  Push Notification ,please Enable Notification from Setting ")
                .setCancelable(false)
                .setPositiveButton("Setting",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id)
                            {

                                openNotificationSettings();

                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    public  void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        context.startActivity(intent);
    }
}
