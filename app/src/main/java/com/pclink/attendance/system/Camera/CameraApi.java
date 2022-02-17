package com.pclink.attendance.system.Camera;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pclink.attendance.system.Activities.MainActivity;
import com.pclink.attendance.system.DataBase.DataConstant;
import com.pclink.attendance.system.DataBase.Promoter;
import com.pclink.attendance.system.DataBase.RealFireBase;
import com.pclink.attendance.system.DataBase.SharedPrefData;
import com.pclink.attendance.system.Dialog.DialogAll;
import com.pclink.attendance.system.LocationFind.GpsHelper;
import com.pclink.attendance.system.NetworkServer.NetworkRequestPr;
import com.pclink.attendance.system.Notification.NotificationHelper;
import com.pclink.attendance.system.Permission.PermissionCheck;
import com.pclink.attendance.system.R;
import com.pclink.attendance.system.ThreadTasks.MainAsynctask;
import com.pclink.attendance.system.DateAndTime.TimeHelper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraApi extends AppCompatActivity
{
    private static final String TAG = "AndroidCameraApi";
    private FloatingActionButton takePictureButton,closeCameraButton;
    private TextureView textureView;
    private ImageView picImageView;
    private Promoter promoter;
    private  String checkTypeEnter = "checkTypeEnter";
    private ProgressBar cameraLoadingPBar;
    String url;
    private  MainAsynctask mainAsynctask;
    private TimeHelper timeHelper;
    private NetworkRequestPr networkRequestPr;
    private DialogAll dialogAll;
    private  int connectFlag = 0, checkType=1;
    private String encodedImageBaseSixFour;
  private GpsHelper gpsHelper;
    private AlertDialog gpsAlertDg=null;
    private NotificationHelper notificationHelper;

    private String cameraId;
    private  String offlineFile= DataConstant.offlineModeFile;
    String promoterID ;
    protected CameraDevice cameraDevice;
      SharedPrefData SharedPrefData;
    protected  int cameraFacing;
    protected CameraManager cameraManager;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
   private  int flagClosecamera=0;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private  final  int imageFormat=ImageFormat.JPEG;

    /* permission */
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    // lists for permissions
    PermissionCheck permissionCheck;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    // log firebase realtime;
    RealFireBase realFireBase ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_api);
        picImageView =findViewById(R.id.pic_imageView);
        textureView = findViewById(R.id.texture);

        assert textureView != null;
        timeHelper=new TimeHelper(CameraApi.this);
        cameraManager =(CameraManager) getSystemService(Context.CAMERA_SERVICE);
        SharedPrefData =new SharedPrefData(this);
         promoterID = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase);

        gpsHelper = new GpsHelper(this);
        notificationHelper = new NotificationHelper(this);

        cameraLoadingPBar=findViewById(R.id.camera_progressBar);
        cameraFacing = CameraCharacteristics.LENS_FACING_FRONT;   //front camera

        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton =  findViewById(R.id.btn_takepicture);
        closeCameraButton=findViewById(R.id.close_camera_floatAButton);
        assert takePictureButton != null;
        dialogAll = new DialogAll(CameraApi.this);


           // log firebase realtime


            realFireBase = new RealFireBase(this);


        // --------------------------------New permission

        //checkDrawOverPermission();

        permissionCheck = new PermissionCheck(CameraApi.this, ALL_PERMISSIONS_RESULT);
        permissions = permissionCheck.permissionListCheckBreak();       // all permission needed
        permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            permissionsToRequest = permissionCheck.permissionsToRequestM(permissions);

            if (permissionsToRequest.size() > 0)
            {
                intentGoActivity(MainActivity.class,222222);

            }
        }


        takePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                takePictureButton.setEnabled(false);
                mainAsynctask = new MainAsynctask(CameraApi.this,-1);
                connectFlag = 1;
                if(!mainAsynctask.isLocationEnabled(CameraApi.this))
                {

                    gpsAlertDg =  dialogAll.showGPSDisabledAlertToUser();
                    gpsAlertDg.show();

                }
                else if(!mainAsynctask.isConnected())
                {
                    takePictureButton.hide();
                   // dialogAll.dialogMsgConnect();

                    connectFlag = 2;
                }

                    if(mainAsynctask.isLocationEnabled(CameraApi.this))
                    {


                        takePicture();      // location handle
                    }
            }
        });
        closeCameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(cameraDevice!=null)
                {
                    cameraDevice.close();
                }
                Intent i = new Intent(CameraApi.this,MainActivity.class);
                startActivity(i);
            }
        });
        if(flagClosecamera!=0)
        {


            Intent i = new Intent(CameraApi.this,MainActivity.class);
            startActivity(i);
        }

    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            if(cameraDevice!=null)
            {
                cameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {

          //  stopBackgroundThread();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            if(cameraDevice!=null)
            {
                cameraDevice.close();

            }else
            {
              //  openCamera();   // boy frozen error
            }

        }
    };


    protected void startBackgroundThread() {

        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }

    protected void takePicture()
    {

        if ( cameraDevice == null)
        {
            Log.e(TAG, "cameraDevice is null");
            return;
        }

        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (
                    characteristics != null) {

                //catch  get image jpeg
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(imageFormat);

            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null) {
                width = jpegSizes[1].getWidth();
                height = jpegSizes[1].getHeight();

            }
            Log.i("hight image ", "" + height);

            ImageReader reader = ImageReader.newInstance(width, height, imageFormat, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.set(CaptureRequest.JPEG_THUMBNAIL_QUALITY, (byte) 50);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {

                    // save base64 image
                    saveImage(reader);
                    String encodeImage = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.imageSP);
                    final Bitmap imageProfile = getEncodeImageProfileToDecodeBitmap(encodeImage);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            picImageView.setImageBitmap(imageProfile);
                            picImageView.setVisibility(View.VISIBLE);
                            textureView.setVisibility(View.GONE);
                        }
                    });

                    //-------------------------test post json
                     promoterID = SharedPrefData.getElementValue(DataConstant.promoterDataNameSpFile, DataConstant.agencyIDJsonKeyUpcase);

                    promoter = new Promoter(CameraApi.this, encodedImageBaseSixFour, promoterID);
                    url = DataConstant.serverUrl + DataConstant.clockingControl + DataConstant.postOnlineClockingAction;
                    networkRequestPr = new NetworkRequestPr(CameraApi.this, "CameraApi");
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                cameraLoadingPBar.setVisibility(View.VISIBLE);
                                takePictureButton.hide();
                                closeCameraButton.hide();

                            }
                        });

                        String t = extras.getString(checkTypeEnter);
                        if (t.equals("check in")) {
                            checkType = Integer.parseInt(DataConstant.checkInType);
                            String timeStage = timeHelper.convertToHHMM(timeHelper.getTimeNow());
                            SharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.timeStageKeySp, timeStage);
                            SharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.hasLateFormKey, false);
                            SharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.bodyFormLate, "");


                            timeHelper.saveArrivedAt();


                            if (connectFlag == 1) {

                                Map<String, String> Inpostbody = promoter.postCheckInDataBody(connectFlag);
                                JSONObject jsonJos = new JSONObject(Inpostbody);
                                String jsonStr = jsonJos.toString();
                                realFireBase.sendLogFB("checkIn","checkin online",url, new HashMap<String, String>(Inpostbody));

                                networkRequestPr.postRequestVolley(url, Inpostbody, checkType, MainActivity.class, "  upLoading .....");

                            } else if (connectFlag == 2) //offline
                            {
                                promoter.saveOfflineData(connectFlag, DataConstant.checkInType);

                            }
                            SharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.hasReportKey, false);


                        } else if (t.equals("check out")) {
                            checkType = Integer.parseInt(DataConstant.checkOutType);
                            timeHelper.saveLeaveAt();
                            String timeStage = timeHelper.convertToHHMM(timeHelper.getTimeNow());
                            SharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.timeStageKeySp, timeStage);
                            gpsHelper.stopGpsRecever();
                            SharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.locationFlag, false);
                            SharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.stillRunning, false);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.cancelAll();

                            if (connectFlag == 1) {

                                Map<String, String> Outpostbody = promoter.postCheckOutDataBody(connectFlag);
                                realFireBase.sendLogFB("checkOut","action online",url, new HashMap<String, String>(Outpostbody));


                                networkRequestPr.postRequestVolley(url, Outpostbody, checkType, MainActivity.class, "upLoading .....");

                            } else if (connectFlag == 2) //offline
                            {
                                promoter.saveOfflineData(connectFlag, DataConstant.checkOutType);

                            }


                        } else if (t.equals("Break in")) {

                            checkType = Integer.parseInt(DataConstant.breakInType);

                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.cancelAll();

                            if (connectFlag == 1) {
                                Map<String, String> BreakInbody = promoter.postBreakInDataBody(connectFlag);
                                realFireBase.sendLogFB("breakIn","action online",url, new HashMap<String, String>(BreakInbody));

                                networkRequestPr.postRequestVolley(url, BreakInbody, checkType, MainActivity.class, " upLoading .....");

                            } else if (connectFlag == 2) //offline
                            {
                                promoter.saveOfflineData(connectFlag, DataConstant.breakInType);

                            }
                        } else if (t.equals("Break out")) {
                            checkType = Integer.parseInt(DataConstant.breakOutType);
                            if (connectFlag == 1) {

                                Map<String, String> BreakOutbody = promoter.postBreakOutDataBody(connectFlag);
                                realFireBase.sendLogFB("breakOut","action online",url, new HashMap<String, String>(BreakOutbody));

                                networkRequestPr.postRequestVolley(url, BreakOutbody, checkType, MainActivity.class, "upLoading .....");

                            } else if (connectFlag == 2) //offline
                            {
                                promoter.saveOfflineData(connectFlag, DataConstant.breakOutType);

                            }


                        } else if (t.equals("Still There")) {

                            checkType = Integer.parseInt(DataConstant.stillThereType);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.cancelAll();
                            notificationHelper.createNotification("still There", "You clicked \"still there\" at this time", "258963", false, "Still There List ", 2222, false, false, R.color.success_Green, R.mipmap.checked, true);
                            if (connectFlag == 1) {
                                Map<String, String> StillTherebody = promoter.postStillThereDataBody(connectFlag);
                                realFireBase.sendLogFB("stillThere","press action online",url, new HashMap<String, String>(StillTherebody));

                                networkRequestPr.postRequestVolley(url, StillTherebody, checkType, MainActivity.class, "upLoading .....");
                            } else if (connectFlag == 2) //offline
                            {
                                promoter.saveOfflineData(connectFlag, DataConstant.stillThereType);
                            }
                            SharedPrefData.putElementBoolean(DataConstant.promoterDataNameSpFile, DataConstant.StillThereisClked, true);

                        }


                    } else {
                        Log.i(TAG, "onImageAvailable: extra null");
                    }

                    //---------------------------------------------------


                }


            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    SharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.imageSP, encodedImageBaseSixFour);


                    createCameraPreview();
                }

            };


            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);


                    } catch (CameraAccessException e) {
                        if (cameraDevice != null) {
                            cameraDevice.close();

                        }
                        intentGoActivity(MainActivity.class, 555554);

                    }

                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            if (cameraDevice != null) {
                cameraDevice.close();

            }
            intentGoActivity(MainActivity.class, 555554);

        }

    }


    private String convertToBaseString(ImageReader reader) {     //base64
        Image image = null;
        String baseSixFour;
        image = reader.acquireLatestImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,null);
           int imageHight =myBitmap.getHeight() ;
           int imageWidth =myBitmap.getWidth() ;
        Log.i(TAG, "convertToBaseString:  " +"hight : "+imageHight+" , width : "+imageWidth);

        if(imageHight>240 && imageWidth>320) {
            // create a matrix object
                        Matrix matrix = new Matrix();
                        matrix.postRotate(-90); // anti-clockwise by 90 degrees
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, (int) (imageWidth*0.5), (int) (imageHight*0.5), true);
            // create a new bitmap from the original using the matrix to transform the result
                        myBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            // display the rotated bitmap
        }else
        {
            // create a matrix object
            Matrix matrix = new Matrix();
            matrix.postRotate(-90); // anti-clockwise by 90 degrees
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, (int) (imageWidth), (int) (imageHight), true);
            // create a new bitmap from the original using the matrix to transform the result
            myBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            // display the rotated bitmap
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        baseSixFour=Base64.encodeToString(byteArray, Base64.DEFAULT);

        if ( image != null )
        {
            image.close();
        }
        return baseSixFour;


    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                  //  Toast.makeText(CameraApi.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {

            if(cameraDevice==null) // boy error camera frozen
           {
               openCamera();
           }

        }
    }

    private void openCamera() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try
        {
            cameraId = getCameraFrontId();//manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(CameraApi.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            cameraManager.openCamera(cameraId, stateCallback,null);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG,"Not Found Camera 2");
        }
        Log.e(TAG, "openCamera X");
    }

    private String getCameraFrontId(){
        String camerFrontId="not found";
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    camerFrontId=cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return camerFrontId;
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        }catch(CameraAccessException e) {
            e.printStackTrace();
        }catch(IllegalStateException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");


        startBackgroundThread();

          if(!mainAsynctask.isLocationEnabled(CameraApi.this))
          {
              gpsAlertDg =  dialogAll.showGPSDisabledAlertToUser();
              gpsAlertDg.show();
          }


          if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause()
    {
        Log.e(TAG, "onPause");
        cameraLoadingPBar.setVisibility(View.GONE);

       if(cameraDevice!=null)
       {
           cameraDevice.close();
       }
        stopBackgroundThread();
        if(gpsAlertDg!=null)
        {
            gpsAlertDg.dismiss();
            gpsAlertDg.cancel();


        }

        super.onPause();
    }


    public void saveImage(ImageReader reader)
    {


        encodedImageBaseSixFour = convertToBaseString(reader);

        SharedPrefData.putElement(DataConstant.promoterDataNameSpFile, DataConstant.imageSP, encodedImageBaseSixFour);



    }



    public Bitmap getEncodeImageProfileToDecodeBitmap(String encodedImage) {


        if (encodedImage == null) {
            return null;
        }
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!permissionCheck.hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            permissionAlertAllRequest();
                            return;
                        }
                    }
                }

                break;
        }
    }

    public void permissionAlertAllRequest() {
        new androidx.appcompat.app.AlertDialog.Builder(this).
                setMessage("These permissions are mandatory to get your location. You need to allow them.").
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissionsRejected.
                                    toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                        }
                    }
                }).setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        finish();
                      finishAffinity();
                        System.exit(0);
                    }
                }).create().show();

    }





    public  void intentGoActivity (Class whereYouGo,int extraTabcheckType)
    {
        if (whereYouGo != null)
        {
            String jumpExtraTab=DataConstant.jumpTab;
            Intent i = new Intent(CameraApi.this, whereYouGo);
            if(extraTabcheckType !=  Integer.parseInt(DataConstant.stillThereType))
            {
                i.putExtra(jumpExtraTab, String.valueOf(extraTabcheckType));
            }
            CameraApi.this.startActivity(i);

        }
    }


   /* public class   CameraAsycTask extends AsyncTask<Void,Void,Location>
    {
        Promoter promoter;

        CameraAsycTask(Context context )
        {
            promoter=new Promoter(context);
        }

        @Override
        protected Location doInBackground(Void... voids) {


            return  promoter.searchCurrentLocation();
        }

        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);
        }
    }
*/
}