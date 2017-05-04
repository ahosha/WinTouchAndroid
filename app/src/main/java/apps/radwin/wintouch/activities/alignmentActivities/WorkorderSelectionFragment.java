package apps.radwin.wintouch.activities.alignmentActivities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.WororderSelectionAdapter;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.utils.PermissionUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class WorkorderSelectionFragment extends Fragment implements AdapterView.OnItemClickListener {


    //TextView mainHeader, secondaryHeader, distanceHeader;
    //Button headerButton;
    //ImageView headerImageView, headerBackgroundImage;
    List<String> projectsListArray;
    ListView lv;

//    @BindView(R.id.workorder_selection_listView)
//    ListView lv;



    public static int WORKORDER_EXPORTED =0;
    WororderSelectionAdapter wororderSelectionAdapter; // will hold an instance of the list adapter
    private SwipeRefreshLayout swipeContainer;

    List<WorkingOrdersModel> workingOrdersFromDataBase = null;

    final String TAG = WorkorderSelectionFragment.class.getSimpleName();

    String selectedProjectId;
    WorkingOrdersModel firstWorkorder;
    Boolean isListInitatedEmpty = true;
    List<WorkingOrdersModel> storedworkingOrdersModels;

    private String mDaysFilterType  = "";
    public final static Boolean SUPPORT_EXPORT = false;

    private WorkingOrdersModel wom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workorder_selection_fragment, container, false);
//        ButterKnife.bind(this, view);


        lv = (ListView) view.findViewById(R.id.workorder_selection_listView);
        lv.setOnItemClickListener(this);

        // 0 - 7 days
        // 1 - 30 days
        // 2 - all
        Bundle bundle = getArguments();
        mDaysFilterType = Integer.toString(bundle.getInt("count"));

        selectedProjectId = bundle.getString("projectId");


//        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
//        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//
//
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//
//                MenuInflater inflater = mode.getMenuInflater();
//                inflater.inflate(R.menu.contextual_menu, menu);
//
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                return false;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//
//            }
//        });


        //old long click behivioer
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Object obj = lv.getAdapter().getItem(position);

                    if (WorkingOrdersModel.class.isInstance(obj)) {
                        WorkingOrdersModel wom = (WorkingOrdersModel) obj;
                        String woId = wom.workordertId;
                        String projectId = wom.projectId;
                        editWODialog(projectId, woId);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return true;
            }
        });

        /////////////////////////////////////////
//        mainHeader = (TextView) view.findViewById(R.id.selectWorkorder_mainworkorder_workorderName);
//        secondaryHeader = (TextView) view.findViewById(R.id.selectWorkorder_mainworkorder_secondaryHeader);
//        distanceHeader = (TextView) view.findViewById(R.id.selectWorkorder_mainworkorder_workorderDistance);
//        headerImageView = (ImageView) view.findViewById(R.id.selectWorkorder_mainworkorder_workorderImage);
//        headerBackgroundImage = (ImageView) view.findViewById(R.id.selectWorkorder_mainworkorder_workorderBackgroundImage);
//        headerButton = (Button) view.findViewById(R.id.selectWorkorder_mainworkorder_workorderBackgroundButton);


        updateWorkOrderList();

        if (SUPPORT_EXPORT) {
            boolean hasPermission = (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        112);
            }
        }


//        try { //try to put the new workorder
//            mainHeader.setText(firstWorkorder.workingOrderName);
//
//            if (mainHeader.length() > 20) { // try to limit project name
//                mainHeader.setText(String.valueOf(mainHeader.getText()).substring(0, 20)+"...");
//            }
//
//            Integer distance = 0;
//            secondaryHeader.setText("Nearest Site");
//            distanceHeader.setText(distance.toString() + " km");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //firstWorkorder.lastUpdateStatus

        //////////////////////////////////////////////////////////////
        //////////////// Swipe container initlization ////////////////
        //////////////////////////////////////////////////////////////
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.workorderSelectionSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        /////////////////////////////////////////////
        //////////On view listener for Image/////////
        /////////////////////////////////////////////
