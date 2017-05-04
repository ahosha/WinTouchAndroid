package apps.radwin.wintouch.activities.alignmentActivities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.SwipeAdapterInstallationView;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 28/12/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class InstallationViewSelectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager mainViewPager;
    AligmentManager alignmentManagerClass;
    public static int selectedPage = 0;
    String positionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.installation_view_selection_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // generated android code

        // fab handeling
        ///////////////////
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent i = new Intent(getApplication(), Add_New_Project_Activity.class); // moves to the fine aligment screen
//                i.putExtra("isCameFromAddNewProject", true);
//                i.putExtra("isInEditMode", false);
//                startActivity(i);

            }
        });

        fab.hide();

        fab.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#05a9f5")));

        // drawer handeling
        //////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.installationGuide);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                positionId = "0";
            } else {
                positionId = extras.getString("positionId");
            }
        } else {
            positionId = (String) savedInstanceState.getSerializable("positionId");
        }

        /////////////////////////////////////////
        //////// Start Of Implementation ////////
        /////////////////////////////////////////
        mainViewPager = (ViewPager) findViewById(R.id.installationViewPagerContainer); // the main view pager

        // setting adapter
        /////////////////////////
        SwipeAdapterInstallationView swipeAdapter = new SwipeAdapterInstallationView(getSupportFragmentManager(), positionId); // calls the adapter and initlizes it
        mainViewPager.setAdapter(swipeAdapter);



        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ///////////////////////////////////////////
        /////start of action bar implementation////
        ///////////////////////////////////////////
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsLayoutInstallationView);
        tabLayout.setupWithViewPager(mainViewPager);

        alignmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar();


    }


    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Installation Guide Screen");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

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
