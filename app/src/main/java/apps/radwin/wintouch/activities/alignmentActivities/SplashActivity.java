package apps.radwin.wintouch.activities.alignmentActivities;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 02/08/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.util.List;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.models.InstallationGuideChecklistModel;
import apps.radwin.wintouch.models.InstallationGuideModel;
import apps.radwin.wintouch.models.InstallationGuideMoviesModel;
import apps.radwin.wintouch.models.ProjectsModel;

public class SplashActivity extends AwesomeSplash {

    /**
     * inizlise the splash screen this function is called once when the application starts
     * it is a mandatory function to use the widget we are using for the splash
     * @param configSplash
     */
    @Override
    public void initSplash(ConfigSplash configSplash) {

        //Remove title bar
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.splash_layout);

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.mainAlignementColor); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(300); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.logo192); //or any other drawable
        configSplash.setAnimLogoSplashDuration(300); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.RotateInUpLeft); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Path
//        configSplash.setPathSplash(); //set path String
//        configSplash.setOriginalHeight(400); //in relation to your svg (path) resource
//        configSplash.setOriginalWidth(400); //in relation to your svg (path) resource
//        configSplash.setAnimPathStrokeDrawingDuration(3000);
//        configSplash.setPathSplashStrokeSize(3); //I advise value be <5
//        configSplash.setPathSplashStrokeColor(R.color.alignment_canvasScanned); //any color you want form colors.xml
//        configSplash.setAnimPathFillingDuration(3000);
//        configSplash.setPathSplashFillColor(R.color.alignment_canvasScanned); //path object filling color


        //Customize Title
        configSplash.setTitleSplash("WINTouch");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(300);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
//        configSplash.setTitleFont("fonts/myfont.ttf"); //provide string to your font located in assets/fonts/



    }

    @Override
    protected void onStart() {
        super.onStart();

        //updates analytics
        //////////////////////
        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();
        analyticsTracker.setScreenName("Splash Screen");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder()
                .build());

        try {
            analyticsTracker.setAppVersion(String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void animationsFinished() {

        //BUILDES ALL INSTALLATION GUIDES OFFLINE
        initlizeIntallationGuides();

        final List<ProjectsModel> storedProjectsModels = ProjectsModel.getAllProjects();
        if (storedProjectsModels.size() > 0) { // means we have projects and need to show the dash board with the projects

            // starts the dashboard
            /////////////////////////////
            Intent i = new Intent(getApplication(), DashboardActivity.class); // moves to the fine aligment screen
            i.putExtra("projectId", storedProjectsModels.get(0).projectId);
            startActivity(i);

            //


            String mailId="yourmail@gmail.com";
//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,  Uri.fromParts("mailto",mailId, null));
//            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject text here");
//            // you can use simple text like this
//            // emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Body text here");
//            // or get fancy with HTML like this
//            emailIntent.putExtra(
//                    Intent.EXTRA_TEXT,
//                    Html.fromHtml(new StringBuilder()
//                            .append("<p><b>Some Content</b></p>")
//                            .append("<a>http://www.google.com</a>")
//                            .append("<small><p>More content</p></small>")
//                            .append(getResources().getString(R.string.testString))
//                            .toString())
//            );
//            startActivity(Intent.createChooser(emailIntent, "Send email..."));
//



//            Intent i = new Intent(getApplication(), AutenticatingUserActivity.class); // moves to the fine aligment screen
//            startActivity(i);

        // means we don't have any projects and need to show the empty dashboard
        /////////////////////
        } else {

            Intent i = new Intent(getApplication(), EmptyDashboardActivity.class); // moves to the fine aligment screen
            startActivity(i);
        }
    }

    /**
     * builds installation Guides
     */
    private void initlizeIntallationGuides() {

        final List<InstallationGuideModel> storedInstallationguides = InstallationGuideModel.getAllInstallationGuides();

        Log.d("storedInstallations", "initlizeIntallationGuides: storedInstallationguides.size(): "+storedInstallationguides.size());

        if (storedInstallationguides.size() == 0) {
            Log.d("storedInstallations", "initlizeIntallationGuides: building installation guides data");

            // runs 2 times, for now to build 2 installation families
            ////////////////////////////////////
            for (int i = 0; i < 1; i++) {

                // generates a random id
                //////////////////////
                String installationID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID) + Math.random();

                // finilize the installation guide
                /////////////////////////
                if (i == 0) {
                    InstallationGuideModel installationGuide = new InstallationGuideModel();
                    installationGuide.installationGuideId = installationID;
                    installationGuide.installationGuideName = "SU Pro / Air";
                    installationGuide.installationGuideDescription = "RADWIN Subscriber Unit (SU) AIR/PRO with an integrated antenna ";
                    installationGuide.save();

                } else if (i == 1) {
//                    InstallationGuideModel installationGuide = new InstallationGuideModel();
//                    installationGuide.installationGuideId = installationID;
//                    installationGuide.installationGuideName = "SU Air";
//                    installationGuide.installationGuideDescription = "Random Description";
//                    installationGuide.save();
                }

                // adds all the checklists lists
                //////////////////////////
                for (int j = 0; j < 8; j++) {
                    // adds 8 checklists it takes from the strings xml file
                    ///////////////////////////
                    String[] checklistStrings = getResources().getStringArray(R.array.su_pro_checklist_names);
                    InstallationGuideChecklistModel checklistItem = new InstallationGuideChecklistModel();
                    checklistItem.checkMarkName = checklistStrings[j];
                    checklistItem.isMarked = false;
                    checklistItem.connectedInstallationModel = installationID;
                    checklistItem.save();
                }


                // adds the movies list
                for (int j = 0; j < 1; j++) {
                    // takes checklist and movie name and descirption data from strings.xml
                    ///////////////////////////
                    String[] movieNameStrings = getResources().getStringArray(R.array.su_pro_movie_names);
                    String[] movieDescriptionsStrings = getResources().getStringArray(R.array.su_pro_movie_descriptions);

                    // adds the correct movies for each installation model
                    //////////////////////////////
                    int moviePictureLink = R.drawable.capture_one;
                    int movieLink = R.raw.ulc_animation_one;
                    if (j == 0) {
                        moviePictureLink = R.raw.ulc_animation_one;
                        movieLink = R.raw.ulc_animation_one;

                    } else if (j == 1) {
                        moviePictureLink = R.raw.ulc_animation_three;
                        movieLink = R.raw.ulc_animation_two;

                    } else if (j == 2) {
                        moviePictureLink = R.raw.ulc_animation_three;
                        movieLink = R.raw.ulc_animation_three;

                    }
                    // adds the installation model
                    /////////////////////////////
//                    InstallationGuideMoviesModel movieItem = new InstallationGuideMoviesModel(moviePictureLink, movieNameStrings[j], movieDescriptionsStrings[j], movieLink, false);
                    InstallationGuideMoviesModel movieItem = new InstallationGuideMoviesModel();
                    movieItem.connectedInstallationModel = installationID;
                    movieItem.pictureLink = moviePictureLink;
                    movieItem.movieName = movieNameStrings[j];
//                    movieItem.movieDescription = movieDescriptionsStrings[j];
                    movieItem.movieDescription = "";
                    movieItem.movieLink = movieLink;
                    movieItem.isSeen = false;
                    movieItem.save();
                }
            }
        }
    }
}
