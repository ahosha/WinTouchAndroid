package apps.radwin.wintouch.screenManagers;

// Project written by: Shay Vidas - shay3112@gmail.com
// Version 0.1
// Created by shay_v on 20/04/2016.
// Imports all the ncesery Packages

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import apps.radwin.wintouch.activities.alignmentActivities.Connect_To_Wif_iActivity;
import apps.radwin.wintouch.activities.alignmentActivities.ReportActivity;
import apps.radwin.wintouch.activities.alignmentActivities.WorkorderSetSettings;
import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.interfaces.HttpCommunicationInterface;
import apps.radwin.wintouch.interfaces.RetrofitInterface;
import apps.radwin.wintouch.interfaces.WiFiConnectionInterface;
import apps.radwin.wintouch.models.BandsModel;
import apps.radwin.wintouch.models.BestPositionListModel;
import apps.radwin.wintouch.models.CommunicationModels.AligmentActionPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.AuthenticationPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.CommonResponseModel;
import apps.radwin.wintouch.models.CommunicationModels.FinalDataPayloadModel;
import apps.radwin.wintouch.models.CommunicationModels.SetBandAligmentModel;
import apps.radwin.wintouch.models.ExpendebleMenuPositionModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.models.pojoModels.pojoAligmentData.AligmentDataPojo;
import apps.radwin.wintouch.models.pojoModels.pojoAuthentication.AuthenticationPojo;
import apps.radwin.wintouch.models.pojoModels.pojoBestPosition.BestPositionPojo;
import apps.radwin.wintouch.models.pojoModels.pojoCurserLocation.CurserLocationPojo;
import apps.radwin.wintouch.models.pojoModels.pojoFineAligment.FineAligmentPojo;
import apps.radwin.wintouch.models.pojoModels.pojoGetAllBands.GetBandsPojo;
import apps.radwin.wintouch.models.pojoModels.pojoGetEvaluationData.EvaluationResultsPojo;
import apps.radwin.wintouch.models.pojoModels.pojoInit.Data;
import apps.radwin.wintouch.models.pojoModels.pojoInit.PojoInit;
import apps.radwin.wintouch.models.pojoModels.pojoLinkState.pojoLinkState;
import apps.radwin.wintouch.models.pojoModels.pojoSetBandCallback.PojoSetBand;

import static java.lang.Integer.parseInt;

/**
 * The aligment manager will be tha main manager that will hold all the states and will know how to change between states and do diffrent things at each state.
 * The aligment manager will also include the projects selection screen and the project report screen
 */
public class AligmentManager {

    //STATES
    int currentState = 0;
    static final int READY_FOR_BARCODE_SCAN_STATE = 1;
    static final int WAITING_FOR_MANUAL_BARCODE_INPUT = 2;
    static final int SCANNING_BARCODE_STATE = 3;
    static final int CHECKING_WIFI_STATE = 4;
    static final int CONNECTING_TO_WIFI = 5;
    static final int APPLICATION_INTERUPTED = 6;
    static final int INIT_ALIGMENT_SCREEN = 7;
    static final int STARTED_FIRST_ALIGMENT_SCAN = 8;
    static final int INITLIZED_BEST_POSITION_ALIGMENT = 9;
    static final int ON_ALIGMENT_SCREEN_EXIT = 10;
    static final int INITLIZED_FINE_ALIGMENT = 11;
    static final int INITIATED_TESTINGS_ACTIVITY = 12;
    private boolean initialized;

    private static GeoLocationWrapper geoLocationWrapper;

    /**
     * set state will change the state of the aligment manager,
     * for now it only logs in a message on screen that a state has been changed and logs what state has been changed
     *
     * @param state state is the new state we are changing to
     */
    public void setState(int state) {
        switch (state) {

            case READY_FOR_BARCODE_SCAN_STATE:
                Log.d("aligmentManager", "State: " + "READY_FOR_BARCODE_SCAN_STATE");

                break;


            case WAITING_FOR_MANUAL_BARCODE_INPUT:
                Log.d("aligmentManager", "State: " + "WAITING_FOR_MANUAL_BARCODE_INPUT");

                break;


            case SCANNING_BARCODE_STATE:
                Log.d("aligmentManager", "State: " + "SCANNING_BARCODE_STATE");

                break;


            case CHECKING_WIFI_STATE:
                Log.d("aligmentManager", "State: " + "CHECKING_WIFI_STATE");

                break;


            case CONNECTING_TO_WIFI:
                Log.d("aligmentManager", "State: " + "CONNECTING_TO_WIFI");

                break;


            case APPLICATION_INTERUPTED:
                Log.d("aligmentManager", "State: " + "APPLICATION_INTERUPTED");

                break;


            case INIT_ALIGMENT_SCREEN:
                Log.d("aligmentManager", "State: " + "INIT_ALIGMENT_SCREEN");

                break;

            case STARTED_FIRST_ALIGMENT_SCAN:
                Log.d("aligmentManager", "State: " + "STARTED_FIRST_ALIGMENT_SCAN");

                break;

            case ON_ALIGMENT_SCREEN_EXIT:
                Log.d("aligmentManager", "State: " + "ON_ALIGMENT_SCREEN_EXIT");

                break;

            case INITLIZED_BEST_POSITION_ALIGMENT:
                Log.d("aligmentManager", "State: " + "INITLIZED_BEST_POSITION_ALIGMENT");

                break;

            case INITLIZED_FINE_ALIGMENT:
                Log.d("aligmentManager", "State: " + "INITLIZED_FINE_ALIGMENT");

                break;

        }
    }


    //sets the current state

    /**
     * change state willl log the old state, and adress the new state in the set staate function
     *
     * @param state state is the new state we are changing to
     */
    public void changeState(int state) {
        currentState = state;
        setState(currentState);
    }


    ///////////////////////
    //Local veriabls to work with
    ////////////////////////
    Timer firstAligmentTimer, fineAlignmentPonterLocationTimer, fineAlignmentTimer, updateEvaluationLinkTimer, getEvaluationResultsTimer, curserLocationMainAlginmentTimer;

    ArrayList<BestPositionListModel> arrayOfBaseStationsAligmentManager = new ArrayList<BestPositionListModel>();

    List<BandsModel> supportedDeviceBands;

    String TAG = "AlignemtntManager";

    HttpCommunicationService _service;

    private static String bestHsuElevation = "not initilized";

