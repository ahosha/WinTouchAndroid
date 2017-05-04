package apps.radwin.wintouch.devicePackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import apps.radwin.wintouch.activities.alignmentActivities.Connect_To_Wif_iActivity;
import apps.radwin.wintouch.interfaces.WiFiConnectionInterface;
import apps.radwin.wintouch.models.WorkingOrdersModel;
import apps.radwin.wintouch.screenManagers.AligmentManager;

/**
 * Created by shay_v on 25/04/2016.
 */
public class wifiWrapper {

    //checks if the wifi is On or Off
    public void checkWifiState(Context context, Connect_To_Wif_iActivity.isWifiOnCallableInterface callbackFunctionForisWifiOn) {

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean isOn;

        if (wifi.isWifiEnabled()) {
            Toast.makeText(context.getApplicationContext(), "Your Wi-Fi is enabled", //wifi is enabled
                    Toast.LENGTH_SHORT).show();
            isOn = true;
        } else {
            Toast.makeText(context.getApplicationContext(), "Your Wi-Fi is disabled", //wifi is disabled
                    Toast.LENGTH_SHORT).show();
            isOn = false;
        }

        try { //trys to call back to the callable function
            callbackFunctionForisWifiOn.isWifiOnCallable(isOn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //toggles a wifi state on and off
    public void toggleWiFi(Context contxt, boolean status) {
        WifiManager wifiManager = (WifiManager) contxt
                .getSystemService(Context.WIFI_SERVICE);
        if (status == true && !wifiManager.isWifiEnabled()) { // turns wifi on
            wifiManager.setWifiEnabled(true);
        }
//        } else if (status == false && wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(false);

//        }
    }


    public void connectToNetwork(final String ssidName, final String netPassword, final Context applicationContext, final WiFiConnectionInterface callbackFunctionForWifiConnection) { // in order to call callback from another thread it needs to be final

        if (AligmentManager.IS_ALIGMENT_DEBUG_ON() == false) {
            Runnable runnable = new Runnable() {

                public void run() {

                    final String networkSSID = ssidName;

                    String networkPass = netPassword;
                    Boolean ssidExist = false;

                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain
                    conf.preSharedKey = "\"" + networkPass + "\"";

                    WifiManager wifiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);

                    try {
                        //checks if ssid exists
                        /////////////////////////
                        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                        for (WifiConfiguration i : list) {
                            if (i.SSID == networkSSID) {
                                Log.d("myLohs", "run: ssid exists no need to add it");
                                ssidExist = true;
                                break;
                            }
                        }

                        // if ssid doesn't exist we add the network
                        /////////////////////////
                        if (!ssidExist) {
                            Log.d("myLohs", "run: adding ssid");
                            wifiManager.addNetwork(conf);
                        }

                        wifiManager.disconnect();
                        Log.d("myLohs", "run: disconnected from wifi");
//                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                        for (WifiConfiguration i : list) {
                            Log.d("myLohs", "i.SSID: "+i.SSID+" networkSSID: "+"\"" + networkSSID + "\"");
                            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                                Log.d("myLohs", "connecting to network");
                                wifiManager.enableNetwork(i.networkId, true);
                                wifiManager.reconnect();

                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        callbackFunctionForWifiConnection.CallBackFromWiFi(); // calls the callback
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();

        } else { //means we are in debug mode
            callbackFunctionForWifiConnection.CallBackFromWiFi(); // calls the callback
        }


    }


    public static boolean isNetworkMatch (String networkSSID, Context applicatationContext) {

        try {

            WifiManager wifiManager = (WifiManager) applicatationContext.getSystemService (Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo ();
            String ssid  = info.getSSID();

            ConnectivityManager connManager = (ConnectivityManager) applicatationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mainWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


            Log.d("myapp", "isNetworkConnected: SSID of connected is: "+ssid);

            if (networkSSID != null && mainWifi.isConnected()) {
                if (ssid.equals(networkSSID)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void forgetNetwork(final WorkingOrdersModel selectedWorkorder, final Context applicationContext) {

        if (selectedWorkorder.workorderWifiSSID != null) {

            try {

                Runnable runnable = new Runnable() {

                    public void run() {

                        WifiManager wifiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
                        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

                        try {
                            for (WifiConfiguration i : list) {

                                String testString = i.SSID.substring(1, i.SSID.length() - 1);
                                // remoces the selected connection
                                //////////////////////////////////////
                                if (testString.equals(selectedWorkorder.workorderWifiSSID)) {

                                    if (wifiManager.removeNetwork(i.networkId)) {
                                        Log.d("myLogs", "removed network");
                                    } else {
                                        Log.d("myLogs", "run: couldn't remove network");
                                    }
                                    wifiManager.saveConfiguration();

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("wifi", "run: cannot delete wifi connections");
            }


        }

    }


    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean hasInternetAccess(Context context) {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204").openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();

                    return (urlc.getResponseCode() == 204 &&
                            urlc.getContentLength() == 0);
                } catch (Exception e) {
                    Log.e("WinTouch wifi", "Error checking internet connection", e);
                }
            } else {
                Log.d("WinTouch wifi", "No network available!");
            }
            return false;
        } catch (Exception e) {

        }
        return false;
    }


}


