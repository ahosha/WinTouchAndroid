package apps.radwin.wintouch.activities.alignmentActivities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import apps.radwin.wintouch.R;
import apps.radwin.wintouch.appContext;
import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;

public class EmptyDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_dashboard);
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
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.dashboard);
        navigationView.setNavigationItemSelectedListener(this);

        Button createProjectButton = (Button) findViewById(R.id.createProjectButton);

        createProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), AddNewProjectActivity.class); // moves to the fine aligment screen
                i.putExtra("isCameFromAddNewProject", true);
                i.putExtra("isInEditMode", false);
                startActivity(i);
            }
        });

        // geo location
        /////////////////////////
        GeoLocationWrapper geoLocationWrapper = ((appContext) getApplication()).getGeoLocationWrapper();

        if (!geoLocationWrapper.IsGeoLocationPermissionEnabled())
            DashboardActivity.requestCapabilityPermission(this, "GeoLocation is required", Manifest.permission.ACCESS_FINE_LOCATION);

        if (geoLocationWrapper.IsGeoLocationPermissionEnabled() && !geoLocationWrapper.IsGpsOn()) {
            geoLocationWrapper.DeInit();

            new AlertDialog
                    .Builder(this)
                    .setMessage("Geo Location is Disabled, please enable it in order to correct application functionality")
                    .show();
        }

        geoLocationWrapper.Init();
        GeoLocation location = geoLocationWrapper.GetGeoLocation();
        geoLocationWrapper.DeInit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            //closes the app
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker analyticsTracker = ((appContext) getApplication()).getDefaultTracker();

        analyticsTracker.setScreenName("Dashboard Empty");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            // Handle the camera action
        } else if (id == R.id.projects) {
            Intent i = new Intent(getApplication(), projectSelectionMainFragment.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.news) {

        } else if (id == R.id.support) {

        } else if (id == R.id.tutorials) {

        }else if (id == R.id.about_section) {

            Intent i = new Intent(getApplication(), AboutActivity.class); // moves to the fine aligment screen
            startActivity(i);

        } else if (id == R.id.installationGuide) {
            Intent i = new Intent(getApplication(), InstallationGuideActivity.class); // moves to the fine aligment screen
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
