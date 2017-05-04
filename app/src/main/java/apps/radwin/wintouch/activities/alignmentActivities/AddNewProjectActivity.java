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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.models.ProjectsModel;

import static apps.radwin.wintouch.R.id.addNewProject_layout_spinner;

public class AddNewProjectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnFocusChangeListener {

    private static final String TAG = "AddNewProject";

    private String selectedProjectId = "";

    // counts how many items you clicked on
    private int clickedItemsCounter = 0;
    private boolean isInEditMode = false;

    private ArrayList<String> selectedFrequency = new ArrayList<String>();

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private EditText networkIdBoxTxt;
    private EditText truePutUpTxt;
    private EditText truePutDownTxt;
    private EditText projectNameBoxTxt;
    private MaterialEditText projectDescriptionBoxTxt;
    private MaterialEditText projectEmailTxt;
    private Spinner serviceTypeSpinner;

    private Tracker analyticsTracker;

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

        initViews();
        showSoftKeyboard();
        setPreconditions(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        analyticsTracker.setScreenName("Add New Project");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(view.getId() == R.id.newProject_networkId) {
            if (!b) {
                final String netId = networkIdBoxTxt.getText().toString();
                networkIdBoxTxt.setText(
                        AddNewProjectActivity.removeSpecialChars(netId)
                );
            }
        }
    }

    /**
     * listener for back pressed on the screen
     */
    @Override
    public void onBackPressed() {
        final DrawerLayout drawer =
                (DrawerLayout) findViewById(R.id.drawer_layout);

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
        List<ProjectsModel> projectModels =
                ProjectsModel.getAllProjects(); // gets all the projects model so that we will know the size for it

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
            final Intent i = new Intent(
                    getApplication(),
                    projectSelectionMainFragment.class
            ); // moves to the fine aligment screen
            startActivity(i);
            finish();
        }

        // closes up the keyboard
        final View view = this.getCurrentFocus();

        if (view != null) {
            final InputMethodManager inputMethodManager =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            final List<ProjectsModel> storedProjectsModels =
                    ProjectsModel.getAllProjects();
            if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects
                final Intent intent = new Intent(
                        getApplication(),
                        DashboardActivity.class
                ); // moves to the fine aligment screen
                intent.putExtra(
                        "projectId",
                        storedProjectsModels.get(0).projectId
                );
                startActivity(intent);

            } else { // means we don't have any projects and need to show the empty dashboard
                final Intent intent = new Intent(
                        getApplication(),
                        EmptyDashboardActivity.class
                ); // moves to the fine aligment screen
                startActivity(intent);
            }

        } else if (id == R.id.projects) {
            final Intent intent = new Intent(
                    getApplication(),
                    projectSelectionMainFragment.class
            ); // moves to the fine aligment screen
            startActivity(intent);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        } else if (id == R.id.about_section) {

            final Intent intent = new Intent(
                    getApplication(),
                    AboutActivity.class
            ); // moves to the fine aligment screen
            startActivity(intent);

        } else if (id == R.id.installationGuide) {
            final Intent intent = new Intent(
                    getApplication(),
                    InstallationGuideActivity.class
            ); // moves to the fine aligment screen
            startActivity(intent);
        }

        final DrawerLayout drawer =
                (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void initViews() {
        final Toolbar toolbar =
                (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView =
                (NavigationView) findViewById(R.id.nav_view);
        drawerLayout =
                (DrawerLayout) findViewById(R.id.drawer_layout);
        projectNameBoxTxt =
                (EditText) findViewById(R.id.newProject_projectName);
        projectDescriptionBoxTxt =
                (MaterialEditText) findViewById(R.id.newProject_projectDescription);
        projectEmailTxt =
                (MaterialEditText) findViewById(R.id.newProject_email);
        networkIdBoxTxt =
                (EditText) findViewById(R.id.newProject_networkId);
        serviceTypeSpinner =
                (Spinner) findViewById(addNewProject_layout_spinner);
        truePutUpTxt =
                (EditText) findViewById(R.id.addNewProject_layout_tput_up);
        truePutDownTxt =
                (EditText) findViewById(R.id.addNewProject_layout_tput_down);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setViews();

        networkIdBoxTxt.setOnFocusChangeListener(this);
    }

    private void setViews() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        final Drawable upArrow =
                ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.ic_arrow_back_black_24dp
                );
        upArrow.setColorFilter(
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.white
                ),
                PorterDuff.Mode.SRC_ATOP
        );

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            setActionbarTextColor(getSupportActionBar(), Color.WHITE);
        }

        projectDescriptionBoxTxt.setFocusFraction(1f);