    private static int bestHsuCellNumber = 9999;

    private String currentBandIdFromDevice;
    private int currentCbwFromDevice;

    private int AllBandsCallDataTryouts = 0;

    private boolean isAlignmentScreenActive = true;

    ///////////////////////
    //inputsFromScreens
    ////////////////////////


    // C-TOR
    public AligmentManager() {
        _service = new HttpCommunicationService();
        _service.Start();
    }

    /**
     * function is called when the Fine_Aligment_Activity is being called, and is entering it's onCreate Stage
     * it changes the state to INITLIZED_FINE_ALIGMENT, and also starts the fine aligment loop to do REST calls for the fine aligment Function
     *
     * @param callbackForFineAlignmentInterface
     * @param pointerLocationFunction
     * @param applicationContext
     */
    public void initializedFineAlignment(HttpCommunicationInterface<FineAligmentPojo> callbackForFineAlignmentInterface, HttpCommunicationInterface<CurserLocationPojo> pointerLocationFunction, final Context applicationContext) {

        changeState(INITLIZED_FINE_ALIGMENT);

        // starts the fine alignment loop
        ////////////////////////////////////
        startFineAlignmentLoop(callbackForFineAlignmentInterface, applicationContext);

        // starts the PointerLocation
        ////////////////////////////////////
        fineAlignmentPointerLocationLoop(pointerLocationFunction, applicationContext);
    }


    /**
     * Happens when screen aligmentScanActivity is finished the create method
     * happens when the aligment screen is initlized for now it starts the cursor location loop, and the aligment scan loop
     * it is initating the aligment screen logic, and is acting as the main buisness logic for the first part of the aligment process
     * it also changes the state to STARTED_FIRST_ALIGMENT_SCAN
     *
     * @param callbackForAlignmentScanRetrofit
     * @param applicationContext
     */
    public void initilizedAligmentScanActivity(HttpCommunicationInterface callbackForAlignmentScanRetrofit, Context applicationContext) {

        changeState(STARTED_FIRST_ALIGMENT_SCAN); // changes the state for the start of alignment

        startAlignmentScanRetrofitLoop(callbackForAlignmentScanRetrofit, applicationContext); // starts the alignment retrofit scan (ignite the web Rest call)

    }


    /**
     * user pressed next button from alignment screen, and finishes the measuring
     *
     * @param bestPositionCallback
     * @param selectedWorkorderId
     * @param applicationContext
     */
    public void alignmentScanHbsFound(HttpCommunicationInterface<ArrayList<BestPositionListModel>> bestPositionCallback, String selectedWorkorderId, Context applicationContext) {

        changeState(ON_ALIGMENT_SCREEN_EXIT);

        // stops the first alignment timer
        ///////////////////////////////
        stopFirstaligmentTimer();

        //action finish to finish alignment
        ///////////////////////////////
        SendFinishAlignmentRequest();

        // get best HBS list
        ////////////////////////////////
        getBestPosition(bestPositionCallback, selectedWorkorderId, applicationContext);

    }

    /**
     * this functino will happen once all data has been saved on the sql
     * after the user has pressed save button from the frequencys dialog he will eventually come to this function
     * this function will also start the post to set the badn at the server
     *
     * @param networkId
     * @param callbackForonSavedDataInSql the first callback that will be called once the sql data will be saved
     * @param selectedBandIdFromPopup     the selected band the user choose on the ui level
     * @param selectedBandWidthFromPopup  the selectect bandWidth the user choose on the ui level
     * @param frequencyStringList         the selectect Frequencys the user choose on the ui level they come as an array list
     */
    public void savedDataOnSql(String networkId, WorkorderSetSettings.onPostToBoxInterface callbackForonSavedDataInSql, String selectedBandIdFromPopup, String selectedBandWidthFromPopup,
                               ArrayList<String> frequencyStringList, List<Integer> allowableFrequencies) {
        //Handle special treatment for HBS Beacons
        List<Integer> extraChannelsList = new ArrayList<>();
        Collections.sort(frequencyStringList);
        for (int i = 0; i < frequencyStringList.size(); i++) {
            Integer prevChannel = parseInt(frequencyStringList.get(i)) - 10;
            Integer nextChannel = parseInt(frequencyStringList.get(i)) + 10;
            if (!extraChannelsList.contains(prevChannel) && allowableFrequencies.contains(prevChannel)) {
                extraChannelsList.add(prevChannel);
            }
            if (!extraChannelsList.contains(parseInt(frequencyStringList.get(i)))) {
                extraChannelsList.add(parseInt(frequencyStringList.get(i)));
            }
            if (!extraChannelsList.contains(nextChannel) && allowableFrequencies.contains(nextChannel)) {
                extraChannelsList.add(nextChannel);
            }
        }

        ArrayList<String> newFrequencyStringList = new ArrayList<>();

        for (int i = 0; i < extraChannelsList.size(); i++) {
            newFrequencyStringList.add(extraChannelsList.get(i).toString());
        }

        postSetBand(networkId, callbackForonSavedDataInSql, selectedBandIdFromPopup, selectedBandWidthFromPopup, newFrequencyStringList); // passes the callback to the post set band and call the set band option

    }


    /**
     * this function is just updateding a state to connect to wifi
     */
    public void initlizedConnectingToWifi() {
        changeState(CONNECTING_TO_WIFI);
    }


    /////////////////////
    //PRIVATE FUNCTIONS//
    /////////////////////

    //GETTERS SETTERS FOR PRIVATE VARIABLES
    public static Boolean IS_ALIGMENT_DEBUG_ON() {
        return IS_ALIGMENT_DEBUG_ON;
    }

    /**
     * starts alignment action when we want to start the alitnment
     *
     * @param action                          what actino we want to iniate
     * @param callbackForstartAlignmentAction the callback for the function
     */
    public void startAlignmentAction(String action, HttpCommunicationInterface<Boolean> callbackForstartAlignmentAction) {

        if (action.equals("start")) {
            SendStartAlignmentRequest(callbackForstartAlignmentAction);
        }
    }

