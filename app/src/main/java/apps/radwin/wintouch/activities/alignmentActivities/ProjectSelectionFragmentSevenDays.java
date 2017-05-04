package apps.radwin.wintouch.activities.alignmentActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.ProjectSelectionAdapter;
import apps.radwin.wintouch.models.ProjectsModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ProjectSelectionFragmentSevenDays extends Fragment implements AdapterView.OnItemClickListener {

    ListView lv;
    ProjectSelectionAdapter projectSelectionAdapter; // will hold an instance of the list adapter
    private SwipeRefreshLayout swipeContainer;
    final String TAG = ProjectSelectionFragmentSevenDays.class.getSimpleName();
    Boolean screenInitatedEmpty = false;

    // type of filtert
    private String mDaysFilterType  = "";
    ArrayList<ProjectsModel> listProjectsModels = new ArrayList<ProjectsModel>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_selection_fragment_seven_days, container, false);

        lv = (ListView) view.findViewById(R.id.project_selection_listView);
        lv.setOnItemClickListener(this);

        lv.setDivider(null);
        lv.setDividerHeight(0);


        loadListFiltered();

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                android.util.Log.d("myLogs", "onItemLongClick: position: " + position);
                try {
                    Object obj = lv.getAdapter().getItem(position);

                    if (ProjectsModel.class.isInstance(obj)) {
                        ProjectsModel pm = (ProjectsModel) obj;
                        editProjectDialogByProject(pm.projectId);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            }
        });


        //////////////////////////////////////////////////////////////
        //////////////// Swipe container initlization ////////////////
        //////////////////////////////////////////////////////////////
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.projectSelectionSwipeContainer);
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


        return view;
    }

    public void loadListFiltered()
    {
        try {
            // 0 - 7 days
            // 1 - 30 days
            // 2 - all
            Bundle bundle = getArguments();
            mDaysFilterType = Integer.toString(bundle.getInt("count"));

            final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();
            ArrayList<ProjectsModel> listProjectsByDays = new ArrayList<ProjectsModel>();

            for (int x = 0; x < storedProjectsModels.size(); x++) {
                ProjectsModel pm = storedProjectsModels.get(x);

                // by default
                String daysFilterType = "2";
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String[] sep = pm.creationDate.split("_");
                    Date date1 = sdf.parse(sep[0]);
                    Date date2 = sdf.parse(sdf.format(new Date()));
                    long diff = date2.getTime() - date1.getTime();
                    int daysPassed = (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                    if (daysPassed <= 7)
                        daysFilterType = "0";
                    else if (daysPassed <= 30)
                        daysFilterType = "1";

                } catch (Exception e) {
                    e.printStackTrace();
                    daysFilterType = "0";
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    Date tmpDate = new Date();
                    String currentDateandTime = sdf1.format(tmpDate);
                    pm.creationDate = currentDateandTime;

                }

                // check days filter, start with ALL category
                // 2 - ALL, everything goes
                // 0 - is 7 days, only 7 days pass
                // 1 - 30 days - both 0 & 1 go - just not 2
                if (mDaysFilterType.equalsIgnoreCase("2"))
                    listProjectsByDays.add(pm);
                else if ((mDaysFilterType.equalsIgnoreCase("0") && (daysFilterType.equalsIgnoreCase("0"))))
                    listProjectsByDays.add(pm);
                else if ((mDaysFilterType.equalsIgnoreCase("1") && (!daysFilterType.equalsIgnoreCase("2"))))
                    listProjectsByDays.add(pm);
            }

            //projectSelectionAdapter
            projectSelectionAdapter = new ProjectSelectionAdapter(getActivity(), listProjectsByDays); // Create the adapter to convert the array to views

            //lv.setAdapter(arrayAdapter);
            lv.setAdapter(projectSelectionAdapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void editProjectDialogByProject(final String projectId) {
        String commands[] = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Options")
                .setItems(commands, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) { //Edit
                            Intent i = new Intent(getActivity(), AddNewProjectActivity.class); // moves to the fine aligment screen
                            i.putExtra("isCameFromAddNewProject", true);
                            i.putExtra("projectId", projectId);
                            i.putExtra("isInEditMode", true);
                            startActivity(i);
                        } else if (which == 1) {
                            //Delete -
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Delete Project");
                            alert.setMessage("Project and all its work orders will be deleted\nAre you sure?");
                            alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        //do your work here
                                        Boolean isPojectDeleted = ProjectsModel.deleteProjectWithId(projectId); // deltes the project and get an input back

                                        if (isPojectDeleted) {
                                            projectSelectionMainFragment.refreshLists();
                                            Toast.makeText(getActivity(), getResources().getString(R.string.addNewProject_toast_deleteSussful), Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.addNewProject_toast_deleteUnSussful), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                }
                            });
                            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void editProjectDialog1(final int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.project_selection_edit_title))
                .setMessage(getResources().getString(R.string.project_selection_edit_message))
                .setCancelable(true)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // edit the project

                        final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects(); // get all projects
                        String selectedProjectIdForEdit = storedProjectsModels.get(position).projectId; // gets the project id

                        Intent i = new Intent(getActivity(), AddNewProjectActivity.class); // moves to the fine aligment screen
                        i.putExtra("isCameFromAddNewProject", true);
                        i.putExtra("projectId", selectedProjectIdForEdit);
                        i.putExtra("isInEditMode", true);
                        startActivity(i);

                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() { // delete the project
                    public void onClick(DialogInterface dialog, int which) {


                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Delete Project");
                        alert.setMessage("Are you sure to delete project");
                        alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do your work here
                                dialog.dismiss();
                                //gets the id - storedProjectsModels.get(position).projectId
                                final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects(); // get all projects
                                Boolean isPojectDeleted = ProjectsModel.deleteProjectWithId(storedProjectsModels.get(position).projectId); // deltes the project and get an input back

                                if (isPojectDeleted) {
                                    //projectSelectionAdapter
                                    final List<ProjectsModel> storedProjectsModelsUpdated = ProjectsModel.getAllProjects();
                                    projectSelectionAdapter = new ProjectSelectionAdapter(getActivity(), storedProjectsModelsUpdated); // Create the adapter to convert the array to views
                                    projectSelectionAdapter.notifyDataSetChanged(); // notifys the list that the adapter is completed
                                    lv.setAdapter(projectSelectionAdapter);

                                    Toast.makeText(getActivity(), getResources().getString(R.string.addNewProject_toast_deleteSussful),Toast.LENGTH_LONG).show();

                                } else { // delte failed
                                    Toast.makeText(getActivity(), getResources().getString(R.string.addNewProject_toast_deleteUnSussful), Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        alert.show();

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
                android.util.Log.d("myLohsd", "onTick: ticking of timer");
            }

            public void onFinish() {
                try {
                    swipeContainer.setRefreshing(false); // cancels the timer
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();


//        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
//            public void onSuccess(JSONArray json) {
//                // Remember to CLEAR OUT old items before appending in the new ones
//                adapter.clear();
//                // ...the data has come back, add new items to your adapter...
//                adapter.addAll(...);
//                // Now we call setRefreshing(false) to signal refresh has finished
//                swipeContainer.setRefreshing(false);
//            }
//
//            public void onFailure(Throwable e) {
//                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
//            }
//        });
    }

    public void updateList()
    {
        try {
            this.loadListFiltered();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            Object obj = lv.getAdapter().getItem(position);

            if (ProjectsModel.class.isInstance(obj)) {
                ProjectsModel pm = (ProjectsModel) obj;
                Intent i = new Intent(getActivity(), WorkordersSelectionActivity.class);
                i.putExtra("projectId", pm.projectId);
                startActivity(i);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
