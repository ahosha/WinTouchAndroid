package apps.radwin.wintouch;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import apps.radwin.wintouch.devicePackage.GeoLocationWrapper;
import apps.radwin.wintouch.screenManagers.AligmentManager;

/**
 * Created by shay_v on 16/05/2016.
 * app context is expended in this class so that we can make global varaibles
 * the app Contextx extends com.activeandroid.app.Application so that we can work with the active android sql helper
 * mroe info about the project and Project LTD can be found on > P:\HighLink\Groups\System\Docs\P2MP\ULC
 * see notes in the folder attached to handle project better
 */

//////////////////////////////////////////////////////////////////////////////
//THIS IS THE APP CONTEXT OF THE APPLICATION - COUTION REQUIRED - HDNLE GENTLY
//////////////////////////////////////////////////////////////////////////////

public class appContext extends com.activeandroid.app.Application {

    private AligmentManager alignmentManagerVar; // making aligment Manager accecible to all classes
    private GeoLocationWrapper _geoLocationWrapper;
    private Tracker mTracker;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public AligmentManager getAligmentManagerVar() {
        if (alignmentManagerVar == null)
            alignmentManagerVar = new AligmentManager();

        return alignmentManagerVar;
    }

    public GeoLocationWrapper getGeoLocationWrapper() {
        if (_geoLocationWrapper == null)
            _geoLocationWrapper = new GeoLocationWrapper(this);

        return _geoLocationWrapper;
    }

    /**
     * Gets the default {@link Tracker} for this
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            mTracker.enableAdvertisingIdCollection(true);

        }
        return mTracker;
    }


    ///////////////////////////////////////////////////////////
    //////////////DATA SIMULATE FROM THER SERVER///////////////
    ///////////////////////////////////////////////////////////
    //do this only for now to simulate data from the server
//    SharedPreferences sharedpreferences; // opens up a shared prefrence file
//    ArrayList<String> arrayOfBandNames = new ArrayList<String>();
//
//
//    private String generateId() { // generates a random id
//
//        //
//
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(20); // 20 is the max string langth were going to return
//        char tempChar;
//        for (int i = 0; i < randomLength; i++) {
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//
//    }
//
//
//    //generating a random list
//    private String[] generateCBWList() { // generates a random bandwith array
//
//        int stringLanth = 15 + (int) (Math.random() * 85);
//
//        String[] list = new String[stringLanth];
//
//        for (int i = 0; i < stringLanth; i++) {
//            list[i] = String.valueOf((int) ((Math.random() * 100)*10)+5000);
//        }
//
//        return list;
//    }
//
//    //creating a new scene
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        sharedpreferences = getSharedPreferences("myPrefreces", Context.MODE_PRIVATE); // initlizing the shared prefrence
//        arrayOfBandNames.add("5.150-5.335 GHz Universal"); // generates band names
//        arrayOfBandNames.add("5.740-5.835 GHz MII"); // generates band names
//        arrayOfBandNames.add("5.480-5.715 GHz FCC/IC"); // generates band names
//        arrayOfBandNames.add("5.160-5.245 GHz FCC"); // generates band names
//
//        ActiveAndroid.initialize(this); // initlizes active android library
//
//        boolean isAppRunningFirstTime = sharedpreferences.getBoolean("isFirstTimeAppRunning", true); // checks the shared prefrence that we have  run the app for the first time
//        final String[] bandwithArray = new String[4]; // initlizes the channel bandwith list
//        bandwithArray[0] = String.valueOf(5);
//        bandwithArray[1] = String.valueOf(10);
//        bandwithArray[2] = String.valueOf(20);
//        bandwithArray[3] = String.valueOf(40);
//
//        final String[] bandIds = new String[8]; // initlizes the channel bandwith list
//        bandIds[0] = "5K/F58/SU2/50/FCC/INT/HG";
//        bandIds[1] = "5K/F58/SU/50/IDA/INT";
//        bandIds[2] = "BAND5K/F51/SU/50/UNI/INT";
//        bandIds[3] = "BAND5K/F59/SU/50/UNI/INT";
//        bandIds[4] = "BAND5K/F58/CN/SU/INT";
//        bandIds[5] = "5k5k5k5k5k";
//        bandIds[6] = "6k6k6k6k6k";
//        bandIds[7] = "7k7k7k7k7k";
//
//
//        if (isAppRunningFirstTime == true) { // only runs the thread if this is the first time the application is running
//            Log.d("app", "FIRST TIME THE APP IS RUNNING");
//
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    //code to do the HTTP request
//
//                    ActiveAndroid.beginTransaction();
//
//                    try { // simulates data
//
//                        for (int i = 0; i < 8; i++) {
//
//                            BandsModel bandsModel = new BandsModel(); // projects modol saving to the sql
//
//                            bandsModel.bandId = bandIds[i];
//                            bandsModel.bandName = arrayOfBandNames.get((int) (Math.random()*arrayOfBandNames.size()));
//                            bandsModel.bandwithList = TextUtils.join(", ", bandwithArray); // will ransform the list into text
//                            bandsModel.frequency5List = TextUtils.join(", ", generateCBWList());
//                            bandsModel.frequency10List = TextUtils.join(", ", generateCBWList());
//                            bandsModel.frequency20List = TextUtils.join(", ", generateCBWList());
//                            bandsModel.frequency40List = TextUtils.join(", ", generateCBWList());
//                            bandsModel.frequency80List = TextUtils.join(", ", generateCBWList());
//                            bandsModel.save();
//
//                        }
//                        ActiveAndroid.setTransactionSuccessful();
//                    } finally {
//                        ActiveAndroid.endTransaction();
//
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putBoolean("isFirstTimeAppRunning", false);
//                        editor.commit();
//
//
//                    }
//
//                }
//            });
//            thread.start();
//        }


    //}
}
