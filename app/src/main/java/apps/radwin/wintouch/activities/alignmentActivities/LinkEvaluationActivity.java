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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.SectorListAdapter;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.canvasRelated.CircleProgressWidget;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.interfaces.HttpCommunicationInterface;
import apps.radwin.wintouch.models.BestPositionListModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.models.pojoModels.pojoGetEvaluationData.EvaluationResultsPojo;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class LinkEvaluationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int MAX_SYNC_TRYOUTS = 5;
    int syncErrorTryouts = 0;
    String TAG = "LinkEvaluationActivity";//the activity tag
    Boolean errorDialogDisplayed = false;
    Tracker analyticsTracker;
    Button completeButton;
    Boolean finishedAlignment = false;
    Double downLink;
    Double upLink;
    Dialog sectorListChoosedDialog;
    int timerIncrements = 0;
    AligmentManager aligmentManagerClass;
    Button testAgainButton, nextHbsButton;
    ArcProgress arc_progressUplink, arc_progressDownlink;
    DonutProgress arc_progressFinished;
    TextView upLinkWidgetText, downLinkWidgetText, finishedWidgetText, sectorIdField, truputDownProjectId, truputUpProjectId, errorText;
    ImageView finishedCheckMark;
    View finishedOuterCircle;
    RelativeLayout finishedWidgetLayout;
    WorkingOrdersModel selectedWorkoder;
    ArrayList<BestPositionListModel> bestHbsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_activity_layout);
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

        //gets the aligment manager class
        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // a flag to keep the screen on

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        String selectedWorkorderId;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedWorkorderId = "";
            } else {
                selectedWorkorderId = extras.getString("workorderId");
            }
        } else {
            selectedWorkorderId = (String) savedInstanceState.getSerializable("workorderId");
        }

        selectedWorkoder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // gets the current workorder

        //locals
        //////////////////////////////
        arc_progressUplink = (ArcProgress) findViewById(R.id.arc_progressUplink);
        arc_progressDownlink = (ArcProgress) findViewById(R.id.arc_progressDownlink);
        arc_progressFinished = (DonutProgress) findViewById(R.id.arc_progressFinished);

        upLinkWidgetText = (TextView) findViewById(R.id.upLinkWidgetText);
        downLinkWidgetText = (TextView) findViewById(R.id.downLinkWidgetText);
        finishedWidgetText = (TextView) findViewById(R.id.finishedLinkWidgetText);
        sectorIdField = (TextView) findViewById(R.id.sectorIdTextLinkEvaluation);
        truputDownProjectId = (TextView) findViewById(R.id.truputDownProjectId);
        truputUpProjectId = (TextView) findViewById(R.id.truputUpProjectId);

        testAgainButton = (Button) findViewById(R.id.linkEvaluationTestAgainButton);

        nextHbsButton = (Button) findViewById(R.id.linkEvaluationNextHbsButton);

        errorText = (TextView) findViewById(R.id.linkEvaluationErrorText);

        finishedCheckMark = (ImageView) findViewById(R.id.finishedCheckMark);

        finishedOuterCircle = (View) findViewById(R.id.finishedOuterCircle);

        finishedWidgetLayout = (RelativeLayout) findViewById(R.id.finishedWidgetLayout);

        completeButton = (Button) findViewById(R.id.linkEvaluationContinueButton);

        finishedWidgetLayout.setVisibility(View.INVISIBLE);

        completeButton.setVisibility(View.INVISIBLE);

        errorText.setVisibility(View.INVISIBLE);

        ///////////////Constants//////////////////
        CircleProgressWidget stepsView = (CircleProgressWidget) findViewById(R.id.linkEvaluationViewStepsView);
        stepsView.changeOutlineColor(true); // changes to a lighter color
        stepsView.changeIndication(5);


        // starting testing activity
        //////////////////////////////////////////
        // sets the frequency
        aligmentManagerClass.getLinkData(selectedWorkoder);

        aligmentManagerClass.initlizeTestings_Activity(callbackForLinkEvaluationActionStart, callbackForLinkEvaluationLinkProgress, getApplication()); // initating the aligment manager to tell we have inited the screen, we are also giving away the callback function

        try {
            sectorIdField.setText(selectedWorkoder.selectedSectorId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // sets the true put
        /////////////////////////////
        try {
            truputDownProjectId.setText("REQUIRED " + String.valueOf(selectedWorkoder.truePutDown) + " Mbps");
            truputUpProjectId.setText("REQUIRED " + String.valueOf(selectedWorkoder.truePutUp) + " Mbps");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //make testagain invisible
        /////////////////////////////
        testAgainButton.setVisibility(View.INVISIBLE);
        nextHbsButton.setVisibility(View.INVISIBLE);

    }


    @Override
    protected void onStart() {
        super.onStart();

        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Link Evaluation");
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

        wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedWorkoder.projectId);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.testings_, menu);
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
        final int id = item.getItemId();

        new AlertDialog.Builder(this)
                .setTitle("warning")
                .setMessage("Exiting will stop alignment process, are you sure ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        if (id == R.id.dashboard) {
                            // Handle the camera action
                        } else if (id == R.id.projects) {

                            wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
                            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
                            startActivity(i);

                        } else if (id == R.id.news) {

                        } else if (id == R.id.support) {

                        } else if (id == R.id.tutorials) {

                        } else if (id == R.id.about_section) {

                            wifiWrapper.forgetNetwork(selectedWorkoder, getApplicationContext());
                            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
                            startActivity(i);

                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //CALLBACK for When wifi Is turned on or Not
    HttpCommunicationInterface<String> callbackForLinkEvaluationActionStart = new HttpCommunicationInterface<String>() {

        @Override
        public void Invocator(String result) {

            if (result != null) {

                errorText.setVisibility(View.INVISIBLE);

                if (result.equals("Passed")) { //passed the call

                } else if (result.equals("Done")) { //passed the final call

                    Log.d(TAG, "Invocator: link evaluation ENTER");
                    arc_progressUplink.setProgress(99);
                    arc_progressDownlink.setProgress(99);

                    finishedWidgetLayout.setVisibility(View.VISIBLE);

                    completeButton.setVisibility(View.VISIBLE);

                    aligmentManagerClass.completedLinkEvaluation(callbackForonFinishEvaluationLink);
                    //

                } else { //ERROR

                }

            } else { // ERROR
//                errorText.setVisibility(View.VISIBLE);

                if (!finishedAlignment) {

                    aligmentManagerClass.completedLinkEvaluation(null);

                    finishedAlignment = true;

                    callbackForonFinishEvaluationLink.Invocator(false);

                }

            }
        }
    };

    //CALLBACL for When wifi Is turned on or Not
    HttpCommunicationInterface<EvaluationResultsPojo> callbackForLinkEvaluationLinkProgress = new HttpCommunicationInterface<EvaluationResultsPojo>() {

        @Override
        public void Invocator(EvaluationResultsPojo response) {

            if (response != null && response.getData() != null) {

                errorText.setVisibility(View.INVISIBLE);

                // checks link state
                ////////////////////////////

                if (!response.getData().getLinkState().equals("syncUnregistered")) {

                    //updates the sync error
                    /////////////////////////
                    syncErrorTryouts++;

                    //checks if we got max tryouts for the sync
                    /////////////////////////
                    if ((syncErrorTryouts >= MAX_SYNC_TRYOUTS) && (!errorDialogDisplayed)) {

                        //updates teh error dialog to displayed true
                        errorDialogDisplayed = true;

                        //stops all timers in the screen
                        ////////////////////////
//                        aligmentManagerClass.completedLinkEvaluation(null);

                        //shows a dialog to the user
                        ////////////////////////
                        aligmentManagerClass.completedLinkEvaluation(callbackForonFinishEvaluationLink);

//                        new android.support.v7.app.AlertDialog.Builder(LinkEvaluationActivity.this)
//                                .setTitle(getResources().getString(R.string.dialog_lostSyncDialog_headline))
//                                .setMessage(getResources().getString(R.string.dialog_lostSyncDialog_body))
//                                .setPositiveButton(getResources().getString(R.string.dialog_lostSyncDialog_negetive), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // continue with delete
//
//                                        selectedWorkoder.upLinkResult = 0.0;
//                                        selectedWorkoder.downLinkResult = 0.0;
//
//                                        Intent i = new Intent(getApplication(), ReportActivity.class); // moves to the fine aligment screen
//                                        i.putExtra("selectedWorkorderId", selectedWorkoder.workordertId);
//                                        i.putExtra("selectedProjectId", selectedWorkoder.projectId);
//                                        i.putExtra("upLinkResult", 0.0);
//                                        i.putExtra("downLinkResult", 0.0);
//                                        i.putExtra("status", "complete");
//                                        startActivity(i);
//                                    }
//                                })
//                                .setCancelable(false)
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();

                    }

                    // sync returns something else then -syncUnregistered
                    ///////////////////////////////////////
                } else {

                    // try catch the whole operation in case of applciation misfuncioning
                    ////////////////////////////////
                    try {
                        // checks if the project is best effort or CIR
                        /////////////////////////////////////
                        if (selectedWorkoder.isBestEffort) {
                            // devides the answer from the server by 1,000,0000 and times it in 100,000
                            downLink = Double.valueOf(response.getData().getDownLink()) / 10000;
                        } else {
                            // devides the answer from the server by 1,000,0000 and times it in (the avilable resources devided by 10)
                            downLink = ((Double.valueOf(response.getData().getUpLink())) / 1000000) * ((Double.valueOf(aligmentManagerClass.getBestHbs().availableResourcesDL)) / 10);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // try catch the whole operation in case of applciation misfuncioning
                    ////////////////////////////////
                    try {
                        // checks if the project is best effort or CIR
                        /////////////////////////////////////
                        if (selectedWorkoder.isBestEffort) {
                            // devides the answer from the server by 1,000,0000 and times it in 100,000
                            upLink = Double.valueOf(response.getData().getUpLink()) / 10000;
                        } else {// devides the answer from the server by 1,000,0000 and times it in (the avilable resources devided by 10)
                            upLink = ((Double.valueOf(response.getData().getUpLink())) / 1000000) * ((Double.valueOf(aligmentManagerClass.getBestHbs().availableResourcesUL)) / 10);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    timerIncrements++;

                    Log.d(TAG, "linkEvaluationLinkPrograssFunction: downlink: " + downLink + " uplink: " + upLink);

                    arc_progressUplink.setProgress((100 / 35) * timerIncrements);
                    arc_progressDownlink.setProgress((100 / 35) * timerIncrements);

                    try {
                        downLinkWidgetText.setText(String.format("%.1f", downLink));
                        upLinkWidgetText.setText(String.format("%.1f", upLink));

                    } catch (Exception e) {
                    }
                }


            } else { // ERROR ERROR

//                errorText.setVisibility(View.VISIBLE);

                if (!finishedAlignment) {

                    aligmentManagerClass.completedLinkEvaluation(null);

                    finishedAlignment = true;

                    callbackForonFinishEvaluationLink.Invocator(false);

                }


            }


        }
    };

    /**
     * initlizes the sector list dialog
     */
    public void initSectorListPopup() {

        // initlizing popup
        //////////////////////////////
        sectorListChoosedDialog = new Dialog(this);

        // casting the sector list
        ////////////////////////////////
        sectorListChoosedDialog.setContentView(R.layout.dialog_sector_list_layout);

//        arraySectors = new ArrayList<HBSModel>();

        SectorListAdapter sectorAdapter = new SectorListAdapter(this, bestHbsList);

        // listview
        //////////////////
        ListView listView = (ListView) sectorListChoosedDialog.findViewById(R.id.sectorList_list_dialog); // connecting listview to display
        listView.setAdapter(sectorAdapter);

        // button
        //////////////////
        Button cancelButton = (Button) sectorListChoosedDialog.findViewById(R.id.sectirList_list_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //closes the dialog
                sectorListChoosedDialog.cancel();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                try {
                    // sets the best in the display
                    /////////////////////////////////
//                    final alignmentWidget alignmentView = (alignmentWidget) findViewById(R.id.mainAlignmentWidget);
//                    alignmentView.setBestCell(bestHbsList.get(0).cellNumber, bestHbsList.get(0).elvationCell); // updates the best HBS


                    //closes the dialog
                    sectorListChoosedDialog.cancel();

                    // initlizes the bestcell
                    /////////////////////////////////
//                    BestPositionListModel bestCell = bestHbsList.get(0); // initlizes the bestcell

                    //sets the best hbs

                    try {
                        aligmentManagerClass.setBestHbs(bestHbsList.get(position));
                        aligmentManagerClass.setBestHbsList(bestHbsList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // going to alignment Main Activity with flag that came from link evaluation
                    Intent intent = new Intent(getApplication(), AlignmentMainActivity.class); //go to the next screen
                    intent.putExtra("projectId", selectedWorkoder.projectId);
                    intent.putExtra("workorderId", selectedWorkoder.workordertId);
                    intent.putExtra("isCameFromLinkEvaluation", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("hbsSectorPosition", position);
                    startActivity(intent);


                    // initlizes best sector id
                    /////////////////////////////////
//                    bestHbsSectorId = bestHbsList.get(0).sectorID;
//                    bestHbsFrequency = bestHbsList.get(0).channel;


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        sectorListChoosedDialog.setTitle("Hbs List"); // gives a title to the dialog

        sectorListChoosedDialog.show(); // shows the dialog

    }

    /**
     * link evaluation has passed sussfuly
     */
    public interface onFinishEvaluationLinkInterface {

        void onFinishEvaluationLinkFunction(Boolean result);
    }

    /**
     *
     */
    //CALLBACL for When wifi Is turned on or Not
    HttpCommunicationInterface<Boolean> callbackForonFinishEvaluationLink = new HttpCommunicationInterface<Boolean>() {

        @Override
        public void Invocator(Boolean result) {

            try {
                finishedWidgetText.setText(String.format("%.1f", downLink + upLink));
            } catch (Exception e) {
                e.printStackTrace();
                finishedWidgetText.setText("0.0");
            }

            Log.d(TAG, "Invocator: 1");

            // checks if test has passed or not
            /////////////////////////////////////
            Boolean isPassedEvaluation = true;
            String passStatus = "complete";

            try {
                if ((downLink < selectedWorkoder.truePutDown) || (upLink < selectedWorkoder.truePutUp)) {
                    isPassedEvaluation = false;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Invocator: 2");
            // shows red dial if evaluation failed
            /////////////////////////////////////////
            if (isPassedEvaluation == false) {
                //make testagain invisible
                /////////////////////////////
                testAgainButton.setVisibility(View.VISIBLE);
                nextHbsButton.setVisibility(View.VISIBLE);
                arc_progressFinished.setFinishedStrokeColor(getResources().getColor(R.color.alignementColorRed));
                finishedCheckMark.setImageResource(R.drawable.ic_close_black_24dp);
                //passStatus = "Failed";

                testAgainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finishedAlignment = false;

                        //zeroes out first vars
                        //////////////////////////////
                        arc_progressUplink.setProgress(0);
                        arc_progressDownlink.setProgress(0);
                        timerIncrements = 0;
                        truputDownProjectId.setText("REQUIRED " + String.valueOf(selectedWorkoder.truePutDown) + " Mbps");
                        truputUpProjectId.setText("REQUIRED " + String.valueOf(selectedWorkoder.truePutUp) + " Mbps");

                        // starting testing activity
                        //////////////////////////////////////////
                        aligmentManagerClass.initlizeTestings_Activity(callbackForLinkEvaluationActionStart, callbackForLinkEvaluationLinkProgress, getApplication()); // initating the aligment manager to tell we have inited the screen, we are also giving away the callback function

                        try {
                            sectorIdField.setText(selectedWorkoder.selectedSectorId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //make testagain invisible
                        /////////////////////////////
                        finishedWidgetLayout.setVisibility(View.INVISIBLE);
                        completeButton.setVisibility(View.INVISIBLE);
                        testAgainButton.setVisibility(View.INVISIBLE);
                        nextHbsButton.setVisibility(View.INVISIBLE);

                    }
                });

                nextHbsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aligmentManagerClass.getBestPositionLinkEvaluation(bestPositionCallbackLinkEvaluation, selectedWorkoder, getApplication());
                    }
                });

            }


            Log.d(TAG, "Invocator: 3");

            // saves the up link and downlink
            //////////////////////////
            try {
                selectedWorkoder.upLinkResult = roundOneDecimals(upLink);
                selectedWorkoder.downLinkResult = roundOneDecimals(downLink);

                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm aaa");
                String dateString = sdf.format(date);

                selectedWorkoder.lastUpdateTime = dateString;
                selectedWorkoder.save();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Invocator: 4");

            final String finalPassStatus = passStatus;
            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // ANALYTICS
                    //////////////////////
                    try {
                        int selectedWorkoderAnalytics = (int ) selectedWorkoder.truePutUp.doubleValue();
                        analyticsTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Link Evaluation")
                                .setAction("Required Throughput up")
                                .setLabel(String.valueOf(selectedWorkoder.truePutUp.doubleValue()))
                                .setValue(1)
                                .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        int selectedWorkoderAnalytics = (int ) selectedWorkoder.truePutDown.doubleValue();
                        analyticsTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Link Evaluation")
                                .setAction("Required Throughput down")
                                .setLabel(String.valueOf(selectedWorkoder.truePutDown.doubleValue()))
                                .setValue(1)
                                .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        int upLinkAnalytics = (int ) upLink.doubleValue();
                        analyticsTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Link Evaluation")
                                .setAction("Result Throughput up")
                                .setLabel(String.valueOf(upLink.doubleValue()))
                                .setValue(1)
                                .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        int downLinkAnalytics = (int ) downLink.doubleValue();
                        analyticsTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Link Evaluation")
                                .setAction("Result Throughput down")
                                .setLabel(String.valueOf(downLink.doubleValue()))
                                .setValue(1)
                                .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    Log.d(TAG, "Invocator: 5");
                    Intent i = new Intent(getApplication(), ReportActivity.class); // moves to the fine aligment screen
                    i.putExtra("selectedWorkorderId", selectedWorkoder.workordertId);
                    i.putExtra("selectedProjectId", selectedWorkoder.projectId);
                    i.putExtra("upLinkResult", roundOneDecimals(upLink));
                    i.putExtra("downLinkResult", roundOneDecimals(downLink));
                    i.putExtra("status", finalPassStatus);
                    startActivity(i);

                }
            });


        }
    };

    double roundOneDecimals(double d) {
        final DecimalFormat twoDForm = new DecimalFormat("#.#");
        String formattedValue = twoDForm.format(d);
        if(formattedValue.contains(",")) {
            formattedValue = formattedValue.replace(",", ".");
        }

        return Double.valueOf(formattedValue);
    }

    /**
     * CALLBACK for When wifi Is turned on or Not
     * the callback is called every time with a SORTED list of ready HB's
     * This is the END function that we get after the user pressed the next and the system choose what it thought for the best position
     */
    HttpCommunicationInterface<ArrayList<BestPositionListModel>> bestPositionCallbackLinkEvaluation = new HttpCommunicationInterface<ArrayList<BestPositionListModel>>() {

        @Override
        public void Invocator(ArrayList<BestPositionListModel> baseStationsArray) {

            if (baseStationsArray != null) {

                if (baseStationsArray.size() > 0) {


                    bestHbsList = baseStationsArray;

                    initSectorListPopup();

                } else {
                    Log.d(TAG, "bestPositionCallbck: size of base station array is bigger then zero");

                }

            } else {
                Log.d(TAG, "bestPositionCallbck: couldn't get best position");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //stops all timers in the screen
        ////////////////////////
        if (aligmentManagerClass != null) {
            aligmentManagerClass.completedLinkEvaluation(null);
        }
    }

}
