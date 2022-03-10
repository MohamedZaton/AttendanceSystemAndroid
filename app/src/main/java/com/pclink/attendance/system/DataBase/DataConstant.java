package com.pclink.attendance.system.DataBase;

import com.pclink.attendance.system.R;

import org.w3c.dom.Comment;

public class DataConstant
{
    // pwd PclinkP@ssw0rd2019
    // url path Route
    public static final String APP_Msg_ID = "2CD6B177-C450-4953-96EF-4F12E06455CB"; // sendbird
    public   final    static  String versionUrl="https://api.myjson.com/bins/penyg";
    public   final    static  String versionOldKey="versionOld";
    public   final    static  String versionInstalled="versionInstalled";
    public   final    static String serverUrl="http://40.89.167.7/EATAPI/api/";
    public   final    static  String superUrl = "user/GetSaffSupervisorByAgencyID/";
    public   final    static   String LoginAuthPostControl=  "Authorization/AccessToPromoterV2/";
    public   final    static   String clockingControl=  "Clocking/";
    public   final    static   String postGenerateUrl=  "Form/GenerateFormLink/";
    public   final    static   String postOnlineClockingAction= "PromoterOnlineClocking/";
    public final  static  String getVacationDaysUrl="LeavingTimeOff/RemaingVacations/";
    public  final  static String vacLisJsonKey = "vacLisJsonKey";
    public   final    static String  hasReportKey = "hasReportKey";
    public   final    static String  hasLateFormKey = "hasLateFormKey";

    // fake Location request
    public   final    static  String fakeLoctUrl = "log/AddFackeLocationLog";
    public   final    static  String fakeLoctBodyOffline = "fakeLoctBody";

    // supervisor info
    public  final  static  String supervisorID = "supervisorID";
    public  final  static   String  exGetControl ="LeavingTimeOff/",
            exGetAction = "GetExcuseList/";

     public  final  static   String  ptGetControl ="ProductLine/",
            ptGetAction = "GetSalesProductList/"
             ,prodSkuId = "/1",compSkuId="/2";

    public  final  static   String  exPostControl ="LeavingTimeOff/",
            exPostAction = "CreateExcuse/";
    //vacation route

    public  final  static   String  vacGetControl ="LeavingTimeOff/",
            vacGetAction = "GetVacationListApp/";
    //mission route

    public  final  static   String  missionGetControl ="LeavingTimeOff/",
            missionGetAction = "CreateMission/";

    public  final  static   String  vacPostControl ="LeavingTimeOff/",
            vacPostAction = "CreateVacationRequest";

    public  final  static  String getVacationDays = "LeavingTimeOff/RemaingVacations/";
    public  final  static  String saveExcuseIdjson = "saveExcuseId";
    public  final  static  String saveExcuseTxtjson = "saveExcuseTxt";
    public  final  static String vacDaysKey = "vacDaysKey";

    // chat notification api
    public   final static   String postChatUrl=  "chat/";

    // log route
    public  final static String getLogAction = "log/";
    public final static String[] arrayListVacLogKeys = {"DateFrom","DateTo","AgencyID","VacID","Status"};
    public final static String[] arrayListExcLogKeys = {"EXMessage","Date","AgencyID","EXID","Status"};
    public final static String[] arrayListVacLogKeyTemps = {"dateFrom","dateTo","agencyID","vacID","status"};
    public final static String[] arrayListExcLogKeyTemps = {"exMessage","date","agencyID","exid","status"};

    // log json keys
    // check Data
    public  final static String checkDataKeyArrayLog = "checkdata" , excKeyArrayLog = "excuses" , vacKeyArrayLog = "vacations";
    public  final static String arrayLogListjsonKey[] = {"lat","lng","checkType","checkTime","checkDate"};
    public final static  String  dateLogString = "Data : ",timeLogString ="Time : " , locationLogString= "Location : ";


    // json Key data server
    // check , break , still keys

    public   final static String agencyIDJsonKeyUpcase="AgencyID";
    public   final static String checkInIDJsonKey="checkinID";
    public   final static String checkTypeJsonKey="CheckType";
    public   final static String stillThereStatus="StillThereStatus";
        public   final static String imageJsonKey="Image";
    public   final static String latLocationJsonKey="Lat";
    public   final static String lngLocationJsonKey="Lng";
    public   final static String nameLocationJsonKey="Location";
    public   final static String timeMilsGps="timeGps";
    public   final static String isUseMockFakeLoctkey="isUseMockFakeLoct";


