package apps.radwin.wintouch.activities.alignmentActivities;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.screenManagers.AligmentManager;

public class PendingReset_Alignement_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int MIN_SECOND_TIME_TO_CONNECT_TO_ULC = 45;
    int MAX_CONNECTION_ATTEMPTS_TO_UNIT = 10;
    ProgressBar mainPrograssBar;
    TextView mainTextView;
    Timer pendingResetTimer;
    int pendingResetTimerCounter = 0;
    int connectionsAttemptsToUlc = 0;
    String TAG = "PendingTimerScreen";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_reset_alignemnt_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainTextView = (TextView) findViewById(R.id.pending_reset_layout_Text); // casting and asigningn
        mainPrograssBar = (ProgressBar) findViewById(R.id.pending_reset_layout_prograssBar); // prograss bar

        //mainTextView.setText("Trying to connect To Unit in: ");


        iniatePendingResetTimer(); // initlizes the reset timer for the unite

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * the function will inite a timer that will run every second to check for the unite if we can connect to it after the set time has pased
     */
    public void iniatePendingResetTimer() {

        pendingResetTimer = new Timer();
        pendingResetTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.d(TAG, "run: runs on timer !");
                pendingResetTimerCounter++;
                Log.d(TAG, "run: 1 ?");
                Log.d(TAG, "run: MIN_SECOND_TIME_TO_CONNECT_TO_ULC - pendingResetTimerCounter: "+(String.valueOf(MIN_SECOND_TIME_TO_CONNECT_TO_ULC - pendingResetTimerCounter)));
                mainTextView.setText("Trying to Connect To Unit in: 00:" + (String.valueOf(MIN_SECOND_TIME_TO_CONNECT_TO_ULC - pendingResetTimerCounter)));
                Log.d(TAG, "run: 2 ?");

                if (pendingResetTimerCounter > MIN_SECOND_TIME_TO_CONNECT_TO_ULC) {
                    Log.d(TAG, "run: Finished Counter");
                    pendingResetTimer.cancel();
                    connectToUnit(connectionsAttemptsToUlc); // try to connect to unit

                } else {
                    Log.d(TAG, "run: 3 ?");
                }

                Log.d(TAG, "run: 4 ?");


            }
        }, 0, 2000);

    }


    /**
     * the functino will try to connect to the unit and informs the user if unable to connect
     *
     * @param connectionAttempt
     */
    public void connectToUnit(int connectionAttempt) {

        mainTextView.setText("Connection Attempt: " + connectionAttempt + " /" + MAX_CONNECTION_ATTEMPTS_TO_UNIT);

        if (connectionAttempt < MAX_CONNECTION_ATTEMPTS_TO_UNIT) { // trying to connect to unite

            forgetAllWifiNetWorks(); //forgetallwifinetworks before tryign to connect so spacific one

        } else { // reached the maximum alowed connections to unite
            mainPrograssBar.setProgress(0);
            mainTextView.setText("Unable to Connect To Unite");
            //finilizeWifi();

        }

    }


    /**
     * will forget all of the wifi networks
     */
    public void forgetAllWifiNetWorks() {

        Runnable runnable = new Runnable() {

            public void run() {

                try {

                    WifiManager wifiManager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);

                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration i : list) {
                        wifiManager.removeNetwork(i.networkId);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("wifi", "run: cannot delete wifi connections");
                }


                onFinishForgetAllWifiNetworks(true); // notifies the callback

            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pending_reset__alignement_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectToWifiNetwork(final String ssidName, final String netPassword) {

        if (AligmentManager.IS_ALIGMENT_DEBUG_ON() == false) { // makes sure we are not in debug mode
            Runnable runnable = new Runnable() {

                public void run() {

                    final String networkSSID = ssidName;
                    String networkPass = netPassword;

                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain
                    conf.preSharedKey = "\"" + networkPass + "\"";

                    WifiManager wifiManager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.addNetwork(conf);

                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration i : list) {

                        if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {

                            wifiManager.disconnect();
                            wifiManager.enableNetwork(i.networkId, true);
                            wifiManager.reconnect();

                            break;
                        }
                    }


                    ///////////////////////////////////
                    ////////CONNECTION ACHIEVED////////
                    ///////////////////////////////////
                    //Make Sure We Have a Viable Connection
                    //wait a deley and turn on the to tell the server we initeted a connectino
                    final Handler handler = new Handler(); // sets a deley for the operation
                    handler.postDelayed(new Runnable() { // runs a deley in the code for 0.5 second
                        @Override
                        public void run() { // runs a deley in the code for 1 second

                            WifiManager wifiManager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);

                            //makes sure we have a viable connection
                            if (wifiManager.getConnectionInfo().getLinkSpeed() > 1) { // makes sure that the link speed is bigger then one meaning we have connection
                                //move to the next screen
                                mainTextView.setText(getResources().getString(R.string.addNewProject_toast_connectedSuccessfully));

                            } else { //connection is unstable we need to connect again
                                connectionsAttemptsToUlc++; // updates the attempts and reconnects to unite
                                connectToUnit(connectionsAttemptsToUlc);

                            }

                        }
                    }, 500);




                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();

        } else { //debug mode is on calls back to as if we are connected

        }

    }

    /**
     * callback in case forgot all network has finished
     *
     * @param result result will return true in case the callback has been sussful
     */
    public void onFinishForgetAllWifiNetworks(Boolean result) {

        if (result) { //all wifi'es has been stopped
            connectToWifiNetwork("R-UMNG10U100A0000", "wireless");

        } else { //wifi is OFF SHOW DIALOG FOR WIFI IS OFF
            Log.d("myLogs", "wifi is OFF");

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PendingReset_Alignement_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://apps.radwin.wintouch.activities.alignmentActivities/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PendingReset_Alignement_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://apps.radwin.wintouch.activities.alignmentActivities/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    ;


}
