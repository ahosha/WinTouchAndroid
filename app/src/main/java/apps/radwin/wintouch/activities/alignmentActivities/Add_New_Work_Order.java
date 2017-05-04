package apps.radwin.wintouch.activities.alignmentActivities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;
import apps.radwin.wintouch.screenManagers.PojoParserHelper;
import apps.radwin.wintouch.utils.PermissionUtils;

public class Add_New_Work_Order extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    com.rengwuxian.materialedittext.MaterialEditText workorderAdress, workorderName, workorderPhoneNumber;
    Button saveButton, beginAlignmentButton;
    Tracker analyticsTracker;
    ProgressBar spinner = null;
    WorkingOrdersModel selectedWorkingOrder;
    String selectedProjectId;
    Boolean isInEditMode = false;
    ProjectsModel connectedProject;
    ArrayList<String> selectedFrequency = new ArrayList<String>();
    String workorderIdFromWorkorders = "";
    String[] selectedFrequencys;
    String TAG = "AddNewWorkorder";
    Dialog inputFieldDialog;
    int listItemCounter = 0; // counts how many items you clicked on
    private GeoLocationWrapper geoLocationWrapper;
    private AligmentManager aligmentManagerClass;
    public LatLng woLatLng = new LatLng(0.0,0.0);
    private Dialog addressDlg;
    FloatingActionButton fab = null;
    private String strLastKnownAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_workorder_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar();

        geoLocationWrapper = ((appContext) getApplication()).getGeoLocationWrapper();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                spinner.setVisibility(View.VISIBLE);
                final String strOldAddress = workorderAdress.getText().toString();


                Thread geoThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // turn off wifi if has no internet
                        Boolean hasInternet = false;
                        try {
                            hasInternet = wifiWrapper.hasInternetAccess(Add_New_Work_Order.this);
                            if (hasInternet==false)
                            {
                                WifiManager wifiManager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
                                wifiManager.setWifiEnabled(true);
                                wifiManager.setWifiEnabled(false);
                            }
                        }
                        catch (Exception e)
                        {

                        }



                        ArrayList<Address> arrAddr = new ArrayList<Address>();
                        // address already written, look for coordinates
                        if (!strOldAddress.isEmpty())
                        {
                            arrAddr =  geoLocationWrapper.getLocationsFromAddress( strOldAddress );
                        }
                        else {

                            String address = "";

                            // sometimes it takes a fgew queries to get a fix
                            // once we get results stop.
                            for (int i = 0; i < 10; i++) {
                                try {
                                    arrAddr = geoLocationWrapper.GetAddressList();
                                    if (arrAddr.size() > 0)
                                        break;
                                    ;

                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                }
                            }
                        }
                        final ArrayList<Address> arr = arrAddr;

                        runOnUiThread(new Runnable() { // runs everything back in the UI thread
                            @Override
                            public void run() {

                                try {
                                    if (arr.size() == 0) {
                                        // even if we don't have a real address, allow to skip it.
                                        strLastKnownAddress = workorderAdress.getText().toString();
                                        woLatLng = new LatLng(0.0,0.0);
                                    }
                                }catch ( Exception e) {}

                                showAddressList(arr);
                                spinner.setVisibility(View.GONE);
                            }
                        });
                    }
                });

                geoThread.start();
            }
        });

        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#05a9f5")));

        //fab.hide(); // hides the fab button

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // does the back button
        /////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        workorderName = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.newWorkorder_workorderName); // casting
        workorderAdress = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.newWorkorder_workorderAdress); // casting
        workorderPhoneNumber = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.newWorkorder_phoneNumber); // casting
        beginAlignmentButton = (Button) findViewById(R.id.newWorkorder_beginAlignment_button); // casting
        spinner = (ProgressBar)findViewById(R.id.loading_spinner);
        spinner.setVisibility(View.GONE);

        beginAlignmentButton.setOnClickListener(this);


        //getting the saved instances
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                selectedProjectId = "";
                isInEditMode = false;
                workorderIdFromWorkorders = "";
            } else {
                selectedProjectId = extras.getString("projectId");
                isInEditMode = extras.getBoolean("isInEditMode");
                workorderIdFromWorkorders = extras.getString("workorderId");
            }
        } else {

            selectedProjectId = (String) savedInstanceState.getSerializable("projectPosition");
            isInEditMode = (Boolean) savedInstanceState.getSerializable("projectId");
            workorderIdFromWorkorders = (String) savedInstanceState.getSerializable("workorderId");
        }

        Log.d("myLogs", "selectedProjectId in add new workorder is: " + selectedProjectId);

        try {
            //changes the text for the buttons by the workorder
            connectedProject = ProjectsModel.getProjectWithId(selectedProjectId); // initilizes the connected project

        } catch (Exception e) {
            e.printStackTrace();
        }

        connectedProject = ProjectsModel.getProjectWithId(selectedProjectId); // initilizes the connected project

        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        if (isInEditMode) { // checks if the user is in edit mode


            try { // updates ui if it's in edit mode
              //  saveButton.setText("Update WorkOrder"); - not initilaized and causes exception
                WorkingOrdersModel selectedWorkorder = WorkingOrdersModel.getWorkorderById(workorderIdFromWorkorders);
                workorderName.setText(selectedWorkorder.workingOrderName);
                workorderName.setSelection( selectedWorkorder.workingOrderName.length(),selectedWorkorder.workingOrderName.length() );
                workorderAdress.setText(selectedWorkorder.workingOrderAdress);
                workorderPhoneNumber.setText(selectedWorkorder.workingOrderPhoneNumber);
                setTitle(getResources().getString(R.string.title_activity_edit__new__work__order));

                // start with the address we have
                strLastKnownAddress = selectedWorkorder.workingOrderAdress;

                woLatLng = new LatLng( selectedWorkorder.orderLatitude, selectedWorkorder.orderLongitude );
                String lng = String.valueOf(woLatLng.longitude);
                String lat = String.valueOf(woLatLng.latitude);
                String url = "https://maps.googleapis.com/maps/api/streetview?size=400x200&location=" + lat + "," + lng;
                new LongOperation().execute(url);

            } catch (Exception e) {
                e.printStackTrace();
            }

            analyticsTracker.setScreenName("Edit Project");
            analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

        }
        else {
            setTitle(getResources().getString(R.string.title_activity_add__new__work__order));
            workorderName.setText("");
            workorderAdress.setText("");
            workorderPhoneNumber.setText("");

            // start with an empty address
            strLastKnownAddress = "";

        }



        // change color when text is changed
        ///////////////////////////////////////////
        workorderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    beginAlignmentButton.setBackgroundColor(getResources().getColor(R.color.fabBuuttonAlignementColor));
                } else {
                    beginAlignmentButton.setBackgroundColor(getResources().getColor(R.color.alignment_canvasShading_light));
                }
            }
        });


        Log.d(TAG, "onCreate: --------STARTED UNIT TESTINGS");
        PojoParserHelper helperPojo = new PojoParserHelper();
        helperPojo.unitTestingAngleBearing();
        Log.d(TAG, "onCreate: --------ENDED UNIT TESTINGS");

    }


    private void showAddressList(final ArrayList<Address> arr)
    {
        if (arr.size()==0)
            return;

        ArrayList<String> a = new ArrayList<String>();

        try {
            for (int i = 0; i < arr.size(); i++) {
                String strTmpAddress = "";
                if (arr.get(i).getMaxAddressLineIndex() > 1) {
                    strTmpAddress = arr.get(i).getAddressLine(0) + ", " + arr.get(i).getAddressLine(1);
                } else if (arr.get(i).getMaxAddressLineIndex() > 0) {
                    strTmpAddress = arr.get(i).getAddressLine(0);
                }

                try {
                    if (strTmpAddress.isEmpty()) {
                        strTmpAddress = arr.get(i).getFeatureName();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                if (!strTmpAddress.isEmpty()) {
                    a.add(strTmpAddress);
                }
            }


            if (a.size() == 1) {
                workorderAdress.setText(a.get(0));
                woLatLng = new LatLng(arr.get(0).getLatitude(), arr.get(0).getLongitude());
                String lng = String.valueOf(woLatLng.longitude);
                String lat = String.valueOf(woLatLng.latitude);
                String url = "https://maps.googleapis.com/maps/api/streetview?size=400x200&location=" + lat + "," + lng;

                workorderAdress.setSelection( workorderAdress.getText().toString().length(), workorderAdress.getText().toString().length());
                workorderAdress.requestFocus();

                // marked as verified address
                strLastKnownAddress = workorderAdress.getText().toString();

                new LongOperation().execute(url);

                return;
            }


            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Add_New_Work_Order.this);
            builderSingle.setIcon(R.drawable.ic_my_location_black_24dp);
            builderSingle.setTitle("Select Address");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    Add_New_Work_Order.this, android.R.layout.select_dialog_singlechoice);

            for (int i = 0; i < arr.size(); i++) {
                arrayAdapter.add((a.get(i)));
            }


            builderSingle.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            workorderAdress.setText(strName);
                            woLatLng = new LatLng(arr.get(which).getLatitude(), arr.get(which).getLongitude());
                            String lng = String.valueOf(woLatLng.longitude);
                            String lat = String.valueOf(woLatLng.latitude);
                            String url = "https://maps.googleapis.com/maps/api/streetview?size=400x200&location=" + lat + "," + lng;

                            workorderAdress.setSelection( workorderAdress.getText().toString().length(), workorderAdress.getText().toString().length());
                            workorderAdress.requestFocus();

                            // marked as verified address
                            strLastKnownAddress = workorderAdress.getText().toString();

                            new LongOperation().execute(url);

                        }
                    });
            builderSingle.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
            Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class);
            i.putExtra("projectId", selectedProjectId);
            startActivity(i);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Add new Workorder");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //removes the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_workorder_check_mark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        List<WorkingOrdersModel> workingOrdersModel = WorkingOrdersModel.getWorkingOrderForProject(selectedProjectId);
        List<WorkingOrdersModel> completedWorkingOrdersModel = WorkingOrdersModel.getCompletedWorkordersForProject(selectedProjectId);

        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Global Counters")
                .setAction("Number of working orders")
                .setLabel(String.valueOf(workingOrdersModel.size()))
                .setValue(1)
                .build());

        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Global Counters")
                .setAction("Number of completed working orders")
                .setLabel(String.valueOf(completedWorkingOrdersModel.size()))
                .setValue(1)
                .build());

        //noinspection SimplifiableIfStatement
        if (id == R.id.checkMarkButton) {
            if (PermissionUtils.isPermissionGrantedInActivity(this, Manifest.permission.CAMERA)) {
                if (onSaveButton(false)) {
                    finish();
                    Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                    i.putExtra("projectId", selectedProjectId);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplication(), getResources().getString(R.string.loadingAddress), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        } else {
            Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
            i.putExtra("projectId", selectedProjectId);
            startActivity(i);
            finish();

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


    //shows a mannual dialog
    private void showManualInputField(final String[] frequencys) {

        //changes the frequencys display
        String[] convertedFrequencysArray = new String[frequencys.length];

        //CHANGING THE STIRNG TO SHOW IN THE LIST
        for (int i = 0; i < convertedFrequencysArray.length; i++) { // iritates between the numbers to show the string langth
            String sub = frequencys[i].substring(0, 1);
            String remainder = frequencys[i].substring(1);
            convertedFrequencysArray[i] = sub + "," + remainder + "GHz";
        }

        inputFieldDialog = new Dialog(this); // initlizing

        inputFieldDialog.setContentView(R.layout.dialog_frequency_list_layout); // CASTING

        final ListView listView; // initlizing listview
        listView = (ListView) inputFieldDialog.findViewById(R.id.frequency_list_dialog); // connecting listview to display
        Button saveButton = (Button) inputFieldDialog.findViewById(R.id.frequency_list_button);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, convertedFrequencysArray)); // setting a simple adapter to the list view

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // does the list view multiple choices

        try {
            for (int i = 0; i < frequencys.length; i++) { // colors the list cells
                for (int t = 0; t < selectedFrequency.size(); t++) {
                    if (frequencys[i].equals(selectedFrequency.get(t))) {
                        listView.setItemChecked(i, true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // sets an item listener for the list view
            @Override
            public void onItemClick(AdapterView parent, View v, int pos, long id) {

                listItemCounter++; // updates the frequency

                //
                if (listItemCounter > 10) { // cound how many items a user has added and shows a counter when it reaches 10
                    Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_missing_sucessful_exeeded_maximum_bands),
                            Toast.LENGTH_SHORT).show();
                } else { // adds the frequency to the user databaser
                    selectedFrequency.add(String.valueOf(frequencys[pos]));

                }
            }
        });


        inputFieldDialog.setTitle(getResources().getString(R.string.workorderSettings_layout_buttonText)); // gives a title to the dialog

        inputFieldDialog.show(); // shows the dialog

        saveButton.setOnClickListener(new View.OnClickListener() { // sets onclick listener that will close the dialog on click
            @Override
            public void onClick(View v) {
                inputFieldDialog.hide();

                String stringToShowInButton = "";
                selectedFrequencys = new String[selectedFrequency.size()];

                for (int i = 0; i < selectedFrequency.size(); i++) {
                    if (stringToShowInButton != "") {
                        stringToShowInButton = stringToShowInButton + ",";
                    }
                    stringToShowInButton = stringToShowInButton + selectedFrequency.get(i);
                    selectedFrequencys[i] = selectedFrequency.get(i); // saves the selected frequencys at the selected frequencys array
                }


            }
        });
    }


    /**
     * saves the workorder
     */
    public Boolean onSaveButton(boolean startAlign) {


        try {
            String strAddress = workorderAdress.getText().toString().trim();

            // if address empty, than ok. /// @@@
            if (strAddress.isEmpty()==false) {
                if (strLastKnownAddress.compareToIgnoreCase(strAddress) != 0) {
                    fab.callOnClick();
                    return false;
                }
            }
            else {
                // empty address
                woLatLng = new LatLng(0.0,0.0);
            }
        }
        catch (Exception e) {}


        if (workorderName.getText().toString().trim().isEmpty() == false) { //checks if user has written something in the workorder text

            //gets the switch state
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date tmpDate = new Date();
            String currentDateandTime = sdf1.format(tmpDate);

            if (isInEditMode == false) { // checks if the user is in edit mode

                WorkingOrdersModel workingordersModel = new WorkingOrdersModel(); // collectes data to save in the working orders model

                // one month hack
//                Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.MONTH, -1);
//                java.util.Date dt = cal.getTime();
//                String currentDateandTime = sdf1.format(dt);

                String strName = workorderName.getText().toString().trim();

                workingordersModel.projectId = selectedProjectId;
                workingordersModel.workordertId = UUID.randomUUID().toString();
                workingordersModel.workingOrderName = strName; //workorderName.getText().toString();
                workingordersModel.workingOrderAdress = workorderAdress.getText().toString();
                workingordersModel.isBestEffort = true;
                workingordersModel.CurrentSelectedBand = "";
                workingordersModel.currentChannelBandwith = "";
                workingordersModel.workingOrderPhoneNumber = workorderPhoneNumber.getText().toString();
                workingordersModel.selectedFrequencysList = ""; // will ransform the list into text;
                workingordersModel.lastUpdateTime = currentDateandTime;
                workingordersModel.lastUpdateStatus = "";
                workingordersModel.wifiPassword = "wireless";
                workingordersModel.unitPassword = "netman";
				
                workingordersModel.orderLatitude = woLatLng.latitude;
                workingordersModel.orderLongitude = woLatLng.longitude;

                if ( (workingordersModel.orderLatitude==0.0) && (workingordersModel.orderLongitude==0.0))
                {
                    try {
                        // get coordinates from address
                        if (!workingordersModel.workingOrderAdress.isEmpty()) {
                            GeoLocation loc = geoLocationWrapper.getLocationFromAddress(workingordersModel.workingOrderAdress);
                            if (loc != null) {
                                workingordersModel.orderLatitude = loc.getLatitude();
                                workingordersModel.orderLongitude = loc.getLongitude();
                            }
                        }
                    }
                    catch (Exception e)
                    { e.printStackTrace(); }
                }
				
				
                workingordersModel.save();
                selectedWorkingOrder = workingordersModel;

                return true;

            } else {
                // starts the activity since you don't have anything in the workorder (meaning we didn't donnect to the box yet
                //user is in Edit mode


                WorkingOrdersModel workingordersModel = WorkingOrdersModel.getWorkorderById(workorderIdFromWorkorders);

                workingordersModel.projectId = selectedProjectId;
                workingordersModel.workingOrderName = workorderName.getText().toString();
                workingordersModel.workingOrderAdress = workorderAdress.getText().toString();
                workingordersModel.workingOrderPhoneNumber = workorderPhoneNumber.getText().toString();
                if (false) {
                    workingordersModel.isBestEffort = true;
                    workingordersModel.CurrentSelectedBand = "";
                    workingordersModel.currentChannelBandwith = "";
                    workingordersModel.selectedFrequencysList = ""; // will ransform the list into text;
                    workingordersModel.lastUpdateStatus = "";
                }
                workingordersModel.lastUpdateTime = currentDateandTime;
                workingordersModel.orderLatitude = woLatLng.latitude;
                workingordersModel.orderLongitude = woLatLng.longitude;
                if(startAlign == true) {
                    workingordersModel.lastUpdateStatus = "inComplete";
                }

                if ( (workingordersModel.orderLatitude==0.0) && (workingordersModel.orderLongitude==0.0))
                {
                    try {
                        // get coordinates from address
                        if (!workingordersModel.workingOrderAdress.isEmpty()) {
                            GeoLocation loc = geoLocationWrapper.getLocationFromAddress(workingordersModel.workingOrderAdress);
                            if (loc != null) {
                                workingordersModel.orderLatitude = loc.getLatitude();
                                workingordersModel.orderLongitude = loc.getLongitude();
                            }
                        }
                    }
                    catch (Exception e)
                    { e.printStackTrace(); }
                }

                workingordersModel.save();


                Toast.makeText(getApplication(), getResources().getString(R.string.work_order_updated_successfully),
                        Toast.LENGTH_SHORT).show();

                selectedWorkingOrder = workingordersModel;

                //moves to the the wororders screen
                return true;
            }

        } else { // case that the user didn't write anything in the workorder name
            Toast.makeText(getApplication(), getResources().getString(R.string.missing_workorder_name),
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

	 @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.CAMERA:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startAlignment();
                    }
            }
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.newWorkorder_beginAlignment_button) {
            if (isInEditMode == true) {
                WorkingOrdersModel workingordersModel = WorkingOrdersModel.getWorkorderById(workorderIdFromWorkorders);
                if ((workingordersModel != null) && (workingordersModel.lastUpdateStatus.equals("complete"))) {
                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle(getResources().getString(R.string.workorder_selection_confirm))
                            .setMessage(getResources().getString(R.string.workorder_selection_confirm_message))
                            .setCancelable(true)
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (onSaveButton(true)) {
                                        if (PermissionUtils.isPermissionGrantedInActivity(Add_New_Work_Order.this, Manifest.permission.CAMERA)) {
                                            startAlignment();
                                        }
                                    } else {
                                        Toast.makeText(getApplication(), getResources().getString(R.string.loadingAddress), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // delete the project
                                public void onClick(DialogInterface dialog, int which) {

                                    return;
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    if (onSaveButton(true)) {
                        if (PermissionUtils.isPermissionGrantedInActivity(Add_New_Work_Order.this, Manifest.permission.CAMERA)) {
                            startAlignment();
                        }
                    } else {
                        Toast.makeText(getApplication(), getResources().getString(R.string.loadingAddress), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (onSaveButton(true)) {
                    if (PermissionUtils.isPermissionGrantedInActivity(Add_New_Work_Order.this, Manifest.permission.CAMERA)) {
                        startAlignment();
                    }
                } else {
                    Toast.makeText(getApplication(), getResources().getString(R.string.loadingAddress), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void startAlignment() {
        Intent i = new Intent(getApplication(), Scanning_Barcode_Activity_With_Fragment.class); // moves to the fine aligment screen
        i.putExtra("projectId", selectedProjectId);

        if(selectedWorkingOrder != null) {
            i.putExtra("workorderId", selectedWorkingOrder.workordertId);
        }
        startActivity(i);

                    // WINT-155
                    finish();
                }
            
    private class LongOperation extends AsyncTask<String, Void, String> {

        Drawable drawable = null;

        @Override
        protected String doInBackground(String... params) {

            try {
                String url = params[0];
                InputStream is = (InputStream) new URL(url).getContent();
                drawable  = Drawable.createFromStream(is, "src name");

                // street view hack
                // we cannot know that there is no image
                // only by the entire color of the image
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                int color1 = bitmap.getPixel(5, 5);
                int color2 = bitmap.getPixel(15, 15);
                int color3 = bitmap.getPixel(25, 25);

                if ( (color1==color2) && (color1==color3))
                    drawable = null;


            } catch (Exception e) {
                return "ERROR";
            }

            return "OK";
        }


        @Override
        protected void onPostExecute(String result) {
            try {
                ImageView img = (ImageView) findViewById(R.id.mainBackgroundaddNewWorkorder);
                if (drawable != null) {
                    img.setBackground(drawable);
                } else {
                    img.setBackgroundResource(R.drawable.backgound_image_new_project);
                }
                spinner.setVisibility(View.GONE);
            }
            catch (Exception e) {
            e.printStackTrace();
            }

            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