    // Excise  keys  json + shared pref
    public   final static String  exNameListKey = "name" , exIdListKey =  "id", exMessageKey = "EXMessage"  , exDateKey="Date" , exId ="EXID";/*agencyIDJsonKeyUpcase*/

    public final static String[] arrayExcuseListjsonKey ={"EXMessage","Date","AgencyID ","EXID"};
    public static final String LogOutClk = "LogOutClK";

    public static String[] arrayListExcuse = {"id","name"};
    public static String[] arrayListMission = {"id","name"};

        // Location flag

    public final static  String locationFlag = "locationSwatch";

    // vacation  keys  json + shared pref
    public final static String vacDaysCikKey = "daysleft";
    public  final static String vacNameListKey = "type" , vacIdListKey =  "id",vacDateFromKey="DateFrom",vacDateToKey="DateTo",vacIdKey = "VacID";
    public final static String[] arrayVacationListjsonKey ={"id","type","enabled"};
    public final static String[] arrayVacationPostjsonKey ={"DateFrom","DateTo","AgencyID","VacID"};
    public  final  static  String saveVacationIdjson = "saveVacationId";
    public  final  static  String saveVacationTxtjson = "saveVacationTxt";

    //--------

    // json Value data server
    public   final static String checkInType="1";
    public   final static String checkOutType="2";
    public   final static String breakInType="3";
    public   final static String breakOutType="4";
    public   final static String stillThereType="5";

    // sharedPreferences data

    public  final  static  String checkToggleButtonSP="checkToggleButton";
    public  final  static  String breakToggleButtonSP="breakToggleButton";

    public  final  static  String imageSP="encodedImageBaseSixFour";
    public   final static   String promoterDataNameSpFile="promoterData";

    public  final static String otherDataFile="otherDatafile";
//    public   final static String promoterIdKey="promoterId";
    public final static String stillRepeatingJsonKey = "stillRepeating";
    public   final static   String workingHoursJsonKey="workingHours";
    public   final static   String stillDurationJsonKey="stillDuration";
    public static final String StillThereisClked ="stillisClked" ;
    public static final String stillRunning ="stillRunning" ;



    public   final static String promoterUrlPathKey="promoterUrlpath";
    public  final  static  String  chackInFlagSp = "checkInFlag";
    public  final  static  String  checkOutFlagSp = "checkOutFlag";
    public  final  static  String  breakInFlagSp = "breakInFlag";
    public  final  static  String  breakOutFlagSp = "breakOutFlag";
    public  final  static String stillThereOutAppkey="stillThereOutApp";
    public static final String stillNotificationKeySp ="stillNotification";
    public  final static String timeStageKeySp="timeStage";
    public static final int NotificationThreadReceverId = 11;
    public static final int GpsReceverId = 15;

    public static final String CheckInTimeSp = "checkInAt";
    public static final String CheckOutTimeSp = "checkOutAt";
    public static final String stillNotificationTimeKeySp = "stillNotificationTimeSp";
    public static String idNotifyStillKey = "idNotifyStill";


    public static String stillThereRunKeysp = "stillThereRunGate";
    public static String promoterUserName="nameUser";
    public static String wrongLoginMsgKey="message";
    public final static String jumpTab = "jumpTab";

    //------------------------ job id smart Scheduler

    public  final  static int   still_there_thread_JOB_ID= 1;     //still there
    public  final  static int    location_thread_JOB_ID= 2;     //gps location search


    //------------
    // dialog permission
    public static final String  skipMsgStillThereSp="skipMessageStillThere";
    public static final String OVERLAY_PERMISSION = "overplayPermission" ;
    public static final String  allowValue="allow";
    public static final String  denyValue="deny";

    // flag  check  wheel needed
    public final static String pauseCheckWheelSp   = "pauseCheckWheel";
    public final static String resumeCheckWheelSp  = "resumeCheckWheel";
    public final static String startCheckWheelSp   = "startCheckWheel";
    public final static String stopCheckWheelSp    = "stopCheckWheel";
    public   final  static  String  breakInTimeSp = "breakInTime";

