package apps.radwin.wintouch.activities.alignmentActivities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.zxing.BarcodeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.canvasRelated.CircleProgressWidget;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.utils.PermissionUtils;
import apps.radwin.wintouch.utils.WifiUtils;

public class ScanningBarcodeActivityWithFragment extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IScanResultHandler {

    BarcodeFragment fragment;
    String selectedProjectId, selectedWorkorderId;
    WorkingOrdersModel currentSelectedWorkorder;
    String TAG = "ScanningBarcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_barcode_activity_with_fragment_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ScanningBarcodeManualInputActivity.class); //go to the next screen
                intent.putExtra("projectId", selectedProjectId);
                intent.putExtra("workorderId", selectedWorkorderId);
                startActivity(intent);
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

        ///////////////////////////////////////
        ////// Start of Implementation ///////
        //////////////////////////////////////

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

        currentSelectedWorkorder.lastUpdateStatus = "inComplete";

        try {
            String name = currentSelectedWorkorder.workingOrderName;
            if (currentSelectedWorkorder.workingOrderName.length() > 20)
                name = currentSelectedWorkorder.workingOrderName.substring(0, 20) + "...";
            setTitle(getResources().getString(R.string.title_activity_scanning__barcode__activity__with__fragment) + " - " + name);
        } catch (Exception e) {
        }
        
            fragment = (BarcodeFragment) getSupportFragmentManager().findFragmentById(R.id.scanFragmentLayoutFragment);

            fragment.setScanResultHandler(this);

            // Support for adding decoding type
            fragment.setDecodeFor(EnumSet.of(BarcodeFormat.QR_CODE));




