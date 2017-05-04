package apps.radwin.wintouch.activities.alignmentActivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.canvasRelated.CircleProgressWidget;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.interfaces.HttpCommunicationInterface;
import apps.radwin.wintouch.interfaces.WiFiConnectionInterface;
import apps.radwin.wintouch.models.BandsModel;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class Connect_To_Wif_iActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private ProgressBar mainPrograssBar;
    int MAX_WIFI_TRYOUTS = 5;
    Button openDevicePopupButton;
    AligmentManager aligmentManagerClass;
    int errorTimeOuts = 0;
    String selectedProjectId, selectedWorkorderId;
    WorkingOrdersModel currentSelectedWorkorder;
    TextView mainText;
    int textState = 1;
    String TAG = "ConnectToWifi";
    ProgressBar mainPrograssBar;
    private static boolean activityVisible = true;
    String ipAddressString;
    String networkToConnectTo;
    Dialog settingsDialog;
    String paswordForUnit;
    ImageView errorImageView;
    View errorBackgroundView;
    Button tryAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_wifi_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        /////////////////////////////
        ///start of implementation///
        ////////////////////////////

        //gets the project id
        //getting the saved instances
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedProjectId = "";
                selectedWorkorderId = "";
            } else {
                selectedProjectId = extras.getString("projectId");
                selectedWorkorderId = extras.getString("workorderId");
            }
        } else {
            selectedProjectId = (String) savedInstanceState.getSerializable("projectId");
            selectedWorkorderId = (String) savedInstanceState.getSerializable("workorderId");
        }

        //WORKORDER SQL UPDATE
        currentSelectedWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // updates the data in the sql

        //gets the aligment manager class
        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar();


        mainPrograssBar = (ProgressBar) this.findViewById(R.id.connectToWifiAcitivity_prograsBar);
        mainText = (TextView) findViewById(R.id.connectToWifiActivity_mainText);
        openDevicePopupButton = (Button) findViewById(R.id.connectToWifiAcitivity_connectManualyButton);
        errorImageView = (ImageView) findViewById(R.id.connectToWifiAcitivity_ImageView);
        errorBackgroundView = (View) findViewById(R.id.errorBackgroundView);
        tryAgainButton = (Button) findViewById(R.id.connectToWifiAcitivity_reconnectButton);
        errorImageView.setVisibility(View.INVISIBLE);
        openDevicePopupButton.setVisibility(View.INVISIBLE);
        errorBackgroundView.setVisibility(View.INVISIBLE);
        tryAgainButton.setVisibility(View.INVISIBLE);

        wifiWrapper myWifiWrapper = new wifiWrapper(); //- INIZILIZING WIFI
        myWifiWrapper.checkWifiState(getApplication(), callbackFunctionForisWifiOn);


        openDevicePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });



        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //fixe's display
                ///////////////////////
                errorImageView.setVisibility(View.INVISIBLE);
                openDevicePopupButton.setVisibility(View.INVISIBLE);
                errorBackgroundView.setVisibility(View.INVISIBLE);
                tryAgainButton.setVisibility(View.INVISIBLE);
                mainPrograssBar.setVisibility(View.VISIBLE);

                errorTimeOuts = 0;

                wifiWrapper WifiWrapper = new wifiWrapper();
                WifiWrapper.connectToNetwork(networkToConnectTo, paswordForUnit, getApplication(), callbackFunctionForWifiConnection); //in the future we will pass the scan results to the network

            }
        });



        ///////////////Constants//////////////////
        CircleProgressWidget stepsView = (CircleProgressWidget) findViewById(R.id.wifiConnectionViewStepsView);
        stepsView.changeIndication(2);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.enableAdvertisingIdCollection(true);

        analyticsTracker.setScreenName("Connect To Wifi");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Connect_To_Wif_iActivity.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Connect_To_Wif_iActivity.activityPaused();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());

        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedProjectId);
        startActivity(i);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connect__to__wif_i, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {

            final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();
            if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects

                wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
                Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", storedProjectsModels.get(0).projectId);
                startActivity(i);

            } else { // means we don't have any projects and need to show the empty dashboard

                wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
                Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
                startActivity(i);
            }

        } else if (id == R.id.projects) {

            wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        } else if (id == R.id.about_section) {

            wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * returns a dialog for when the wifi is off to show the user that hies wifi is off and he should turn it on
     *
     * @return returns the actual dialog
     */
    //get the alert dialog for when the wifi is OFF
    public AlertDialog getAlertDialogForWifiOff() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Connect_To_Wif_iActivity.this);


        alertDialogBuilder.setTitle("Wi-Fi disconnected");// set title


        alertDialogBuilder // set dialog message
                .setMessage("To continue installation reconnect the Wi-Fi")
                .setCancelable(false)
                .setPositiveButton("RECONNECT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //on button clicked

                        mainText.setText(getResources().getString(R.string.connect_to_wifi_acitivity_mainText_turnOnWifi)); // shows on screen message

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                wifiWrapper WifiWrapper = new wifiWrapper();

                                WifiWrapper.toggleWiFi(Connect_To_Wif_iActivity.this, true); // turns wifi ON

                                aligmentManagerClass.initlizedConnectingToWifi(); // tells the manager that we started looking for a wifi

                                callbackForCancelingAllWifies.cancelAllWifiCallbackFunction(true);


                            }
                        }, 500); //deley in order to start the animation before hand (otherwise the dialog shows after, 2, 3 seconds


                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { // exit app button clicked

                        Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
                        finish();
                        startActivity(i);

                    }
                });

        // create alert dialog
        return alertDialogBuilder.create();

    }


    /**
     *
     */
    //Interface for the callback function - Defyining the interface
    public interface isWifiOnCallableInterface {

        void isWifiOnCallable(boolean result);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
//        activityVisible = false;
        activityVisible = true;
    }

    /**
     *
     */
    //CALLBACL for When wifi Is turned on or Not
    isWifiOnCallableInterface callbackFunctionForisWifiOn = new isWifiOnCallableInterface() {

        @Override
        public void isWifiOnCallable(boolean result) {

//            if (isActivityVisible()) {

            if (result) { //wifi is ON
                callbackForCancelingAllWifies.cancelAllWifiCallbackFunction(true);

            } else { //wifi is OFF SHOW DIALOG FOR WIFI IS OFF

                AlertDialog offdialog = getAlertDialogForWifiOff(); //shows to user alert dialog for wifi is off

                offdialog.show(); // show it

            }

//            }
        }
    };


    /**
     * callback interface for canceling the wifies
     */
    public interface cancelAllWifiCallbackInterface {

        void cancelAllWifiCallbackFunction(boolean result);
    }


    /**
     * tha callback is called once all the wifies's are forgotten
     * the callback will be called once we forgot all the networks
     */
    cancelAllWifiCallbackInterface callbackForCancelingAllWifies = new cancelAllWifiCallbackInterface() {

        @Override
        public void cancelAllWifiCallbackFunction(boolean result) {

            if (isActivityVisible()) {

                if (result) { //all wifi'es has been stopped

                    final String StringToShowInMainText;

                    if (AligmentManager.IS_ALIGMENT_DEBUG_ON()) { // checks if the debus mode is on or off
                        networkToConnectTo = "R-DEFAULT_SERIAL"; // if debug mode is on puts a default serial number
                        StringToShowInMainText = "Cheat is ON, Connecting to SSID: \n" + networkToConnectTo;
                    } else {
                        networkToConnectTo = currentSelectedWorkorder.workorderWifiSSID; // if debug mode is off the applciation takes the ssid from the workorder and scan input
                        StringToShowInMainText = "Install the SU and power on. \nWait for Wi-Fi connection";
                        //StringToShowInMainText = "Connecting to SSID: \n" + networkToConnectTo;

                    }


                    runOnUiThread(new Runnable() { // runs everything back in the UI thread
                        @Override
                        public void run() {
                            mainText.setText(StringToShowInMainText);


                        }
                    });

                    // gets password from workorder
                    ////////////////////////////////
                    paswordForUnit = "wireless";
                    try {
                        paswordForUnit = currentSelectedWorkorder.wifiPassword;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //conencts to network
                    ////////////////////////////////
                    wifiWrapper WifiWrapper = new wifiWrapper();
                    WifiWrapper.connectToNetwork(networkToConnectTo, paswordForUnit, getApplication(), callbackFunctionForWifiConnection); //in the future we will pass the scan results to the network


                } else { //wifi is OFF SHOW DIALOG FOR WIFI IS OFF
                    Log.d("myLogs", "wifi is OFF");

                }

            }
        }
    };

    /**
     * the actual callback that will be called once WIFI HAS Established or fail the connection
     */
    WiFiConnectionInterface callbackFunctionForWifiConnection = new WiFiConnectionInterface() {

        public void CallBackFromWiFi() { //callbacked from wifi

            if (isActivityVisible()) {
                aligmentManagerClass.gotDataFromWifiConnectionThread(getApplication(), callbackFunctionForWifiConnection); // updates the alignment manager that we have recieved
            }
        }

        /**
         * this function will be called when we either esteblished that the link we have is good or it isn't
         * meaning the speed of it is either 1 (viable link) or 0 (not viable link)
         * @param result
         */
        @Override
        public void ConnectionEstablished(Boolean result) {

            if (result) { //wifi is ON

                runOnUiThread(new Runnable() { // runs everything back in the UI thread
                    @Override
                    public void run() {
                        //checks wifi status
                        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE); //checks wifi status
                        DhcpInfo dhcp = wifiMgr.getDhcpInfo();
                        int gatewayInt = dhcp.gateway;
                        // Convert little-endian to big-endianif needed
                        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                            gatewayInt = Integer.reverseBytes(gatewayInt);
                        }

                        byte[] ipByteArray = BigInteger.valueOf(gatewayInt).toByteArray();


                        try {
                            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
                        } catch (UnknownHostException ex) {
                            Log.e("WIFIIP", "Unable to get host address.");
                            ipAddressString = null;
                        }

                        Log.d(TAG, "run: wifi connection info: " + ipAddressString); //checks wifi status
                        //generates an authentication post to the server to get a device token
                        ////////////////////////////////////////////////////////
                        aligmentManagerClass.authPost(ipAddressString, currentSelectedWorkorder.unitPassword, callbackForAuth);


                    }
                });


            } else { //do something in case wifi couldn't connect to network - failed connection to wifi

                // failed connection to WIFI
                /////////////////////////////////////////

                runOnUiThread(new Runnable() { //Loader screen main text is in the main UI thead so we need to run it inside the ui thread
                    @Override
                    public void run() {

                        try {


                            errorTimeOuts++;

                            if (errorTimeOuts >= Math.round(MAX_WIFI_TRYOUTS/2)) {
                                //updates screen graphics
                                ///////////////////////////
                                mainText.setText(R.string.main_text_wifi_connection_error);
//                                mainText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.alignementWhite));
                                tryAgainButton.setVisibility(View.VISIBLE);
                                errorImageView.setVisibility(View.VISIBLE);
                                errorBackgroundView.setVisibility(View.VISIBLE);
                                mainPrograssBar.setVisibility(View.GONE);
                            } else {
                                wifiWrapper WifiWrapper = new wifiWrapper();
                                WifiWrapper.connectToNetwork(networkToConnectTo, paswordForUnit, getApplication(), callbackFunctionForWifiConnection); //in the future we will pass the scan results to the network
                            }

                            // if error time outs is bigger then one we suggest to open the device wifi connectinos
                            if (errorTimeOuts >= 1) {
                                openDevicePopupButton.setVisibility(View.VISIBLE);
                            }

                            //changes the main text to update the user that the screen is reconnecting
                            ////////////////////////////////
                            if (errorTimeOuts < MAX_WIFI_TRYOUTS) {
                                if (textState == 1) {
                                    mainText.setText("Reconnecting");
                                    textState = 2;
                                } else if (textState == 2) {
                                    mainText.setText(mainText.getText() + "\n Reconnecting..");
                                    textState = 1;
                                }
                            }

//                            new AlertDialog.Builder(Connect_To_Wif_iActivity.this)
//                                    .setTitle("Error")
//                                    .setMessage("Wi-Fi connection failure")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // continue with delete
//                                            //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                                            wifiWrapper myWifiWrapper = new wifiWrapper(); //- INIZILIZING WIFI
//                                            myWifiWrapper.checkWifiState(getApplication(), callbackFunctionForisWifiOn);
//
//                                        }
//                                    })
//                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // do nothing
//                                            Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
//                                            i.putExtra("projectId", selectedProjectId);
//                                            startActivity(i);
//                                        }
//                                    })
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                });
            }
        }
    };

    /**
     * callback thats called once all the sql data has been saved in the aligment manager
     * if we get the project bands as null that means the manager tried for 4 times to do the call and couldn't complete it
     */
    HttpCommunicationInterface<List<BandsModel>> callbackForAllBandsSqlInquiryFinish = new HttpCommunicationInterface<List<BandsModel>>() {

        @Override
        public void Invocator(final List<BandsModel> projectBands) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //shows the dialog popup to let the user select a bandwidth and a band, and frequency

                    if (projectBands == null) { // checks if we got a null pointer from the alignment manager after 4 tries

                        if (isActivityVisible()) {
                            new AlertDialog.Builder(Connect_To_Wif_iActivity.this)
                                    .setTitle("Error")
                                    .setMessage(getResources().getString(R.string.connect_to_wifi_cannot_get_bands))
                                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete

                                            wifiWrapper myWifiWrapper = new wifiWrapper(); //- INIZILIZING WIFI
                                            myWifiWrapper.checkWifiState(getApplication(), callbackFunctionForisWifiOn);
                                            mainText.setText(getResources().getString(R.string.connect_to_wifi_acitivity_mainText_connectingToWifi));

                                        }
                                    })
                                    .setNegativeButton("Back to projects screen", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
                                            startActivity(i);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }

                    } else { // meaning we got all the information needed from the user and we can move to the workorder screen
                        if (isActivityVisible()) {
                            final Handler handler = new Handler(); // sets a deley for the operation
                            handler.postDelayed(new Runnable() { // runs a deley in the code for 0.5 second
                                @Override
                                public void run() { // runs a deley in the code for 1 second

                                    // sets the SSID IN THE HTTP COMMUNICATION SERVICE
                                    /////////////////////////////////////
//                                    aligmentManagerClass.setSSID(networkToConnectTo);

                                    Intent intent = new Intent(getApplication(), WorkorderSetSettings.class); //go to the next screen
                                    finish();
                                    intent.putExtra("projectId", selectedProjectId);
                                    intent.putExtra("workorderId", selectedWorkorderId);
                                    startActivity(intent);

                                }
                            }, 500);
                        }
                    }
                }
            });
        }
    };


    public void onChangePassword() {

        settingsDialog = new Dialog(Connect_To_Wif_iActivity.this);
        settingsDialog.setContentView(R.layout.dialog_settings);
        settingsDialog.setCancelable(false);
        settingsDialog.show();

        final Button dialogSaveButton = (Button) settingsDialog.findViewById(R.id.settingsDialog_button_save);
        final Button dialogCancelButton = (Button) settingsDialog.findViewById(R.id.settingsDialog_button_cancel);
        final EditText dialogUnitPasswordText = (EditText) settingsDialog.findViewById(R.id.settingsDialog_editText_unitPassword);
        final EditText dialogWifiPasswordText = (EditText) settingsDialog.findViewById(R.id.settingsDialog_editText_wiFiPassword);


        try {
            dialogUnitPasswordText.setText(currentSelectedWorkorder.unitPassword);
            dialogWifiPasswordText.setText(currentSelectedWorkorder.wifiPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // go to workorder selection on click
        ///////////////////////////////////
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", selectedProjectId);
                startActivity(i);
                finish();
            }
        });

        // saves the passwords
        ////////////////////////////
        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checks if passwords passed
                Boolean passedPasswordVerification = true;
                //filters out unit password
                // rule 1 - everything must be small letters, big letters in english and numbers
                // ruile 2 - langth must be bigger then 5
                // rule 3 langth must be smaller then 32
                /////////////////////////////////////////////////////
                if ((String.valueOf(dialogUnitPasswordText.getText()).matches("[a-zA-Z0-9]*")) && (dialogUnitPasswordText.length() > 5) && (dialogUnitPasswordText.length() < 32)) {
                    Log.d(TAG, "only has small letters");

                    //filters out wifi password
                    // rule 1 - everything must be small letters, big letters in english and numbers
                    // ruile 2 - langth must be bigger then 7
                    // rule 3 langth must be smaller then 30
                    /////////////////////////////////////////////////////
                    if ((String.valueOf(dialogWifiPasswordText.getText()).matches("[a-zA-Z0-9]*")) && (dialogWifiPasswordText.length() > 7) && (dialogWifiPasswordText.length() < 30)) {
                        Log.d(TAG, "only has small letters");
                    } else {
                        Toast.makeText(getApplication(), getResources().getString(R.string.settings_wrong_wifi_password_message),
                                Toast.LENGTH_LONG).show();

                        passedPasswordVerification = false;

                    }

                } else {
                    Toast.makeText(getApplication(), getResources().getString(R.string.settings_wrong_unit_password_message),
                            Toast.LENGTH_LONG).show();

                    passedPasswordVerification = false;

                }

                //checks if we passed password verification and updates workorder
                /////////////////////////////////////////////////
                if (passedPasswordVerification) {
                    try {
                        currentSelectedWorkorder.unitPassword = String.valueOf(dialogUnitPasswordText.getText());
                        currentSelectedWorkorder.wifiPassword = String.valueOf(dialogWifiPasswordText.getText());
                        currentSelectedWorkorder.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplication(), getResources().getString(R.string.settings_password_approved_message),
                            Toast.LENGTH_LONG).show();



                    // password verification passed
                    //generates an authentication post to the server to get a device token
                    //in this case already connected to wifi, not required to change base url before request
                    ////////////////////////////////////////////////////////
                    aligmentManagerClass.authPost(null, currentSelectedWorkorder.unitPassword, callbackForAuth);


                    // sets the two buttons to do nothing so the user doesn't press them again by mestake
                    ///////////////////////////////////////////////////////
                    dialogSaveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });

                    dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                }


            }
        });

    }


    /**
     * callback for authentication
     */
    public interface onAuthInterface {

        void onAuthFunction(Boolean result, String passwordFail);

    }


    /**
     * callback function for authentication
     */
    onAuthInterface callbackForAuth = new onAuthInterface() {

        @Override
        public void onAuthFunction(Boolean result, String params) {

            // try to hide the settings dialog
            try {
                if(settingsDialog != null){
                    settingsDialog.hide();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (result) {

                errorTimeOuts = 0;

                if (params.equals("pass")) {

                    Log.d(TAG, "onAuthFunction: 1111");
                    // AUTHENTICATION HAS PASSED MOVING TO THE NEXT SCREEN
                    // do the init function
                    // when finished the funcion will do a Post to get Device Bands
                    aligmentManagerClass.initPost(currentSelectedWorkorder, callbackFunctionOnInitInterface, getApplication());


                } else {

                    //shows dialog for the user that the password is wrong
                    ///////////////////////////////////
                    new AlertDialog.Builder(Connect_To_Wif_iActivity.this)
                            .setTitle(getResources().getString(R.string.dialog_userAuth_headline))
                            .setCancelable(false)
                            .setMessage(getResources().getString(R.string.dialog_userAuth_body))
                            .setPositiveButton(getResources().getString(R.string.dialog_userAuth_positive), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    onChangePassword();


                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.dialog_userAuth_negetive), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                    i.putExtra("projectId", selectedProjectId);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }


            } else {

                errorTimeOuts++;

                if (errorTimeOuts >= Math.round(MAX_WIFI_TRYOUTS/2)) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //shows dialog for failed comunication is wrong
                            ///////////////////////////////////
                            new AlertDialog.Builder(Connect_To_Wif_iActivity.this)
                                    .setTitle(getResources().getString(R.string.dialog_connectionFail_headline))
                                    .setCancelable(false)
                                    .setMessage(getResources().getString(R.string.dialog_connectionFail_body))
                                    .setNegativeButton(getResources().getString(R.string.dialog_connectionFail_negetive), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                            i.putExtra("projectId", selectedProjectId);
                                            startActivity(i);
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    });

                }



            }


        }
    };







    /**
     *
     */
    //Interface for the callback function - Defyining the interface
    public interface onInitInterface {

        void isWifiOnCallable(boolean result);
    }


    /**
     *
     */
    //CALLBACL for When wifi Is turned on or Not
    onInitInterface callbackFunctionOnInitInterface = new onInitInterface() {

        @Override
        public void isWifiOnCallable(boolean result) {

            if (result) { //

                Log.d(TAG, "isWifiOnCallable: checking deregister");

                if (currentSelectedWorkorder.hsuLinkState.equals("Active")) {
                    Log.d(TAG, "isWifiOnCallable: checking deregister 1");
                    // shows dialog to the user notifying an error
                    ///////////////////////////////////////
                    new AlertDialog.Builder(Connect_To_Wif_iActivity.this)
                            .setTitle("Warning")
                            .setMessage(getString(R.string.ulc_linkstate_error_dialog))
                            .setCancelable(false)
                            .setPositiveButton("back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    try {
                                        dialog.dismiss();
                                        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                        i.putExtra("projectId", selectedProjectId);
                                        startActivity(i);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                } else if (currentSelectedWorkorder.hsuId > 1 && currentSelectedWorkorder.hsuId < 255 && currentSelectedWorkorder.hsuLinkState.equals("Not Synchronized")) {
                    Log.d(TAG, "isWifiOnCallable: checking deregister 2");

                    // shows dialog to the user notifying an error
                    ///////////////////////////////////////
                    // shows dialog to the user notifying an error
                    ///////////////////////////////////////
                    new AlertDialog.Builder(Connect_To_Wif_iActivity.this)
                            .setTitle("Warning")
                            .setMessage(getString(R.string.ulc_hsuId_error_dialog))
                            .setCancelable(false)
                            .setPositiveButton("back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    try {

                                        dialog.dismiss();
                                        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                        i.putExtra("projectId", selectedProjectId);
                                        startActivity(i);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    try {
                                        dialog.dismiss();
                                        aligmentManagerClass.postDeregester();
                                        aligmentManagerClass.getBandsCall(selectedProjectId, callbackForAllBandsSqlInquiryFinish, getApplication());
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                } else {
                    // post to get device bands
                    /////////////////////////////////////
                    aligmentManagerClass.getBandsCall(selectedProjectId, callbackForAllBandsSqlInquiryFinish, getApplication());

                }



            } else { //wifi is OFF SHOW DIALOG FOR WIFI IS OFF
                mainText.setText(R.string.caompatibilityText); // shows on screen message
                Log.d(TAG, "isWifiOnCallable: ERROR ERROR ERROR");
            }

        }
    };



}
