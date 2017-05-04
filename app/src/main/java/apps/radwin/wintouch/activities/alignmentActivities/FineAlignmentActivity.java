package apps.radwin.wintouch.activities.alignmentActivities;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 02/08/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.canvasRelated.CircleProgressWidget;
import apps.radwin.wintouch.canvasRelated.FineAlignmentView;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.interfaces.HttpCommunicationInterface;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.models.pojoModels.pojoCurserLocation.CurserLocation;
import apps.radwin.wintouch.models.pojoModels.pojoCurserLocation.CurserLocationPojo;
import apps.radwin.wintouch.models.pojoModels.pojoFineAligment.FineAligmentPojo;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class FineAlignmentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AligmentManager aligmentManagerClass; // will hold an instance of the aligment manager

    int MAX_SYNC_TRYOUTS = 20;
    Boolean isFinealignmentErrorDisplayed = false;
    String latestUplink = "0";
    String latestDownlink = "0";
    Boolean isTimerOn = false;
    Tracker analyticsTracker;
    int MIN_SECONDS_FOR_NEWORKD_TIMEOUT = 30;
    long lastUpdateFromUlcTime;
    Boolean wifiToastOn = false;
    int syncErrorTryouts = 0;
    FloatingActionButton fab;
    Boolean isScreenActive = true;
    int maxRss = -1000;
    String HbssLinkState;
    int combinedRss = 0;
    Boolean errorDialogDisplayed = false;
    String TAG = "fineAlignmentScreen";
    FineAlignmentView fineAlignmentWidget;
    WorkingOrdersModel selectedWorkoder;
    TextView linkSyncErrorText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fine_aligment_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar(); // points to the aligment manager

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // analytics
                /////////////
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Measuring")
                        .setAction("Best RSS")
                        .setLabel(String.valueOf(maxRss))
                        .setValue(1)
                        .build());


                isScreenActive = false;
                aligmentManagerClass.onStopAlignmentTimers(callbackFromStopAlignment);

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

        // keeps phone alive
        ////////////////////////
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String selectedWorkorderId, selectedSectorId;
        Integer bestHbsFrequency;
        int hbsCenterAngle;
        selectedSectorId = "";
        selectedWorkorderId = "";
        bestHbsFrequency = 0;
        hbsCenterAngle = 50;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedSectorId = "";
                selectedWorkorderId = "";
                bestHbsFrequency = 0;
                hbsCenterAngle = 50;
            } else {
                selectedSectorId = extras.getString("sectorId");
                selectedWorkorderId = extras.getString("workorderId");
                hbsCenterAngle = extras.getInt("hbsCenterAngle");
                bestHbsFrequency = extras.getInt("frequency");
            }
        } else {
            try {
                selectedSectorId = (String) savedInstanceState.getSerializable("sectorId");
                selectedWorkorderId = (String) savedInstanceState.getSerializable("workorderId");
                bestHbsFrequency = (Integer) savedInstanceState.getSerializable("frequency");
                hbsCenterAngle = (Integer) savedInstanceState.getSerializable("hbsCenterAngle"); // temporary
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        try {
            selectedWorkoder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // gets the current workorder
            selectedWorkoder.selectedSectorId = selectedSectorId;
            selectedWorkoder.selectedFrequency = bestHbsFrequency;
            selectedWorkoder.save();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        fineAlignmentWidget = (FineAlignmentView) findViewById(R.id.fineAlignmentWidget);

        fineAlignmentWidget.setLinkStateCheat(aligmentManagerClass.getShowLinkStateCheat());

        //Step View
        ///////////////////////////
        CircleProgressWidget stepsView = (CircleProgressWidget) findViewById(R.id.fine_alignmentViewStepsView);
        stepsView.changeOutlineColor(true); // changes to a lighter color
        stepsView.changeIndication(4);

        ////Iniating the senter angle
        /////////////////////////////////
        Log.d(TAG, "onCreate: sets senter angle: "+hbsCenterAngle);
        fineAlignmentWidget.setCenterAngle(hbsCenterAngle);

        //casting the text into a text view
        ////////////////////////////////
        linkSyncErrorText = (TextView) findViewById(R.id.textView268);

//        linkSyncErrorText.setVisibility(View.INVISIBLE);

        linkSyncErrorText.setText(getResources().getString(R.string.noLink_error_fineAlignment));
        linkSyncErrorText.setTextColor(getResources().getColor(R.color.alignementColorRed));
        linkSyncErrorText.setVisibility(View.VISIBLE);

    }


//    public interface fineAlignmentCallbackInterface {
//
//        void fineAlignmentCallbackFunction(Response<FineAligmentPojo> response);
//    }

    /**
     * interface for return for testing activity
     */
    HttpCommunicationInterface<FineAligmentPojo> callbackForFineAlignmentInterface = new HttpCommunicationInterface<FineAligmentPojo>() {

        @Override
        public void Invocator(FineAligmentPojo response) {

            if ((response != null && response.getData() !=  null)) {
                try {

                    lastUpdateFromUlcTime = System.currentTimeMillis() / 1000;

                    isFinealignmentErrorDisplayed = false;



                    // checks link state
                    ////////////////////////////
                    if (! response.getData().getLinkState().equals("syncUnregistered")) {

                        //updates the sync error
                        /////////////////////////
                        syncErrorTryouts++;

                        //checks if we got max tryouts for the sync
                        /////////////////////////
                        if ((syncErrorTryouts >= MAX_SYNC_TRYOUTS) && (errorDialogDisplayed == false)) {

                            //updates teh error dialog to displayed true
                            //errorDialogDisplayed = true;

                            //stops all timers in the screen
                            ////////////////////////
//                            aligmentManagerClass.onStopAlignmentTimers(null);

                            //makes the view visible again
                            ///////////////////////
                            linkSyncErrorText.setText(getResources().getString(R.string.noLink_error_fineAlignment));
                            linkSyncErrorText.setTextColor(getResources().getColor(R.color.alignementColorRed));
                            linkSyncErrorText.setVisibility(View.VISIBLE);

                            // hides the fab button
                            //////////////////////
                            fab.hide();

                            //shows a dialog to the user
                            ////////////////////////
//                            new android.support.v7.app.AlertDialog.Builder(Fine_Aligment_Activity.this)
//                                    .setTitle(getResources().getString(R.string.dialog_lostSyncDialog_headline))
//                                    .setMessage(getResources().getString(R.string.dialog_lostSyncDialog_body))
//                                    .setPositiveButton(getResources().getString(R.string.dialog_lostSyncDialog_negetive), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // continue with delete
//                                            Intent i = new Intent(Fine_Aligment_Activity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
//                                            i.putExtra("projectId", selectedWorkoder.projectId);
//                                            startActivity(i);
//                                        }
//                                    })
//                                    .setCancelable(false)
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();

                        }


                    }else if ((response.getData().getRSSUL() != null) && (isScreenActive)) {

                        linkSyncErrorText.setVisibility(View.INVISIBLE);

                        //makes the error view invisible
                        ///////////////////////
                        linkSyncErrorText.setVisibility(View.INVISIBLE);

                        // hides the fab button
                        //////////////////////
                        fab.show();

                        // updates teh sync error tryouts
                        /////////////////////
                        syncErrorTryouts = 0;

                        //updates latest up link and downlink from device
                        //////////////////////////
                        latestUplink = String.valueOf(response.getData().getRSSUL());
                        latestDownlink = String.valueOf(response.getData().getRSSDL());

                        combinedRss = response.getData().getRSSUL();
                        HbssLinkState = response.getData().getLinkState();

                        if (response.getData().getRSSUL() > maxRss) {
                            maxRss = response.getData().getRSSUL();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    if (!isFinealignmentErrorDisplayed) {

                        new android.support.v7.app.AlertDialog.Builder(FineAlignmentActivity.this)
                                .setTitle("Error")
                                .setMessage("Cannot get RSS")
                                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        gotoWorkOrderActivity();

                                    }
                                })
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }
            } else {

                linkSyncErrorText.setText(getResources().getString(R.string.linkLostAligmentScreen));
                linkSyncErrorText.setTextColor(getResources().getColor(R.color.alignementColorRed));
                linkSyncErrorText.setVisibility(View.VISIBLE);

//                //check if we have passed the minimum time for log out and moves to
//                /////////////////////////////////////////////////////////////
//                if (((System.currentTimeMillis() / 1000) - lastUpdateFromUlcTime) > MIN_SECONDS_FOR_NEWORKD_TIMEOUT ) {
//
//                    // try stops the measeuring call
//                    ///////////////////////////////////
//                    aligmentManagerClass.stopMeasuringCallOnBack();
//
//                    wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
//                    Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
//                    i.putExtra("projectId", selectedWorkoder.projectId);
//                    startActivity(i);
//
//                }

                // OLD BEHIVIOR DO NOT DELETE
//                if (!isFinealignmentErrorDisplayed) {
//
//                    isFinealignmentErrorDisplayed = true;
//                    new android.support.v7.app.AlertDialog.Builder(Fine_Aligment_Activity.this)
//                            .setTitle("Error")
//                            .setMessage("Cannot get RSS")
//                            .setPositiveButton("Back", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    gotoWorkOrderActivity();
//                                }
//                            })
//                            .setCancelable(false)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//
//                }



            }

        }
    };


    private void gotoWorkOrderActivity()
    {
        try {
            // kill timers
            aligmentManagerClass.onStopAlignmentTimers(null);

            Intent i = new Intent(FineAlignmentActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
            i.putExtra("projectId", selectedWorkoder.projectId);
            startActivity(i);
            finish();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //CALLBACKL for pointer location loop
    HttpCommunicationInterface<CurserLocationPojo> pointerLocationFunction = new HttpCommunicationInterface<CurserLocationPojo>() {

        @Override
        public void Invocator(CurserLocationPojo response) {

            //                        setHorizontalBoxPosition(String.valueOf(response.body().getData().getCurserLocation().getHorizontal())); // updates the curser location
//                        setVerticalBoxPosition(String.valueOf(response.body().getData().getCurserLocation().getElevation()));// updates the curser location

            if (response != null) {
                try {
                    if ((isScreenActive) && (response.getData().getCurserLocation().getElevationCell() != null)) {


                        lastUpdateFromUlcTime = System.currentTimeMillis() / 1000;

                        isFinealignmentErrorDisplayed = false;

                        if (response.getData()!=null)
                        {
                            CurserLocation cl =  response.getData().getCurserLocation();
                            if (cl!=null)
                            {
                                String ele = cl.getElevation();
                                String hor = cl.getHorizontal();

                                int elevation = 0;
                                try {
                                    elevation = Integer.parseInt(ele);
                                }
                                catch (Exception e) { e.printStackTrace(); }

                                int horiz = 0;
                                try {
                                    horiz = Integer.parseInt(hor);
                                }
                                catch (Exception e) { e.printStackTrace(); }

                                try {
                                    fineAlignmentWidget.setCellInfo(elevation, horiz, combinedRss, HbssLinkState, maxRss);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        }

//                        fineAlignmentWidget.setCellInfo(Integer.parseInt(response.getData().getCurserLocation().getElevation()), Integer.parseInt(response.getData().getCurserLocation().getHorizontal()), combinedRss, HbssLinkState, maxRss);

                    }

                } catch (NumberFormatException e) {
                    new android.support.v7.app.AlertDialog.Builder(FineAlignmentActivity.this)
                            .setTitle(getResources().getString(R.string.alignmentErrorTitle))
                            .setMessage(getResources().getString(R.string.alignmentErrorCursorLocation))
                            .setPositiveButton(getResources().getString(R.string.alignmentErrorBack), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    gotoWorkOrderActivity();
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }


            } else {

                // shows a toast to the user for connection isn't correct
                ////////////////////////////////////
                if (! wifiToastOn) {
                    if (!wifiWrapper.isNetworkMatch(selectedWorkoder.workorderWifiSSID, getApplicationContext())) {
                        Toast.makeText(getApplicationContext().getApplicationContext(), R.string.wifi_connection_toast_wifi, //wifi is enabled
                                Toast.LENGTH_SHORT).show();
                        wifiToastOn = true;

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                try {
                                    wifiToastOn = false;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 10000);


                    }
                }

                linkSyncErrorText.setText(getResources().getString(R.string.linkLostAligmentScreen));
                linkSyncErrorText.setTextColor(getResources().getColor(R.color.alignementColorRed));
                linkSyncErrorText.setVisibility(View.VISIBLE);


                //check if we have passed the minimum time for log out and moves to
                /////////////////////////////////////////////////////////////
                if (((System.currentTimeMillis() / 1000) - lastUpdateFromUlcTime) > MIN_SECONDS_FOR_NEWORKD_TIMEOUT ) {

                    // try stops the measeuring call
                    ///////////////////////////////////
                    if (isScreenActive) {

                        //stops the timers
                        ////////////////////////
                        aligmentManagerClass.onStopAlignmentTimers(null);

                        wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
                        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                        i.putExtra("projectId", selectedWorkoder.projectId);
                        startActivity(i);

                    }

                }

                // OLD BEHIVIOR DO NOT DELETE
//                if (! isFinealignmentErrorDisplayed) {
//
//                    isFinealignmentErrorDisplayed = true;
//
//                    new android.support.v7.app.AlertDialog.Builder(Fine_Aligment_Activity.this)
//                            .setTitle(getResources().getString(R.string.alignmentErrorTitle))
//                            .setMessage(getResources().getString(R.string.alignmentErrorCursorLocation))
//                            .setPositiveButton(getResources().getString(R.string.alignmentErrorBack), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    gotoWorkOrderActivity();
//                                }
//                            })
//                            .setCancelable(false)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//                }

            }

        }

    };

    @Override
    protected void onStart() {
        super.onStart();

        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Fine Alignment");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        //stops the timers
        ////////////////////////
        aligmentManagerClass.onStopAlignmentTimers(null);

        wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId",selectedWorkoder.projectId);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fine__aligment_, menu);
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
            Toast.makeText(getApplication(), getResources().getString(R.string.Menu_pressed_settings), // shows a toast to the user that the name exists
                    Toast.LENGTH_SHORT).show();
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
            // Handle the camera action
        } else if (id == R.id.projects) {

            aligmentManagerClass.onStopAlignmentTimers(null);

            wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        }else if (id == R.id.about_section) {

            aligmentManagerClass.onStopAlignmentTimers(null);

            wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public interface onStopAlignmentInterface {

        void onStopAlignmentCallbackFunction(Boolean result);
    }

    /**
     * interface for return for testing activity
     */
    HttpCommunicationInterface<Boolean> callbackFromStopAlignment = new HttpCommunicationInterface<Boolean>() {

        @Override
        public void Invocator(Boolean result) {

            if (result != null) {

                if (result == true) {

                    Dialog dialog = new Dialog(FineAlignmentActivity.this);
                    dialog.setContentView(R.layout.dialog_fine_alignment_popup);

                    Button continueButton = (Button ) dialog.findViewById(R.id.button);

                    dialog.setTitle("Lock Antenna");
                    dialog.setCancelable(false);
                    dialog.show();

                    continueButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //saves workorder downlink and uplink
                            /////////////////////////////
                            selectedWorkoder.lateseDownlink = latestDownlink;
                            selectedWorkoder.latestUplink = latestUplink;
                            selectedWorkoder.save();

                            //intent for link evaluation screen
                            ///////////////////
                            Intent i = new Intent(getApplication(), LinkEvaluationActivity.class); // moves to the fine aligment screen
                            i.putExtra("workorderId", selectedWorkoder.workordertId);
                            i.putExtra("projectId", selectedWorkoder.projectId);
                            i.putExtra("sectorId", selectedWorkoder.selectedSectorId);
                            startActivity(i);

                        }
                    });



                } else {
                    new android.support.v7.app.AlertDialog.Builder(FineAlignmentActivity.this)
                            .setTitle(getResources().getString(R.string.alignmentErrorTitle))
                            .setMessage(getResources().getString(R.string.alignmentErrorCursorLocation))
                            .setPositiveButton(getResources().getString(R.string.alignmentErrorBack), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    gotoWorkOrderActivity();
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }

            } else {
                new android.support.v7.app.AlertDialog.Builder(FineAlignmentActivity.this)
                        .setTitle(getResources().getString(R.string.alignmentErrorTitle))
                        .setMessage(getResources().getString(R.string.alignmentErrorCursorLocation))
                        .setPositiveButton(getResources().getString(R.string.alignmentErrorBack), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                gotoWorkOrderActivity();
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        // kill timers

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(aligmentManagerClass != null && (isTimerOn)) {
            isTimerOn = false;
            aligmentManagerClass.onStopAlignmentTimers(null);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        if(aligmentManagerClass != null && (! isTimerOn)) {
            // iniates fine alignment
            /////////////////////////////////
            aligmentManagerClass.initializedFineAlignment(callbackForFineAlignmentInterface, pointerLocationFunction, getApplication());
            isTimerOn = true;

        }



    }
}