        try {
            if (currentSelectedWorkorder.workorderWifiSSID != null && currentSelectedWorkorder.workorderWifiSSID.length() > 0) {

                new AlertDialog.Builder(ScanningBarcodeActivityWithFragment.this)
                        .setTitle("Results")
                        .setCancelable(true)
                        .setMessage("Proceed with? \n" + currentSelectedWorkorder.workorderWifiSSID)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                if(WifiUtils.isPoorNetworkAvoidanceEnabled(ScanningBarcodeActivityWithFragment.this)) {
                                    new android.support.v7.app.AlertDialog.Builder(ScanningBarcodeActivityWithFragment.this)
                                            .setTitle(R.string.WifiPopupHeadline)
                                            .setMessage(R.string.WifiPopupMain)
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.WifiPopupPositive, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    WifiUtils.showAdvancedWifiIfAvailable(ScanningBarcodeActivityWithFragment.this);
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton(R.string.WifiPopupNegative, new DialogInterface.OnClickListener() { // delete the project
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();

                                                    Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                                    i.putExtra("projectId", selectedProjectId);
                                                    startActivity(i);
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                                    else {
                                    Intent intent = new Intent(getApplication(), Connect_To_Wif_iActivity.class); //go to the next screen
                                    finish();
                                    intent.putExtra("projectId", selectedProjectId);
                                    intent.putExtra("workorderId", selectedWorkorderId);
                                    startActivity(intent);

                                }

                            }
                        })
                        .setNegativeButton("Scan again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.restart(); // scans again


                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //shows a popup if the workorder has some
        ///////////////Constants//////////////////
        CircleProgressWidget stepsView = (CircleProgressWidget) findViewById(R.id.scanViewStepsView);
        stepsView.changeIndication(1);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PermissionUtils.isPermissionGrantedInActivity(
                this,
                Manifest.permission.CAMERA,
                new Runnable() {
                    @Override
                    public void run() {
                        ScanningBarcodeActivityWithFragment.this.finish();
                    }
                }
        )) {

            Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

            analyticsTracker.setScreenName("Scan Barcode QR Code");
            analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }


    /**
     * when device resumes
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (fragment != null) {
            fragment.restart(); // scans again
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

        Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedProjectId);
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanning__barcode__activity__with_, menu);
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
            final Dialog dialog = new Dialog(ScanningBarcodeActivityWithFragment.this);
            dialog.setContentView(R.layout.dialog_settings);
            dialog.setTitle("Lock Antenna");
//            dialog.setCancelable(false);
            dialog.show();


            Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

            analyticsTracker.setScreenName("Password Dialog");
            analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());


            final Button dialogSaveButton = (Button) dialog.findViewById(R.id.settingsDialog_button_save);
            Button dialogCancelButton = (Button) dialog.findViewById(R.id.settingsDialog_button_cancel);
            final EditText dialogUnitPasswordText = (EditText) dialog.findViewById(R.id.settingsDialog_editText_unitPassword);
            final EditText dialogWifiPasswordText = (EditText) dialog.findViewById(R.id.settingsDialog_editText_wiFiPassword);


            try {
                dialogUnitPasswordText.setText(currentSelectedWorkorder.unitPassword);
                dialogWifiPasswordText.setText(currentSelectedWorkorder.wifiPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }

            dialogUnitPasswordText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.toString().equals("") || dialogWifiPasswordText.getText().toString().equals("")) {
                        dialogSaveButton.setBackground(getResources().getDrawable(R.drawable.gray_rounded_button));
                        dialogSaveButton.setEnabled(false);
                    } else {
                        dialogSaveButton.setBackground(getResources().getDrawable(R.drawable.red_rounded_button_background));
                        dialogSaveButton.setEnabled(true);
                    }
                }
            });

            dialogWifiPasswordText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.toString().equals("") || dialogUnitPasswordText.getText().toString().equals("")) {
                        dialogSaveButton.setBackground(getResources().getDrawable(R.drawable.gray_rounded_button));
                        dialogSaveButton.setEnabled(false);
                    } else {
                        dialogSaveButton.setBackground(getResources().getDrawable(R.drawable.red_rounded_button_background));
                        dialogSaveButton.setEnabled(true);
                    }
                }
            });

            // hides the dialog on click
            ///////////////////////////////////
            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });

            // saves the passwords
            ////////////////////////////
            dialogSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // checks if passwords passed
                    Boolean passedPasswordVerificationUnit = false;
                    Boolean passedPasswordVerificationWifi = false;
                    boolean stopSave= false;
                    //filters out unit password
                    // rule 1 - everything must be small letters, big letters in english and numbers
                    // ruile 2 - langth must be bigger then 5
                    // rule 3 langth must be smaller then 32
                    /////////////////////////////////////////////////////
                    if ((String.valueOf(dialogUnitPasswordText.getText()).matches("[a-zA-Z0-9]*")) && (dialogUnitPasswordText.length() > 5 )  && (dialogUnitPasswordText.length() < 32 ) ) {
                        Log.d(TAG, "only has small letters");
                        passedPasswordVerificationUnit = true;
                    }
                    else {
                        if (dialogUnitPasswordText.length() != 0) {
                            Toast.makeText(getApplication(), getResources().getString(R.string.settings_wrong_unit_password_message),
                                    Toast.LENGTH_LONG).show();
                            stopSave = true;

                        }

                    }
                    //filters out wifi password
                    // rule 1 - everything must be small letters, big letters in english and numbers
                    // ruile 2 - langth must be bigger then 7
                    // rule 3 langth must be smaller then 30
                    /////////////////////////////////////////////////////
                    if ((String.valueOf(dialogWifiPasswordText.getText()).matches("[a-zA-Z0-9]*")) && (dialogWifiPasswordText.length() > 7) && (dialogWifiPasswordText.length() < 30)) {
                        Log.d(TAG, "only has small letters");
                        passedPasswordVerificationWifi = true;
                    } else {
                        if (dialogWifiPasswordText.length() != 0) {
                            Toast.makeText(getApplication(), getResources().getString(R.string.settings_wrong_wifi_password_message),
                                    Toast.LENGTH_LONG).show();
                            stopSave=true;
                        }
                    }

                    //checks if we passed password verification and updates workorder
                    /////////////////////////////////////////////////

                    if (passedPasswordVerificationUnit) {
                        currentSelectedWorkorder.unitPassword = String.valueOf(dialogUnitPasswordText.getText());

                    }
                    if(passedPasswordVerificationWifi) {

                        currentSelectedWorkorder.wifiPassword = String.valueOf(dialogWifiPasswordText.getText());

                    }

                    if( (passedPasswordVerificationWifi || passedPasswordVerificationUnit)  && (stopSave==false)){
                        try {
                            currentSelectedWorkorder.save();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplication(), getResources().getString(R.string.settings_password_approved_message),
                                Toast.LENGTH_LONG).show();
                        dialog.hide();
                    }
                    else if((dialogWifiPasswordText.length()==0) && (dialogUnitPasswordText.length()==0))
                        Toast.makeText(getApplication(), "No Passwords were given",
                                Toast.LENGTH_LONG).show();





                }
            });

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


    /**
     * auto generated to get the scan results
     *
     * @param scanResult
     */
    @Override
    public void scanResult(ScanResult scanResult) {

        if(WifiUtils.isPoorNetworkAvoidanceEnabled(this)) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle(R.string.WifiPopupHeadline)
                    .setMessage(R.string.WifiPopupMain)
                    .setCancelable(false)
                    .setPositiveButton(R.string.WifiPopupPositive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            WifiUtils.showAdvancedWifiIfAvailable(ScanningBarcodeActivityWithFragment.this);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.WifiPopupNegative, new DialogInterface.OnClickListener() { // delete the project
                        public void onClick(DialogInterface dialog, int which) {
                            if (fragment != null) {
                                fragment.restart(); // scans again
                            }

                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            try {
                if (scanResult == null)
                    return;

            // hanndles text finds out if it has an R- or not then adds it
            //////////////////////////////////////
            String scanTextToSave = "0000";
            if (scanResult.getRawResult().getText().length() > 0) {
                if (scanResult.getRawResult().getText().substring(0, 2).equals("R-")) {
                    scanTextToSave = scanResult.getRawResult().getText();
//                    Toast.makeText(getApplicationContext(), "Serial has -R prefext \nR- was not added", //wifi is enabled
//                            Toast.LENGTH_SHORT).show();
                } else {
                    scanTextToSave = "R-"+scanResult.getRawResult().getText();
                }
            }

            // cutts the string to a spacific point
            if (scanTextToSave.contains("|")) {
                String[] textParts = scanTextToSave.split("\\|");
                scanTextToSave = textParts[0];
            }

            scanTextToSave = scanTextToSave.replace(" ", "");


            Log.d(TAG, "scanResult: scanTextToSave: "+scanTextToSave);

            currentSelectedWorkorder.workorderWifiSSID = scanTextToSave;
            currentSelectedWorkorder.lastUpdateStatus = "inComplete";
            try {
                String currentDateandTime="";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date tmpDate = new Date();
                currentDateandTime = sdf.format(tmpDate);
                currentSelectedWorkorder.lastUpdateTime = currentDateandTime;
            }
            catch(Exception e) {

            }

            currentSelectedWorkorder.save(); //saves the work order


            Intent intent = new Intent(getApplication(), Connect_To_Wif_iActivity.class); //go to the next screen
            finish();
            intent.putExtra("projectId", selectedProjectId);
            intent.putExtra("workorderId", selectedWorkorderId);
            startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
}
        // commented code brings up a popup before moving on to the next screen
//        new AlertDialog.Builder(Scanning_Barcode_Activity_With_Fragment.this)
//                .setTitle("Results")
//                .setCancelable(false)
//                .setMessage("is this correct ? \n"+scanResult.getRawResult().getText())
//                .setPositiveButton("Proceede", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                        Intent intent = new Intent(getApplication(), Connect_To_Wif_iActivity.class); //go to the next screen
//                        startActivity(intent);
//
//                    }
//                })
//                .setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        fragment.restart(); // scans again
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();


    }
}
