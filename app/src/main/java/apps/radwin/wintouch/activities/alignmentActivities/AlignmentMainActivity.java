package apps.radwin.wintouch.activities.alignmentActivities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.SectorListAdapter;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.canvasRelated.CircleProgressWidget;
import apps.radwin.wintouch.canvasRelated.alignmentWidget;
import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.interfaces.HttpCommunicationInterface;
import apps.radwin.wintouch.models.BestPositionListModel;
import apps.radwin.wintouch.models.CommunicationModels.CommonResponseModel;
import apps.radwin.wintouch.models.HBSModel;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.models.pojoModels.pojoAligmentData.AligmentDataPojo;
import apps.radwin.wintouch.models.pojoModels.pojoCurserLocation.CurserLocationPojo;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class AlignmentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView headerText;

    // will hold an instance of the aligment manager
    FloatingActionButton fab;

    int SENSITIVITY_TOGO_TO_FINE_ALIGNMENT = 5;

    TextView descriptionText;

    Tracker analyticsTracker;

    Boolean isErrorTimerRuning = false;

    Timer errorConnectionTimeoutTimer;

    Boolean linkLost = false;

    Boolean wifiToastOn = false;

    int MIN_SECONDS_FOR_NEWORKD_TIMEOUT = 30;

    Boolean sectorListChoosedDialogActive = false;

    AligmentManager aligmentManagerClass;

    Menu globalOptionsMenuItem;

    BestPositionListModel bestCell;

    long lastUpdateFromUlcTime;

    Dialog sectorListChoosedDialog;

    int elevationsScanedLow = 0;

    int elevationsScanedMedium = 0;

    int elevationsScanedHigh = 0;

    String selectedProjectId, selectedWorkorderId, TAG, bestHbsSectorId;

    Integer bestHbsFrequency;

    ArrayList<BestPositionListModel> bestHbsList;

    Boolean elvationLowScanned = false;

    Boolean elvationMediumScanned = false;

    Boolean elvationHighScanned = false;

    Boolean HBS_FOUND = false;

    Boolean isInAlignmentPhase = false;

    Boolean isScreenActive = true;

    Boolean isBestInitlized = false;

    Boolean isTimerOn = false;

    Boolean isConnectionFailDialogDisplayed = false;

    SectorListAdapter sectorAdapter;

    WorkingOrdersModel currentSelectedWorkorder;

    ArrayList<HBSModel> arraySectors;

    private GeoLocationWrapper geoLocationWrapper;

    int elvationsScanned = 1;

    Boolean isFromLinkEvaluation = false;
    private boolean isBestEffortWorkingOrder;
    private boolean isNetworkIdEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alignment_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fab handeling
        //////////////////////////
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (HBS_FOUND) {

                    if (!linkLost) {
                        isInAlignmentPhase = true;


                        //analytics
                        ////////////////////
                        String antennaType = "Normal";
                        if (aligmentManagerClass.getAzimutBeamwidth() != 10) {
                            antennaType = "Attached";
                        }


                        if (elvationHighScanned) {
                            analyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Measuring")
                                    .setAction("Elevation Scanned High")
                                    .setLabel(String.valueOf(elevationsScanedHigh))
                                    .setValue(1)
                                    .build());

                        }

                        if (elvationMediumScanned) {
                            analyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Measuring")
                                    .setAction("Elevation Scanned Medium")
                                    .setLabel(String.valueOf(elevationsScanedMedium))
                                    .setValue(1)
                                    .build());

                        }

                        if (elvationLowScanned) {
                            analyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Measuring")
                                    .setAction("Elevation Scanned Low")
                                    .setLabel(String.valueOf(elevationsScanedLow))
                                    .setValue(1)
                                    .build());

                        }


                        analyticsTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Site Properties")
                                .setAction("Antenna type")
                                .setLabel(antennaType)
                                .setValue(1)
                                .build());

                        //gets the best position rest call
                        /////////////////////////////////
                        aligmentManagerClass.alignmentScanHbsFound(bestPositionCallback, selectedWorkorderId, getApplication());

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //do nothing
                            }
                        });
                    } else { // show toast that link has been lost
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.linkeLostToastAligmentScreen, Toast.LENGTH_SHORT);
                        toast.show();

                    }

                } else { // show toast in case didn't found any hbs
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.no_hbs_alignment_toast), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        //////////////////////////////////////////
        ///////Start of implementation////////////
        //////////////////////////////////////////

        ///////////////////RULES///////////////////
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int bestHbsPosition = 0;

        /////////////imports//////////////////////
        //gets the project id
        //getting the saved instances
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedProjectId = "";
                selectedWorkorderId = "";
                bestHbsPosition = 0;
                isFromLinkEvaluation = false;
            } else {
                selectedProjectId = extras.getString("projectId");
                selectedWorkorderId = extras.getString("workorderId");
                isFromLinkEvaluation = extras.getBoolean("isCameFromLinkEvaluation");
                bestHbsPosition = extras.getInt("hbsSectorPosition");

            }
        } else {
            selectedProjectId = (String) savedInstanceState.getSerializable("projectId");
            selectedWorkorderId = (String) savedInstanceState.getSerializable("workorderId");
            isFromLinkEvaluation = (Boolean) savedInstanceState.getBoolean("isCameFromLinkEvaluation");
            bestHbsPosition = (Integer) savedInstanceState.getInt("hbsSectorPosition");
        }

        geoLocationWrapper = ((appContext) getApplication()).getGeoLocationWrapper();

        currentSelectedWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // updates the data in the sql

        ///////////////Constants//////////////////
        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar(); // points to the aligment manager
        WorkingOrdersModel currentWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);
        ProjectsModel workingProject = ProjectsModel.getProjectWithId(selectedProjectId);
        isBestEffortWorkingOrder = currentWorkorder.isBestEffort;
        isNetworkIdEmpty = (workingProject.networkId == null || workingProject.networkId.equals("")) ? true : false;
        descriptionText = (TextView) findViewById(R.id.alignmentMainTextDescription);

        // stpes view
        ////////////////////////
        headerText = (TextView) findViewById(R.id.alignmentMainTextHeadline);
        CircleProgressWidget stepsView = (CircleProgressWidget) findViewById(R.id.alignmentViewStepsView);
        stepsView.changeOutlineColor(true); // changes to a lighter color
        stepsView.changeIndication(4);



        // try's to put the degrees cheat that is takes from the aligment screen
        ///////////////////////
        alignmentWidget alignmentView = (alignmentWidget) findViewById(R.id.mainAlignmentWidget);

        try {
            alignmentView.setDegreesCheat(aligmentManagerClass.getShowTextCheat());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        // try stops the measeuring call
        ///////////////////////////////////
        aligmentManagerClass.stopMeasuringCallOnBack();

        wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedProjectId);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isFromLinkEvaluation) {
            getMenuInflater().inflate(R.menu.alignment_main, menu);
        }

        globalOptionsMenuItem = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.avialable_sectors) {

            if (isBestInitlized) {

                if (bestHbsList.size() > 1) {

                    initSectorListPopup();

                } else {
                    Toast.makeText(getApplication(), "Only one HBS was found",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplication(), "A best HBS needs to be selected first",
                        Toast.LENGTH_LONG).show();

            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int hbsClickedPosition = 0;
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

        sectorAdapter = new SectorListAdapter(this, bestHbsList);
        sectorAdapter.setLayoutColor(hbsClickedPosition);

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

                sectorListChoosedDialogActive = false;

                //closes the dialog
                sectorListChoosedDialog.cancel();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    // sets the best in the display
                    /////////////////////////////////
                    alignmentWidget alignmentView = (alignmentWidget) findViewById(R.id.mainAlignmentWidget);

                    alignmentView.setBestCell(bestHbsList.get(position).cellNumber, bestHbsList.get(position).elvationCell); // updates the best HBS

                    // initlizes the bestcell
                    /////////////////////////////////
                    bestCell = bestHbsList.get(position); // initlizes the bestcell
                    hbsClickedPosition = position;

                    // initlizes best sector id
                    /////////////////////////////////
                    bestHbsSectorId = bestHbsList.get(position).sectorID;
                    bestHbsFrequency = bestHbsList.get(position).channel;

                    headerText.setText("HBS: " + bestHbsSectorId + " located");

                    sectorListChoosedDialogActive = false;

                    //closes the dialog
                    sectorListChoosedDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        sectorListChoosedDialog.setTitle("Hbs List"); // gives a title to the dialog

        sectorListChoosedDialogActive = true;

        sectorListChoosedDialog.show(); // shows the dialog

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


//    public interface aligmanetRetrofitCallbackInterface {
//
//        void aligmanetRetrofitCallback(Response<AligmentDataPojo> response);
//    }


    //main data callable
    HttpCommunicationInterface<AligmentDataPojo> callbackForAligmentScanRetrofit = new HttpCommunicationInterface<AligmentDataPojo>() {

        @Override
        public void Invocator(AligmentDataPojo response) {

            if (response != null && response.getData() != null) { // we have a response

                lastUpdateFromUlcTime = System.currentTimeMillis() / 1000;

                Log.d(TAG, "aligmanetRetrofitCallback: cellNumber: " + response.getData().getCurserLocation().getCellNumber());

                final alignmentWidget alignmentView = (alignmentWidget) findViewById(R.id.mainAlignmentWidget);

                descriptionText.setText(R.string.alignment_secondary_headline);
                descriptionText.setTextColor(getResources().getColor(R.color.alignment_canvasOutline));
                linkLost = false;

                //lets the canvas widget check for array size
                //////////////////////////////////////////////
//                alignmentView.setArraySize(response.getData().getSamples().size());

                boolean hbsFoundLow = false, hbsFoundMedium = false, hbsFoundHigh = false;

                elevationsScanedLow = 0;
                elevationsScanedMedium = 0;
                elevationsScanedHigh = 0;



                for (int i = 0; i < response.getData().getSamples().size(); i++) {



                    if (!isInAlignmentPhase) { //
                        try {




                            if (response.getData().getSamples().get(i).getElevationLow().getScanned()) {
                                //counts how much high, medium, and low elevations are there
                                elevationsScanedLow ++;
                                // checks if scanned
                                hbsFoundLow = (isNetworkIdEmpty ? response.getData().getSamples().get(i).getElevationLow().getSectorFound() :
                                        response.getData().getSamples().get(i).getElevationLow().getSectorWithSpecSectorId())
                                        && (isBestEffortWorkingOrder ? response.getData().getSamples().get(i).getElevationLow().getBeResourcesExist() :
                                        response.getData().getSamples().get(i).getElevationLow().getCirResourcesExist());
                                alignmentView.changeColor(i, "low", hbsFoundLow);
                            }

                            if (response.getData().getSamples().get(i).getElevationMedium().getScanned()) {
                                //counts how much high, medium, and low elevations are there
                                elevationsScanedMedium ++;
                                // checks if scanned
                                hbsFoundMedium = (isNetworkIdEmpty ? response.getData().getSamples().get(i).getElevationMedium().getSectorFound() :
                                        response.getData().getSamples().get(i).getElevationMedium().getSectorWithSpecSectorId())
                                        && (isBestEffortWorkingOrder ? response.getData().getSamples().get(i).getElevationMedium().getBeResourcesExist() :
                                        response.getData().getSamples().get(i).getElevationMedium().getCirResourcesExist());
                                alignmentView.changeColor(i, "middle", hbsFoundMedium);
                            }

                            if (response.getData().getSamples().get(i).getElevationHigh().getScanned()) { // checks if scanned
                                //counts how much high, medium, and low elevations are there
                                elevationsScanedHigh ++;

                                hbsFoundHigh = (isNetworkIdEmpty ? response.getData().getSamples().get(i).getElevationHigh().getSectorFound() :
                                        response.getData().getSamples().get(i).getElevationHigh().getSectorWithSpecSectorId())
                                        && (isBestEffortWorkingOrder ? response.getData().getSamples().get(i).getElevationHigh().getBeResourcesExist() :
                                        response.getData().getSamples().get(i).getElevationHigh().getCirResourcesExist());
                                alignmentView.changeColor(i, "high", hbsFoundHigh);

                            }


                            //// updates the hbs found veriable
                            //////////////////////////
                            if (hbsFoundLow || hbsFoundMedium || hbsFoundHigh) {
                                HBS_FOUND = true;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            new AlertDialog.Builder(AlignmentMainActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Cannot bring Alignment Data: " + e)
                                    .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            Intent i = new Intent(AlignmentMainActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                            i.putExtra("projectId", selectedProjectId);
                                            startActivity(i);
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                }

                try {
                    alignmentView.changeElvation(response.getData().getCurserLocation().getElevationCell(), Integer.parseInt(response.getData().getCurserLocation().getElevation())); // checks if scanned
                    alignmentView.setAngle(Integer.parseInt(response.getData().getCurserLocation().getHorizontal())); // checks if scanned
                } catch (NumberFormatException e) {

                    e.printStackTrace();
                    new AlertDialog.Builder(AlignmentMainActivity.this)
                            .setTitle("Error")
                            .setMessage("Cannot bring Alignment Data: " + e)
                            .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    Intent i = new Intent(AlignmentMainActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                    i.putExtra("projectId", selectedProjectId);
                                    startActivity(i);
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }


            } else {

                // shows a toast to the user for connection isn't correct
                ////////////////////////////////////
                if (!wifiToastOn) {
                    if (!wifiWrapper.isNetworkMatch(currentSelectedWorkorder.workorderWifiSSID, getApplicationContext())) {
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

                try {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            descriptionText.setText(R.string.linkLostAligmentScreen);
                            descriptionText.setTextColor(getResources().getColor(R.color.mainAlignementColor));
                            linkLost = true;
                        }
                    });


                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

                //check if we have passed the minimum time for log out and moves to
                /////////////////////////////////////////////////////////////
                if (((System.currentTimeMillis() / 1000) - lastUpdateFromUlcTime) > MIN_SECONDS_FOR_NEWORKD_TIMEOUT) {

                    aligmentManagerClass.stopMeasuringCallOnBack();

                    wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());

                    if (isScreenActive) {
                        // try stops the measeuring call
                        ///////////////////////////////////
                        isScreenActive = false;

                        wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
                        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                        i.putExtra("projectId", selectedProjectId);
                        startActivity(i);

                    }

                }

                // starts a timer to count 30 second

                // OLD BEHIVIOR DO NOT DELETE
                // checks if the dialog is displayed or not
//                if (isConnectionFailDialogDisplayed == false) {
//
//                    // stops the timer for the network calls
//                    ///////////////////////////////////////
//                    aligmentManagerClass.stopMeasuringCallOnBack();
//
//                    isConnectionFailDialogDisplayed = true;
//
//                    Log.d(TAG, "aligmanetRetrofitCallback: null null null");
//                    new AlertDialog.Builder(AlignmentMainActivity.this)
//                            .setTitle("Error")
//                            .setMessage("Cannot bring Alignment Data ")
//                            .setPositiveButton("Back", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // continue with delete
//                                    Intent i = new Intent(AlignmentMainActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
//                                    i.putExtra("projectId", selectedProjectId);
//                                    startActivity(i);
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


    //Interface for the callback function - Defyining the interface
//    public interface bestPositionCallbackInterface {
//
//        void bestPositionCallbck(ArrayList<BestPositionListModel> baseStationsArray);
//
//    }


    /**
     * CALLBACK for When wifi Is turned on or Not
     * the callback is called every time with a SORTED list of ready HB's
     * This is the END function that we get after the user pressed the next and the system choose what it thought for the best position
     */
    HttpCommunicationInterface<ArrayList<BestPositionListModel>> bestPositionCallback = new HttpCommunicationInterface<ArrayList<BestPositionListModel>>() {

        @Override
        public void Invocator(ArrayList<BestPositionListModel> baseStationsArray) {

            Log.d(TAG, "Invocator: objects: ");

            if (baseStationsArray != null) {

                lastUpdateFromUlcTime = System.currentTimeMillis() / 1000;

                if (baseStationsArray.size() > 0) {

                    analyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Measuring")
                            .setAction("Number Of Hbs Found")
                            .setLabel(String.valueOf(baseStationsArray.size()))
                            .setValue(1)
                            .build());

                    try {

                        Log.d("myLogs", "bestPositionCallbck: baseStationsArray.get(0).cellNumber: " + baseStationsArray.get(0).cellNumber);
                        Log.d("myLogs", "bestPositionCallbck: baseStationsArray.get(0).elevationCell: " + baseStationsArray.get(0).elvationCell);
                        int bestRss = baseStationsArray.get(0).bestRSS;
                        int bestRssPosition = 0;
                        for (int i = 0; i < baseStationsArray.size(); i++) {
                            Log.d(TAG, "Invocator: running on position: "+i+" with rss: "+baseStationsArray.get(i).bestRSS);
                            if (baseStationsArray.get(i).bestRSS > bestRss) {
                                bestRss = baseStationsArray.get(i).bestRSS;
                                bestRssPosition = i;
                            }
                        }

                        //opens up the menu
                        getMenuInflater().inflate(R.menu.alignment_main, globalOptionsMenuItem);

                        bestHbsList = baseStationsArray;

                        alignmentWidget alignmentView = (alignmentWidget) findViewById(R.id.mainAlignmentWidget);

                        alignmentView.setBestCell(baseStationsArray.get(bestRssPosition).cellNumber, baseStationsArray.get(bestRssPosition).elvationCell); // updates the best HSU

                        // updates the text for the best position
                        /////////////////////////////////////////////
                        headerText = (TextView) findViewById(R.id.alignmentMainTextHeadline);

                        try {
                            // writes down the best hbs id
                            /////////////////////////////////////////////
                            if (baseStationsArray.get(bestRssPosition).sectorID.length() < 15) {
                                headerText.setText("HBS: " + baseStationsArray.get(bestRssPosition).sectorID + " located");
                            } else {
                                headerText.setText("HBS: " + baseStationsArray.get(bestRssPosition).sectorID.substring(0, 12) + "... located");
                            }
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            descriptionText.setText(getResources().getString(R.string.alignment_secondary_headline_best_found));
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }

                        //hides the fab button
                        ///////////////////////////
                        fab.hide();

                        Log.d(TAG, "Invocator: initlizing best position as: "+bestRssPosition);

                        // initlizes the bestcell
                        /////////////////////////////////
                        bestCell = baseStationsArray.get(bestRssPosition); // initlizes the bestcell

                        // initlizes best sector id and channel
                        // initlizes best sector id
                        /////////////////////////////////
                        bestHbsSectorId = baseStationsArray.get(bestRssPosition).sectorID;
                        bestHbsFrequency = baseStationsArray.get(bestRssPosition).channel;

                        //starts the pointer Location Loop
                        aligmentManagerClass.cursorLocationLoopMainAlignment(pointerLocationFunction, getApplication()); // notifies the manager about the start scanning

                        isBestInitlized = true;

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d(TAG, "bestPositionCallbck: size of base station array is bigger then zero");
                    new AlertDialog.Builder(AlignmentMainActivity.this)
                            .setTitle("Error")
                            .setMessage(R.string.no_hbs_alignment_toast)
                            .setPositiveButton("Back to workorder", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    Intent i = new Intent(AlignmentMainActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                    i.putExtra("projectId", selectedProjectId);
                                    startActivity(i);
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }

            } else {
                Log.d(TAG, "bestPositionCallbck: couldn't get best position");
            }
        }
    };


    //Interface for the callback function - Defyining the interface
//    public interface pointerLocationInterface {
//
//        void pointerLocationCallbck(Response<CurserLocationPojo> response);
//
//    }


    /**
     * CALLBACL for pointer location loop
     */
    int curserLocationFailCounter = 0;
    HttpCommunicationInterface<CurserLocationPojo> pointerLocationFunction = new HttpCommunicationInterface<CurserLocationPojo>() {
        @Override
        public void Invocator(CurserLocationPojo response) {

            if (response != null) {

                lastUpdateFromUlcTime = System.currentTimeMillis() / 1000;

                try {
                    if ((isScreenActive) && (response.getData().getCurserLocation().getElevationCell() != null)) {

                        descriptionText.setText(getResources().getString(R.string.alignment_secondary_headline_best_found));
                        descriptionText.setTextColor(getResources().getColor(R.color.alignment_canvasOutline));
                        linkLost = false;

                        alignmentWidget alignmentView = (alignmentWidget) findViewById(R.id.mainAlignmentWidget);

                        // counts elevations
                        if ((response.getData().getCurserLocation().getElevationCell().equals("low"))) {
                            elvationLowScanned = true;

                        } else if (response.getData().getCurserLocation().getElevationCell().equals("high")) {
                            elvationHighScanned = true;

                        } else {
                            elvationMediumScanned = true;
                        }

                        curserLocationFailCounter = 0;
                        alignmentView.changeElvation(response.getData().getCurserLocation().getElevationCell(), Integer.parseInt(response.getData().getCurserLocation().getElevation())); // checks if scanned
                        alignmentView.setAngle(Integer.parseInt(response.getData().getCurserLocation().getHorizontal())); // checks if scanned

                        ////////////////////////////////
                        // CHECKS BEST POSITION STATEMENT
                        ////////////////////////////////
                        try {
                            if (bestCell != null) { //
                                if ((response.getData().getCurserLocation().getElevationCell().equals(bestCell.elvationCell)) &&
                                        ((Integer.parseInt(response.getData().getCurserLocation().getHorizontal()) > bestCell.horizontal - SENSITIVITY_TOGO_TO_FINE_ALIGNMENT) && (Integer.parseInt(response.getData().getCurserLocation().getHorizontal()) < bestCell.horizontal + SENSITIVITY_TOGO_TO_FINE_ALIGNMENT)) && (!sectorListChoosedDialogActive)) {
                                    Log.d(TAG, "pointerLocationCallbck: move to next screen !:");

                                    if (isScreenActive && (!linkLost)) {

                                        isScreenActive = false;

                                        aligmentManagerClass.stopCurserLocation();

                                        //starts the sync process
                                        //////////////////////////////////
                                        aligmentManagerClass.SendStartSyncRequest(callbackFromStartSyncDone, bestCell.sectorID);

                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (NumberFormatException e) {

                    descriptionText.setText(R.string.linkLostAligmentScreen);
                    descriptionText.setTextColor(getResources().getColor(R.color.mainAlignementColor));
                    linkLost = true;


                    //OLD BEHIVIOR DO NOT DELETE
//                    curserLocationFailCounter++;
//
//                    if (curserLocationFailCounter > 10) {
//                        new AlertDialog.Builder(AlignmentMainActivity.this)
//                                .setTitle("Error")
//                                .setMessage("Cannot bring cursor Location")
//                                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // continue with delete
//                                        Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
//                                        startActivity(i);
//                                    }
//                                })
//                                .setCancelable(false)
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();
//
//
//                    }
                }


            } else {


                descriptionText.setText(R.string.linkLostAligmentScreen);
                descriptionText.setTextColor(getResources().getColor(R.color.alignementColorRed));
                linkLost = true;

                // OLD behivior DO not Delete
//                new AlertDialog.Builder(AlignmentMainActivity.this)
//                        .setTitle(getResources().getString(R.string.alignmentErrorTitle))
//                        .setMessage(getResources().getString(R.string.alignmentErrorCursorLocation))
//                        .setPositiveButton(getResources().getString(R.string.alignmentErrorBack), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // continue with delete
//                                Intent i = new Intent(AlignmentMainActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
//                                i.putExtra("projectId", selectedProjectId);
//                                startActivity(i);
//                            }
//                        })
//                        .setCancelable(false)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();

                //check if we have passed the minimum time for log out and moves to
                /////////////////////////////////////////////////////////////
                if (((System.currentTimeMillis() / 1000) - lastUpdateFromUlcTime) > MIN_SECONDS_FOR_NEWORKD_TIMEOUT) {

                    //
                    if (isScreenActive) {

                        // try stops the measeuring call
                        ///////////////////////////////////
                        aligmentManagerClass.stopMeasuringCallOnBack();

                        isScreenActive = false;

                        wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
                        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                        i.putExtra("projectId", selectedProjectId);
                        startActivity(i);

                    }

                }


            }

        }

    };

    /**
     * start sync clalback interface
     */
//    public interface onStartSyncDoneInterface {
//
//        void onStartSyncDoneFunction(Boolean result);
//    }


    /**
     * callback for start sync
     */
    HttpCommunicationInterface<CommonResponseModel> callbackFromStartSyncDone = new HttpCommunicationInterface<CommonResponseModel>() {

        @Override
        public void Invocator(CommonResponseModel commonResponseModel) {

            if (commonResponseModel.getStatus()) {

                lastUpdateFromUlcTime = System.currentTimeMillis() / 1000;

                try {
                    Log.d(TAG, "onStartSyncDoneFunction: horizontal to give is: " + bestCell.horizontal);

                    aligmentManagerClass.setBestHbs(bestCell);

                    sectorListChoosedDialogActive = false;

                    //opens up the fine alignment activity
                    ////////////////////////////////////
                    Intent i = new Intent(getApplication(), FineAlignmentActivity.class); // moves to the fine aligment screen
                    i.putExtra("workorderId", selectedWorkorderId);
                    i.putExtra("hbsCenterAngle", bestCell.horizontal);
                    i.putExtra("hbsElvation", bestCell.elevation); // temporary should go once bug is fixed
                    i.putExtra("sectorId", bestHbsSectorId);
                    i.putExtra("frequency", bestHbsFrequency);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(AlignmentMainActivity.this)
                            .setTitle("Error")
                            .setMessage("Cannot Post Sync Action, " + e)
                            .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    Intent i = new Intent(AlignmentMainActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                    i.putExtra("projectId", selectedProjectId);
                                    startActivity(i);
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }


            } else { // incase the sync has failed

                new AlertDialog.Builder(AlignmentMainActivity.this)
                        .setTitle("Error")
                        .setMessage("Cannot Post Sync Action")
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                Intent i = new Intent(AlignmentMainActivity.this, WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                i.putExtra("projectId", selectedProjectId);
                                startActivity(i);
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Alignment Main Screen");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());


    }

    @Override
    protected void onPause() {
        super.onPause();
        // try stops the measeuring call
        ///////////////////////////////////
        if (aligmentManagerClass != null && isTimerOn) {
            isTimerOn = false;
            aligmentManagerClass.stopMeasuringCallOnBack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // try stops the measeuring call
        ///////////////////////////////////
        if ((aligmentManagerClass != null) && (! isTimerOn)) {

            ///////////////////////////////////////////
            ////////ImplementationStart////////////////
            ///////////////////////////////////////////
            if (! isFromLinkEvaluation) {
                isTimerOn = true;
                aligmentManagerClass.initilizedAligmentScanActivity(callbackForAligmentScanRetrofit, getApplication()); // notifies the manager about the start scanning
            }

            // coming from link evaluation
            /////////////////////////////
            if (isFromLinkEvaluation) {

                // stops the first timer
                try {

                    headerText.setText("HBS: " + aligmentManagerClass.getBestHbs().sectorID + " located");

                    isTimerOn = false;
                    aligmentManagerClass.stopFirstaligmentTimer();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //hides the fab button
                fab.hide();

                //starts the pointer Location Loop
                aligmentManagerClass.cursorLocationLoopMainAlignment(pointerLocationFunction, getApplication()); // notifies the manager about the start scanning
                isBestInitlized = true;
                isTimerOn = true;

//            alignmentView.invalidate();

//            ;

//            alignmentView.setBestCell(aligmentManagerClass.getBestHbsList().cellNumber, aligmentManagerClass.getBestHbsList().elvationCell); // updates the best HBS


                //runs without a timer by reposting this handler at the end of the runnable
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() { // runs a deley in the code for 1 second
                    @Override
                    public void run() { // runs a deley in the code for 1 second
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final alignmentWidget alignmentView = (alignmentWidget) findViewById(R.id.mainAlignmentWidget);
                                alignmentView.setBestCell(aligmentManagerClass.getBestHbs().cellNumber, aligmentManagerClass.getBestHbs().elvationCell); // updates the best HBS
                            }
                        });
                    }
                }, 500);


                // initlizes the bestcell
                /////////////////////////////////
                bestCell = aligmentManagerClass.getBestHbs(); // initlizes the bestcell

                // initlizes best sector id
                /////////////////////////////////
                bestHbsSectorId = aligmentManagerClass.getBestHbs().sectorID;
                bestHbsFrequency = aligmentManagerClass.getBestHbs().channel;

                //initlizes the Hbs list
                bestHbsList = aligmentManagerClass.getBestHbsList();

            }


        }

    }
}
