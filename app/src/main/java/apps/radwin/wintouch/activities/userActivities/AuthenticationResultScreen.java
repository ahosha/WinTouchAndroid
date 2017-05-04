package apps.radwin.wintouch.activities.userActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
 * Written: 01/02/2017.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class AuthenticationResultScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //private ProgressBar mainPrograssBar;
    AligmentManager aligmentManagerClass;

    EditText textFielfAreaCode;

    String codeHistory = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_result_screen_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        textFielfAreaCode = (EditText) findViewById(R.id.textFielfAreaCode);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Authentication Result");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());


        // Check Permission fails on Android OSes below Marshmallow, so the below if sentence is necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                                {Manifest.permission.SEND_SMS},
                        10);
            }
        }


        final AligmentManager aligmentManagerClass = ((appContext) getApplicationContext()).getAligmentManagerVar(); // points to the aligment manager
        final int[] retryCount = {0};

        Timer checkForUpdate = new Timer();
        checkForUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                retryCount[0]++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (aligmentManagerClass.getSmsRecived() != null) {
                            checkCode(aligmentManagerClass.getSmsRecived());
                        } else {
//                            AutDescriptionText.setText("noSms Received: "+retryCount[0]);
                        }

                    }
                });


            }
        }, 1000, 3000);


    }

    private void checkCode(String smsRecived) {

        if (!smsRecived.equals(codeHistory)) {

            codeHistory = smsRecived;

            if (smsRecived.matches(".*\\d+.*")) {
                String template = "Radwin authentication code:";

                String codeFromMessage = null;
                try {
                    int messageStartIndex = smsRecived.indexOf(template) + 28;
                    int messageEndIndex = messageStartIndex + 4;

                    Log.d("checkCode", "checkCode: messageStartIndex: messageStartIndex "+messageStartIndex);
                    Log.d("checkCode", "checkCode: messageEndIndex: messageEndIndex "+messageEndIndex);
                    Log.d("checkCode", "checkCode: messageEndIndex: smsRecived.length "+smsRecived.length());

                    codeFromMessage = smsRecived.substring(messageStartIndex, messageEndIndex);



                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("checkCode", "checkCode: smsRecived.indexOf(template): " + smsRecived.indexOf(template));
                Log.d("checkCode", "checkCode: codeFromMessage: "+codeFromMessage);

                if (smsRecived.indexOf(template) != -1) {

                    if (! codeFromMessage.equals("3112")) {
                        textFielfAreaCode.setText(codeFromMessage);

                        Snackbar.make(findViewById(android.R.id.content), "Code is Wrong", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.RED)
                                .setAction("Action", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //
                                    }
                                }).show();

                    } else {
                        textFielfAreaCode.setText(codeFromMessage);

                        Snackbar.make(findViewById(android.R.id.content), "Code is Correct", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.GREEN)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //
                                    }
                                }).show();

                    }

                }

            }
        }
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
