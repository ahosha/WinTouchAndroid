package apps.radwin.wintouch.activities.alignmentActivities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.ExpandableFrequencyListAdapter;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.devicePackage.wifiWrapper;
import apps.radwin.wintouch.models.BandsModel;
import apps.radwin.wintouch.models.ExpendebleMenuPositionModel;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

import static apps.radwin.wintouch.screenManagers.AligmentManager.checkedExpendbleMenuItems;
import static java.lang.Integer.parseInt;

public class WorkorderSetSettings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    final static String TAG = WorkorderSetSettings.class.getSimpleName();

    AligmentManager aligmentManagerClass;
    Button frequencyButton, saveButton;
    CheckBox checkAllBox;
    List<BandsModel> supportedDeviceBands;
    SharedPreferences.Editor editor;
    Timer deviceRestartTimer;
    Timer deviceConnectionTimer;
    Timer wififailedConnectionTimer;
    CheckBox setAsDefaultCeckBox;
    List<String> bandWidthCategories;
    int frequencyListItemCounter = 0;
    ArrayList<String> selectedFrequency = new ArrayList<String>();
    Spinner bandWidthsSpinner;
    String selectedBandWidthFromPopup, selectedProjectId, selectedWorkorderId, selectedBandIdFromPopup, selectedBandDescription;
    //    String TAG = "WorkorderSetSettings";
    BandsModel selectedBandFromPopup;
    SharedPreferences boxSharedPref;
    ArrayAdapter<String> bandWidthdataAdapter;
    Dialog frequencysChoosedDialog;
    Boolean chooseFrequencyIniated = false;
    Boolean chooseBandWidthInitated = false;
    EditText trueputUpText, trueputDownText;
    AlertDialog.Builder updatingUlcDialog;
    AlertDialog alert11;
    Dialog restartDialog;
    int selectedBandPositionfromServer = 0;
    int selectedCbwPosition = 0;
    String projectCbw;
    Boolean isBeServiceTypeSelected;
    Boolean bandChangedWithReset;
    ProjectsModel workingProject;
    WorkingOrdersModel workingOrder;
    Spinner serviceTypeSpinner;
    private boolean useWorkingOrderData;
    TextView resetDialogHeadlineText;
    TextView resetDialogContentText;
    Button resetDialogCancelButton;
    Button resetDialogRestartButton;
    Button resetDialogExitButton;
    DonutProgress resetDialogdialogDonutProgress;
    TextView resetDialogDonutProgressText;
    List<Integer> allowableFrequencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workorder_set_settings_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boxSharedPref = getSharedPreferences("ULCSharePrefrenceTable", Context.MODE_PRIVATE); //prepers the shared prefrence
        editor = boxSharedPref.edit();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButton();
            }
        });
        fab.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        ///////////////////////////////////////
        ////////START OF IMPLEMENTATION////////
        //////////////////////////////////////
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

        workingProject = ProjectsModel.getProjectWithId(selectedProjectId);
        workingOrder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId);
        if (workingOrder != null && !workingOrder.currentChannelBandwith.equals(""))
            useWorkingOrderData = true;

        //gets the aligment manager class
        aligmentManagerClass = ((appContext) getApplication()).getAligmentManagerVar();

        Spinner bandSpinner = (Spinner) findViewById(R.id.workorderSettings_avilableBands_spinner); // Casting
        bandWidthsSpinner = (Spinner) findViewById(R.id.workorderSettings_avilableBandWidths_spinner); // Casting
        frequencyButton = (Button) findViewById(R.id.workorderSettings_frequency_button); // Casting
        checkAllBox = (CheckBox) findViewById(R.id.checkBox_set_settings); // Casting
        serviceTypeSpinner = (Spinner) findViewById(R.id.workorderSettings_bestEffort_spinner); // casting
        trueputUpText = (EditText) findViewById(R.id.workorderSettings_trueputUp_editText); // Casting
        trueputDownText = (EditText) findViewById(R.id.workorderSettings_trueputUpown_editText); // Casting
        saveButton = (Button) findViewById(R.id.continueButtonWorkorder);
        setAsDefaultCeckBox = (CheckBox) findViewById(R.id.workorderSetSettings_setAsDefault_checkBox);
        final TextView setDefaultText = (TextView) findViewById(R.id.workorderSetSettings_setAsDefault_text);

        if (workingProject != null && workingOrder.truePutUp != null && workingOrder.truePutDown != null) {
            trueputDownText.setText(String.valueOf(workingOrder.truePutDown));
            trueputUpText.setText(String.valueOf(workingOrder.truePutUp));
        }

        // sets an on save listener for the default
        setAsDefaultCeckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setAsDefaultCeckBox.isChecked()) {
//                    setDefaultText.setText("Default will be set in project");
                    setDefaultText.setTextColor(getResources().getColor(R.color.alignementColorRed));
                } else {
//                    setDefaultText.setText("Set as default");
                    setDefaultText.setTextColor(getResources().getColor(R.color.alignment_canvasOutline));
                }

                workingProject.save();
            }
        });


        //check box handeling for the set all
        ///////////////////////////////
        checkAllBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    frequencyButton.setClickable(false);
                    frequencyButton.setAlpha(.5f);
                    frequencyButton.setText("Scanning All Frequencies");


                } else {
                    frequencyButton.setClickable(true);
                    frequencyButton.setAlpha(1f);

                    //bring the selected frequency's from the aligment manager
                    //////////////////////////////
                    ArrayList<ExpendebleMenuPositionModel> selectedCheckboxes = aligmentManagerClass.getCheckedExpendbleMenuItems();

                    //initlizes the selected frequencys
                    selectedFrequency = new ArrayList<String>();

                    //converted alignment manager class to variable
                    for (int i = 0; i < selectedCheckboxes.size(); i++) {
                        selectedFrequency.add(selectedCheckboxes.get(i).frequencyId);
                    }

                    String stringToShowInButton = TextUtils.join(", ", selectedFrequency);

                    //adds a default string
                    if (stringToShowInButton.length() < 2) {
                        stringToShowInButton = getApplication().getString(R.string.workorderSettings_layout_buttonText);
                    }

                    Log.d(TAG, "onClick: Strings one are: ");
                    frequencyButton.setText(stringToShowInButton);


                }

            }
        });

        bandChangedWithReset = false;
        allowableFrequencies = new ArrayList<>();
        List<String> bandCategories = BuildBandsForDisplay();

        bandWidthCategories = BuildCbwsForDisplay();

        ArrayAdapter<String> bandDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bandCategories); // Creating adapter for spinner
        bandWidthdataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bandWidthCategories); // Creating adapter for spinner


        bandDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Drop down layout style - list view with radio button
        bandWidthdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Drop down layout style - list view with radio button


        bandSpinner.setAdapter(bandDataAdapter); // attaching data adapter to spinner
        bandWidthsSpinner.setAdapter(bandWidthdataAdapter); // attaching data adapter to spinner

        bandSpinner.setSelection(selectedBandPositionfromServer); // sets the positino of the selected band
        bandWidthsSpinner.setSelection(selectedCbwPosition); // sets the position of the selected CBW

        if (!useWorkingOrderData) {
            isBeServiceTypeSelected = workingProject.isBestEffort;
            trueputUpText.setText(String.valueOf(workingProject.truePutUp));
            if (String.valueOf(workingProject.truePutUp).contains(".0")) {
                String[] parts = String.valueOf(workingProject.truePutUp).split("\\.");
                trueputUpText.setText(parts[0]);
            }
            trueputDownText.setText(String.valueOf(workingProject.truePutDown));
            if (String.valueOf(workingProject.truePutDown).contains(".0")) {
                String[] parts = String.valueOf(workingProject.truePutDown).split("\\.");
                trueputDownText.setText(parts[0]);
            }
        } else {
            isBeServiceTypeSelected = workingOrder.isBestEffort;
            if (workingOrder.truePutDown != null && workingOrder.truePutUp != null) {
                trueputUpText.setText(String.valueOf(workingOrder.truePutUp));
                if (String.valueOf(workingOrder.truePutUp).contains(".0")) {
                    String[] parts = String.valueOf(workingOrder.truePutUp).split("\\.");
                    trueputUpText.setText(parts[0]);
                }
                trueputDownText.setText(String.valueOf(workingOrder.truePutDown));
                if (String.valueOf(workingOrder.truePutDown).contains(".0")) {
                    String[] parts = String.valueOf(workingOrder.truePutDown).split("\\.");
                    trueputDownText.setText(parts[0]);
                }
            }
        }


        ////////////////////////////
        //////CLICK LISTENERS///////
        ////////////////////////////

        /**
         * A listener for the band spinner
         * the item click will update selectedBandIdFromPopup and selectedBandFromPopup
         */
        bandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedBandDescription = supportedDeviceBands.get(position).bandName; // VERYFIED that gets the correct band id
                selectedBandIdFromPopup = supportedDeviceBands.get(position).bandId; // VERYFIED that gets the correct band id
                selectedBandFromPopup = supportedDeviceBands.get(position); // puts the selected band in a veriable

                if (aligmentManagerClass.getDeviceBandId().equals(selectedBandIdFromPopup)) {
                    bandChangedWithReset = false;
                } else {
                    bandChangedWithReset = true;
                }

                // resets frequencies
                ResetFrequencies();

                //sets an on item selected listener to the bandwidth spinner
                if (chooseBandWidthInitated) {
                    setOnItemSelectListenerOnBandwidth();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bandWidthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // Actualy sets an item click on the spinner

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aligmentManagerClass.getCheckedExpendbleMenuItems().clear();

                selectedBandWidthFromPopup = bandWidthCategories.get(position);
                if (selectedBandWidthFromPopup.startsWith("Auto")) {
                    selectedBandWidthFromPopup = "20";
                }
                chooseBandWidthInitated = true;

                //init allowable frequencies list
                String[] allowableFrequenciesArr = getFrequencyListForBand(selectedBandWidthFromPopup);
                allowableFrequencies.clear();
                for (int i = 0; i < allowableFrequenciesArr.length; i++) {
                    allowableFrequencies.add(parseInt(allowableFrequenciesArr[i]));
                }

                // resets frequencies
                ResetFrequencies();

                if (!chooseFrequencyIniated) {
                    BuildFrequenciesForDisplay(selectedBandWidthFromPopup);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * a listener for the service type switch
         * the listener will change the service type switch.
         */
        if (workingOrder.hsuServiceType != null) { // checks if we have
            if (workingOrder.hsuServiceType.equals("New Residential")) {
                isBeServiceTypeSelected = true;
                serviceTypeSpinner.setEnabled(false);
            } else {
                serviceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (serviceTypeSpinner.getSelectedItemPosition() == 0) { // gets the best effort state
                            isBeServiceTypeSelected = true;
                        } else {
                            isBeServiceTypeSelected = false;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Your device might not contain newest version \n please upgrade it", //wifi is enabled
                    Toast.LENGTH_SHORT).show();

        }

        if (!isBeServiceTypeSelected) { //sets the best effort switch to the correct position
            serviceTypeSpinner.setSelection(1);
        }
        /**
         * a listener for the frequency's button
         */
        frequencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFrequencyPressed();

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButton();
            }
        });

        //


        // TESTINGS
        // TESTINGS
