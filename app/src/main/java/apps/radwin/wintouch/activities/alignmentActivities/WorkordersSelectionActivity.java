package apps.radwin.wintouch.activities.alignmentActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.SwipeAdapterWorkorderSelection;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;

public class WorkordersSelectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ViewPager mainViewPager;
    String selectedProjectId = "";
    Button popupPlannedAllButton, popupIncompletteButon, popupCompleteButton, popupShowAllButton;
    ImageView showAllCheckMark, plannedCheckMark, incompleteCheckMark, completeCheckMark;
    private BottomSheetBehavior mBottomSheetBehavior;
    View menuPopupView;

    String TAG = WorkordersSelectionActivity.class.getSimpleName();
    private Menu menuFilter = null;

    static boolean ShowPlanned = true;
    static boolean ShowIncomplete = true;
    static boolean ShowComplete = true;
    static public int selectedPage = 0;

    static WorkordersSelectionActivity _instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workorder_selection_layout_with_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _instance =  this;


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewWorkOrderButton();


            }
        });

        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#05a9f5")));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        ///////////////////////////////////////////
        /////getting the saved instances////
        ///////////////////////////////////////////
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedProjectId = "";
            } else {
                selectedProjectId = extras.getString("projectId");
            }
        } else {
            selectedProjectId = (String) savedInstanceState.getSerializable("projectPosition");
        }

        ///////////////////////////////////
        /////Start of implementation///////
        ///////////////////////////////////
        mainViewPager = (ViewPager) findViewById(R.id.workorderSelectionMainViewPagerContainer); // the main view pager

        SwipeAdapterWorkorderSelection swipeAdapter = new SwipeAdapterWorkorderSelection(getSupportFragmentManager(), selectedProjectId); // calls the adapter and initlizes it

        mainViewPager.setAdapter(swipeAdapter); // sets the adapter

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                WorkordersSelectionActivity.selectedPage = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ///////////////////////////////////////////
        /////start of action bar implementation////
        ///////////////////////////////////////////
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsLayoutWorkorderSelection);
        tabLayout.setupWithViewPager(mainViewPager);


        ///////////////////////////////////////////
        //////////////Buttom Sheet/////////////////
        ///////////////////////////////////////////

        View bottomSheet = findViewById(R.id.bottom_sheet);

        // buttons
        popupShowAllButton = (Button) findViewById(R.id.filter_menu_workorders_show_all);
        popupPlannedAllButton = (Button) findViewById(R.id.filter_menu_workorders_planned);
        popupIncompletteButon = (Button) findViewById(R.id.filter_menu_workorders_incomplete);
        popupCompleteButton = (Button) findViewById(R.id.filter_menu_workorders_complete);

        // check marks
        showAllCheckMark = (ImageView) findViewById(R.id.workorderFilterPopup_showAllCheckMark);
        plannedCheckMark = (ImageView) findViewById(R.id.workorderFilterPopup_PlannedCheckMark);
        incompleteCheckMark = (ImageView) findViewById(R.id.workorderFilterPopup_IncompleteCheckMark);
        completeCheckMark = (ImageView) findViewById(R.id.workorderFilterPopup_completeCheckMark);

        //event listeners
        popupShowAllButton.setOnClickListener(this);
        popupPlannedAllButton.setOnClickListener(this);
        popupIncompletteButon.setOnClickListener(this);
        popupCompleteButton.setOnClickListener(this);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //hides and shows the fab button
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "onStateChanged: state changed 1: " + newState);
                if (newState == 3) { // means the menu is open
                    fab.hide();
                } else if (newState == 4) { // means the menu is down
                    fab.show();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //do nothing

            }
        });

        // by default all checked.
        plannedCheckMark.setVisibility(WorkordersSelectionActivity.ShowPlanned?View.VISIBLE:View.GONE);
        incompleteCheckMark.setVisibility(WorkordersSelectionActivity.ShowIncomplete?View.VISIBLE:View.GONE);
        completeCheckMark.setVisibility(WorkordersSelectionActivity.ShowComplete?View.VISIBLE:View.GONE);
        showAllCheckMark.setVisibility((ShowComplete&&ShowIncomplete&&ShowPlanned)?View.VISIBLE:View.GONE);

        
        supportInvalidateOptionsMenu();
        invalidateOptionsMenu();

        try {
            ProjectsModel pm = ProjectsModel.getProjectWithId(selectedProjectId);
            if (pm!=null) {
                String wo = getResources().getString(R.string.title_activity_workorders_selection);

                String name = pm.projectName;
                if (pm.projectName.length()>20)
                    name = pm.projectName.substring(0,20);

                setTitle(name + " - " + wo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        try {
            mainViewPager.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mainViewPager.setCurrentItem(WorkordersSelectionActivity.selectedPage, true);
                }
            }, 100);
        }
        catch (Exception e ) {}

    }


    public ArrayList<android.support.v4.app.Fragment> getVisibleFragments() {

        List<android.support.v4.app.Fragment> allFragments = getSupportFragmentManager().getFragments();
        ArrayList<android.support.v4.app.Fragment> visibleFragments = new ArrayList<android.support.v4.app.Fragment>();

        try {
            for (android.support.v4.app.Fragment fragment : allFragments) {
                if ((fragment != null) && (fragment.isVisible())) {
                    visibleFragments.add(fragment);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return visibleFragments;
    }

    //user pressed a new work order button
    public void onNewWorkOrderButton() {

        Intent i = new Intent(getApplication(), AddNewWorkOrder.class); // moves to the fine aligment screen
        finish();
        Log.d("projectTags", "work orders selection screen gave the id: " + selectedProjectId);
        i.putExtra("projectId", selectedProjectId);
        i.putExtra("workorderId", "");
        i.putExtra("isInEditMode", false);


//        i.setAction(Intent.ACTION_VIEW);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        else {
            super.onBackPressed();
        }

        Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workorders_selection, menu);

        this.menuFilter = menu;

        try {
            if (menuFilter != null)
            {
                if (WorkordersSelectionActivity.ShowComplete &&
                        WorkordersSelectionActivity.ShowPlanned &&
                        WorkordersSelectionActivity.ShowIncomplete)

                    menuFilter.getItem(0).setIcon(R.drawable.ic_filter_not_used);
                else
                    menuFilter.getItem(0).setIcon(R.drawable.ic_noun_602583_cc);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

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

        /// @@@ EXPORT work orders to json
//        if (id == R.id.action_export)
//        {
//            try {
//                export();
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//            return true;
//        }

        if (id == R.id.action_filter)
        {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void export()
    {
        FileOutputStream fos = null;

        List<WorkingOrdersModel> workOrders = WorkingOrdersModel.getWorkingOrderForProject(selectedProjectId);

        JSONArray arrJson = new JSONArray();
        for (int i=0; i<workOrders.size(); i++) {

            try {
                WorkingOrdersModel wom = workOrders.get(i);
                arrJson.put(wom.toJson());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        String strJson = arrJson.toString();
        String  tmpPath = "";
        try {

            File f  = null;
            File ff = null;
            boolean hasPermission = (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        112);
            }
            // You are allowed to write external storage:
            tmpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WinTouch";
            f = new File(tmpPath);
            if (!f.exists() && !f.mkdirs()) {
                // This should never happen - log handled exception!
            }

            ff = new File(tmpPath,"wo.win");
            fos = new FileOutputStream(ff);
            fos.write(strJson.getBytes(), 0, strJson.length());
            fos.close();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        try {
            if (fos != null)
                fos.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("application/json");
        ProjectsModel connectedProject = ProjectsModel.getProjectWithId(selectedProjectId);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Work Orders from Project: " + connectedProject.projectName);
        intent.putExtra(Intent.EXTRA_TEXT, "This Work Order was sent to you by Wintouch \n");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+tmpPath+"/wo.win"));
        intent.setData(Uri.parse("mailto:"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {

            final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();

            if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects
                Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", storedProjectsModels.get(0).projectId);
                startActivity(i);

            } else { // means we don't have any projects and need to show the empty dashboard
                Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
                startActivity(i);
            }

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

    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Workorder Selection");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

        try {

            if (menuFilter != null)
            {
                if (WorkordersSelectionActivity.ShowComplete &&
                        WorkordersSelectionActivity.ShowPlanned &&
                        WorkordersSelectionActivity.ShowIncomplete)

                    menuFilter.getItem(0).setIcon(R.drawable.ic_filter_not_used);
                else
                    menuFilter.getItem(0).setIcon(R.drawable.ic_noun_602583_cc);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (menuFilter != null)
            {
                if (WorkordersSelectionActivity.ShowComplete &&
                        WorkordersSelectionActivity.ShowPlanned &&
                        WorkordersSelectionActivity.ShowIncomplete)

                    menuFilter.getItem(0).setIcon(R.drawable.ic_filter_not_used);
                else
                    menuFilter.getItem(0).setIcon(R.drawable.ic_noun_602583_cc);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {

        int stateAll        = showAllCheckMark.getVisibility();
        int statePlanned    = plannedCheckMark.getVisibility();
        int stateIncomplete = incompleteCheckMark.getVisibility();
        int stateComplete   = completeCheckMark.getVisibility();


        switch(v.getId()) {
            case R.id.filter_menu_workorders_show_all:
                if (stateAll == View.VISIBLE)
                    stateAll = View.GONE;
                else stateAll = View.VISIBLE;

                stateComplete = stateAll;
                stateIncomplete = stateAll;
                statePlanned = stateAll;
                showAllCheckMark.setVisibility(stateAll);
                plannedCheckMark.setVisibility(stateAll);
                incompleteCheckMark.setVisibility(stateAll);
                completeCheckMark.setVisibility(stateAll);
                break;
            case R.id.filter_menu_workorders_planned:
                if (statePlanned == View.VISIBLE)
                    statePlanned = View.GONE;
                else statePlanned = View.VISIBLE;
                plannedCheckMark.setVisibility(statePlanned);

                if ( (statePlanned==View.GONE) && (stateAll==View.VISIBLE) ) {
                    showAllCheckMark.setVisibility(View.GONE);
//                    return;
                }
                break;
            case R.id.filter_menu_workorders_incomplete:
                if (stateIncomplete== View.VISIBLE)
                    stateIncomplete = View.GONE;
                else stateIncomplete = View.VISIBLE;

                incompleteCheckMark.setVisibility(stateIncomplete);
                if (stateIncomplete== View.GONE && stateAll==View.VISIBLE) {
                    showAllCheckMark.setVisibility(View.GONE);
//                    return;
                }
                break;
            case R.id.filter_menu_workorders_complete:
                if (stateComplete== View.VISIBLE)
                    stateComplete = View.GONE;
                else stateComplete= View.VISIBLE;

                completeCheckMark.setVisibility(stateComplete);
                if (stateComplete== View.GONE && stateAll==View.VISIBLE) {
                    showAllCheckMark.setVisibility(View.GONE);
//                    return;
                }
                break;

        }

        /// If all selected, make sure the ALL category selected as well
        if ( (stateAll==View.GONE) &&
                (stateComplete==View.VISIBLE) &&
                (stateIncomplete==View.VISIBLE) &&
                (statePlanned==View.VISIBLE) ) {
            showAllCheckMark.setVisibility(View.VISIBLE);
        }

        WorkordersSelectionActivity.ShowPlanned = (statePlanned==View.VISIBLE)?true:false;
        WorkordersSelectionActivity.ShowIncomplete = (stateIncomplete==View.VISIBLE)?true:false;
        WorkordersSelectionActivity.ShowComplete= (stateComplete==View.VISIBLE)?true:false;
        // refresh filter changes

        if (WorkordersSelectionActivity.ShowComplete &&
                WorkordersSelectionActivity.ShowPlanned &&
                WorkordersSelectionActivity.ShowIncomplete)

            menuFilter.getItem(0).setIcon(R.drawable.ic_filter_not_used);
        else
            menuFilter.getItem(0).setIcon(R.drawable.ic_noun_602583_cc);



        ArrayList<android.support.v4.app.Fragment> frs =  getVisibleFragments();
        if (frs!=null)
        for (int i=0; i<frs.size(); i++)
        {
            try {
                if (WorkorderSelectionFragment.class.isInstance( frs.get(i) ))
                {
                    final WorkorderSelectionFragment wo = (WorkorderSelectionFragment)frs.get(i);
                    final boolean bShowPlanned = (statePlanned == View.VISIBLE) ? true : false;
                    final boolean bShowIncomplete = (stateIncomplete == View.VISIBLE) ? true : false;
                    final boolean bShowComplete = (stateComplete == View.VISIBLE) ? true : false;

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wo.updateWorkOrderList();
                        }
                    }, 250);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    static void refreshLists()
    {
        try {

            if (_instance == null)
                return;

            ArrayList<android.support.v4.app.Fragment> frs =  _instance.getVisibleFragments();
            if (frs!=null)
                for (int i=0; i<frs.size(); i++)
                {
                    try {
                        if (WorkorderSelectionFragment.class.isInstance( frs.get(i) ))
                        {
                            final WorkorderSelectionFragment wo = (WorkorderSelectionFragment)frs.get(i);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    wo.updateWorkOrderList();
                                }
                            }, 100);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
