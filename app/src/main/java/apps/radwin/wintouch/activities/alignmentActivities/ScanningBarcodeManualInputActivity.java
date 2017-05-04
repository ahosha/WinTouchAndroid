package apps.radwin.wintouch.activities.alignmentActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.canvasRelated.CircleProgressWidget;
import apps.radwin.wintouch.interfaces.RetrofitInterface;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;
import apps.radwin.wintouch.utils.WifiUtils;

public class ScanningBarcodeManualInputActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText manualInputText;
    Button saveButton;
    String selectedProjectId, selectedWorkorderId;
    WorkingOrdersModel currentSelectedWorkorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_barcode_manual_input_layout);
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        //gets the project id
        //getting the saved instances
        if (savedInstanceState == null) {
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

        //WORKORDER SQL UPDATE
        currentSelectedWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // updates the data in the sql

        manualInputText = (EditText) findViewById(R.id.scanning_barcode_manual_barcodeNumber_text);
        saveButton = (Button) findViewById(R.id.scanning_barcode_manual_barcodeNumber_saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSavePressed();

            }
        });

        saveButton.setVisibility(View.GONE);

        manualInputText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // Perform action on key press
//                     WINT-283 - Manually Barcode Screen Serial Number Input
//                     Avner: I'm not sure about it...
                    onSavePressed();
                }
                return false;
            }
        });


        if (manualInputText.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


        ///////////////Constants//////////////////
        //shows a popup if the workorder has some
        ///////////////Constants//////////////////
        CircleProgressWidget stepsView = (CircleProgressWidget) findViewById(R.id.manualScanViewStepsView);
        stepsView.changeIndication(1);

//        StepsView mStepsView = (StepsView) findViewById(R.id.);
//
//        final String[] labels = {"", "", "", "", ""};
//
//        mStepsView.setLabels(labels)
//                .setBarColorIndicator(getApplication().getResources().getColor(R.color.material_blue_grey_800))
//                .setProgressColorIndicator(getApplication().getResources().getColor(R.color.alignementColorRed))
//                .setLabelColorIndicator(getApplication().getResources().getColor(R.color.orange))
//                .setCompletedPosition(0
//                )
//                .drawView();


    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Manual Input QR Code");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onSavePressed() {
        if(WifiUtils.isPoorNetworkAvoidanceEnabled(ScanningBarcodeManualInputActivity.this)) {
            new android.support.v7.app.AlertDialog.Builder(ScanningBarcodeManualInputActivity.this)
                    .setTitle(R.string.WifiPopupHeadline)
                    .setMessage(R.string.WifiPopupMain)
                    .setCancelable(false)
                    .setPositiveButton(R.string.WifiPopupPositive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            WifiUtils.showAdvancedWifiIfAvailable(ScanningBarcodeManualInputActivity.this);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.WifiPopupNegative, new DialogInterface.OnClickListener() { // delete the project
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            // checks if user has input anything in the text field
            if (manualInputText.getText().length() > 0) {

            if (validateSSID(String.valueOf(manualInputText.getText()))) {

                new AlertDialog.Builder(ScanningBarcodeManualInputActivity.this)
                        .setTitle("Results")
                        .setCancelable(false)
                        .setMessage("Is this correct? \n R-" + manualInputText.getText())
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String ssid = "R-"+String.valueOf(manualInputText.getText());
                                currentSelectedWorkorder.workorderWifiSSID = ssid;
                                currentSelectedWorkorder.lastUpdateStatus = "inComplete";
                                try {
                                    String currentDateandTime = "";
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                                    Date tmpDate = new Date();
                                    currentDateandTime = sdf.format(tmpDate);
                                    currentSelectedWorkorder.lastUpdateTime = currentDateandTime;
                                } catch (Exception e) {

                                }
                                currentSelectedWorkorder.save();
                                if (AligmentManager.IS_ALIGMENT_DEBUG_ON()) {
                                    String newIpAddressForDebug = ssid;
                                    RetrofitInterface.Factory.changeApiBaseUrl(newIpAddressForDebug);
                                }

                                Intent intent = new Intent(getApplication(), Connect_To_Wif_iActivity.class); //go to the next screen
                                intent.putExtra("projectId", selectedProjectId);
                                intent.putExtra("workorderId", selectedWorkorderId);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Edit Text", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (manualInputText.requestFocus()) {
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                }
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                                if (inputMethodManager != null) {
                                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                }
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        } else { // user didn't input anything in the editfield

            Toast.makeText(ScanningBarcodeManualInputActivity.this, getResources().getString(R.string.manual_bacode_enter_sn),
                    Toast.LENGTH_SHORT).show();

            }
        }
    }

    private boolean validateSSID(String text) {
        if (text.length() == 16) {
            if (text.matches("[A-Za-z0-9]+")) {
                return true ;
            } else {
                Toast.makeText(ScanningBarcodeManualInputActivity.this, R.string.mainual_ssid_barcode_charechters_error,
                        Toast.LENGTH_LONG).show();
                return false;
            }

        } else {
            Toast.makeText(ScanningBarcodeManualInputActivity.this, R.string.mainual_ssid_barcode_langth_error,
                    Toast.LENGTH_LONG).show();
            return false;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.scanning__barcode__manual__input_, menu);
        getMenuInflater().inflate(R.menu.create_workorder_check_mark, menu);
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
        if (id == R.id.checkMarkButton) {
            onSavePressed();
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
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        } else if (id == R.id.installationGuide) {
            Intent i = new Intent(getApplication(), InstallationGuideActivity.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.about_section) {

            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
