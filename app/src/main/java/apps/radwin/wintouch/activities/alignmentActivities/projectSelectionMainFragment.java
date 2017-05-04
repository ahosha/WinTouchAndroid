package apps.radwin.wintouch.activities.alignmentActivities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
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
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.SwipeAdapterProjectSelection;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;
import apps.radwin.wintouch.utils.PermissionUtils;


public class projectSelectionMainFragment extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager mainViewPager;
    AligmentManager alignmentManagerClass;
    private GeoLocationWrapper geoLocationWrapper;
    static public projectSelectionMainFragment _instance = null;
    public static int selectedPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_selection_main_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // generated android code

        // closes up the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        _instance =  this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplication(), AddNewProjectActivity.class); // moves to the fine aligment screen
                i.putExtra("isCameFromAddNewProject", true);
                i.putExtra("isInEditMode", false);
                startActivity(i);

            }
        });

        fab.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#05a9f5")));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.projects);
        navigationView.setNavigationItemSelectedListener(this);

        /////////////////////////////////////////
        //////// Start Of Implementation ////////
        /////////////////////////////////////////
        mainViewPager = (ViewPager) findViewById(R.id.projectSelectionMainViewPagerContainer); // the main view pager

        SwipeAdapterProjectSelection swipeAdapter = new SwipeAdapterProjectSelection(getSupportFragmentManager()); // calls the adapter and initlizes it

        mainViewPager.setAdapter(swipeAdapter); // sets the adapter

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                projectSelectionMainFragment.selectedPage = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // asks permission to use the devices camera
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(projectSelectionMainFragment.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            requestExternalStoragePermission(this);


        ///////////////////////////////////////////
        /////start of action bar implementation////
        ///////////////////////////////////////////
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsLayoutProjectSelection);
        tabLayout.setupWithViewPager(mainViewPager);

        alignmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar();

        // asks permission to use the devices camera
        if (PermissionUtils.isPermissionGrantedInActivity(this, Manifest.permission.CAMERA)) {}

        try {
            geoLocationWrapper = ((appContext) getApplication()).getGeoLocationWrapper();

            if (!geoLocationWrapper.IsGeoLocationPermissionEnabled())
                DashboardActivity.requestCapabilityPermission(this, "GeoLocation is required", Manifest.permission.ACCESS_FINE_LOCATION);

            if (geoLocationWrapper.IsGeoLocationPermissionEnabled() && !geoLocationWrapper.IsGpsOn()) {
                new AlertDialog
                        .Builder(this)
                        .setMessage("Geo Location is Disabled, please enable it in order to correct application functionality")
                        .show();
            }

            geoLocationWrapper.Init();
            GeoLocation location = geoLocationWrapper.GetGeoLocation();
            geoLocationWrapper.DeInit();

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
                    mainViewPager.setCurrentItem(projectSelectionMainFragment.selectedPage, true);
                }
            }, 100);
        }
        catch (Exception e ) {}

//
//
//        new AlertDialog
//                .Builder(this)
//                .setMessage("Long: " + location.getLongitude() + " Lat: " + location.getLatitude())
//                .show();

        // closes up the keyboard
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


    }



    private static void requestExternalStoragePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setMessage("This app needs permission to use the phone external storage in order to save final images")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                        }
                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }




    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Projects Selection");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();

            if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects
                Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", storedProjectsModels.get(0).projectId);
                startActivity(i);

            } else { // means we don't have any projects and need to show the empty dashboard
                Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
                startActivity(i);
            }

//            //closes the app
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        /// @@@
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_selection_main, menu);
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

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Create and show the dialog.
            projectSelectionMainFragment.SomeDialog newFragment = new projectSelectionMainFragment.SomeDialog ();
            newFragment.show(ft, "dialog");


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
                Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", storedProjectsModels.get(0).projectId);
                startActivity(i);

            } else { // means we don't have any projects and need to show the empty dashboard
                Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
                startActivity(i);
            }

        } else if (id == R.id.projects) {
//            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
//            startActivity(i);

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
        catch (Exception e) {
            e.printStackTrace();
        }

        return visibleFragments;
    }

    static void refreshLists()
    {
        try {

            if (_instance == null)
                return;

            ArrayList<android.support.v4.app.Fragment> frs =  _instance.getVisibleFragments();
            if (frs!=null)
                Log.d("projectSelection", "refreshLists: frs.size is: "+frs.size());

                for (int i=0; i<frs.size(); i++)
                {
                    try {
                        if (ProjectSelectionFragmentSevenDays.class.isInstance( frs.get(i) ))
                        {
                            final ProjectSelectionFragmentSevenDays wo = (ProjectSelectionFragmentSevenDays)frs.get(i);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    wo.updateList();
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

    public static class SomeDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_settings);
            dialog.setTitle("Lock Antenna");
            dialog.setCancelable(false);
            dialog.show();

            return dialog;

        }
    }

}