    public  static String pausebreakInWheelKey = "pausebreakWheelLong";
    public final static String resumebreakInWheelKey = "resumebreakWheelLong";



        // offline

    //route

    public final static String offlineControl  ="Clocking/" ,  offlineAction= "PromoterOfflineClocking/";


    public final static String offlineModeFile = "offlineMode";
    public  final static String isSendOfflineFlag = "offlineFlag";
    public final static String stillOfflineCount = "stillOfflineCount";
    public final static String breakOutOfflineCount = "breakOutOfflineCount";
    public final static String breakInOfflineCount = "breakInOfflineCount";
    public final static String stillOffline = "stillOffline";
    public final static String checkInOffline = "checkInOffline";
    public final static String checkOutOffline = "checkOutOffline";
    public final static String breakOutOffline = "breakOutOffline";
    public final static String breakInOffline = "breakInOffline";
    public final static String indexUploadOffline="indexUpload";
    public static String recordOfflineKeys = "recordOffline";



    // tabs icons and text labels


    public  final  static int[]  navIconsActive = {
            R.drawable.ic_check_box_blueo_24dp,
            R.drawable.ic_free_breakfast_blue_24dp,
            R.drawable.ic_notifications_blue_24dp,
            R.drawable.ic_assignment_blue_24dp

    };
    public  final  static int[] navLabels =
            {
                    R.string.nav_home_check,
                    R.string.nav_break,
                    R.string.nav_log,
                    R.string.nav_request
            };
    // another resouces array for active state for the icon
    public  final  static int[] navIcons = {
            R.drawable.ic_check_box_blacko_24dp,
            R.drawable.ic_free_breakfast_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_assignment_black_24dp
    };


    public static String checkTimeJsonKey = "CheckTime";
    public static String checkDateJsonKey = "CheckDate";

    // Report data
    // Sku Code list
    public final static String storeNameSp="storeName";

    public final static String skuFileSp="skuFile";
    public final static String skuProListJsonKey="skuProListJsonKey";
    public final static String skuCompListJsonKey="skuCompListJsonKey";

    public final static String sizeSkuList="sizeSkuList";
    public final static String reportStoreOffline="reportStore";

    public final static String foundsku="foundsku";
    public final static String itemsIdSku="itemIdSku";
    public final static String itemsNameSku="itemNameSku";
    public final static String itemsCodeSku="itemCodeSku";

    public final static String countProdtHdn="countProdtHdn";
    public final static String priceProdtHdn="priceProdtHdn";

    public final static String countCompHdn="countCompHdn";
    public final static String priceCompHdn="priceCompHdn";



   // public final static String salesFormIdKey="salesFormID";
    public final static String salesFromClkKey="SalesFrom";
    public final static String bodyFormLate="bodyFormLate";
    public final static String saveSettingSalesFormKey="SalesFrom";
    public final static String reportDateKeyOffline="Date";

    public final static String salesRepUrl= "clocking/SalesReportForm";
    public final static String offlineSalesRepUrl= "clocking/SalesReportFormOffline";

    // setting sales
    public final static String getSettingSalesUrl = "Form/";


    // post body json  data
    public final static String TotalSoldRp="TotalSold";
    public final static String NumberBuyerRp="NumberBuyer";
    public final static String StoreTrafficRp="StoreTraffic";
    public final static String MainCommentsRp="MainComments";
    public final static String NumberShopperRp="numberOFShopper";
    public final static String myProductsRp="Products";
    public final static String compProductRp="CompProducts";
    public final static String ModelNameRp="ModelName";
    public final static String ItemsRp="Items";
    public final static String ProductIDRp="ProductID";
    public final static String priceRp="price";
    public final static String ImageRp="Image";
    public final static String commentRp="Comment";
     // report get request keys
    public static String[] arrayListReport = {"id","productName","sku"};

    // files
    public final  static  String getAgencyPackageByAgencyID="files/getAgencyPackageByAgencyID/";

    // attachment files keys
    public  static  String ResponseIDKey = "ResponseIDKey";
    public  static  String fileSizeKey = "fileSizeKey";
    public  static  String fileAmountKey = "fileAmountKey";
    public  static  String fileStatesKey = "fileStatesKey";
    public  static  String isEndDayKey = "isEndDayKey";

}
