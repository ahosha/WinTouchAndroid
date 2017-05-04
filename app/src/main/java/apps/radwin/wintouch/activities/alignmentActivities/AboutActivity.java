package apps.radwin.wintouch.activities.alignmentActivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class AboutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Integer clickCounter = 0;

    AligmentManager aligmentManagerClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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

        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar(); // points to the aligment manager

        // sets version
        /////////////////////

        String versionName;
        int versionCode;

        final TextView versionTextView = (TextView) findViewById(R.id.textView18);
        final TextView bottomVersionTextView = (TextView) findViewById(R.id.textView17);

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionName = String.valueOf(1);
            versionCode = 1;
        }
        versionTextView.setText("Version: " + String.valueOf(versionName)); // updates string
        bottomVersionTextView.setText("Build: " + String.valueOf(versionCode)); // updates string);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        versionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickCounter++;

                if (clickCounter==10)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                createDummyProjects();
                                Toast.makeText(getApplication(), "Debug mode activated",
                                        Toast.LENGTH_LONG).show();

                                aligmentManagerClass.setShowTextCheat(true);
                                aligmentManagerClass.setShowLinkStateCheat(true);

                                versionTextView.setVisibility(View.GONE);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }



                        }
                    }, 300);

//                    versionTextView.setText("Creating projects...");
                    versionTextView.setText("setting debug mode");
                    versionTextView.setEnabled(false);
                }
                else if (clickCounter>6)
                {
                    versionTextView.setText("a few more times...");
                }
            }
        });
    }


    private void createDummyProjects()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        for (int i=0;i<20;i++) {
            ProjectsModel p= new ProjectsModel();
            p.projectName = "Random Project "+i;
            p.isBestEffort = ((i%2)==0)?true:false;
            p.description = "dummy description";
            p.truePutUp = 20.0;
            p.truePutDown =30.0;
            p.projectId= UUID.randomUUID().toString();
            p.currentBandId = "";
            p.selectedFrequencysList = "";
            p.currentChannelBandwith = "";
            p.avilableBandsIdLList = "";

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1*(i*2) );
            java.util.Date dt = cal.getTime();


            long date = System.currentTimeMillis();
            SimpleDateFormat sdfFormat = new SimpleDateFormat("MMM dd, yyyy h:mm aaa");
            String dateString = sdfFormat.format(date);

            String currentDateandTime = dateString;
//            String currentDateandTime = sdf.format( new Date() );
            p.creationDate = currentDateandTime;
            p.save();

            for(int t=0;t<100;t++) {
                WorkingOrdersModel w = new WorkingOrdersModel();
//                w.longitude=0;
//                w.latitude=0;
                w.projectId = p.projectId;
                w.workordertId = UUID.randomUUID().toString();
                w.workingOrderName = "my work order "+t;
                w.workingOrderAdress = "Habarzel " + String.valueOf(1+t) + " Tel Aviv";
                w.isBestEffort = true;
                w.workingOrderPhoneNumber = "03-7681678";
                w.macAddress = "0015675ed9ca";
                w.workorderWifiSSID = "abcdefh";
                w.selectedFrequency = 5760;
                w.orderLongitude = 34.29454;
                w.orderLatitude = 32.45324;
                w.lastUpdateTime = currentDateandTime;
                w.lastUpdateStatus = ((t%3)==0)?"complete":"inComplete";
                w.CurrentSelectedBand = "5.475-6.150"; // updates the data in the sql
                w.currentChannelBandwith = "20"; // updates the data in the sql
                w.selectedFrequencysList = "5.8"; // updates the frequency in the sql
                w.truePutUp = 25.0; // updates the trueput up and down
                w.truePutDown = ((t%2)==0)?1.5:20;
                w.downLinkResult = 15.0;
                w.upLinkResult = 30.0;

                w.save();
            }
        }
        Toast.makeText(getApplication(), "50 projects x 50 work orders were created",
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("About Screen");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            // Handle the camera action
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

        } else if (id == R.id.installationGuide) {
            Intent i = new Intent(getApplication(), InstallationGuideActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
