package apps.radwin.wintouch.fragments.alignmentFragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.adapters.aligmentAdapters.VideoguidesChecklistAdapter;
import apps.radwin.wintouch.models.InstallationGuideChecklistModel;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 28/12/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class IstallationGuideFragmentChecklist extends Fragment implements AdapterView.OnItemClickListener {

    ListView checklistList;
    String instllationId;
    SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installationguide_checklist, container, false);

        if (savedInstanceState == null) {
            Bundle extras = getArguments();
            if (extras == null) {
                instllationId = "";
            } else {
                instllationId = extras.getString("instllationId");
            }
        } else {
            Bundle extras = getArguments();
            instllationId = String.valueOf(extras.get("instllationId"));
//                    Log.d("myApp", "onCreateView: extras2.get(\"instllationId\"): "+);
//            instllationId = (String) savedInstanceState.getSerializable("instllationId");
        }

        //setting up adapter data
        //////////////////////////////
        final List<InstallationGuideChecklistModel> storedCheckmarks = InstallationGuideChecklistModel.getCheckmarksById(instllationId);

        //setting adapter
        ////////////////
        VideoguidesChecklistAdapter installationsAdapter = new VideoguidesChecklistAdapter(getActivity().getApplicationContext(), storedCheckmarks); // Create the adapter to convert the array to views

        checklistList = (ListView) view.findViewById(R.id.installationGuides_checklist_list);

        checklistList.setDivider(null);
        checklistList.setDividerHeight(0);

        checklistList.setAdapter(installationsAdapter);

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
                swipeContainer.setRefreshing(false);
//                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        return view;
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

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
//            Object obj = lv.getAdapter().getItem(position);
//
//            if (ProjectsModel.class.isInstance(obj)) {
//                ProjectsModel pm = (ProjectsModel) obj;
//                Intent i = new Intent(getActivity(), WorkordersSelectionActivity.class);
//                i.putExtra("projectId", pm.projectId);
//                startActivity(i);
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
