package apps.radwin.wintouch.adapters.aligmentAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.models.ProjectsModel;
import apps.radwin.wintouch.models.WorkingOrdersModel;

/**
 * Created by shay_v on 04/05/2016.
 */
public class ProjectSelectionAdapter extends ArrayAdapter<ProjectsModel> {

    final static String TAG = ProjectSelectionAdapter.class.getSimpleName();

    public ProjectSelectionAdapter(Context context, List<ProjectsModel> projectsModel) {
        super(context, 0, projectsModel);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ProjectsModel selectedProject = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout_project_selection, parent, false);
        }

        //checks how many connected projects we have
        List<WorkingOrdersModel> workingOrdersFromDataBase = null;
        int listSize = 0;
        Integer completedWorkorders = 0;
        Integer incompletedWorkorders = 0;
        Integer plannedWorkOrders = 0;

        workingOrdersFromDataBase = WorkingOrdersModel.getWorkingOrderForProject(selectedProject.projectId);
        listSize = workingOrdersFromDataBase.size();

        for (int i = 0; i < workingOrdersFromDataBase.size(); i++) {

            String lastStatus = workingOrdersFromDataBase.get(i).lastUpdateStatus;
//            Log.d(TAG, "Status: " + lastStatus);

            if (workingOrdersFromDataBase.get(i).lastUpdateStatus.equalsIgnoreCase("complete")) {
                completedWorkorders++;
            } else if (workingOrdersFromDataBase.get(i).lastUpdateStatus.equalsIgnoreCase("incomplete")) {
                incompletedWorkorders++;
            } else plannedWorkOrders++;
        }


        //lookup the views to change color later
        com.hookedonplay.decoviewlib.DecoView prograssBar = (com.hookedonplay.decoviewlib.DecoView) convertView.findViewById(R.id.projects_list_circleDialog);
        TextView projectNameText = (TextView) convertView.findViewById(R.id.projects_list_circleDialog_projectName);
        TextView projectListPlaned = (TextView) convertView.findViewById(R.id.projects_list_listSize_PositionLeft);
        TextView projectListIncomplete = (TextView) convertView.findViewById(R.id.projects_list_listSize_inComplete);
        TextView projectListComplete = (TextView) convertView.findViewById(R.id.projects_list_listSize_complete);
        TextView projectListDateText = (TextView) convertView.findViewById(R.id.projects_list_dateText);
        TextView projectDescriptionText = (TextView) convertView.findViewById(R.id.projects_list_circleDialog_projectDescription);
        TextView projectSelectionTextPrecentage = (TextView) convertView.findViewById(R.id.circle_presentage_project_selection);


        // TextView projectLimitsText = (TextView) convertView.findViewById(R.id.projects_list_circleDialog_projectlimit);

        projectNameText.setText(selectedProject.projectName); // setting project name
        projectListPlaned.setText(plannedWorkOrders.toString());
        projectListComplete.setText(completedWorkorders.toString());
        projectListIncomplete.setText(incompletedWorkorders.toString());
        projectDescriptionText.setText(selectedProject.description);

        ////////////////////////////
        ///limit the project name///
        ////////////////////////////

        try {
            if (selectedProject.projectName.length() > 15) { // try to limit project name
                projectNameText.setText(selectedProject.projectName.substring(0, 15) + "...");
            }

            if (selectedProject.description.length() > 40) { // try to limit project name
                projectDescriptionText.setText(selectedProject.description.substring(0, 40) + "...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ///////////////////////
        ///TIME MANAGMENT/////
        ///////////////////////
        //gets CURRENT TIME
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String[] sep = selectedProject.creationDate.split("_");
            Date date1 = sdf.parse(sep[0]);
            Date date2 = sdf.parse(sdf.format(new Date()));
            long diff = date2.getTime() - date1.getTime();
            int daysPassed = (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

            if (daysPassed <= 0) {
                projectListDateText.setText("Today");
            } else {
                projectListDateText.setText(daysPassed + "d");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ///////////////////////
        ///End of TIME MANAGMENT/////
        ///////////////////////

        // Create background track
        SeriesItem seriesPlanned = new SeriesItem.Builder(getContext().getResources().getColor(R.color.alignment_canvasShading_light))
                .setRange(-1, listSize, 0)
                .setInitialVisibility(true)
                .setLineWidth(25f)
                .build();

        int backgroundTrackInt = prograssBar.addSeries(seriesPlanned);
        int unCompletedWorkordersInt = 0;
        int completedWorkordersInt = 0;
        try {

            SeriesItem seriesUncompleted = new SeriesItem.Builder(getContext().getResources().getColor(R.color.incompletWorkordersAlignementColor))
                    .setRange(0, listSize, 0)
                    .setLineWidth(25)
                    .setInitialVisibility(false)
                    .build();

            unCompletedWorkordersInt = prograssBar.addSeries(seriesUncompleted);


            SeriesItem seriesCompleted = new SeriesItem.Builder(getContext().getResources().getColor(R.color.highlightAlignementColor))
                    .setRange(0, listSize, 0)
                    .setLineWidth(25)
                    .setInitialVisibility(false)
                    .build();

            completedWorkordersInt = prograssBar.addSeries(seriesCompleted);

            Log.d(TAG, "getView: listSize: " + listSize);
            Log.d(TAG, "getView: completed: " + completedWorkorders);
            Log.d(TAG, "getView: incompleted: " + incompletedWorkorders);
            Log.d(TAG, "getView: planned: " + plannedWorkOrders);


            //creates bacground track animation
            prograssBar.addEvent(new DecoEvent.Builder(listSize)
                    .setIndex(backgroundTrackInt)
                    .setDuration((long) (0))
                    .build());
        }
        catch (Exception ex) {}

        try {
            if (listSize == 0) { // sets the progress  bar to 0 if list size is 0
//                prograssBar.setProgress(0); // setting the progress bar


            } else {
                float percent = (completedWorkorders * 100);
                percent /= listSize;
//                prograssBar.setProgress((int)percent);

                projectSelectionTextPrecentage.setText(String.valueOf((int)percent)+"%");


                if (incompletedWorkorders>0) {
                    //creates incomplete graph animation
                    prograssBar.addEvent(new DecoEvent.Builder(incompletedWorkorders+completedWorkorders)
                            .setIndex(unCompletedWorkordersInt)
                            .setDuration((long) (0))
                            .build());

                }

                if (completedWorkorders>0) {
                    //creates complete graph animation
                    prograssBar.addEvent(new DecoEvent.Builder(completedWorkorders)
                            .setIndex(completedWorkordersInt)
                            .setDuration((long) (0))
                            .build());
                }


            }
        } catch (Exception e) {
//            prograssBar.setProgress(100);

            e.printStackTrace();
        }


        return convertView;

    }


}
