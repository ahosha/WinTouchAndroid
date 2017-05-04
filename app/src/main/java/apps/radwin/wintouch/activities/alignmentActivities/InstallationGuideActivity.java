package apps.radwin.wintouch.activities.alignmentActivities;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.InstallationGuideAdapter;
import apps.radwin.wintouch.models.InstallationGuideModel;
import apps.radwin.wintouch.models.ProjectsModel;


/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 25/12/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class InstallationGuideActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    ListView installationGuideList;
    VideoView videoHolder;
    String TAG = "installagionGuide";
    ArrayList<InstallationGuideModel> installationsData = new ArrayList<InstallationGuideModel>();
    List<InstallationGuideModel> storedInstallationguides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation_guides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.installationGuide);
        navigationView.setNavigationItemSelectedListener(this);

        storedInstallationguides = InstallationGuideModel.getAllInstallationGuides();

        installationGuideList = (ListView) findViewById(R.id.installation_guide_list);

        installationGuideList.setOnItemClickListener(this);

        installationGuideList.setDivider(null);
        installationGuideList.setDividerHeight(0);

        // initilizing installation guides
        ////////////////////////////
        initInstallationData();

        InstallationGuideAdapter installationsAdapter = new InstallationGuideAdapter(getApplicationContext(), installationsData); // Create the adapter to convert the array to views

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data


        installationGuideList.setAdapter(installationsAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.installationGuide_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    startVideo(R.raw.ulc_animation_one);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        fab.hide();


    }

    private void initInstallationData() {

        for (int i = 0; i < storedInstallationguides.size(); i++) {
            installationsData.add(storedInstallationguides.get(i));

        }

        Log.d(TAG, "initInstallationData: installationsDatainitInstallationData: installationsData size: "+installationsData.size());

    }


    public void startVideo(int vidPath) {

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        videoHolder = new VideoView(getApplicationContext());
        //if you want the controls to appear
        videoHolder.setMediaController(new MediaController(getApplicationContext()));
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + vidPath); //do not add any extension
        //if your file is named sherif.mp4 and placed in /raw
        //use R.raw.sherif
        videoHolder.setVideoURI(video);
        setContentView(videoHolder);
        videoHolder.start();

        videoHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    ((VideoView) v).stopPlayback();
                    Log.d(TAG, "onTouch: ");
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();

        if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects
            Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
            i.putExtra("projectId", storedProjectsModels.get(0).projectId);
            startActivity(i);

        } else { // means we don't have any projects and need to show the empty dashboard
            Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
            startActivity(i);
        }

        finish();
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
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // list view item click listener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = new Intent(getApplication(), InstallationViewSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("positionId", storedInstallationguides.get(position).installationGuideId);
        startActivity(i);


    }


}