//        projectNameBoxTxt.setFocusFraction(1f);
        projectNameBoxTxt.setFocusable(true);
    }

    private void showSoftKeyboard() {
        final InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void setPreconditions(Bundle state) {
        if (state == null) {
            final Bundle extras = getIntent().getExtras();

            if (extras == null) {
                isInEditMode = false;
                selectedProjectId = "";
            } else {
                isInEditMode = extras.getBoolean("isInEditMode");
                selectedProjectId = extras.getString("projectId");
            }
        } else {
            selectedProjectId =
                    (String) state.getSerializable("projectPosition");
            isInEditMode =
                    (Boolean) state.getSerializable("projectId");
        }

        loadData();
        analyticsTracker = ((appContext) getApplication()).getDefaultTracker();
    }

    private void loadData() {
        if (isInEditMode) { // the user is in edit mode

            analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

            analyticsTracker.setScreenName("Edit Project");
            analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

            final ProjectsModel savedProjects =
                    ProjectsModel.getProjectWithId(selectedProjectId); // gets the selected project

            projectNameBoxTxt.setText(savedProjects.projectName); // set the correct project name in the name box
            projectEmailTxt.setText(savedProjects.projectEmail); // set the correct project email in the email box
            projectDescriptionBoxTxt.setText(savedProjects.description); // sets the correct project description in the description box

            if (savedProjects.networkId == null)
                savedProjects.networkId = "";

            final String netId =
                    AddNewProjectActivity.removeSpecialChars(savedProjects.networkId);
            networkIdBoxTxt.setText(netId); // sets the correct project network id in the network id box

            if (!savedProjects.isBestEffort) {
                //sets the best effort switch to the correct position
                serviceTypeSpinner.setSelection(1);
            }

            truePutUpTxt.setText(
                    String.valueOf(savedProjects.truePutUp)
            );

            if (String.valueOf(savedProjects.truePutUp).contains(".0")) {
                String[] parts =
                        String.valueOf(savedProjects.truePutUp).split("\\.");
                truePutUpTxt.setText(parts[0]);
            }

            truePutDownTxt.setText(
                    String.valueOf(savedProjects.truePutDown)
            );

            if (String.valueOf(savedProjects.truePutDown).contains(".0")) {
                String[] parts = String.valueOf(savedProjects.truePutDown).split("\\.");
                truePutDownTxt.setText(parts[0]);
            }

            setTitle(getResources().getString(R.string.title_activity_edit__new__project_));

            projectNameBoxTxt.setSelection(
                    savedProjects.projectName.length(),
                    savedProjects.projectName.length()
            );
        } else {
            setTitle(
                    getResources().getString(R.string.title_activity_add__new__project_)
            );
        }
    }

    private void setActionbarTextColor(android.support.v7.app.ActionBar actBar, int color) {

        String title = actBar.getTitle().toString();
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);

    }

    public static String removeSpecialChars(String input) {
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

        final Dialog inputFieldDialog = new Dialog(this); // initlizing

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

                clickedItemsCounter++; // updates the frequency

                if (clickedItemsCounter > 10) { // cound how many items a user has added and shows a counter when it reaches 10
                    Toast.makeText(AddNewProjectActivity.this, getResources().getString(R.string.addNewProject_missing_sucessful_exeeded_maximum_bands),
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

        String projectName = projectNameBoxTxt.getText().toString(); // gets the project name out of the input
        Boolean isBestEffort, isNameExistsInDatabase = false;

        if (serviceTypeSpinner.getSelectedItemPosition() == 0) { // gets the best effort state
            isBestEffort = true;
        } else {
            isBestEffort = false;
        }

        final String emailAddressRegex = "^[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
        final String emailAddress = projectEmailTxt.getText().toString().trim();

        if (isInEditMode) { // is the user in edit mode ?
            if ((truePutUpTxt.getText().toString().equals("")) || (truePutDownTxt.getText().toString().equals("")) || (Integer.parseInt(truePutDownTxt.getText().toString()) <= 0) || (Integer.parseInt(truePutUpTxt.getText().toString()) <= 0)) { // checks if there was data inserted to the trueput up and down
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
                        netId = AddNewProjectActivity.removeSpecialChars( networkIdBoxTxt.getText().toString() );
//                        netId = networkIdBox.getText().toString().replaceAll("^[A-Za-z ][A-Za-z0-9!@#$%^&* ]*$", "");


                    } catch (Exception e) {
                    }

                    networkIdBoxTxt.setText(netId);

                    if ((networkIdBoxTxt.getText().length() == 0) || (networkIdBoxTxt.getText().length() == 4)) { // checks if the network if is equals 4 or 0 (if the user input the network id or is it legal)

                        ProjectsModel projectsModel = ProjectsModel.getProjectWithId(selectedProjectId); // gets the correct project

                        projectsModel.projectName = projectName.trim();
                        projectsModel.description = projectDescriptionBoxTxt.getText().toString();
                        projectsModel.networkId = networkIdBoxTxt.getText().toString();

                        projectsModel.truePutUp = 1.0;
                        projectsModel.truePutDown = 1.0;
                        try {
                            projectsModel.truePutUp = Double.valueOf(truePutUpTxt.getText().toString());
                            projectsModel.truePutDown = Double.valueOf(truePutDownTxt.getText().toString());
                        } catch (Exception e) {
                        }

                            projectsModel.isBestEffort = isBestEffort;
                            projectsModel.projectEmail = projectEmailTxt.getText().toString().trim();

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

            if ((truePutUpTxt.getText().toString().equals("")) || (truePutDownTxt.getText().toString().equals("")) || (Integer.parseInt(truePutDownTxt.getText().toString()) <= 0) || (Integer.parseInt(truePutUpTxt.getText().toString()) <= 0)) { // checks if there was data inserted to the trueput up and down
                Toast.makeText(getApplication(), getResources().getString(R.string.addNewProject_Toast_noUpAndDownData), // shows a toast to the user that the name exists
                        Toast.LENGTH_SHORT).show();

            } else { // meaning we have data in the trueput up and down that the user inputed

                if (projectName != null)
                    projectName = projectName.trim();

                if (projectName.isEmpty() == false) { // checks if you have a name

                    if(emailAddress.equals("") || emailAddress.matches(emailAddressRegex)) {

                    List<ProjectsModel> projectModels = ProjectsModel.getAllProjects(); // gets all the projects model so that we will know the size for it

                    Log.d("myLogs", "onClick: networkid for save is: " + networkIdBoxTxt.getText().toString());

                    //checks if the name exists in the list
                    for (int i = 0; i < projectModels.size(); i++) {
                        if (projectName.toLowerCase().equals(projectModels.get(i).projectName.toLowerCase())) {
                            isNameExistsInDatabase = true;
                        }
                    }

                    Log.d(TAG, "onClick: trueput up: " + String.valueOf(truePutUpTxt.getText()));
                    Log.d(TAG, "onClick: truePutDown : " + String.valueOf(truePutDownTxt.getText()));

                    if (isNameExistsInDatabase == false) {  // checks statment if the name exists in the database or not

                        String netId = "";
                        try {
//                            netId = networkIdBox.getText().toString().replaceAll("[^a-zA-Z0-9]", "");
//                            netId = networkIdBox.getText().toString().replaceAll("^[A-Za-z ][A-Za-z0-9!@#$%^&* ]*$", "");
                            netId = AddNewProjectActivity.removeSpecialChars( networkIdBoxTxt.getText().toString() );


                        } catch (Exception e) {
                        }

                        networkIdBoxTxt.setText(netId);

                        if ((networkIdBoxTxt.getText().length() == 0) || (networkIdBoxTxt.getText().length() == 4)) { // checks if the network if is equals 4 or 0 (if the user input the network id or is it legal)

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
                            projectsModel.description = projectDescriptionBoxTxt.getText().toString();
                            projectsModel.networkId = networkIdBoxTxt.getText().toString();
                            projectsModel.isBestEffort = isBestEffort;
                            projectsModel.currentBandId = "";
                            projectsModel.truePutUp = 1.0;
                            projectsModel.truePutDown = 1.0;
                            try {
                                projectsModel.truePutUp = Double.valueOf(truePutUpTxt.getText().toString());
                                projectsModel.truePutDown = Double.valueOf(truePutDownTxt.getText().toString());
                            } catch (Exception e) {
                            }

                            projectsModel.selectedFrequencysList = ""; // will ransform the list into text;
                            projectsModel.currentChannelBandwith = "";
                            projectsModel.avilableBandsIdLList = "";
                            projectsModel.creationDate = currentDateandTime;

                            projectsModel.projectEmail = projectEmailTxt.getText().toString().trim();

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
}
