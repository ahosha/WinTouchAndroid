package apps.radwin.wintouch.activities.alignmentActivities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.utils.PermissionUtils;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtLastAddress;
    TextView txtLastDate;
    TextView txtPlanned;
    TextView txtCompleted;
    TextView txtUncompleted;
    TextView txtPercent;
    TextView txtProjectName;
    TextView txtPlannedText;
    TextView txtCompletedText;
    TextView txtUncompletedText;
    TextView txtChartName;
    TextView txtLastName;
    ImageView WorkOrderIcon;
    View viewLayout;

    ImageView plannedIcon, completeIcon, inProgressIcon;

    DecoView arcView;
    int series1IndexC = 0;
    int series1IndexP = 0;
    int series1IndexU = 0;
    int totalWorkorders = 0;

    String strLastAddress = "";
    String strLastDate = "";
    String TAG = "Dashboard Activity";
    public static boolean in7DaysMode = true;
    Integer uncompletedWorkorders = 0;
    Integer completedWorkorders = 0;
    Integer plannedWorkorders = 0;
    int[] WOlastWeek = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    String selectedProjectId = "";
    private GeoLocationWrapper geoLocationWrapper;

    WorkingOrdersModel selectedWorkingOrder = null;

    protected com.github.mikephil.charting.charts.BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // content view handeling
        //////////////////////////////
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.dashboard);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNew);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplication(), AddNewWorkOrder.class);
                i.putExtra("projectId", selectedProjectId);
                i.putExtra("workorderId", "");
                i.putExtra("isInEditMode", false);
                startActivity(i);

            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab2.hide();

        // casting
        ////////////////////////
        try {
            txtCompleted = (TextView) findViewById(R.id.circle_completed);
            txtUncompleted = (TextView) findViewById(R.id.circle_incomplete);
            txtPlanned = (TextView) findViewById(R.id.circle_planned);
            txtLastAddress = (TextView) findViewById(R.id.txt_address);
            txtLastDate = (TextView) findViewById(R.id.txt_days_ago);
            txtPercent = (TextView) findViewById(R.id.text_percent);
            arcView = (DecoView) findViewById(R.id.progress_indicator);
            txtProjectName = (TextView) findViewById(R.id.title_project);
            txtPlannedText = (TextView) findViewById(R.id.circle_planned_text);
            txtCompletedText = (TextView) findViewById(R.id.circle_completed_text);
            txtUncompletedText = (TextView) findViewById(R.id.circle_incompleted_text);
            mChart = (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.chart1);
            txtChartName = (TextView) findViewById(R.id.title);
            txtLastName = (TextView) findViewById(R.id.txt_name);
            WorkOrderIcon = (ImageView) findViewById(R.id.wo_icon);
            plannedIcon = (ImageView) findViewById(R.id.dashboard_icon_planned_full);
            completeIcon = (ImageView) findViewById(R.id.dashboard_icon_complete_full);
            inProgressIcon = (ImageView) findViewById(R.id.dashboard_icon_inComplete_full);


            //charts handeling
            /////////////////////
            viewLayout = (View) findViewById(R.id.swipe_holder);
            viewLayout.setOnTouchListener(new OnSwipeTouchListener(getApplication()) {
                @Override
                public void onSwipeLeft() {
                    //  Toast.makeText(getApplication(), " over left", Toast.LENGTH_SHORT).show();
                    updateContent(null, false, false);
                }

                @Override
                public void onSwipeRight() {
                    //Toast.makeText(getApplication(), "over right", Toast.LENGTH_SHORT).show();
                    updateContent(null, false, true);
                }
            });
            viewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickProject(v);
                }
            });


            updateContent(savedInstanceState, true, false);


            mChart.getDescription().setEnabled(false);
            mChart.getLegend().setEnabled(false);
            mChart.setPinchZoom(false);
            mChart.setDoubleTapToZoomEnabled(false);
            mChart.setDrawBarShadow(false);
            mChart.setDrawGridBackground(true);
            mChart.setClickable(true);

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setEnabled(false);

            XAxis xa = mChart.getXAxis();
            xa.setPosition(XAxis.XAxisPosition.BOTTOM);
            xa.setAvoidFirstLastClipping(true);
            xa.setDrawAxisLine(false);
            xa.setDrawLabels(false);
            xa.setEnabled(false);

            setData(false);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    ;

    @Override
    protected void onStart() {
        super.onStart();

        // analytics
        /////////////////
        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();
        analyticsTracker.setScreenName("Dashboard");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.CAMERA:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startBarcodeActivity();
                    }
            }
        }
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(getApplication(), new GestureListener());
        }

        public void onSwipeLeft() {

        }

        public void onSwipeRight() {

        }


        public boolean onTouch(View v, MotionEvent event) {
            //   Toast.makeText(getApplication(), v.toString(), Toast.LENGTH_SHORT).show();
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_DISTANCE_THRESHOLD = 20;
            private static final int SWIPE_VELOCITY_THRESHOLD = 20;

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();

                if (Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
                return false;
            }
        }
    }

    private void updateContent(Bundle savedInstanceState, boolean defaultDashBoard, boolean toRight) {

        ProjectsModel selectedProject = null;
        selectedWorkingOrder = null;
        uncompletedWorkorders = 0;
        completedWorkorders = 0;
        plannedWorkorders = 0;

        selectedWorkingOrder = WorkingOrdersModel.getLastUpdatedWorkorder();

        if (defaultDashBoard == true) {
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    selectedProjectId = "";
                } else {
                    selectedProjectId = extras.getString("projectId");
                }
            } else {
                selectedProjectId = (String) savedInstanceState.getSerializable("projectId");
            }

            try {
                selectedWorkingOrder = WorkingOrdersModel.getLastUpdatedWorkorder();
                if (selectedWorkingOrder != null) {
                    selectedProjectId = selectedWorkingOrder.projectId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else { //called from long click to change to the next project in the list - make it circular
            List<ProjectsModel> tmpProjectList = ProjectsModel.getAllProjects();
            if (toRight == true) {
                for (int i = 0; i < tmpProjectList.size(); i++) {
                    if (tmpProjectList.get(i).projectId.equals(selectedProjectId)) {
                        if (i < tmpProjectList.size() - 1) {
                            selectedProjectId = tmpProjectList.get(i + 1).projectId;
                        } else {

                            selectedProjectId = tmpProjectList.get(0).projectId;
                        }
                        break;
                    }
                }
            } else {
                for (int i = tmpProjectList.size() - 1; i >= 0; i--) {
                    if (tmpProjectList.get(i).projectId.equals(selectedProjectId)) {
                        if (i > 0) {
                            selectedProjectId = tmpProjectList.get(i - 1).projectId;
                        } else {
                            selectedProjectId = tmpProjectList.get(tmpProjectList.size() - 1).projectId;
                        }
                        break;
                    }
                }
            }
            // clean all the data
            mChart.clearValues();
            selectedWorkingOrder = null;
            for (int i = 0; i < 30; i++) {
                WOlastWeek[i] = 0;
            }
            //try to bring in the last updated workorder
            try {
                selectedWorkingOrder = WorkingOrdersModel.getLastUpdatedWorkorderByProjectId(selectedProjectId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // getting project details
        ////////////////
        selectedProject = ProjectsModel.getProjectWithId(selectedProjectId);
        List<WorkingOrdersModel> workingOrderList = WorkingOrdersModel.getWorkingOrderForProject(selectedProjectId);


        //project name
        /////////////////////
        try {
            String projectName = selectedProject.projectName;
            if (projectName.length() > 50)
                projectName = projectName.substring(0, 50);

            txtProjectName.setText(projectName);
        } catch (Exception e) {
            txtProjectName.setText(getResources().getString(R.string.dashboard_noprojects_headline));
            e.printStackTrace();
        }

        //Adress
        /////////////////////
        try {
            txtLastAddress.setText(selectedWorkingOrder.workingOrderAdress);
        } catch (Exception e) {
            txtLastAddress.setText(getResources().getString(R.string.dashboard_default_adress_field));
            e.printStackTrace();
        }

        //Name
        /////////////////////
        try {
            txtLastName.setText(selectedWorkingOrder.workingOrderName);
            if (selectedWorkingOrder.workingOrderName.length() > 50)
                txtLastName.setText(selectedWorkingOrder.workingOrderName.substring(0, 50));

        } catch (Exception e) {
            txtLastName.setText(getResources().getString(R.string.dashboard_default_name_field));
            e.printStackTrace();
        }

        // update time
        /////////////////////
        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
//                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy h:mm aaa");
            txtLastDate.setText(sdf1.format(sdf2.parse(selectedWorkingOrder.lastUpdateTime)));

        } catch (Exception e) {
            txtLastDate.setText(getResources().getString(R.string.dashboard_default_date_field));
            e.printStackTrace();
        }


        // completed work orders
        //////////////////////
        try {
            Integer planned = 0;
            completedWorkorders = 0;
            uncompletedWorkorders = 0;
            plannedWorkorders = 0;
            Log.d(TAG, "onCreate: size is: " + workingOrderList.size());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            for (int i = 0; i < workingOrderList.size(); i++) {
                WorkingOrdersModel wom = workingOrderList.get(i);

                if (wom == null)
                    continue;

                String lastStatus = wom.lastUpdateStatus;

                if (lastStatus.equalsIgnoreCase("complete"))
                    completedWorkorders++;
                else if (lastStatus.equalsIgnoreCase("incomplete"))
                    uncompletedWorkorders++;
                else plannedWorkorders++;


                //lets also collect the data for the buttom graph
                try {
                    String[] sep = workingOrderList.get(i).lastUpdateTime.split(" ");
                    Date date1 = sdf.parse(sep[0]);
                    Date date2 = sdf.parse(sdf.format(new Date()));
                    long diff = date2.getTime() - date1.getTime();
                    int daysPassed = (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                    if (daysPassed < 30) {
                        WOlastWeek[daysPassed]++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            txtCompleted.setText(completedWorkorders.toString());
            txtUncompleted.setText(uncompletedWorkorders.toString());
            txtPlanned.setText(plannedWorkorders.toString());

            //sets the background grey if numbers are 0
            ///////////////////////////////////
            if (completedWorkorders == 0) {
                completeIcon.setImageResource(R.drawable.dashboard_complet_empty);
                txtCompleted.setTextColor(getResources().getColor(R.color.grey_dashboard_notInUse));
                txtCompletedText.setTextColor(getResources().getColor(R.color.grey_dashboard_notInUse));
            } else {
                completeIcon.setImageResource(R.drawable.dashboard_completed_icon);
                txtCompleted.setTextColor(getResources().getColor(R.color.white));
                txtCompletedText.setTextColor(getResources().getColor(R.color.white));
            }

            if (uncompletedWorkorders == 0) {
                inProgressIcon.setImageResource(R.drawable.dashboard_inprogras_inactive_icon);
                txtUncompleted.setTextColor(getResources().getColor(R.color.grey_dashboard_notInUse));
                txtUncompletedText.setTextColor(getResources().getColor(R.color.grey_dashboard_notInUse));
            } else {
                inProgressIcon.setImageResource(R.drawable.dashboard_inprograss_icon);
                txtUncompleted.setTextColor(getResources().getColor(R.color.white));
                txtUncompletedText.setTextColor(getResources().getColor(R.color.white));
            }
            if (plannedWorkorders == 0) {
//                    plannedIcon.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                txtPlanned.setTextColor(getResources().getColor(R.color.grey_dashboard_notInUse));
                txtPlannedText.setTextColor(getResources().getColor(R.color.grey_dashboard_notInUse));
            } else {
//                    plannedIcon.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                txtPlanned.setTextColor(getResources().getColor(R.color.white));
                txtPlannedText.setTextColor(getResources().getColor(R.color.white));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // touch listeners
        /////////////////////////

        txtUncompletedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });
        txtPlannedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });
        txtCompletedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });
        txtUncompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });

        txtPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });

        txtProjectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });


        txtPlanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });

        txtCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickProject(v);
            }
        });

        txtLastAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLastAddress(v);
            }
        });
        txtLastDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLastAddress(v);
            }
        });
        txtLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLastAddress(v);
            }
        });
        WorkOrderIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLastAddress(v);
            }
        });


        //sets range
        //////////////////////////
        try {
            totalWorkorders = plannedWorkorders + uncompletedWorkorders + completedWorkorders;

            Integer percent = 0;
            if (totalWorkorders > 0)
                percent = (100 * completedWorkorders) / totalWorkorders;

            txtPercent.setText(percent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //txtPercent.setAlpha(0.1f);
        try {
            // Create background track
            SeriesItem seriesPlanned = new SeriesItem.Builder(getResources().getColor(R.color.alignment_canvasShading))
                    .setRange(-1, totalWorkorders, 0)
                    .setInitialVisibility(true)
                    .setLineWidth(35f)
                    .build();


            series1IndexP = arcView.addSeries(seriesPlanned);


            SeriesItem seriesCompleted = new SeriesItem.Builder(getResources().getColor(R.color.highlightAlignementColor))
                    .setRange(0, totalWorkorders, 0)
                    .setLineWidth(35)
                    .setInitialVisibility(false)
                    .build();

            series1IndexC = arcView.addSeries(seriesCompleted);


            SeriesItem seriesUncompleted = new SeriesItem.Builder(getResources().getColor(R.color.incompletWorkordersAlignementColor))
                    .setRange(0, totalWorkorders, 0)
                    .setLineWidth(35)
                    .setInitialVisibility(false)
                    .build();

            series1IndexU = arcView.addSeries(seriesUncompleted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createEvents();
        createBarGraph();

        // geo location
        /////////////////////////
        try {
            GeoLocationWrapper geoLocationWrapper = ((appContext) getApplication()).getGeoLocationWrapper();

            if (!geoLocationWrapper.IsGeoLocationPermissionEnabled())
                requestCapabilityPermission(this, "GeoLocation is required", Manifest.permission.ACCESS_FINE_LOCATION);

            if (geoLocationWrapper.IsGeoLocationPermissionEnabled() && !geoLocationWrapper.IsGpsOn()) {
                new AlertDialog
                        .Builder(this)
                        .setMessage("Geo Location is Disabled, please enable it in order to correct application functionality")
                        .show();
            }

            geoLocationWrapper.Init();
            GeoLocation location = geoLocationWrapper.GetGeoLocation();
            geoLocationWrapper.DeInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setData(false);

    }

    private void createBarGraph() {

        try {
            int maxDaysInChart = 30;
            if (in7DaysMode == true) {
                maxDaysInChart = 7;
            }
            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

            for (int i = 0; i < maxDaysInChart; i++) {
                if (WOlastWeek[i] > 0)
                    yVals1.add(new BarEntry(i, WOlastWeek[i]));
                else
                    yVals1.add(new BarEntry(i, 0.1f));
            }

            BarDataSet set1;

            if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(yVals1, "");
                set1.setColor(0xff26d091);
                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);
                BarData data = new BarData(dataSets);
                data.setValueTextSize(2);
                data.setDrawValues(true);

                data.setBarWidth(0.7f);
                mChart.setData(data);
            }

            mChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickUpdateChart(v);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestCapabilityPermission(final Context context, final String message, final String capability) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, capability)) {   // Manifest.permission.CAMERA
            new AlertDialog.Builder(context)
                    .setMessage(message) // "This app needs permission to use The phone Camera in order to activate the Scanner"
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{capability}, 1);
                        }
                    }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{capability}, 1);
        }
    }

    private void createEvents() {

        arcView.executeReset();

        arcView.addEvent(new DecoEvent.Builder(totalWorkorders)
                .setIndex(series1IndexP)
                .setDuration((long) (100))
                .setDelay((long) (0))
                .build());

        arcView.addEvent(new DecoEvent.Builder(totalWorkorders)
                .setIndex(series1IndexU)
                .setDuration(200)
                .setEffectRotations(1)
                .setDelay((long) (50))
                .build());

        arcView.addEvent(new DecoEvent.Builder(uncompletedWorkorders)
                .setIndex(series1IndexU)
                .setDuration(800)
                .setDelay((long) (50))
                .build());

        arcView.addEvent(new DecoEvent.Builder(totalWorkorders)
                .setIndex(series1IndexC)
                .setDuration(500)
                .setDelay((long) (50))
                .build());

        arcView.addEvent(new DecoEvent.Builder(completedWorkorders + uncompletedWorkorders)
                .setIndex(series1IndexC)
                .setDuration(800)
                .setDelay((long) (550))
                .build());

    }

    public void onClickLastAddress(View view) {

        try {
            if (totalWorkorders == 0) {
                Intent i = new Intent(getApplication(), AddNewWorkOrder.class);
                i.putExtra("projectId", selectedProjectId);
                i.putExtra("workorderId", "");
                i.putExtra("isInEditMode", false);
                startActivity(i);

            } else {
                if (selectedWorkingOrder != null) {
                    if (selectedWorkingOrder.lastUpdateStatus.equals("complete")) {
                        Intent i = new Intent(getApplication(), ReportActivity.class);
                        i.putExtra("selectedWorkorderId", selectedWorkingOrder.workordertId);
                        i.putExtra("selectedProjectId", selectedProjectId);
                        i.putExtra("upLinkResult", selectedWorkingOrder.upLinkResult);
                        i.putExtra("downLinkResult", selectedWorkingOrder.downLinkResult);
                        i.putExtra("status", "complete");
                        startActivity(i);
                    } else {
                        if(PermissionUtils.isPermissionGrantedInActivity(this, Manifest.permission.CAMERA)) {
                            startBarcodeActivity();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startBarcodeActivity() {
        Intent i = new Intent(getApplication(), ScanningBarcodeActivityWithFragment.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedProjectId);
        i.putExtra("workorderId", selectedWorkingOrder.workordertId);
        startActivity(i);
    }

    public void onClickProject(View view) {

        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedProjectId);
        startActivity(i);

    }


    public void onClickUpdateChart(View view) {

        setData(true);
    }

    private void setData(boolean toggle) {

        mChart.clearValues();
        int maxDaysInChart = 7;
        TextView txtChartName = (TextView) findViewById(R.id.title);
        if (toggle)
            in7DaysMode = !in7DaysMode;

        if (in7DaysMode == false) {
            maxDaysInChart = 30;
            txtChartName.setText("     Installations - Past 30 Days");
        } else {
            txtChartName.setText("     Installations - Past 7 Days");
        }


        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < maxDaysInChart; i++) {
            if (WOlastWeek[i] > 0)
                yVals1.add(new BarEntry(i, WOlastWeek[i]));
            else
                yVals1.add(new BarEntry(i, 0.1f));
        }

        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            set1.setColor(0x8026d091);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(1);
            data.setDrawValues(true);
            data.setBarWidth(0.7f);
            mChart.setData(data);
        }

        mChart.setAutoScaleMinMaxEnabled(false);
        float yMax = mChart.getYMax();
        if (yMax < 10)
            yMax = 10;
        mChart.setVisibleYRange(0, yMax + 1, YAxis.AxisDependency.LEFT);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            //closes the app
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            // Handle the camera action
        } else if (id == R.id.projects) {
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        } else if (id == R.id.about_section) {

            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.installationGuide) {
            Intent i = new Intent(getApplication(), InstallationGuideActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