    /**
     * posts the set band from set bands screen
     *
     * @param networkId                   network id to post
     * @param callbackForonSavedDataInSql callback for saved data in sql
     * @param selectedBandIdFromPopup     selected id from popup
     * @param selectedBandWidthFromPopup  selected bandwidth from popup
     * @param frequencyStringList         selected frequecnty list
     */
    private void postSetBand(String networkId, final WorkorderSetSettings.onPostToBoxInterface callbackForonSavedDataInSql, final String selectedBandIdFromPopup, String selectedBandWidthFromPopup, ArrayList<String> frequencyStringList) {

        int retryCount = 3;

        SetBandAligmentModel setBandData = new SetBandAligmentModel(networkId, selectedBandIdFromPopup, selectedBandWidthFromPopup, frequencyStringList);

        _service.SetRequest(HttpInterrupts.SET_BAND, setBandData, retryCount, new HttpCallBack<PojoSetBand>() {
            @Override
            public void invocator(HttpResponse<PojoSetBand> result) {
                String response = result != null ? result.Data().getData().getMessage() : "";
                callbackForonSavedDataInSql.onPostToBoxFunction(response);
            }
        });
    }

    /**
     * sends the start aligment action to the server
     *
     * @param callbackForStartAlignmentAction callback function to return
     */
    private void SendStartAlignmentRequest(final HttpCommunicationInterface<Boolean> callbackForStartAlignmentAction) {

        AligmentActionPayloadModel aligmentActionPayloadModel = new AligmentActionPayloadModel("SuperSector12345", "start");
        int retryCount = 3;

        _service.SetRequest(HttpInterrupts.SET_START_ALIGNMENT, aligmentActionPayloadModel, retryCount, new HttpCallBack<PojoSetBand>() {
            @Override
            public void invocator(HttpResponse<PojoSetBand> response) {
                if (response == null) {
                    callbackForStartAlignmentAction.Invocator(false);

                } else {
                    Boolean result = PojoParserHelper.getSetAlignmentCommandsResponse(response.Data());
                    callbackForStartAlignmentAction.Invocator(result);
                }
            }
        });
    }

    /**
     * will send a finish command to the server to finish the alignement
     */
    public void SendFinishAlignmentRequest() {

        AligmentActionPayloadModel aligmentActionPayloadModel = new AligmentActionPayloadModel("SuperSector12345", "start");
        int retryCount = 3;

        _service.SetRequest(HttpInterrupts.SET_FINISH_ALIGNMENT, aligmentActionPayloadModel, retryCount, new HttpCallBack<PojoSetBand>() {
            @Override
            public void invocator(HttpResponse<PojoSetBand> response) {
                if (response == null) {
                    Log.d("alignementManager", "Cannot finish alignement");

                } else {
                    Boolean result = PojoParserHelper.getSetAlignmentCommandsResponse(response.Data());
                    Log.d("alignementManager", "onResponse: Alignment finished with status: " + result);
                }
            }
        });
    }

