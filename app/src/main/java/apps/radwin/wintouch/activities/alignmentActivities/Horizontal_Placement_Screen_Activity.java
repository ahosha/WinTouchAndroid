package apps.radwin.wintouch.activities.alignmentActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.canvasRelated.CircleProgressWidget;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.interfaces.HttpCommunicationInterface;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class Horizontal_Placement_Screen_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String selectedProjectId, selectedWorkorderId;
    AligmentManager aligmentManagerClass; // will hold an instance of the aligment manager
    Button mainButton;
    ImageView horizontalImageView;
    Timer animationTimer;
    private final int CALIBRATION_TIME_OUT = 3000;
    WorkingOrdersModel selectedWorkorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horizontal_activity_placement_layout);
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

        if (savedInstanceState == null) { // Gets the selected Project Id and the Selected Workorder Id
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedProjectId = "";
                selectedWorkorderId = "";
            } else {
                selectedProjectId = extras.getString("projectId");
                selectedWorkorderId = extras.getString("workorderId");
            }
        } else {
            selectedProjectId = (String) savedInstanceState.getSerializable("projectId");
            selectedWorkorderId = (String) savedInstanceState.getSerializable("workorderId");
        }

        selectedWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);
        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar(); // points to the aligment manager

        mainButton = (Button) findViewById(R.id.horizontal_screen_dialog_layout_main_Button);
        horizontalImageView = (ImageView) findViewById(R.id.horizontalImageViewPlaceHolder);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //cancels the animation timer
                try {
                    animationTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                aligmentManagerClass.startAlignmentAction("start", callbackForStartAlignmentAction);


            }
        });


        // upper bar
        //////////////////////////
        CircleProgressWidget stepsView = (CircleProgressWidget) findViewById(R.id.horizontalViewStepsView);
        stepsView.changeIndication(4);

        //initlizing timer
        ///////////////////////////
        initlizeAnimationTimer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        wifiWrapper.forgetNetwork(selectedWorkorder, getApplicationContext());
        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId",selectedProjectId);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.horizontal__placement__screen_, menu);
        return true;
    }

    HttpCommunicationInterface<Boolean> callbackForStartAlignmentAction = new HttpCommunicationInterface<Boolean>()
    {
        @Override
        public void Invocator(final Boolean result) {

            new AlertDialog.Builder(Horizontal_Placement_Screen_Activity.this)
                    .setTitle(getResources().getString(R.string.dialog_header_before_alignment))
                    .setMessage(getResources().getString(R.string.dialog_mainText_before_alignment))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            /// Measuring waits 3 seconds
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() { // runs a deley in the code for 1 second
                @Override
                public void run() { // runs a delay in the code for 1 second

                    if (result) {
                        Intent intent = new Intent(getApplication(), AlignmentMainActivity.class); //go to the next screen
                        intent.putExtra("projectId", selectedProjectId);
                        intent.putExtra("workorderId", selectedWorkorderId);
                        intent.putExtra("isCameFromLinkEvaluation", false);
                        startActivity(intent);


                    } else { // cannot start we had an error from the ulc

                        new AlertDialog.Builder(Horizontal_Placement_Screen_Activity.this) // showing the user horizontal placement data
                                .setTitle(getResources().getString(R.string.ulc_statement_title))
                                .setCancelable(false)
                                .setMessage(getResources().getString(R.string.ulc_statement_message))
                                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        // continue with delete
                                        Intent intent = new Intent(getApplication(), projectSelectionMainFragment.class); //go to the next screen
                                        startActivity(intent);

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }, CALIBRATION_TIME_OUT);
        }
    };

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
                wifiWrapper.forgetNetwork(selectedWorkorder, getApplicationContext());
                Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", storedProjectsModels.get(0).projectId);
                startActivity(i);

            } else { // means we don't have any projects and need to show the empty dashboard
                wifiWrapper.forgetNetwork(selectedWorkorder, getApplicationContext());
                Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
                startActivity(i);
            }

        } else if (id == R.id.projects) {
            wifiWrapper.forgetNetwork(selectedWorkorder, getApplicationContext());
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        }else if (id == R.id.about_section) {

            wifiWrapper.forgetNetwork(selectedWorkorder, getApplicationContext());
            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    int timerIndex = 0;

    public void initlizeAnimationTimer() {

        animationTimer = new Timer();
        animationTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                // changes the image
                if (timerIndex == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                horizontalImageView.setImageResource(R.drawable.horizontal_placement_two);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (timerIndex == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                horizontalImageView.setImageResource(R.drawable.horizontal_placement_three);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (timerIndex == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                horizontalImageView.setImageResource(R.drawable.horizontal_placement_one);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (timerIndex == 3) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                horizontalImageView.setImageResource(R.drawable.horizontal_placement_one);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                if (timerIndex < 4) {
                    timerIndex++;
                } else {
                    timerIndex = 0;
                }
            }
        }, 250, 500);
    }
}