/*
        headerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListInitatedEmpty == false) {

                    WorkingOrdersModel selectedWorkorder = firstWorkorder;

                    if (selectedWorkorder.lastUpdateStatus.equals("complete")) {

                        Intent i = new Intent(getActivity(), ReportActivity.class); // moves to the fine aligment screen
                        i.putExtra("selectedWorkorderId", selectedWorkorder.workordertId);
                        i.putExtra("selectedProjectId", selectedProjectId);
                        i.putExtra("upLinkResult", selectedWorkorder.upLinkResult);
                        i.putExtra("downLinkResult", selectedWorkorder.downLinkResult);
                        i.putExtra("status", "Passed");
                        startActivity(i);

                    } else  {

                        //updates teh workorder status
                        selectedWorkorder.lastUpdateStatus = "inComplete";
                        selectedWorkorder.save();

                        Intent i = new Intent(getActivity(), Scanning_Barcode_Activity_With_Fragment.class); // moves to the fine aligment screen
                        i.putExtra("projectId", selectedProjectId);
                        i.putExtra("workorderId", selectedWorkorder.workordertId);
                        startActivity(i);

                    }

                } else { // list is empty open add new workorder

                    // moves to the add new order activity
                    //Todo check edit mode in add new work order
                    Intent i = new Intent(getActivity(), Add_New_Work_Order.class);
                    i.putExtra("projectId", selectedProjectId);
                    i.putExtra("workorderId", "");
                    i.putExtra("isInEditMode", false);
                    startActivity(i);

                }

            }
        });
*/

        ///////////////////////////////////////
        /////////////SETS FIRST IMAGE//////////
        ///////////////////////////////////////

        if (isListInitatedEmpty == false) {
            if (firstWorkorder.lastUpdateStatus.equals("inComplete")) {

                //headerImageView.setImageResource(R.drawable.workorder_selection_incomplete);

            } else if (firstWorkorder.lastUpdateStatus.equals("complete")) {

                //headerImageView.setImageResource(R.drawable.workorder_selection_complete);

            }
        }


        return view;

    }


    public void updateWorkOrderList()
    {
        ArrayList<WorkingOrdersModel> listWorkOrdersByDays = new ArrayList<WorkingOrdersModel>();

        //updates the working orderss from data base
        try {
            // working order from data base (make sure adapter is not null)
            workingOrdersFromDataBase = WorkingOrdersModel.getWorkingOrderForProject(selectedProjectId);
            wororderSelectionAdapter = new WororderSelectionAdapter(getActivity(), listWorkOrdersByDays);

            GeoLocation currentLocation = wororderSelectionAdapter.getCachedCurrentLocation();
            String lat = String.valueOf(currentLocation.getLatitude());
            String lng = String.valueOf(currentLocation.getLongitude());
            workingOrdersFromDataBase  = WorkingOrdersModel.getWorkingOrderForProjectByDistance(selectedProjectId, lat,lng);

            for (int x=0; x<workingOrdersFromDataBase.size(); x++)
            {
                WorkingOrdersModel wom = workingOrdersFromDataBase.get(x);

                // handle filter flags
                Boolean bAdd = false;
                if ( (wom.lastUpdateStatus.equalsIgnoreCase("inComplete")) && WorkordersSelectionActivity.ShowIncomplete)
                {
                    bAdd=true;
                }
                if ( (wom.lastUpdateStatus.equalsIgnoreCase("complete")) && WorkordersSelectionActivity.ShowComplete)
                {
                    bAdd=true;

                }
                if ( (wom.lastUpdateStatus.equalsIgnoreCase("")) && WorkordersSelectionActivity.ShowPlanned)
                {
                    bAdd=true;
                }

                // by default
                String daysFilterType = "2";
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String[] sep = wom.lastUpdateTime.split("_");
                    Date date1 = sdf.parse(sep[0]);
                    Date date2 = sdf.parse(sdf.format(new Date()));
                    long diff = date2.getTime() - date1.getTime();
                    int daysPassed = (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                    if(daysPassed <= 7)
                        daysFilterType = "0";
                    else if (daysPassed<=30)
                        daysFilterType = "1";

                } catch (Exception e) {
                    e.printStackTrace();
                    daysFilterType="0";
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    Date tmpDate = new Date();
                    String currentDateandTime = sdf1.format(tmpDate);
                    wom.lastUpdateTime=currentDateandTime;

                }

                // does it pass the status filter?
                if (bAdd) {
                    // check also days filter, start with ALL category
                    if (mDaysFilterType.equalsIgnoreCase("2"))
                        listWorkOrdersByDays.add(wom);
                    else if ( (mDaysFilterType.equalsIgnoreCase("0") && (daysFilterType.equalsIgnoreCase("0")) ))
                        listWorkOrdersByDays.add(wom);
                    else if ( (mDaysFilterType.equalsIgnoreCase("1") && (!daysFilterType.equalsIgnoreCase("2")) ))
                        listWorkOrdersByDays.add(wom);
                }

                storedworkingOrdersModels = new ArrayList<WorkingOrdersModel>();
                storedworkingOrdersModels.addAll(listWorkOrdersByDays);
                Log.d(TAG, "onCreateView: storedworkingOrdersModels.size(): " + storedworkingOrdersModels.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //////////////
        try {
            if (storedworkingOrdersModels.size() > 0) {
                isListInitatedEmpty = false; // updates that the list didn't initated empty
                firstWorkorder = storedworkingOrdersModels.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "updateWorkOrderList: listWorkOrdersByDays size: "+listWorkOrdersByDays.size());

        wororderSelectionAdapter = new WororderSelectionAdapter(getActivity(), listWorkOrdersByDays); // Create the adapter to convert the array to views

        // sort by distance+name
//        wororderSelectionAdapter.sort();

        lv.setAdapter(wororderSelectionAdapter);
    }


    public void editWODialog(final String projectId, final String woId) {
        String commands[];

        if (SUPPORT_EXPORT)
            commands = new String[] {"Edit", "Delete", "Navigate","Export"};
        else
            commands = new String[] {"Edit", "Delete", "Navigate"};


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Options")
                .setItems(commands, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) { //Edit
                            Intent i = new Intent(getActivity(), AddNewWorkOrder.class); // moves to the fine aligment screen
                            i.putExtra("workorderId", woId);
                            i.putExtra("projectId", selectedProjectId);
                            i.putExtra("isInEditMode", true);
                            startActivity(i);

                        } else if (which == 1) { //Delete
                            Boolean isWODeleted = WorkingOrdersModel.deleteWOWithId(woId); // deltes the project and get an input back
                            if (isWODeleted) {
                                try {
                                    WorkordersSelectionActivity.refreshLists();
//                                    updateWorkOrderList();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(getActivity(), getResources().getString(R.string.addNewWO_toast_deleteSussful), // shows a toast to the user that that delete was sussful
                                        Toast.LENGTH_LONG).show();

                            } else { // delte failed
                                Toast.makeText(getActivity(), getResources().getString(R.string.addNewWO_toast_deleteUnSussful), // shows a toast to the user that that delete FAILED
                                        Toast.LENGTH_LONG).show();
                            }
                        } else if(which ==2) {
                            try {
                                WorkingOrdersModel workingOrdersModel = WorkingOrdersModel.getWorkorderById(woId);

                                String latitude = "0.0";
                                String longitude = "0.0";

                                latitude = String.valueOf(workingOrdersModel.orderLatitude);
                                longitude = String.valueOf(workingOrdersModel.orderLongitude);

                                boolean wazeInstalled = false;
                                try {
                                    getContext().getPackageManager().getApplicationInfo("com.waze", 0);
                                    wazeInstalled= true;
                                }
                                catch (Exception  e) {
                                    e.printStackTrace();
                                }

                                Uri gmmIntentUri;
                                if(wazeInstalled == false) {
                                    gmmIntentUri = Uri.parse("geo:0,0?q=" + latitude + "," + longitude + "(Work Order Site)");
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
                                }
                                else {
                                    gmmIntentUri = Uri.parse("waze://?ll="+latitude+","+longitude+"&z=10");
                                    startActivity(new Intent(Intent.ACTION_VIEW,gmmIntentUri ));
                                }


//                                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+latitude+","+longitude+"(Work Order Site)");
//                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                                mapIntent.setPackage("com.google.android.apps.maps");
//                                startActivity(mapIntent);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else {
                            if (SUPPORT_EXPORT) {
                                FileOutputStream fos = null;
                                WorkingOrdersModel workingOrdersModel = WorkingOrdersModel.getWorkorderById(woId);
                                String tmpPath = "";
                                File f = null;
                                File ff = null;
                                try {

                                    // You are allowed to write external storage:
                                    tmpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WinTouch";
                                    f = new File(tmpPath);
                                    if (!f.exists() && !f.mkdirs()) {
                                        // This should never happen - log handled exception!
                                    }

                                    ff = new File(tmpPath, "wo.win");
                                    fos = new FileOutputStream(ff);
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
                                    objectOutputStream.writeObject(new Integer(WORKORDER_EXPORTED));
                                    objectOutputStream.writeObject(workingOrdersModel);
                                    objectOutputStream.close();
                                    fos.close();


                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), e.toString(), // shows a toast to the user that that delete was sussful
                                            Toast.LENGTH_LONG).show();
                                }
                                try {
                                    if (fos != null)
                                        fos.close();


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                                intent.setType("text/plain");
                                ProjectsModel connectedProject = ProjectsModel.getProjectWithId(workingOrdersModel.projectId);
                                intent.putExtra(Intent.EXTRA_SUBJECT, workingOrdersModel.workingOrderName + " From Project: " + connectedProject.projectName + " Was sent to you");
                                intent.putExtra(Intent.EXTRA_TEXT, "This Work Order was sent to you by Wintouch \n");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + tmpPath + "/wo.win"));
                                intent.setData(Uri.parse("mailto:")); // or just "mailto:" for blank
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                                //startActivity(Intent.createChooser(intent , "Send email..."));
                                startActivity(intent);
                                //  ff.delete();
                            }
                        }
                    }
                });
        AlertDialog dialog =  builder.create();
        dialog.show();
    }


    public void editWODialog1(final int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.workorder_selection_edit_title))
                .setMessage(getResources().getString(R.string.workorder_selection_edit_message))
                .setCancelable(true)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // edit the project

                        try {
                            final List<WorkingOrdersModel> tmpWOModel = WorkingOrdersModel.getWorkingOrderForProject(selectedProjectId);
                            String selectedWOIdForEdit = tmpWOModel.get(position).workordertId;

                            Intent i = new Intent(getActivity(), AddNewWorkOrder.class); // moves to the fine aligment screen
                            i.putExtra("workorderId", selectedWOIdForEdit);
                            i.putExtra("projectId", selectedProjectId);
                            i.putExtra("isInEditMode", true);
                            startActivity(i);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() { // delete the project
                    public void onClick(DialogInterface dialog, int which) {
                        //gets the id - storedProjectsModels.get(position).projectId
                        final List<WorkingOrdersModel> tmpWOModel = WorkingOrdersModel.getWorkingOrderForProject(selectedProjectId);
                        Boolean isWODeleted = WorkingOrdersModel.deleteWOWithId(tmpWOModel.get(position).workordertId); // deltes the project and get an input back

                        if (isWODeleted) {
                            try {
                                //projectSelectionAdapter
//                                final List<WorkingOrdersModel> storedworkingOrdersModelsIsUpdated = WorkingOrdersModel.getWorkingOrderForProject(selectedProjectId);

                                GeoLocation currentLocation = wororderSelectionAdapter.getCachedCurrentLocation();
                                String lat = String.valueOf(currentLocation.getLatitude());
                                String lng = String.valueOf(currentLocation.getLongitude());
                                final List<WorkingOrdersModel> storedworkingOrdersModelsIsUpdated = WorkingOrdersModel.getWorkingOrderForProjectByDistance(selectedProjectId, lat, lng);

                                wororderSelectionAdapter = new WororderSelectionAdapter(getActivity(), storedworkingOrdersModelsIsUpdated); // Create the adapter to convert the array to views
//                              wororderSelectionAdapter.sort();

                                wororderSelectionAdapter.notifyDataSetChanged();
                                lv.setAdapter(wororderSelectionAdapter);

                                Toast.makeText(getActivity(), getResources().getString(R.string.addNewWO_toast_deleteSussful), // shows a toast to the user that that delete was sussful
                                        Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e ) {}

                        } else { // delte failed
                            Toast.makeText(getActivity(), getResources().getString(R.string.addNewWO_toast_deleteUnSussful), // shows a toast to the user that that delete FAILED
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();



    }


    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("myLohsd", "onTick: ticking of timer");
            }

            public void onFinish() {
                try {
                    swipeContainer.setRefreshing(false); // cancels the timer
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.CAMERA:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startBarcodeScanningActivity();
                    }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            Object obj = lv.getAdapter().getItem(position);
            Log.d(TAG, "onItemClick: workingOrdersFromDataBase.get(position).lastUpdateStatus: " + workingOrdersFromDataBase.get(position).lastUpdateStatus);
//            if (workingOrdersFromDataBase.get(position).lastUpdateStatus.equals("complete")) {

                if (WorkingOrdersModel.class.isInstance(obj)) {
                    wom = (WorkingOrdersModel) obj;
                    String woId = wom.workordertId;
                    String projectId = wom.projectId;

                    if (wom.lastUpdateStatus.equalsIgnoreCase("complete")) {


                        Intent i = new Intent(getActivity(), ReportActivity.class);
                        i.putExtra("selectedWorkorderId", wom.workordertId);
                        i.putExtra("selectedProjectId", selectedProjectId);
                        i.putExtra("upLinkResult", wom.upLinkResult);
                        i.putExtra("downLinkResult", wom.downLinkResult);
                        i.putExtra("status", "complete");
                        startActivity(i);

                    } else if(PermissionUtils.isPermissionGrantedInFragment(this, Manifest.permission.CAMERA)){

                        startBarcodeScanningActivity();
                    }

//                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void startBarcodeScanningActivity() {
        //updates teh work order status
        ////////////////////////////////////
        wom.lastUpdateStatus = "inComplete";

                        try {
                            String currentDateandTime="";
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            Date tmpDate = new Date();
                            currentDateandTime = sdf.format(tmpDate);
                            wom.lastUpdateTime = currentDateandTime;
                        }
                        catch(Exception e) {

                        }

                        wom.save();

                        // moves to scanning barcode activity
                        ////////////////////////////////////
                        Intent i = new Intent(getActivity(), ScanningBarcodeActivityWithFragment.class); // moves to the fine aligment screen
                        i.putExtra("projectId", selectedProjectId);
                        i.putExtra("workorderId", wom.workordertId);
                        startActivity(i);

//                }

    }
}