    /**
     * will send a complete command to the server to finish installation
     *
     * @param callbackForactionCompleteCallback
     */
    public void sendComplete(final ReportActivity.actionCompleteCallbackInterface callbackForactionCompleteCallback) {

        AligmentActionPayloadModel aligmentActionPayloadModel = new AligmentActionPayloadModel("SuperSector12345", "start");
        int retryCount = 3;

        Log.d(TAG, "sendComplete: sending complete action");

        _service.SetRequest(HttpInterrupts.SET_COMPLETE, aligmentActionPayloadModel, retryCount, new HttpCallBack<PojoSetBand>() {
            @Override
            public void invocator(HttpResponse<PojoSetBand> response) {
                if (response == null && response.Data() != null) {
                    Log.d("alignementManager", "Cannot finish alignement");
                    callbackForactionCompleteCallback.actionCompleteCallbackFunction(false);

                } else {

                    try {

                        callbackForactionCompleteCallback.actionCompleteCallbackFunction(true);
                        Boolean result = PojoParserHelper.getSetAlignmentCommandsResponse(response.Data());
                        Log.d("alignementManager", "onResponse: Completed with status: " + result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * will send a finish command to the server to finish the alignement
     *
     * @param callbackForomStartSyncDone
     * @param sectorID
     */
    public void SendStartSyncRequest(final HttpCommunicationInterface<CommonResponseModel> callbackForomStartSyncDone, String sectorID) {

        Log.d(TAG, "SendStartSyncRequest: sector id that we send: " + sectorID);

        //mokcup data , doesn't matter what we send to server
        AligmentActionPayloadModel aligmentActionPayloadModel = new AligmentActionPayloadModel(sectorID, "start");

        _service.SetRequest(HttpInterrupts.SET_START_SYNC, aligmentActionPayloadModel, 1, new HttpCallBack<PojoSetBand>() {
            @Override
            public void invocator(HttpResponse<PojoSetBand> response) {
                if (response == null) {
                    callbackForomStartSyncDone.Invocator(null);
                } else {
                    if (response.Data().getData().getMessage() != null) {
                        Boolean result = PojoParserHelper.getSetAlignmentCommandsResponse(response.Data());
                        CommonResponseModel commonResponseModel = new CommonResponseModel(result, response.Data().getData().getMessage());
                        callbackForomStartSyncDone.Invocator(commonResponseModel);

                    } else {
                        callbackForomStartSyncDone.Invocator(null);

                    }
                }
            }
        });
    }

    /**
     * will stop the cursor location timer loop
     */
    public void stopCurserLocation() {
        try {
            curserLocationMainAlginmentTimer.cancel();
            isAlignmentScreenActive = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //int curserLocationMainAlginmentFailTryouts = 0;

    /**
     * generates authentication post to the server
     *
     * @param unitPassword    the password that comes from the workorder for the authentication
     * @param callbackForAuth
     */
    public void authPost(String ipAddress, String unitPassword, final Connect_To_Wif_iActivity.onAuthInterface callbackForAuth) {

        AuthenticationPayloadModel aligmentActionPayloadModel = new AuthenticationPayloadModel("admin", unitPassword); // just simulates data we don't have to send anything

        int retryCount = 3;

        //update base url from connected wifi
        if (ipAddress != null && !AligmentManager.IS_ALIGMENT_DEBUG_ON()) {
            RetrofitInterface.Factory.changeApiBaseUrl(ipAddress);
        }

        _service.SetRequest(HttpInterrupts.SET_AUTH, aligmentActionPayloadModel, retryCount, new HttpCallBack<AuthenticationPojo>() {
            @Override
            public void invocator(HttpResponse<AuthenticationPojo> response) {
                if (response == null || response.Data() == null) {
                    Log.d("alignementManager", "Cannot get token");
                    callbackForAuth.onAuthFunction(false, "");

                } else {
                    // callback - checks for acess token and passes the user
                    //////////////////////////////////////////////////
                    if (response.Data().getAccessToken() != null) {
                        Log.d(TAG, "invocator: acess token is: " + response.Data().getAccessToken());
                        callbackForAuth.onAuthFunction(true, "pass");
                    } else {
                        callbackForAuth.onAuthFunction(true, "passwordFail");
                    }

                    // sets device token
                    /////////////////////
                    TokenHolder.getInstance().setJwtToken(response.Data().getAccessToken());
                }
            }
        });

    }

    int curserLocationMainAlginmentFailTryouts = 0;

    /**
     * starts the curser location timer to do curser location calls
     *
     * @param pointerLocationFunction callback function for the http requests
     * @param applicationContext
     */
    public void cursorLocationLoopMainAlignment(final HttpCommunicationInterface<CurserLocationPojo> pointerLocationFunction, final Context applicationContext) {

        int startAfterMilliseconds = 250;
        int repeatEachMilliseconds = 250;

        curserLocationMainAlginmentTimer = new Timer();
        curserLocationMainAlginmentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int retryCount = 3;
                _service.GetRequest(HttpInterrupts.COURSER_LOCATION, retryCount, new HttpCallBack<CurserLocationPojo>() {
                    @Override
                    public void invocator(HttpResponse<CurserLocationPojo> response) {
                        CurserLocationPojo data = response != null ? response.Data() : null;
                        pointerLocationFunction.Invocator(data);
                    }
                }, applicationContext);
            }
        }, startAfterMilliseconds, repeatEachMilliseconds);

    }

    //starts the retrofit loop every 200ml\sec to check the server for info and send it to the callback
    Boolean isFineAlignmentScreenActive = true;
    int curserLocationFineAlignmentFail = 0;

    private void fineAlignmentPointerLocationLoop(final HttpCommunicationInterface<CurserLocationPojo> pointerLocationFunction, final Context applicationContext) {

        int startAfterMilliseconds = 250;
        int repeatEachMilliseconds = 250;

        fineAlignmentPonterLocationTimer = new Timer();
        fineAlignmentPonterLocationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int retryCount = 3;
                _service.GetRequest(HttpInterrupts.COURSER_LOCATION, retryCount, new HttpCallBack<CurserLocationPojo>() {
                    @Override
                    public void invocator(HttpResponse<CurserLocationPojo> response) {
                        CurserLocationPojo data = response != null ? response.Data() : null;
                        pointerLocationFunction.Invocator(data);
                    }
                }, applicationContext);
            }
        }, startAfterMilliseconds, repeatEachMilliseconds);
    }


    /**
     * got data from wifi is called one the user has got a data from wifi that  we wanted to connect from a network and now the user
     * this function will check the connection speed and see if we have any speed to the connection and if it's a valid connection
     * this function will also notify the activity on the updates whilte it's going
     *
     * @param appContext
     * @param callbackFunctionForWifiConnection
     */
    public void gotDataFromWifiConnectionThread(final Context appContext, final WiFiConnectionInterface callbackFunctionForWifiConnection) {

        final Timer myTimer = new Timer(); //defines a timer
        final int[] connectionTryouts = {0}; // defines tryouts counter

        class MyTimerTask extends TimerTask {

            public void run() { // runs on separate thread

                WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE); // gets the CURRENT wifi State

                if (wifiManager.getConnectionInfo().getLinkSpeed() > 1) { // makes sure that the link speed is bigger then one meaning we have connection

                    myTimer.cancel(); //cancels the timer

                    callbackFunctionForWifiConnection.ConnectionEstablished(true); //callbacks for the function

                } else {

                    connectionTryouts[0]++; // updates the tryouts

                    if (connectionTryouts[0] < 4) {
                        Log.d(TAG, "run: do nothing");

                    } else {

                        myTimer.cancel(); //cancels the timer

                        if (IS_ALIGMENT_DEBUG_ON() == false) { //checks DEBUG MODE
                            callbackFunctionForWifiConnection.ConnectionEstablished(false); //callbacks for the functino that we have failed the connection
                        } else { //we are in DEBUG MODE
                            callbackFunctionForWifiConnection.ConnectionEstablished(true); //we are in DEBUG MODE
                        }
                    }
                }
            }
        }

        MyTimerTask myTask = new MyTimerTask();

        myTimer.schedule(myTask, 5000, 3000);
    }


    //starts the retrofit loop every 200ml\sec to check the server for info and send it to the callback
    //implyes to the first alignment process
    private void startFineAlignmentLoop(final HttpCommunicationInterface<FineAligmentPojo> callbackForFineAlignmentInterface, final Context applicationContext) {

        int startAfterMilliseconds = 250;
        int repeatEachMilliseconds = 1000;

        fineAlignmentTimer = new Timer();
        fineAlignmentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int retryCount = 3;
                _service.GetRequest(HttpInterrupts.FINE_ALIGNMENT, retryCount, new HttpCallBack<FineAligmentPojo>() {
                    @Override
                    public void invocator(HttpResponse<FineAligmentPojo> response) {
                        FineAligmentPojo data = response != null ? response.Data() : null;
                        callbackForFineAlignmentInterface.Invocator(data);
                    }
                }, applicationContext);
            }
        }, startAfterMilliseconds, repeatEachMilliseconds);
    }


    //starts the retrofit loop every 200ml\sec to check the server for info and send it to the callback
    //implies to the first aligment process
    int startAligmentScanRetrofitLoopOnFail = 0;

    private void startAlignmentScanRetrofitLoop(final HttpCommunicationInterface callbackForAlignmentScanRetrofit, final Context applicationContext) {

        Log.d(TAG, "startAligmentScanRetrofitLoop: start alignmentScan");

        int startAfterMilliseconds = 330;
        int repeatEachMilliseconds = 330;

        firstAligmentTimer = new Timer();
        firstAligmentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int retryCount = 3;
                _service.GetRequest(HttpInterrupts.ALIGNMENT_DATA, retryCount, new HttpCallBack<AligmentDataPojo>() {
                    @Override
                    public void invocator(HttpResponse<AligmentDataPojo> response) {
                        AligmentDataPojo data = response != null ? response.Data() : null;
                        callbackForAlignmentScanRetrofit.Invocator(data);
                    }
                }, applicationContext);
            }
        }, startAfterMilliseconds, repeatEachMilliseconds);
    }

    int bestPositionTryoutsFails = 0;

    /**
     * best position function loop
     *
     * @param bestPositionCallback callback for the rest api request
     * @param selectedWorkorderId  id for the selected workorder
     * @param applicationContext
     */
    public void getBestPosition(final HttpCommunicationInterface<ArrayList<BestPositionListModel>> bestPositionCallback, final String selectedWorkorderId, final Context applicationContext) {

        int retryCount = 3;
        _service.GetRequest(HttpInterrupts.BEST_POSITION, retryCount, new HttpCallBack<BestPositionPojo>() {
            @Override
            public void invocator(HttpResponse<BestPositionPojo> response) {
                try {

                    bestPositionTryoutsFails = 0;

                    BestPositionPojo data = response != null ? response.Data() : null;
                    arrayOfBaseStationsAligmentManager = PojoParserHelper.CreateBestHbsList(data);


                    ArrayList<BestPositionListModel> sorteredPointsList; // holds the list to return to the function

                    // filters the list and returns a sorted list that is by the correct order only with the viable points
                    ///////////////////////////////////////////////////////////
                    sorteredPointsList = PojoParserHelper.filterBestHbsFromServer(arrayOfBaseStationsAligmentManager, selectedWorkorderId);

                    bestPositionCallback.Invocator(sorteredPointsList); // once finished with the loop return the values to the callable

                } catch (Exception e) { // in case the list is empty from the server or that we couldn't finish the operation from what reason

                    e.printStackTrace();
                    bestPositionTryoutsFails++;

                    if (bestPositionTryoutsFails > 5) {
                        bestPositionCallback.Invocator(null); // returns null
                    } else { // retry if less then 5 attempts
                        getBestPosition(bestPositionCallback, selectedWorkorderId, applicationContext);
                    }
                }
            }

        }, applicationContext);
    }

    //stops the first alignment timer and gets the best position band
    public void stopFirstaligmentTimer() {

        try {
            if (firstAligmentTimer != null) {
                firstAligmentTimer.cancel();
                firstAligmentTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BestPositionListModel getBestHbs() {
        return bestHbs;
    }

    public void setSmsRecieved(String smsContent) {
        smsContentRecieved = smsContent;
        Log.d(TAG, "setSmsRecieved: ?");
    }

    public String getSmsRecived() {
        Log.d(TAG, "setSmsRecieved: ?");
        return smsContentRecieved;

    }

    public void setBestHbs(BestPositionListModel bestHbs) {
        this.bestHbs = bestHbs;
    }


    public ArrayList<BestPositionListModel> getBestHbsList() {
        return bestHbsList;
    }

    public void setBestHbsList(ArrayList<BestPositionListModel> bestHbsList) {
        this.bestHbsList = bestHbsList;
    }

    public int getAzimutBeamwidth() {
        return azimutBeamwidth;
    }

    public int getElevationBeamwidth() {
        return elevationBeamwidth;
    }

    public void setElevationBeamwidth(int elevationBeamwidth) {
        this.elevationBeamwidth = elevationBeamwidth;
    }

    public int getChannelScanLimit() {
        return channelScanLimit;
    }

    public void setChannelScanLimit(int channelScanLimit) {
        this.channelScanLimit = channelScanLimit;
    }

    public BroadcastReceiver getWifiReceiver() {
        return wifiReceiver;
    }

    public void setWifiReceiver(BroadcastReceiver wifiReceiver) {
        this.wifiReceiver = wifiReceiver;
    }

    public void setAzimutBeamwidth(int azimutBeamwidth) {
        this.azimutBeamwidth = azimutBeamwidth;
    }

    private static Boolean IS_ALIGMENT_DEBUG_ON = false; // TURNS DEBUG MODE ON & OFF

    private BestPositionListModel bestHbs;

    BroadcastReceiver wifiReceiver;

    private ArrayList<BestPositionListModel> bestHbsList;

    public String getWorkorderFrequency() {
        String frequencyToReturn;

        if ((workorderFrequency != null) && (workorderFrequency.length() > 2)) {
            frequencyToReturn = workorderFrequency;
        } else {
            frequencyToReturn = "Not Set";
        }

        return frequencyToReturn;
    }

    public void setWorkorderFrequency(String workorderFrequency) {
        this.workorderFrequency = workorderFrequency;
    }

    private String workorderFrequency = "";

    private int azimutBeamwidth = 10; // the opening of a spacific angle in the hsu (10, 15, etc)

    private int elevationBeamwidth = 10; // the vertical marging for the antena

    private int channelScanLimit = 6;

    private String smsContentRecieved;

    public ArrayList<ExpendebleMenuPositionModel> getCheckedExpendbleMenuItems() {
        return checkedExpendbleMenuItems;
    }

    /// AVNER -- @@@@@ remove public static
    public static ArrayList<ExpendebleMenuPositionModel> checkedExpendbleMenuItems = new ArrayList<ExpendebleMenuPositionModel>();

    public void setCheckedExpendbleMenuItems(ArrayList<ExpendebleMenuPositionModel> newArraylist) {
        checkedExpendbleMenuItems = newArraylist;
    }

    public Boolean getShowTextCheat() {
        return showTextCheat;
    }

    public void setShowTextCheat(Boolean showTextCheat) {
        this.showTextCheat = showTextCheat;
    }

    private Boolean showTextCheat = false;

    public Boolean getShowLinkStateCheat() {
        return showLinkStateCheat;
    }

    public void setShowLinkStateCheat(Boolean showLinkStateCheat) {
        this.showLinkStateCheat = showLinkStateCheat;
    }

    private Boolean showLinkStateCheat = false;

    //setters and getters for the models at the top of the manager

    public String getDeviceBandId() {
        return currentBandIdFromDevice;
    }

    private void setDeviceBandId(String currentBandId) {
        currentBandIdFromDevice = currentBandId;
    }

    public int getCbwFromDevice() {
        return currentCbwFromDevice;
    }

    private void setDeviceCbw(int currentCbw) {
        currentCbwFromDevice = currentCbw;
    }


    public void insertCheckedExpendbleMenuItems(ExpendebleMenuPositionModel menuItem, Context applicationContext) {

        Boolean itemIsInList = false;

        //removes the list item if it exists
        for (int i = 0; i < checkedExpendbleMenuItems.size(); i++) {
            if ((checkedExpendbleMenuItems.get(i).groupPosition == menuItem.groupPosition) && ((checkedExpendbleMenuItems.get(i).itemPosition == menuItem.itemPosition))) {
                itemIsInList = true;
                checkedExpendbleMenuItems.remove(i);

            }
        }

        // validates teh size is smaller then six
        if (checkedExpendbleMenuItems.size() < 6) {

            //if item doesn't exist adds him to the checked array
            if (!itemIsInList) {
                checkedExpendbleMenuItems.add(menuItem);
            }

        } else {
            Toast.makeText(applicationContext, "Up to 6 frequencies can be selected",
                    Toast.LENGTH_LONG).show();
        }

    }

    /**
     * inserts the retrofit response from the bads to the database
     * it opens up  another thread and does all the data saving process in a bolk sectino so really tries not to intefeer with the normal ui operations of the application
     * this function is happening after the user has connected to the wifi in the scanning screen so by this time we already know that project id, and the selected bandwidth
     *
     * @param response                            response is the object were passing to the call to save all the data from, it comes from the get data REST call
     * @param selectedProjectId                   the selected project id that the user choose
     * @param callbackForAllBandsSqlInquiryFinish
     */
    private void insertBandsToDataBase(final HttpResponse<GetBandsPojo> response, final String selectedProjectId, final HttpCommunicationInterface<List<BandsModel>> callbackForAllBandsSqlInquiryFinish) {

        Log.d(TAG, "insertBandsToDataBase: CALLING FuNCTIOn");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //code to do the HTTP request

                ActiveAndroid.beginTransaction();

                Log.d(TAG, "run: ???");

                try { // try's to add everything to the database
                    Log.d(TAG, "run2:  ???");
                    List<BandsModel> projectsBandsInTheSql = BandsModel.getBandsByProject(selectedProjectId); // gets the correct bands from the server
                    Log.d(TAG, "run1:  ???");
//                    Log.d(TAG, "run: number of bands in device" + response.Data().getData().getBandsList().size());

                    if (projectsBandsInTheSql.size() == 0) { // checks the size of the aray in the server to know if we already initlized it or not

                        //create list of bands and save to DB
                        boolean isSaveToDbRequired = true;

                        supportedDeviceBands = PojoParserHelper.CreateBandsList(response.Data(), selectedProjectId, isSaveToDbRequired);
                        ActiveAndroid.setTransactionSuccessful();

                    } else {
                        //create list of bands and DOES NOT save to DB
                        boolean isSaveToDbRequired = false;
                        supportedDeviceBands = PojoParserHelper.CreateBandsList(response.Data(), selectedProjectId, isSaveToDbRequired);
                    }

                } finally {
                    ActiveAndroid.endTransaction();

                    callbackForAllBandsSqlInquiryFinish.Invocator(supportedDeviceBands); // notifies the callback we have finished with adding
                }

            }
        });
        thread.start();
    }

    /**
     * meaning we got sussful wifi connection and we can move on to the band download presidure
     * this function is called every time a sussful wifi connection is initlized
     *
     * @param selectedProjectId                   the selected project's id
     * @param callbackForAllBandsSqlInquiryFinish the callback for when the application finish
     * @param applicationContext
     * @return void  doesn't return anything
     */
    public void getBandsCall(final String selectedProjectId, final HttpCommunicationInterface<List<BandsModel>> callbackForAllBandsSqlInquiryFinish, final Context applicationContext) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // runs a deley in the code for 1 second
            @Override
            public void run() { // runs a deley in the code for 1 second
                _service.GetRequest(HttpInterrupts.BANDS_LIST, 0, new HttpCallBack<GetBandsPojo>() {
                    @Override
                    public void invocator(HttpResponse<GetBandsPojo> response) {
                        try {
                            //sets the currently selected band id
                            String currentBand = response != null ? response.Data().getData().getCurrentBandId() : null;
                            Integer currentCbw = response != null ? response.Data().getData().getCurrentCbw() : 0;

                            setDeviceBandId(currentBand); // sets the currently selected band id
                            setDeviceCbw(currentCbw);     // sets the currently selected cbw
                        } catch (Exception e) {
                            e.printStackTrace();
                            List<BandsModel> projectBands = null; // opens up a null variable to return
                            callbackForAllBandsSqlInquiryFinish.Invocator(projectBands); // returns a default variable to function
                            return;
                        }


                        AllBandsCallDataTryouts = 0; // update the appropriate tryouts we have managed to sussfuly connect and no need to call that

                        Log.d(TAG, "onResponse: 1065 ?");
                        //creating sql data and saving the bads in the bads list
                        insertBandsToDataBase(response, selectedProjectId, callbackForAllBandsSqlInquiryFinish); // starts inserting everything to database
                    }
                }, applicationContext);

            }
        }, 1000);
    }


    public void initlizeGetEvaluationResultsLoop(final HttpCommunicationInterface<EvaluationResultsPojo> callbackForLinkEvaluationLinkProgress, final Context applicationContext) {

        getEvaluationResultsTimer = new Timer();
        getEvaluationResultsTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                _service.GetRequest(HttpInterrupts.EVALUATION_RESULTS, 1, new HttpCallBack<EvaluationResultsPojo>() {
                    @Override
                    public void invocator(HttpResponse<EvaluationResultsPojo> response) {
                        try {
                            EvaluationResultsPojo data = response != null ? response.Data() : null;
                            callbackForLinkEvaluationLinkProgress.Invocator(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, applicationContext);


            }

        }, 1000, 1000); //start getting result after 1 sec to get proper results


    }

    /**
     * doing another call to the server to do the testings (thus update the server timeout)
     *
     * @param callbackForLinkEvaluationActionStart
     * @param applicationContext
     */
    public void updateEvaluationLink(final HttpCommunicationInterface<String> callbackForLinkEvaluationActionStart, Context applicationContext) {

        final int[] timesRunInCounter = {0};

        updateEvaluationLinkTimer = new Timer();
        updateEvaluationLinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {


                AligmentActionPayloadModel aligmentActionPayloadModel = new AligmentActionPayloadModel("SuperSector12345", "start"); // just simulates data we don't have to send anything

                _service.SetRequest(HttpInterrupts.SET_START_EVALUATION, aligmentActionPayloadModel, 3, new HttpCallBack<PojoSetBand>() {
                    @Override
                    public void invocator(HttpResponse<PojoSetBand> response) {
                        timesRunInCounter[0]++;

                        Log.d(TAG, "onResponse: finished alignment/evaluation/start with timer: " + timesRunInCounter[0]);
                        if (timesRunInCounter[0] < 3) {
                            callbackForLinkEvaluationActionStart.Invocator("Passed");
                        } else {
                            callbackForLinkEvaluationActionStart.Invocator("Done");
                            updateEvaluationLinkTimer.cancel(); // stops the timer

                        }
                    }
                });
            }

        }, 250, 20000);
    }

    /**
     * in here we entered the onCreate phase of the Testings Activity
     * we are changing the state for INITIATED_TESTINGS_ACTIVITY
     * <p/>
     * //* @param callbackForInitiatedScreen
     *
     * @param callbackForLinkEvaluationActionStart
     * @param callbackForLinkEvaluationLinkProgress
     * @param applicationContext
     */
    public void initlizeTestings_Activity(HttpCommunicationInterface callbackForLinkEvaluationActionStart, HttpCommunicationInterface callbackForLinkEvaluationLinkProgress, Context applicationContext) {

        changeState(INITIATED_TESTINGS_ACTIVITY); // changes the state to the correct state

        updateEvaluationLink(callbackForLinkEvaluationActionStart, applicationContext); // updates a link evaluatin every 20 seconds
        initlizeGetEvaluationResultsLoop(callbackForLinkEvaluationLinkProgress, applicationContext); // starts a evaluation report loop that runs every second
    }

    /**
     * gets the selected project bands we get back from the function call once it is set
     *
     * @return list of bands models
     */
    public List<BandsModel> getSupportedDeviceBands() {
        return supportedDeviceBands;
    }

    /**
     * this will happen when the link evaluation will be finished
     *
     * @param callbackForonFinishEvaluationLink
     */
    public void completedLinkEvaluation(HttpCommunicationInterface<Boolean> callbackForonFinishEvaluationLink) {

        if (updateEvaluationLinkTimer != null) {
            updateEvaluationLinkTimer.cancel(); // cancels the update Evaluation Timer
        }

        if (getEvaluationResultsTimer != null) {
            getEvaluationResultsTimer.cancel(); // cancels the evaluation results timer
        }

        if (callbackForonFinishEvaluationLink != null) {

            try {
                callbackForonFinishEvaluationLink.Invocator(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * happens when the user pressed the next button from the fine alignment screen
     * the call will stop the fine alignment timer and the fine alignment pointer location timer
     *
     * @param callbackForonStopAlignment
     */
    public void onStopAlignmentTimers(HttpCommunicationInterface<Boolean> callbackForonStopAlignment) {

        try {

            if (fineAlignmentTimer != null) {
                fineAlignmentTimer.cancel();
            }

            if (fineAlignmentPonterLocationTimer != null) {
                fineAlignmentPonterLocationTimer.cancel();
            }

            if (callbackForonStopAlignment != null) {
                callbackForonStopAlignment.Invocator(true);
            }


        } catch (Exception e) {
            e.printStackTrace();
            callbackForonStopAlignment.Invocator(null);

        }


    }

    /**
     * stops the timers for the first aligment timer and curser location
     *
     * @return
     */
    public Boolean stopMeasuringCallOnBack() {
        try {
            if (firstAligmentTimer != null) {
                firstAligmentTimer.cancel();
                firstAligmentTimer = null;
            }

            if (curserLocationMainAlginmentTimer != null) {
                curserLocationMainAlginmentTimer.cancel();
                curserLocationMainAlginmentTimer = null;
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    int bestPositionLinkEvaluationTryoutsFails = 0;

    /**
     * gets the best position when we return from the evaluation link
     * this function happens when user pressed on the next  HBS button
     *
     * @param bestPositionCallbackLinkEvaluation callback for the http request
     * @param selectedWorkoder                   selected workorder to return with
     * @param applicationContext
     */
    public void getBestPositionLinkEvaluation(final HttpCommunicationInterface<ArrayList<BestPositionListModel>> bestPositionCallbackLinkEvaluation, final WorkingOrdersModel selectedWorkoder, final Context applicationContext) {

        int retryCount = 3;
        _service.GetRequest(HttpInterrupts.BEST_POSITION, retryCount, new HttpCallBack<BestPositionPojo>() {
            @Override
            public void invocator(HttpResponse<BestPositionPojo> response) {
                try {

                    bestPositionLinkEvaluationTryoutsFails = 0;

                    BestPositionPojo data = response != null ? response.Data() : null;
                    arrayOfBaseStationsAligmentManager = PojoParserHelper.CreateBestHbsList(data);


                    ArrayList<BestPositionListModel> sorteredPointsList; // holds the list to return to the function

                    // filters the list and returns a sorted list that is by the correct order only with the viable points
                    ///////////////////////////////////////////////////////////
                    sorteredPointsList = PojoParserHelper.filterBestHbsFromServer(arrayOfBaseStationsAligmentManager, selectedWorkoder.workordertId);

                    bestPositionCallbackLinkEvaluation.Invocator(sorteredPointsList); // once finished with the loop return the values to the callable

                } catch (Exception e) { // in case the list is empty from the server or that we couldn't finish the operation from what reason

                    e.printStackTrace();
                    bestPositionLinkEvaluationTryoutsFails++;

                    if (bestPositionLinkEvaluationTryoutsFails > 5) {
                        bestPositionCallbackLinkEvaluation.Invocator(null); // returns null
                    } else { // retry if less then 5 attempts
                        getBestPositionLinkEvaluation(bestPositionCallbackLinkEvaluation, selectedWorkoder, applicationContext);
                    }
                }
            }

        }, applicationContext);
    }


    public void initPost(final WorkingOrdersModel currentSelectedWorkorder, final Connect_To_Wif_iActivity.onInitInterface callbackFunctionOnInitInterface, final Context applicationContext) {

        _service.GetRequest(HttpInterrupts.SET_INIT, 1, new HttpCallBack<PojoInit>() {
            @Override
            public void invocator(HttpResponse<PojoInit> response) {
                try {

                    // verify's reponse
                    /////////////////////
                    if (response == null || response.Data() == null || response.Data().getData() == null) {
                        callbackFunctionOnInitInterface.isWifiOnCallable(false);
                        return;
                    }

                    PojoInit data = response.Data();

                    // updates the azumuth beamwidth from the pojo call
                    ///////////////////////////////////////
                    try {
                        azimutBeamwidth = data.getData().getAzimutBeamwidth();
                        elevationBeamwidth = data.getData().getElevationBeamwidth();
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackFunctionOnInitInterface.isWifiOnCallable(false);
                    }

                    // try's to update the channelScanLimit
                    ///////////////////////////////
                    try {
                        if (data.getData().getChannelScanLimit() != null) {
                            channelScanLimit = data.getData().getChannelScanLimit();
                        } else {
                            Log.d(TAG, "invocator: couldn't set chanel scan limit from init call, using default: " + channelScanLimit + " instead");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackFunctionOnInitInterface.isWifiOnCallable(false);
                    }

                    //updates workorder
                    //////////////////////////////
                    updateWorkorderWithInitData(currentSelectedWorkorder, data.getData());

                    //calls the callback
                    /////////////////////////////
                    callbackFunctionOnInitInterface.isWifiOnCallable(true);


                } catch (Exception e) {
                    Log.d(TAG, "invocator: error ?");
                    e.printStackTrace();
                }
            }
        }, applicationContext);


//            }
//
//        }, 1000, 1000); //start getting result after 1 sec to get proper results


    }

    /**
     * updates the workorder with the data from the init get call
     *
     * @param currentSelectedWorkorder selected workorder
     * @param data                     data from init get call
     */
    private void updateWorkorderWithInitData(WorkingOrdersModel currentSelectedWorkorder, Data data) {

        // try's to update the workorder setings
        //////////////////////////////
        Log.d(TAG, "updateWorkorderWithInitData: 1");
        try {
            Log.d(TAG, "updateWorkorderWithInitData: 2");
            currentSelectedWorkorder.hsuId = data.getHsuId();

            currentSelectedWorkorder.elevationBeamwidth = data.getElevationBeamwidth();

            currentSelectedWorkorder.azimutBeamwidth = data.getAzimutBeamwidth();

            currentSelectedWorkorder.hsuLinkState = data.getHsuLinkState();

            currentSelectedWorkorder.hsuServiceType = data.getHsuServiceType();

            currentSelectedWorkorder.macAddress = data.getMacAddress();

            currentSelectedWorkorder.numOfElevationZones = data.getNumOfElevationZones();

            currentSelectedWorkorder.radiusInstallConfirmationRequired = data.getRadiusInstallConfirmationRequired();

            currentSelectedWorkorder.save();

        } catch (Exception e) {
            Log.d(TAG, "updateWorkorderWithInitData: 3");
            e.printStackTrace();
        }

    }

    /**
     * posts deregester post to the server
     */
    public void postDeregester() {

        AligmentActionPayloadModel aligmentActionPayloadModel = new AligmentActionPayloadModel("SuperSector12345", "start");
        int retryCount = 1;

        _service.SetRequest(HttpInterrupts.SET_DEREGISTER, aligmentActionPayloadModel, retryCount, new HttpCallBack<PojoSetBand>() {
            @Override
            public void invocator(HttpResponse<PojoSetBand> response) {
                Log.d(TAG, "invocator: response...");
                if (response == null || response.Data() == null) {
                    Log.d("alignementManager", "Cannot post  SET_DEREGISTER");

                } else {
//                    Boolean result = PojoParserHelper.getSetAlignmentCommandsResponse(response.Data());
                    if (response.Data().getData() != null && response.Data().getData().getMessage() != null) {
                        Log.d("alignementManager", "onResponse: post SET_DEREGISTER with status: " + response.Data().getData().getMessage());
                    } else {
                        Log.d(TAG, "invocator: SET_DEREGISTER data is null");
                    }
                }
            }
        });


    }

    /**
     * @param orderLatitude
     * @param orderLongitude
     * @param isRequired
     * @param dateForInstallation
     * @param currentSelectedBand
     * @param currentChannelBandwith
     * @param selectedFrequency
     * @param installationUlRss
     * @param installationDlRss
     * @param upLinkResult
     * @param downLinkResult
     */
    public void setGeoLocation(double orderLatitude, double orderLongitude, Boolean isRequired, String dateForInstallation, String installationType, String currentSelectedBand, String currentChannelBandwith, Integer selectedFrequency, String installationUlRss, String installationDlRss, Double upLinkResult, Double downLinkResult, String installationGenStr) {

        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.CEILING);

        Log.d(TAG, "OrderLatitude that we send is: "+String.valueOf(df.format(orderLatitude)));
        Log.d(TAG, "StringLongToUpdate that we send is: "+String.valueOf(df.format(orderLongitude)));

        FinalDataPayloadModel finalDataPayloadModel = new FinalDataPayloadModel(String.valueOf(df.format(orderLatitude)), String.valueOf(df.format(orderLongitude)), isRequired, dateForInstallation, installationType, currentSelectedBand, currentChannelBandwith, String.valueOf(selectedFrequency), String.valueOf(installationUlRss), String.valueOf(installationDlRss), String.valueOf(upLinkResult), String.valueOf(downLinkResult), installationGenStr);
        int retryCount = 1;

        _service.SetRequest(HttpInterrupts.SET_FINAL_DATA, finalDataPayloadModel, retryCount, new HttpCallBack<PojoSetBand>() {
            @Override
            public void invocator(HttpResponse<PojoSetBand> response) {
                Log.d(TAG, "invocator: response...");
                if (response == null || response.Data() == null) {
                    Log.d("alignementManager", "Cannot post  SET_FINAL_DATA");

                } else {
//                    Boolean result = PojoParserHelper.getSetAlignmentCommandsResponse(response.Data());
                    if (response.Data().getData() != null && response.Data().getData().getMessage() != null) {
                        Log.d("alignementManager", "onResponse: post SET_FINAL_DATA with status: " + response.Data().getData().getMessage());
                    } else {
                        Log.d(TAG, "invocator: SET_DEREGISTER data is null");
                    }
                }
            }
        });
    }

    public void getLinkData(final WorkingOrdersModel selectedWorkoder) {
        int retryCount = 3;

        _service.GetRequest(HttpInterrupts.LINK_STATE, retryCount, new HttpCallBack<pojoLinkState>() {
            @Override
            public void invocator(HttpResponse<pojoLinkState> response) {

                try {
                    selectedWorkoder.selectedFrequency = Integer.valueOf(response.Data().getData().getfrequency());
                    selectedWorkoder.save();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, null);

    }


}
