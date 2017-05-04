package apps.radwin.wintouch.fragments.alignmentFragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.models.InstallationGuideMoviesModel;

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

public class InstallationGuideFragmentAntennaAlignment extends Fragment {

    ListView videoGuidesList;
    String instllationId;
    LayoutInflater fragmentInflater;
    SwipeRefreshLayout swipeContainer;
    ViewGroup fragmentContainer;
    List<InstallationGuideMoviesModel> storedMovies;
    View mainView;
    VideoView mainVideo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_antena_alignment, container, false);
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

        mainVideo = (VideoView) mainView.findViewById(R.id.installation_guide_alignment_tab_videoView);

//        mainVideo.stopPlayback();

        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(mainVideo);
        mainVideo.setMediaController(mediaController);
        mainVideo.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.radwin_suscriber_unit));

        mainVideo.seekTo(1);

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

        mainVideo.stopPlayback();
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