//        aligmentManagerClass.logInToUlc();

    }

    private void ResetFrequencies() {
        frequencyButton.setText(getResources().getString(R.string.workorderSettings_layout_buttonText));
        selectedFrequency = new ArrayList<String>();
    }

    private void BuildFrequenciesForDisplay(String cbw) {
        //init frequencies from project
        if (!useWorkingOrderData) {
            if (workingProject.selectedFrequencysList.length() > 0) {
                String[] selectedArr = TextUtils.split(workingProject.selectedFrequencysList, ", ");
                selectedFrequency = new ArrayList<>(Arrays.asList(selectedArr));
            }
        } else {
            if (workingOrder.selectedFrequencysList.length() > 0) {
                String[] selectedArr = TextUtils.split(workingOrder.selectedFrequencysList, ", ");
                selectedFrequency = new ArrayList<>(Arrays.asList(selectedArr));
            }
        }

        int lowestFrequency = 0;
        int highestFrequency = 0;

        if (!selectedBandDescription.isEmpty()) {
            try {
                final String parsedBandDescription =
                        selectedBandDescription.substring(
                                0,
                                selectedBandDescription.indexOf(" ")
                        ).replaceAll("\\.", "");
                lowestFrequency =
                        Integer.parseInt(
                                parsedBandDescription.substring(
                                        0,
                                        parsedBandDescription.indexOf("-")
                                )
                        );
                highestFrequency =
                        Integer.parseInt(
                                parsedBandDescription.substring(
                                        parsedBandDescription.indexOf("-") + 1,
                                        parsedBandDescription.length()
                                )
                        );
            } catch (Exception e) {
                lowestFrequency = 0;
                highestFrequency = 0;
            }
        }

        if (lowestFrequency != 0 && highestFrequency != 0) {

            for (final Iterator<String> frequency = selectedFrequency.iterator(); frequency.hasNext(); ) {
                final int persistedFrequency = Integer.parseInt(frequency.next());

                if (lowestFrequency > persistedFrequency || persistedFrequency > highestFrequency) {
                    frequency.remove();
                }
            }
        }

        String stringToShowInButton = TextUtils.join(", ", selectedFrequency);

        if (stringToShowInButton.trim().isEmpty()) {
            frequencyButton.setText(getResources().getString(R.string.workorderSettings_layout_buttonText));
        } else if (selectedFrequency.size() > 10) {
            frequencyButton.setText("Scanning All Frequencies");
        } else {
            Log.d(TAG, "BuildFrequenciesForDisplay: ");
            frequencyButton.setText(stringToShowInButton);
        }
        chooseFrequencyIniated = true;


    }

    private List<String> BuildCbwsForDisplay() {

        bandWidthCategories = new ArrayList<String>(); // initlizes a bandswidth category list

        if (!useWorkingOrderData) {
            projectCbw = !workingProject.currentChannelBandwith.equals("") ? workingProject.currentChannelBandwith : String.valueOf(aligmentManagerClass.getCbwFromDevice());
        } else {
            projectCbw = workingOrder.currentChannelBandwith;
        }

        BuildDynamicCbwList();

        return bandWidthCategories;
    }

    private void BuildDynamicCbwList() {
        selectedCbwPosition = 0;
        String[] cbwArray = TextUtils.split(selectedBandFromPopup.bandwithList, ", ");
        String dynamicCbw = "Auto(";
        for (int i = 0; i < cbwArray.length; i++) {
            if (cbwArray[i].equals("20") || cbwArray[i].equals("40") || cbwArray[i].equals("80")) {
                dynamicCbw = dynamicCbw + cbwArray[i] + "/";
            } else {
                bandWidthCategories.add(cbwArray[i]);
                if (bandWidthCategories.get(i).equals(projectCbw)) { // checks the position of the selected cbw we got from the server
                    selectedCbwPosition = i;
                }
            }
        }
        //remove last "," and add ")"
        dynamicCbw = dynamicCbw.substring(0, dynamicCbw.length() - 1);
        dynamicCbw = dynamicCbw + ")";
        bandWidthCategories.add(dynamicCbw);
        if (selectedCbwPosition == 0) {
            selectedCbwPosition = bandWidthCategories.size() - 1;
        }
    }

    private List<String> BuildBandsForDisplay() {
        //sets the currently selected band id
        String bandIdFromDevice = aligmentManagerClass.getDeviceBandId();
        String bandIdFromProject = "";
        String selectedBandId = "";

        BandsModel projectBand = null;

        if (useWorkingOrderData) {
            bandIdFromProject = workingOrder.CurrentSelectedBand;
        } else {
            bandIdFromProject = workingProject.currentBandId;
        }
        supportedDeviceBands = aligmentManagerClass.getSupportedDeviceBands(); // gets the supported the device bands
        //first WO in project
        if (bandIdFromProject.equals("")) {
            selectedBandId = bandIdFromDevice;
        } else {
            projectBand = BandsModel.getBandById(bandIdFromProject);
            //project band not supported in device
            if (!supportedDeviceBands.contains(projectBand)) {
                //TODO error - project band not supported by device
                //alert user and current band will be selected
                selectedBandId = bandIdFromDevice; //select device current band
                ResetFrequencies();
            } else {
                //check if reset required
                if (!bandIdFromDevice.equals(bandIdFromProject)) {
                    //reset required
                    bandChangedWithReset = true;
                }
                selectedBandId = bandIdFromProject; //select project band
            }
        }

        List<String> bandCategories = new ArrayList<String>(); // initlizes a band Category list

        for (int i = 0; i < supportedDeviceBands.size(); i++) { //sets bands list in the bands category list
            bandCategories.add(supportedDeviceBands.get(i).bandName);

            if (supportedDeviceBands.get(i).bandId.equals(selectedBandId)) { // checks the position of the selected band we got from the server
                selectedBandPositionfromServer = i;
            }
        }

        selectedBandFromPopup = supportedDeviceBands.get(selectedBandPositionfromServer);

        return bandCategories;
    }


    /**
     * The Functino is Initlized  whenever the user has pressed the save button
     */
    public void onSaveButton() {

        //bring the selected frequency's from the aligment manager
        //////////////////////////////
        //ArrayList<ExpendebleMenuPositionModel> selectedCheckboxes = aligmentManagerClass.getCheckedExpendbleMenuItems();

        //initlizes the selected frequencys
        //selectedFrequency = new ArrayList<String>();

        //converted alignment manager class to variable
//        for (int i = 0; i < selectedCheckboxes.size(); i++) {
//            selectedFrequency.add(selectedCheckboxes.get(i).frequencyId);
//        }


        //checks if the set all is checked
        /////////////////////////
        if (checkAllBox.isChecked()) {
            selectedFrequency = new ArrayList<String>(Arrays.asList(getFrequencyListForBand(selectedBandWidthFromPopup)));
        }

        //selected size
        if (selectedFrequency.size() > 0) { // the user didn't put anything in the true put up or down

            if ((String.valueOf(trueputUpText.getText()).equals("")) || (Integer.valueOf(String.valueOf(trueputUpText.getText())) <= 0) || (String.valueOf(trueputDownText.getText()).equals("")) || (trueputDownText.getText().toString().equals("0")) || (Integer.valueOf(String.valueOf(trueputDownText.getText())) <= 0) || (trueputUpText.getText().toString().equals("0"))) { // checks if the user has put anything in the required truput up and down

                new AlertDialog.Builder(WorkorderSetSettings.this)
                        .setTitle(getResources().getString(R.string.alignmentErrorTitle))
                        .setMessage(getResources().getString(R.string.addNewProject_Toast_noUpAndDownData))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else { // checks if the user has selected any frequency's yet

                if (setAsDefaultCeckBox.isChecked()) {
                    workingProject.truePutDown = Double.valueOf(trueputDownText.getText().toString());
                    workingProject.truePutUp = Double.valueOf(trueputUpText.getText().toString());

                    workingProject.save();
                }

                try {

                    //PROJECT SQL UPDATE if first time get bands
                    if (workingProject.currentBandId.equals("") || workingProject.currentBandId == null || setAsDefaultCeckBox.isChecked()) {
                        workingProject.currentBandId = selectedBandIdFromPopup; // updates the band id in the Project at the sql
                        workingProject.currentChannelBandwith = selectedBandWidthFromPopup; // updates the band width in the sql
                        workingProject.selectedFrequencysList = TextUtils.join(", ", selectedFrequency); // updates the frequency in the sql

                        workingProject.save();
                    }


                    //WORKORDER SQL UPDATE
                    WorkingOrdersModel currentSelectedWorkorder = WorkingOrdersModel.getWorkorderById(selectedWorkorderId); // updates the data in the sql
                    currentSelectedWorkorder.CurrentSelectedBand = selectedBandDescription; // updates the data in the sql
                    currentSelectedWorkorder.currentChannelBandwith = selectedBandWidthFromPopup; // updates the data in the sql
                    currentSelectedWorkorder.selectedFrequencysList = TextUtils.join(", ", selectedFrequency); // updates the frequency in the sql
                    currentSelectedWorkorder.isBestEffort = isBeServiceTypeSelected; // updates service type
                    try {
                        currentSelectedWorkorder.truePutUp = Double.valueOf(trueputUpText.getText().toString());
                        currentSelectedWorkorder.truePutDown = Double.valueOf(trueputDownText.getText().toString());
                    } catch (Exception e) {

                    }
                    String currentDateandTime = "";
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        Date tmpDate = new Date();
                        currentDateandTime = sdf.format(tmpDate);
                    } catch (Exception e) {

                    }
                    currentSelectedWorkorder.lastUpdateTime = currentDateandTime;
                    currentSelectedWorkorder.save();

                    //wait a deley and turn on the to tell the server we initeted a connectino
                    final Handler handler = new Handler(); // sets a deley for the operation
                    handler.postDelayed(new Runnable() { // runs a deley in the code for 0.5 second
                        @Override
                        public void run() { // runs a deley in the code for 1 second

                            //SAVING TO THE Shared Prefrence that this is not the first time
                            editor.putBoolean("isFirstTimeSelectedBands", false); // tells the user this isn't the first time we initated the screen
                            editor.commit();

                            if (bandChangedWithReset) {

                                onDeviceRestartRequired();

                            } else {
                                onFinishedSaveSettingsInSql();
                            }

                        }
                    }, 500);

                } catch (Exception e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(WorkorderSetSettings.this)
                            .setTitle(getResources().getString(R.string.alignmentErrorTitle))
                            .setMessage(getResources().getString(R.string.connect_set_data))
                            .setPositiveButton("try again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete

                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
//                                        selectBandDialog.hide(); // hides the dialog
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }


            }


        } else { // the user didn't choose any frequency's yet
            new AlertDialog.Builder(WorkorderSetSettings.this)
                    .setTitle("Frequency")
                    .setMessage("Missing frequencies")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    /**
     * needs to try to reconnect to units
     */
    public void reConnectToUnitRequired() {


        // setting new data to dialog veriables to show
        ////////////////////////////////////////
        try {
            resetDialogHeadlineText.setText("Reconnect");
            resetDialogContentText.setText(R.string.reconnectDialogBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            resetDialogCancelButton.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            resetDialogRestartButton.setText("connect Now");
            resetDialogdialogDonutProgress.setProgress(100);
            resetDialogDonutProgressText.setText("10");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int SECONDS_FOR_TIMER = 10;
        final int[] timerProgress = {0};


        wififailedConnectionTimer = new Timer();
        wififailedConnectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerProgress[0]++;

                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        resetDialogDonutProgressText.setText(String.valueOf((SECONDS_FOR_TIMER + 1) - timerProgress[0]));
                    }
                });


                if (timerProgress[0] >= SECONDS_FOR_TIMER) {

                    // timer ended
                    /////////////////

                    try {
                        wififailedConnectionTimer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    WifiManager wifiManager = (WifiManager) WorkorderSetSettings.this.getSystemService(Context.WIFI_SERVICE); // gets the CURRENT wifi State
                    if (wifiManager.getConnectionInfo().getLinkSpeed() > 1) { // makes sure that the link speed is bigger then one meaning we have connection
                        aligmentManagerClass.savedDataOnSql(workingProject.networkId, callbackForOnPostToBoxFunction, selectedBandIdFromPopup, selectedBandWidthFromPopup, selectedFrequency, allowableFrequencies);

                    } else {
                        reConnectToUnitRequired();

                    }

                }


            }
        }, 250, 1000);

        resetDialogRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    try {
                        wififailedConnectionTimer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    WifiManager wifiManager = (WifiManager) WorkorderSetSettings.this.getSystemService(Context.WIFI_SERVICE); // gets the CURRENT wifi State
                    if (wifiManager.getConnectionInfo().getLinkSpeed() > 1) { // makes sure that the link speed is bigger then one meaning we have connection
                    } else {
                        reConnectToUnitRequired();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }


    /**
     * Functin Teels of Device Restart That needs to be implemented
     */
    public void onDeviceRestartRequired() {

        restartDialog = new Dialog(WorkorderSetSettings.this);
        restartDialog.setContentView(R.layout.dialog_restart_required);
        restartDialog.setTitle(getResources().getString(R.string.restart_required));
        restartDialog.setCancelable(false);
        try {
            restartDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //casting
        /////////////////
        resetDialogHeadlineText = (TextView) restartDialog.findViewById(R.id.dialog_restart_headline);
        resetDialogContentText = (TextView) restartDialog.findViewById(R.id.dialog_restart_content);
        resetDialogCancelButton = (Button) restartDialog.findViewById(R.id.restart_dialog_CancelButton);
        resetDialogRestartButton = (Button) restartDialog.findViewById(R.id.restart_dialog_restartButton);
        resetDialogdialogDonutProgress = (DonutProgress) restartDialog.findViewById(R.id.arc_progressRestartDialog);
        resetDialogDonutProgressText = (TextView) restartDialog.findViewById(R.id.dialog_restart_seconds_text);
        resetDialogExitButton = (Button) restartDialog.findViewById(R.id.restart_dialog_backButton);


        resetDialogdialogDonutProgress.setProgress(0);

        resetDialogDonutProgressText.setText("9");

        final int SECONDS_FOR_TIMER = 9;
        final int[] timerProgress = {0};

        deviceRestartTimer = new Timer();
        deviceRestartTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerProgress[0]++;

                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // updates the display
                            ////////////////////////////
                            resetDialogDonutProgressText.setText(String.valueOf((SECONDS_FOR_TIMER + 1) - timerProgress[0]));
                            resetDialogdialogDonutProgress.setProgress((100 / SECONDS_FOR_TIMER) * timerProgress[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                if (timerProgress[0] >= SECONDS_FOR_TIMER) {

                    Log.d(TAG, "run: ");

                    // cancels the timer
                    ////////////////////////
                    try {
                        deviceRestartTimer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    aligmentManagerClass.savedDataOnSql(workingProject.networkId, callbackForOnPostToBoxFunction, selectedBandIdFromPopup, selectedBandWidthFromPopup, selectedFrequency, allowableFrequencies);
                }


            }
        }, 250, 1000);

        resetDialogRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deviceRestartTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                aligmentManagerClass.savedDataOnSql(workingProject.networkId, callbackForOnPostToBoxFunction, selectedBandIdFromPopup, selectedBandWidthFromPopup, selectedFrequency, allowableFrequencies);

            }
        });

        resetDialogExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    deviceRestartTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                wifiWrapper.forgetNetwork(workingOrder, getApplicationContext());

                Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", selectedProjectId);
                startActivity(i);
                finish();

            }
        });

        resetDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deviceRestartTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    restartDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onPendingDeviceReset() {

        // setting new data to dialog veriables to show
        ////////////////////////////////////////
        try {
            resetDialogHeadlineText.setText(R.string.restarting_device_workorder_settings);
            resetDialogContentText.setText(R.string.reset_dialog_connecting_to_device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            resetDialogCancelButton.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            resetDialogRestartButton.setText(R.string.restart_dialog_connect_now);
            resetDialogdialogDonutProgress.setProgress(0);
            resetDialogDonutProgressText.setText("1:29");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int SECONDS_FOR_TIMER = 90;
        final int[] timerProgress = {0};


        deviceConnectionTimer = new Timer();
        deviceConnectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerProgress[0]++;

                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // updates the display
                            ////////////////////////////
                            if (timerProgress[0] < (SECONDS_FOR_TIMER - 60)) {
                                resetDialogDonutProgressText.setText("1:" + String.valueOf((SECONDS_FOR_TIMER - 1) - timerProgress[0] - 60));
                            } else {
                                resetDialogDonutProgressText.setText("0:" + String.valueOf(SECONDS_FOR_TIMER - timerProgress[0]));
                            }

                            resetDialogdialogDonutProgress.setProgress((100 / SECONDS_FOR_TIMER) * timerProgress[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // checks if the app is in prograss
                        if (timerProgress[0] >= SECONDS_FOR_TIMER) {

                            //timer ended
                            /////////////////////////

                            try {
                                deviceConnectionTimer.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //checks connection speed
                            WifiManager wifiManager = (WifiManager) WorkorderSetSettings.this.getSystemService(Context.WIFI_SERVICE); // gets the CURRENT wifi State
                            if (wifiManager.getConnectionInfo().getLinkSpeed() > 1) { // makes sure that the link speed is bigger then one meaning we have connection
                                try {
                                    aligmentManagerClass.savedDataOnSql(workingProject.networkId, callbackForOnPostToBoxFunction, selectedBandIdFromPopup, selectedBandWidthFromPopup, selectedFrequency, allowableFrequencies);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // trying to reconnect to units
                                reConnectToUnitRequired();

                            }


                        }

                    }
                });


            }
        }, 250, 1000);

        resetDialogRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Log.d(TAG, "onClick: reset Dialog Click");

                    try {
                        deviceConnectionTimer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //checks connection speed
                    WifiManager wifiManager = (WifiManager) WorkorderSetSettings.this.getSystemService(Context.WIFI_SERVICE); // gets the CURRENT wifi State
                    if (wifiManager.getConnectionInfo().getLinkSpeed() > 1) { // makes sure that the link speed is bigger then one meaning we have connection
                        try {
                            aligmentManagerClass.savedDataOnSql(workingProject.networkId, callbackForOnPostToBoxFunction, selectedBandIdFromPopup, selectedBandWidthFromPopup, selectedFrequency, allowableFrequencies);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // trying to reconnect to units
                        reConnectToUnitRequired();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * initlizing the data in the bandwidths list, and sets a listener on the bandwidths spinner
     * the listener will update chooseBandWidthInitated to TRUE
     * the listener will also update a veriable called selectedBandWidthFromPopup to the selected bandwidth the user actually selected
     */
    public void setOnItemSelectListenerOnBandwidth() {



            if (bandWidthCategories != null) {
                bandWidthCategories.clear();
            }

            BuildDynamicCbwList();

            bandWidthdataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bandWidthCategories); // sets an adapter to get the new bandwidth's Category list

            bandWidthdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            bandWidthsSpinner.setAdapter(bandWidthdataAdapter);

            bandWidthdataAdapter.notifyDataSetChanged();

            bandWidthsSpinner.setSelection(selectedCbwPosition); // sets the positino of the selected CBW


    }

    /**
     * this function will return the corisponding band for the dialog building later
     *
     * @return
     */
    private String[] getFrequencyListForBand(String cbw) { //gets the user the correct bandwidth for the selected band by the selected bandwidth

        String[] convertedFrequencysArray = new String[0]; // default value to return

        if (cbw.equals("5")) {
            return TextUtils.split(selectedBandFromPopup.frequency5List, ", ");
        } else if (cbw.equals("7")) {
            return TextUtils.split(selectedBandFromPopup.frequency7List, ", ");
        } else if (cbw.equals("10")) {
            return TextUtils.split(selectedBandFromPopup.frequency10List, ", ");
        } else if (cbw.equals("14")) {
            return TextUtils.split(selectedBandFromPopup.frequency14List, ", ");
        } else if (cbw.equals("20")) {
            return TextUtils.split(selectedBandFromPopup.frequency20List, ", ");
        } else if (cbw.equals("40")) {
            return TextUtils.split(selectedBandFromPopup.frequency40List, ", ");
        } else if (cbw.equals("80")) {
            return TextUtils.split(selectedBandFromPopup.frequency80List, ", ");
        } else {
            return convertedFrequencysArray;
        }
    }

    /**
     * the function will initlize the on screen popup that jumps on screen to show the user all the supported frequecnty's for him to press
     * the function will try to do 2 methods, selectedFrequencyList and showChooseFrequencyPopup and if not sussful it will catch it with the exception that it cannot select a frequec's from the list
     * method selectedFrequencyList will initlize the list of supported frequencys
     * method showChooseFrequencyPopup will show the popup
     */
    void onFrequencyPressed() {

        aligmentManagerClass.checkedExpendbleMenuItems.clear();

        try {

            // initlizes all the expendebles list data
            ///////////////////////////////////////
            List<String> frequencyCatagoriesStringArray = new ArrayList<String>();
            String[] selectedFrequencyList = getFrequencyListForBand(selectedBandWidthFromPopup);
            HashMap<String, List<String>> frequencyListChildStrings = new HashMap<String, List<String>>();

            buildFrequencyList(frequencyCatagoriesStringArray, selectedFrequencyList, frequencyListChildStrings);


            for (int i = 0; i < selectedFrequencyList.length; i++) {

                //selectedFrequency
                for (int j = 0; j < selectedFrequency.size(); j++) {

                    if (selectedFrequency.get(j).equals(selectedFrequencyList[i])) {

                        String frequencyToUpdate = selectedFrequency.get(j);
                        int groupPosition = frequencyListChildStrings.size() - 1;
                        //finds frequency in the list

                        Log.d(TAG, "onFrequencyPressed: needing to migratte frequency: " + frequencyToUpdate);

//                        //runs on the has map
                        for (Map.Entry<String, List<String>> entry : frequencyListChildStrings.entrySet()) {
                            String key = entry.getKey();
                            List<String> valueStringList = entry.getValue();

                            for (int k = 0; k < valueStringList.size(); k++) {

                                if (valueStringList.get(k).equals(frequencyToUpdate)) {
                                    ExpendebleMenuPositionModel menu = new ExpendebleMenuPositionModel(groupPosition, k, true, frequencyToUpdate);
                                    aligmentManagerClass.checkedExpendbleMenuItems.add(menu);

                                }
                            }

                            groupPosition--;

                        }

//
                    }
                }
            }


            //show the expendeble menu
            showExpendbleMenu(frequencyCatagoriesStringArray, frequencyListChildStrings);

            //this bihevior is changins since the new build to show an expandeble menu
            ///////////////////////////////////////////////////////
//            String[] selectedFrequencyList = getFrequencyListForBand(selectedBandWidthFromPopup);
//            showChooseFrequencyPopup(selectedFrequencyList); // shows the manual field with the correct list notes

        } catch (Exception e) {
            new AlertDialog.Builder(WorkorderSetSettings.this)
                    .setTitle(getResources().getString(R.string.title_workordersetsettings_set_error))
                    .setMessage(getResources().getString(R.string.addNewProject_missing_band_and_bandwidth))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    //this method will build up the frequency list a
    ////////////////////////////////////
    private void buildFrequencyList(List<String> frequencyCatagoriesStringArray, String[] selectedFrequencyList, HashMap<String, List<String>> frequencyListChildStrings) {

        int MAX_FREQEUCY_PER_CATEGORY = 10;
        int firstIndexForList = 0;

        //build first headline
        //////////////////////
        if (selectedFrequencyList.length > 10) {
            frequencyCatagoriesStringArray.add(selectedFrequencyList[0] + " - " + selectedFrequencyList[9]);
        } else {
            frequencyCatagoriesStringArray.add(selectedFrequencyList[0] + " - " + selectedFrequencyList[selectedFrequencyList.length - 1]);
        }

        List<String> top10Frequencys = new ArrayList<String>();


        //runs on all freqeuncys
        for (int i = 0; i < selectedFrequencyList.length; i++) {

            //check 10 next frequencyes
            if (i < (firstIndexForList + MAX_FREQEUCY_PER_CATEGORY)) {

                //adds that frequency
                top10Frequencys.add(selectedFrequencyList[i]);

                // freqeucny number 11 is checked
            } else {

                //adds a new frequency in the hash list
                frequencyListChildStrings.put(frequencyCatagoriesStringArray.get(frequencyCatagoriesStringArray.size() - 1), top10Frequencys); // Header, Child data

                //adds a new headline
                if (selectedFrequencyList.length > (i + 10)) {
                    frequencyCatagoriesStringArray.add(selectedFrequencyList[i] + " - " + selectedFrequencyList[i + 10 - 1]);

                } else {
                    frequencyCatagoriesStringArray.add(selectedFrequencyList[i] + " - " + selectedFrequencyList[selectedFrequencyList.length - 1]);

                }

                //initlizes the frequency list
                //////////////////////////////////////
                top10Frequencys = new ArrayList<String>();

                //add the 10nth frequency as first one
                top10Frequencys.add(selectedFrequencyList[i]);

                firstIndexForList = i;
            }

            //adds the reminder
            /////////////////////////
            if (i == (selectedFrequencyList.length - 1)) {
                //adds a new frequency in the hash list
                frequencyListChildStrings.put(frequencyCatagoriesStringArray.get(frequencyCatagoriesStringArray.size() - 1), top10Frequencys); // Header, Child data

            }

        }

        Log.d(TAG, "buildFrequencyList: ");


    }

    /**
     * the function will check if the frequency has been selected or not, to help with the popup to select frequency's
     *
     * @param selectedFrequencyFromPopup the frequencys the user has checked as string
     * @return return true or false weather the frequency has been found or not
     */
    private Boolean isIFrequencyInSelectedArray(String selectedFrequencyFromPopup) {

        Boolean isInList = false;

        if (selectedFrequency.size() > 0) { // meaning we have any seleted frequencys

            for (int i = 0; i < selectedFrequency.size(); i++) { // runs through the whole array to check if we have the selected frequency in the lsit
                if (selectedFrequency.get(i).equals(selectedFrequencyFromPopup)) {
                    isInList = true;
                    break;
                }
            }

        }

        return isInList;

    }

    /**
     * This implementation of the showing the expendeble menu frequency popup
     *
     * @param frequencyCatagoriesStringArray
     * @param frequencyListChildStrings
     */
    private void showExpendbleMenu(List<String> frequencyCatagoriesStringArray, HashMap<String, List<String>> frequencyListChildStrings) {

        frequencysChoosedDialog = new Dialog(this); // initlizing

        frequencysChoosedDialog.setContentView(R.layout.dialog_expendble_list_layout); // CASTING

        frequencysChoosedDialog.setTitle("Choose Frequencies"); // gives a title to the dialog

        frequencysChoosedDialog.show(); // shows the dialog

        Log.d(TAG, "showExpendbleMenu: frequencyListChildStrings: " + frequencyListChildStrings.size());

        // get the listview
        ExpandableListView expListView = (ExpandableListView) frequencysChoosedDialog.findViewById(R.id.expendbleFrequencyList);

        Button expListViewButton = (Button) frequencysChoosedDialog.findViewById(R.id.frequency_list_button);

        final ExpandableFrequencyListAdapter listAdapter = new ExpandableFrequencyListAdapter(this, frequencyCatagoriesStringArray, frequencyListChildStrings, aligmentManagerClass);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        Button saveButton = (Button) frequencysChoosedDialog.findViewById(R.id.frequency_list_button);

        saveButton.setOnClickListener(new View.OnClickListener() { // sets onclick listener that will close the dialog on click
            @Override
            public void onClick(View v) {

            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                aligmentManagerClass.insertCheckedExpendbleMenuItems(new ExpendebleMenuPositionModel(groupPosition, childPosition, true, ""), getApplicationContext());

                listAdapter.notifyDataSetChanged();

                return false;
            }
        });

        //dismises the dialog on save button pressed
        expListViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ExpendebleMenuPositionModel> checkItems = aligmentManagerClass.getCheckedExpendbleMenuItems();

                if (checkItems.size() < 7) {

                    frequencysChoosedDialog.dismiss();

                    //bring the selected frequency's from the aligment manager
                    //////////////////////////////
                    ArrayList<ExpendebleMenuPositionModel> selectedCheckboxes = aligmentManagerClass.getCheckedExpendbleMenuItems();

                    //initlizes the selected frequencys
                    selectedFrequency = new ArrayList<String>();

                    //converted alignment manager class to variable
                    for (int i = 0; i < selectedCheckboxes.size(); i++) {
                        selectedFrequency.add(selectedCheckboxes.get(i).frequencyId);
                    }

                    String stringToShowInButton = TextUtils.join(", ", selectedFrequency);

                    if (!stringToShowInButton.isEmpty()) {
                        Log.d(TAG, "onClick: ");
                        frequencyButton.setText(stringToShowInButton);
                    } else {
                        frequencyButton.setText("Select Frequencies");
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Up to 6 frequencies can be selected",
                            Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    /**
     * this function will show a manual popup to be displayed to let the user choose all the bands necesery
     *
     * @param frequencys
     */
    private void showChooseFrequencyPopup(final String[] frequencys) {

        //changes the frequencys display
        String[] convertedFrequencysArray = new String[frequencys.length];

        //CHANGING THE STIRNG TO SHOW IN THE LIST
        for (int i = 0; i < convertedFrequencysArray.length; i++) { // iritates between the numbers to show the string langth
            String sub = frequencys[i].substring(0, 1);
            String remainder = frequencys[i].substring(1);
            convertedFrequencysArray[i] = sub + "." + remainder + "GHz";
        }

        frequencysChoosedDialog = new Dialog(this); // initlizing

        frequencysChoosedDialog.setContentView(R.layout.dialog_frequency_list_layout); // CASTING

        final ListView listView; // initlizing listview
        listView = (ListView) frequencysChoosedDialog.findViewById(R.id.frequency_list_dialog); // connecting listview to display
        Button saveButton = (Button) frequencysChoosedDialog.findViewById(R.id.frequency_list_button);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, convertedFrequencysArray)); // setting a simple adapter to the list view

        // minimizes the list layout accordenly
        ////////////////////
//        if (convertedFrequencysArray.length > 10) {
//            ViewGroup.LayoutParams param = listView.getLayoutParams();
//            param.height = 1700;
//            listView.setLayoutParams(param);
//            listView.requestLayout();
//        }

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // does the list view multiple choices

        try {
            frequencyListItemCounter = 0;
            // try's to check the list of items that has been checked.
            for (int i = 0; i < frequencys.length; i++) { // colors the list cells
                for (int t = 0; t < selectedFrequency.size(); t++) {
                    if (frequencys[i].equals(selectedFrequency.get(t))) {
                        listView.setItemChecked(i, true);
                        frequencyListItemCounter++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // sets an item listener for the list view
            @Override
            public void onItemClick(AdapterView parent, View v, int pos, long id) {

                Boolean isIFrequencyInSelectedArray = isIFrequencyInSelectedArray(String.valueOf(frequencys[pos])); //checks if the selected freqency is in the list or not

                if (isIFrequencyInSelectedArray == false) {//is frequency is in the selected frequency's array
                    frequencyListItemCounter++; // updates the frequency
                    Log.d(TAG, "onItemClick: not in frequency list");
                } else {
                    frequencyListItemCounter--; // updates the frequency
                }

                if (frequencyListItemCounter > aligmentManagerClass.getChannelScanLimit()) { // cound how many items a user has added and shows a counter when it reaches the number by default 6
                    Toast.makeText(WorkorderSetSettings.this, "Maximum " + String.valueOf(aligmentManagerClass.getChannelScanLimit()) + " frequencies",
                            Toast.LENGTH_SHORT).show();

                    // sets the frequency to 11 to not let it go over 11
                    ////////////////////////////
//                    frequencyListItemCounter = 11;

                    // removes touching
                    try {
//                        listView.getChildAt(pos).setEnabled(false);
                        listView.setItemChecked(pos, false);
                        frequencyListItemCounter = aligmentManagerClass.getChannelScanLimit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else { // adds the frequency to the user databaser
                    if (isIFrequencyInSelectedArray == false) {
                        selectedFrequency.add(String.valueOf(frequencys[pos]));
                        Collections.sort(selectedFrequency);
                    } else {
                        selectedFrequency.remove(frequencys[pos]);
                    }
                }
                if (frequencyListItemCounter == 0) {
                    ResetFrequencies();
                }
            }
        });

        frequencysChoosedDialog.setTitle("Choose Frequencies"); // gives a title to the dialog

        frequencysChoosedDialog.show(); // shows the dialog

        saveButton.setOnClickListener(new View.OnClickListener() { // sets onclick listener that will close the dialog on click
            @Override
            public void onClick(View v) {
                frequencysChoosedDialog.hide();

                String stringToShowInButton = "";

                for (int i = 0; i < selectedFrequency.size(); i++) {
                    if (stringToShowInButton != "") {
                        stringToShowInButton = stringToShowInButton + ", ";
                    }
                    stringToShowInButton = stringToShowInButton + selectedFrequency.get(i);
                }

                if (selectedFrequency.size() > 0) {
                    if (stringToShowInButton.trim().isEmpty())
                        frequencyButton.setText(getResources().getString(R.string.workorderSettings_layout_buttonText));
                    else
                        Log.d(TAG, "onClick: ");
                    frequencyButton.setText(stringToShowInButton);
                } else {
                    ResetFrequencies();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            wifiWrapper.forgetNetwork(workingOrder, getApplicationContext());

            Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
            i.putExtra("projectId", selectedProjectId);
            startActivity(i);
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workorder_set_settings, menu);
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
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        try {
            deviceConnectionTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            deviceRestartTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (id == R.id.dashboard) {
            final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();
            if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects
                wifiWrapper.forgetNetwork(workingOrder, getApplicationContext());
                Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
                i.putExtra("projectId", storedProjectsModels.get(0).projectId);
                startActivity(i);

            } else { // means we don't have any projects and need to show the empty dashboard
                wifiWrapper.forgetNetwork(workingOrder, getApplicationContext());
                Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
                startActivity(i);
            }
        } else if (id == R.id.projects) {
            wifiWrapper.forgetNetwork(workingOrder, getApplicationContext());
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        } else if (id == R.id.about_section) {
            wifiWrapper.forgetNetwork(workingOrder, getApplicationContext());
            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * A function to be called every time settings have been saved to the sql (
     * Happens when user pressed the save and sql data was saved sussfuly
     * starts aligment, we got all messages from the server
     */
    private void onFinishedSaveSettingsInSql() {

//        try { // try to bring on the main dialog
//            mainDialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /**
         * opens up a new thread
         */
        runOnUiThread(new Runnable() { // runs everything back in the UI thread
            @Override
            public void run() {

                //SHOW A DIALOG to update the ulc
                updatingUlcDialog = new AlertDialog.Builder(WorkorderSetSettings.this);
                updatingUlcDialog.setMessage("Updating SU");
                updatingUlcDialog.setCancelable(false);

                alert11 = updatingUlcDialog.create();
                alert11.show();

                Log.d(TAG, "run: aligmentManager SAVE ON DATABASE");

                try {
                    if (workingProject.networkId.length() == 0) {
                        workingProject.networkId = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                aligmentManagerClass.savedDataOnSql(workingProject.networkId, callbackForOnPostToBoxFunction, selectedBandIdFromPopup, selectedBandWidthFromPopup, selectedFrequency, allowableFrequencies);


            }
        });


    }


    public interface onPostToBoxInterface {

        void onPostToBoxFunction(String params);

    }

    /**
     * this is the actual interdface for the saved data in sql function
     * it will be called after the 2 post calles to the server has been initated
     */
    onPostToBoxInterface callbackForOnPostToBoxFunction = new onPostToBoxInterface() {

        @Override
        public void onPostToBoxFunction(String params) {

            //NEEDS TO ADD RESTART SUPPORT
            Log.d(TAG, "onPostToBoxFunction: params: " + params);


            if ((params != null) && (params != "")) {

                if (params.equals("Done")) { // SUCCESS MEAN WE HAD TOLD THE BOX TO START THE ALIGMENT AND ARE READY TO MOVE TO THE NEW SCREEN


                    try {
                        if (updatingUlcDialog != null) {
                            updatingUlcDialog.setMessage(getResources().getString(R.string.done));     // changes text
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplication(), HorizontalPlacementScreenActivity.class); //go to the next screen
                    intent.putExtra("projectId", selectedProjectId);
                    intent.putExtra("workorderId", selectedWorkorderId);
                    startActivity(intent);

                } else if (params.equals("PendingReset")) { // PendingReset means we reseted the Alignment and we need to reconnect to the unite

                    onPendingDeviceReset();


                } else {

                    Log.d(TAG, "onPostToBoxFunction: ???????????????");
//                    alert11.setMessage(getResources().getString(R.string.connect_set_bands));     // changes text

                    // sets alert dialog builder
                    /////////////////////////////
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(WorkorderSetSettings.this);
                    builder1.setMessage(getResources().getString(R.string.connect_set_bands));
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            R.string.return_to_workorders_dialog,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent i = new Intent(getApplication(), WorkordersSelectionActivity.class); // moves to the fine aligment screen
                                    i.putExtra("projectId", selectedProjectId);
                                    startActivity(i);

                                    dialog.cancel();
                                }
                            });

                    alert11 = builder1.create();
                    alert11.show();

//                    // means the user can cancel the dialog
//                    alert11.setCancelable(true);
//
//                    // future use to maybe go back once the user cancled the dialog
//                    alert11.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//
//                        }
//                    });

                }

            } else {

                //checks if we are connected to the correct network
                if (!wifiWrapper.isNetworkMatch(workingOrder.workorderWifiSSID, getApplicationContext())) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), R.string.wifi_connection_toast_wifi, //wifi is enabled
                            Toast.LENGTH_SHORT).show();
                }

                //alert11.setMessage("couldn't Set Device Band Please restart application");     // changes text
                new android.support.v7.app.AlertDialog.Builder(WorkorderSetSettings.this)
                        .setTitle(getResources().getString(R.string.title_workordersetsettings_set_error))
                        .setMessage(getResources().getString(R.string.set_error_workordersetsettings))
                        .setPositiveButton(getResources().getString(R.string.set_error_button_workordersetsettings), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                wifiWrapper.forgetNetwork(workingOrder, getApplicationContext());
                                Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
                                startActivity(i);
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        }
    };


}
