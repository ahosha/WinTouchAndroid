package apps.radwin.wintouch.activities.userActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.activities.alignmentActivities.AboutActivity;
import apps.radwin.wintouch.activities.alignmentActivities.DashboardActivity;
import apps.radwin.wintouch.activities.alignmentActivities.EmptyDashboardActivity;
import apps.radwin.wintouch.activities.alignmentActivities.projectSelectionMainFragment;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 29/01/2017.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class AutenticatingUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //private ProgressBar mainPrograssBar;
    AligmentManager aligmentManagerClass;
    String messageContent;
    TextView AutDescriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_screen_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent i = new Intent(getApplication(), AuthenticationResultScreen.class); // moves to the fine aligment screen
                startActivity(i);
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

        /////////////////////////////
        ///start of implementation///
        ////////////////////////////

//        //gets the project id
//        //getting the saved instances
//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//            if (extras == null) {
//                selectedProjectId = "";
//                selectedWorkorderId = "";
//            } else {
//                selectedProjectId = extras.getString("projectId");
//                selectedWorkorderId = extras.getString("workorderId");
//            }
//        } else {
//            selectedProjectId = (String) savedInstanceState.getSerializable("projectId");
//            selectedWorkorderId = (String) savedInstanceState.getSerializable("workorderId");
//        }

        //gets the aligment manager class
        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar();

        AutDescriptionText = (TextView) findViewById(R.id.AutDescriptionText);

        Spinner countrySpinner = (Spinner) findViewById(R.id.VerifyPhoneCountrySpinner);

        String[] countrysArray = getResources().getStringArray(R.array.country_arrays);

//        // Check Permission fails on Android OSes below Marshmallow, so the below if sentence is necessary
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]
//                        {Manifest.permission.SEND_SMS},
//                        10);
//            }
//        }



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countrysArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(dataAdapter);

        final AligmentManager aligmentManagerClass =  ((appContext) getApplicationContext()).getAligmentManagerVar(); // points to the aligment manager
        final int[] retryCount = {0};

//        Timer checkForUpdate = new Timer();
//        checkForUpdate.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                retryCount[0]++;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (aligmentManagerClass.getSmsRecived() != null) {
//                            AutDescriptionText.setText(aligmentManagerClass.getSmsRecived());
//                        } else {
//                            AutDescriptionText.setText("noSms Received: "+retryCount[0]);
//                        }
//
//                    }
//                });
//
//
//            }
//        }, 1000, 3000);


    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Authentication Init");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
//        wifiWrapper.forgetNetwork(currentSelectedWorkorder, getApplicationContext());
//
//        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
//        i.putExtra("projectId", selectedProjectId);
//        startActivity(i);
//        finish();

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

            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        } else if (id == R.id.about_section) {

            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
