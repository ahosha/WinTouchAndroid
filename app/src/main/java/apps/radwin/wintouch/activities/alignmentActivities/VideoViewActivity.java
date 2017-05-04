package apps.radwin.wintouch.activities.alignmentActivities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import apps.radwin.wintouch.R;

public class VideoViewActivity extends AppCompatActivity {

    int movieLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_activity_layout);

        try {
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    movieLink = 0;
                } else {
                    movieLink = extras.getInt("movieLink");
                }
            } else {
                movieLink = (int) savedInstanceState.getSerializable("movieLink");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("MovieLink", "onStart: ?? link is: "+movieLink);
        VideoView videoView = (VideoView) findViewById(R.id.videoView_main);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + movieLink));
        videoView.start();

    }
}
