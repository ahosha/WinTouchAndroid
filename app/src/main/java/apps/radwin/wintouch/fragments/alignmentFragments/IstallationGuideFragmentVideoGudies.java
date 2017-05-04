package apps.radwin.wintouch.fragments.alignmentFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.activities.alignmentActivities.VideoViewActivity;
import apps.radwin.wintouch.models.InstallationGuideMoviesModel;

import static android.content.ContentValues.TAG;

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

public class IstallationGuideFragmentVideoGudies extends Fragment implements AdapterView.OnItemClickListener {

    ListView videoGuidesList;
    String instllationId;
    LayoutInflater fragmentInflater;
    SwipeRefreshLayout swipeContainer;
    ViewGroup fragmentContainer;
    List<InstallationGuideMoviesModel> storedMovies;
    View mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_istallation_guide_video_gudies, container, false);
        fragmentInflater = inflater;
        fragmentContainer = container;

        if (savedInstanceState == null) {
            Bundle extras = getArguments();
            if (extras == null) {
                instllationId = "";
            } else {
                instllationId = extras.getString("instllationId");
            }
        } else {
//            instllationId = (String) savedInstanceState.getSerializable("instllationId");
            Bundle extras = getArguments();
            instllationId = String.valueOf(extras.get("instllationId"));
        }

        //setting up adapter data
        //////////////////////////////
        storedMovies = InstallationGuideMoviesModel.getMovieModelById(instllationId);

        Log.d(TAG, "onCreateView: ??");

//        VideoguidesAdapter installationsAdapter = new VideoguidesAdapter(getActivity().getApplicationContext(), storedMovies); // Create the adapter to convert the array to views
//
//        videoGuidesList = (ListView) view.findViewById(R.id.installationGuides_videoguides_list);
//
//        videoGuidesList.setDivider(null);
//        videoGuidesList.setDividerHeight(0);
//
//        videoGuidesList.setOnItemClickListener(this);
//
//        videoGuidesList.setAdapter(installationsAdapter);


        //////////////////////////////////////////////////////////////
        //////////////// Swipe container initlization ////////////////
        //////////////////////////////////////////////////////////////
        swipeContainer = (SwipeRefreshLayout) mainView.findViewById(R.id.projectSelectionSwipeContainer);
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

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: 1");
        Log.d(TAG, "onStart: 2");

        VideoView mainVideo = (VideoView) mainView.findViewById(R.id.installation_guide_videoView);

        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(mainVideo);
        mainVideo.setMediaController(mediaController);
        mainVideo.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.ulc_animation_one));
        mainVideo.start();

        Log.d(TAG, "onStart: 3");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
//            startVideo(storedMovies.get(position).movieLink);

            Intent i = new Intent(getActivity(), VideoViewActivity.class); // moves to the fine aligment screen
            i.putExtra("movieLink", storedMovies.get(position).movieLink);
            startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
