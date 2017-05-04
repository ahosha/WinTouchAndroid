package apps.radwin.wintouch.activities.alignmentActivities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.models.ProjectsModel;

import static apps.radwin.wintouch.R.id.addNewProject_layout_spinner;

public class Add_New_Project_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    String TAG = "AddNewProject";
    Button saveButton, bandButton, bandwidthButton, frequenctyButton;
    Dialog inputFieldDialog;
    int listItemCounter = 0; // counts how many items you clicked on
    Switch isBestEffortSwitch;
    CharSequence bandsCharArray[];
    String selectedBandId, selectedBandWidth; // this will hold the id for the selected band
    ArrayList<String> selectedFrequency = new ArrayList<String>();
    EditText networkIdBox, trueputUp, trueputDown;
    com.rengwuxian.materialedittext.MaterialEditText projectNameBox, projectDescriptionBox, projectEmail;
    Boolean cameFromAddNewProject = true;
    Boolean isInEditMode = false;
    Tracker analyticsTracker;
    String selectedProjectId = "";
    Spinner serviceTypeSpinner;
    private Tracker mTracker;

//

    /**
     * on create is the first function that is being called every time the Activity is beaing called
     * it is called once in the activity's life cycle
     *
     * @param savedInstanceState saved instances, will hold some of the params of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_project_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);

            // does the back button
            /////////////////////////////
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable upArrow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_black_24dp);
            upArrow.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            setActionbarTextColor(getSupportActionBar(), Color.WHITE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        ///////////////////////////////////////
        ///////START OF IMPLEMENTATION/////////

        //connecting to display
        projectNameBox = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.newProject_projectName); // casting
        projectDescriptionBox = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.newProject_projectDescription); // casting
        projectEmail = (com.rengwuxian.materialedittext.MaterialEditText) findViewById(R.id.newProject_email);
        networkIdBox = (EditText) findViewById(R.id.newProject_networkId); // casting
        //saveButton = (Button) findViewById(R.id.newProject_saveButton); // casting
        serviceTypeSpinner = (Spinner) findViewById(addNewProject_layout_spinner); // casting
        trueputUp = (EditText) findViewById(R.id.addNewProject_layout_tput_up);//casting
        trueputDown = (EditText) findViewById(R.id.addNewProject_layout_tput_down);//casting

        try {
            projectDescriptionBox.setFocusFraction(1f);
            projectNameBox.setFocusFraction(1f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        networkIdBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    String netId = networkIdBox.getText().toString();
                    netId = Add_New_Project_Activity.removeSpecialChars(netId);
                    networkIdBox.setText(netId);
                }
            }
        });


        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        //getting the saved instances
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                cameFromAddNewProject = true;
                isInEditMode = false;
                selectedProjectId = "";
            } else {
                cameFromAddNewProject = extras.getBoolean("isCameFromAddNewProject");
                isInEditMode = extras.getBoolean("isInEditMode");
                selectedProjectId = extras.getString("projectId");
            }
        } else {
            cameFromAddNewProject = (Boolean) savedInstanceState.getSerializable("isCameFromAddNewProject");
            selectedProjectId = (String) savedInstanceState.getSerializable("projectPosition");
            isInEditMode = (Boolean) savedInstanceState.getSerializable("projectId");
        }

        //////////////////////////////////
        /////initlizes all veriables//////
        //////////////////////////////////

        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();


        if (isInEditMode) { // the user is in edit mode so we need to choose the correct

            ProjectsModel savedProjects = ProjectsModel.getProjectWithId(selectedProjectId); // gets the selected project
            projectNameBox.setText(savedProjects.projectName); // set the correct project name in the name box
            projectEmail.setText(savedProjects.projectEmail); // set the correct project email in the email box
            projectDescriptionBox.setText(savedProjects.description); // sets the correct project description in the description box

            String netId = "";
            if (savedProjects.networkId == null)
                savedProjects.networkId = "";

            try {
                netId = Add_New_Project_Activity.removeSpecialChars(savedProjects.networkId);
            } catch (Exception e) {
            }

            networkIdBox.setText(netId); // sets the correct project network id in the network id box


            if (savedProjects.isBestEffort == false) {
                //sets the best effort switch to the correct position
                serviceTypeSpinner.setSelection(1);
            }

            trueputUp.setText(String.valueOf(savedProjects.truePutUp));
            if (String.valueOf(savedProjects.truePutUp).contains(".0")) {
                String[] parts = String.valueOf(savedProjects.truePutUp).split("\\.");
                trueputUp.setText(parts[0]);
            }
            trueputDown.setText(String.valueOf(savedProjects.truePutDown));
            if (String.valueOf(savedProjects.truePutDown).contains(".0")) {
                String[] parts = String.valueOf(savedProjects.truePutDown).split("\\.");
                trueputDown.setText(parts[0]);
            }
            setTitle(getResources().getString(R.string.title_activity_edit__new__project_));


            projectNameBox.setSelection(savedProjects.projectName.length(), savedProjects.projectName.length());

            analyticsTracker.setScreenName("Edit Project");
            analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        } else {
            setTitle(getResources().getString(R.string.title_activity_add__new__project_));
        }



    }





    private void setActionbarTextColor(android.support.v7.app.ActionBar actBar, int color) {

        String title = actBar.getTitle().toString();
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);

    }
    public static String removeSpecialChars(String input)
    {
        try {
            char[] allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_!@#$%^&*)(=+|\\/".toCharArray();
            char[] charArray = input.toString().toCharArray();
            StringBuilder result = new StringBuilder();
            for (char c : charArray) {
                for (char a : allowed) {
                    if (c == a) {
                        result.append(a);
                        break;
                    }
                }
            }
            return result.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }


    @Override
    protected void onStart() {
        super.onStart();

        analyticsTracker.setScreenName("Add New Project");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    /**
     * listener for back pressed on the screen
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * will be called once user has pressed the options menu at the top right of the screen
     *
     * @param menu menu will hold the params for the menu iotuibs
     * @return return just returns true to verify theat the lifecycle has been called
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add__new__project_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d(TAG, "onOptionsItemSelected: analitics started");
        List<ProjectsModel> projectModels = ProjectsModel.getAllProjects(); // gets all the projects model so that we will know the size for it

        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Global Counters")
                .setAction("Number of Projects")
                .setLabel(String.valueOf(projectModels.size()))
                .setValue(1)
                .build());

        Log.d(TAG, "onOptionsItemSelected: analitics finished");

        Log.d(TAG, "onOptionsItemSelected: id is: " + id);
        if (id == R.id.action_name) {
            onOptionMenuSelected();
        } else {
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);
            finish();
        }

        // closes up the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

                if (listItemCounter > 10) { // cound how many items a user has added and shows a counter when it reaches 10
                    Toast.makeText(Add_New_Project_Activity.this, getResources().getString(R.string.addNewProject_missing_sucessful_exeeded_maximum_bands),
                            Toast.LENGTH_SHORT).show();
                } else { // adds the frequency to the user databaser
                    selectedFrequency.add(String.valueOf(frequencys[pos]));

                }
            }
        });

        inputFieldDialog.setTitle("Choose Frequencys"); // gives a title to the dialog

        inputFieldDialog.show(); // shows the dialog

    }


    /**
     * heppens whenever the options menu is selected
     */
    public void onOptionMenuSelected() {

        String projectName = projectNameBox.getText().toString(); // gets the project name out of the input
        Boolean isBestEffort, isNameExistsInDatabase = false;

        if (serviceTypeSpinner.getSelectedItemPosition() == 0) { // gets the best effort state
            isBestEffort = true;
        } else {
            isBestEffort = false;
        }

        final String emailAddressRegex = "^[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
        final String emailAddress = projectEmail.getText().toString();
        if (isInEditMode) { // is the user in edit mode ?

            if ((trueputUp.getText().toString().equals("")) || (trueputDown.getText().toString().equals("")) || (trueputDown.getText().toString().equals("0")) || (trueputUp.getText().toString().equals("0")) ) { // checks if there was data inserted to the trueput up and down
                Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_Toast_noUpAndDownData), // shows a toast to the user that the name exists
                        Toast.LENGTH_SHORT).show();
            } else {


                if (projectName.trim().isEmpty() == false) {

                List<ProjectsModel> projectModels = ProjectsModel.getAllProjects(); // gets all the projects model so that we will know the size for it
                ProjectsModel savedProjects = ProjectsModel.getProjectWithId(selectedProjectId); // gets the selected project

                //checks if the name exists in the list
                for (int i = 0; i < projectModels.size(); i++) {
                    if (projectName.toLowerCase().equals(projectModels.get(i).projectName.toLowerCase())) {

                        isNameExistsInDatabase = true;

                        if (projectName.toLowerCase().equals(savedProjects.projectName.toLowerCase())) {
                            isNameExistsInDatabase = false;
                        }
                    }
                }
                    if (emailAddress.equals("") || emailAddress.matches(emailAddressRegex)) {

                if (isNameExistsInDatabase == false) {  // checks statment if the name exists in the database or not

                    String netId = "";
                    try {
//                        netId = networkIdBox.getText().toString().replaceAll("[^a-zA-Z0-9]", "");
                        netId = Add_New_Project_Activity.removeSpecialChars( networkIdBox.getText().toString() );
//                        netId = networkIdBox.getText().toString().replaceAll("^[A-Za-z ][A-Za-z0-9!@#$%^&* ]*$", "");


                    } catch (Exception e) {
                    }

                    networkIdBox.setText(netId);

                    if ((networkIdBox.getText().length() == 0) || (networkIdBox.getText().length() == 4)) { // checks if the network if is equals 4 or 0 (if the user input the network id or is it legal)

                        ProjectsModel projectsModel = ProjectsModel.getProjectWithId(selectedProjectId); // gets the correct project

                        projectsModel.projectName = projectName.trim();
                        projectsModel.description = projectDescriptionBox.getText().toString();
                        projectsModel.networkId = networkIdBox.getText().toString();

                        projectsModel.truePutUp = 1.0;
                        projectsModel.truePutDown = 1.0;
                        try {
                            projectsModel.truePutUp = Double.valueOf(trueputUp.getText().toString());
                            projectsModel.truePutDown = Double.valueOf(trueputDown.getText().toString());
                        } catch (Exception e) {
                        }

                            projectsModel.isBestEffort = isBestEffort;
                            projectsModel.projectEmail = projectEmail.getText().toString();

                        projectsModel.save(); // projects updated

                        Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_same_project_updated), // shows a toast to the user that the name exists
                                Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the projects selection screen
                        startActivity(i);
                        finish();

                        try {
                            View view = this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        } catch (Exception e) {
                        }


                    } else { // text of network id is diffrent then four

                        Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_networkId_iliigal), // shows a toast to the user that the name exists
                                Toast.LENGTH_SHORT).show();

                    }

                } else {

                    Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_same_project_name), // shows a toast to the user that the name exists
                            Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(this, getString(R.string.addNewProject_Toast_invalid_email), Toast.LENGTH_SHORT).show();
                    }

            } else { // handels the event that we don't have a name , and the user cannot continue

                Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_missing_project_name),
                        Toast.LENGTH_SHORT).show();


            }

            }

        } else { //handles the case  user isn't in edit mode but came from adding a new project screen

            if ((trueputUp.getText().toString().equals("")) || (trueputDown.getText().toString().equals("")) || (trueputDown.getText().toString().equals("0")) || (trueputUp.getText().toString().equals("0")) ) { // checks if there was data inserted to the trueput up and down
                Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_Toast_noUpAndDownData), // shows a toast to the user that the name exists
                        Toast.LENGTH_SHORT).show();

            } else { // meaning we have data in the trueput up and down that the user inputed

                if (projectName != null)
                    projectName = projectName.trim();

                if (projectName.isEmpty() == false) { // checks if you have a name

                    if(emailAddress.equals("") || emailAddress.matches(emailAddressRegex)) {

                    List<ProjectsModel> projectModels = ProjectsModel.getAllProjects(); // gets all the projects model so that we will know the size for it

                    Log.d("myLogs", "onClick: networkid for save is: " + networkIdBox.getText().toString());

                    //checks if the name exists in the list
                    for (int i = 0; i < projectModels.size(); i++) {
                        if (projectName.toLowerCase().equals(projectModels.get(i).projectName.toLowerCase())) {
                            isNameExistsInDatabase = true;
                        }
                    }

                    Log.d(TAG, "onClick: trueput up: " + String.valueOf(trueputUp.getText()));
                    Log.d(TAG, "onClick: truePutDown : " + String.valueOf(trueputDown.getText()));

                    if (isNameExistsInDatabase == false) {  // checks statment if the name exists in the database or not

                        String netId = "";
                        try {
//                            netId = networkIdBox.getText().toString().replaceAll("[^a-zA-Z0-9]", "");
//                            netId = networkIdBox.getText().toString().replaceAll("^[A-Za-z ][A-Za-z0-9!@#$%^&* ]*$", "");
                            netId = Add_New_Project_Activity.removeSpecialChars( networkIdBox.getText().toString() );


                        } catch (Exception e) {
                        }

                        networkIdBox.setText(netId);

                        if ((networkIdBox.getText().length() == 0) || (networkIdBox.getText().length() == 4)) { // checks if the network if is equals 4 or 0 (if the user input the network id or is it legal)

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

                            // one month hack
//                            Calendar cal = Calendar.getInstance();
//                            cal.add(Calendar.MONTH, -1);
//                            java.util.Date dt = cal.getTime();
//                            String currentDateandTime = sdf.format(dt);

                            String currentDateandTime = sdf.format(new Date());


                            ProjectsModel projectsModel = new ProjectsModel(); // projects modol saving to the sql
                            projectsModel.projectId = UUID.randomUUID().toString();
                            projectsModel.projectName = projectName.trim();
                            projectsModel.description = projectDescriptionBox.getText().toString();
                            projectsModel.networkId = networkIdBox.getText().toString();
                            projectsModel.isBestEffort = isBestEffort;
                            projectsModel.currentBandId = "";
                            projectsModel.truePutUp = 1.0;
                            projectsModel.truePutDown = 1.0;
                            try {
                                projectsModel.truePutUp = Double.valueOf(trueputUp.getText().toString());
                                projectsModel.truePutDown = Double.valueOf(trueputDown.getText().toString());
                            } catch (Exception e) {
                            }

                            projectsModel.selectedFrequencysList = ""; // will ransform the list into text;
                            projectsModel.currentChannelBandwith = "";
                            projectsModel.avilableBandsIdLList = "";
                            projectsModel.creationDate = currentDateandTime;

                            projectsModel.projectEmail = projectEmail.getText().toString();

                            projectsModel.save();

                            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the projects selection screen
                            startActivity(i);
                            finish();

                            try {
                                View view = this.getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            } catch (Exception e) {
                            }


                        } else { // text of network id is diffrent then four

                            Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_networkId_iliigal), // shows a toast to the user that the name exists
                                    Toast.LENGTH_SHORT).show();

                        }

                    } else { //case the name already exists
                        Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_same_project_name), // shows a toast to the user that the name exists
                                Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(this, getString(R.string.addNewProject_Toast_invalid_email), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_missing_project_name),
                            Toast.LENGTH_SHORT).show();

                }

            }

        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


        }


    }
}
