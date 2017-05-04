package apps.radwin.wintouch.devicePackage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import apps.radwin.wintouch.interfaces.GeoLocationListener;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;

public class GeoLocationWrapper {

    private int minimalDistanceForChangeLocationNotification = 0;   // Meters
    private int minimalTimeForChangeLocationNotification = 0;     // Milliseconds

    Context _context;
    GeoLocation _geoLocation;
    InternalLocationListener locationListener;
    LocationManager locationManager;
    ArrayList<GeoLocationListener> _listeners;
    private boolean _isInitiated;

    public GeoLocationWrapper(Context context){
        _listeners = new ArrayList<GeoLocationListener>();

        _context = context;

        locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new InternalLocationListener();
    }

    public void DeInit()
    {
        try {
            while (_listeners.size() > 0) {
                _listeners.remove(0);
            }

            locationManager.removeUpdates(locationListener);
            _isInitiated = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void Init() {

        if (_isInitiated)
            return;

        // Permission check
        boolean permissionGranted = IsGeoLocationPermissionEnabled();
        if (!permissionGranted)
            return;

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        locationListener.AddListener(new GeoLocationListener(){

            @Override
            public void OnGeoLocationChanged(GeoLocation location) {
                if (_listeners.isEmpty())
                    return;

                try {
                    locationManager.removeUpdates(locationListener);
                }
                catch (Exception e) { e.printStackTrace(); };

                _geoLocation = location;

                for(Iterator<GeoLocationListener> i = _listeners.iterator(); i.hasNext(); ) {
                    GeoLocationListener item = i.next();
                    item.OnGeoLocationChanged(_geoLocation);
                }
            }
        });

        _isInitiated = true;
    }

    public GeoLocation GetGeoLocation() {
        try {
            if (locationListener.getGeoLocation() != null) {
                _geoLocation = locationListener.getGeoLocation();
            } else {
                if (IsGpsOn()) {
                    //noinspection MissingPermission
                    Location data = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (data == null)
                        _geoLocation = new GeoLocation(0, 0);
                    else
                        _geoLocation = new GeoLocation(data.getLatitude(), data.getLongitude());
                } else if (IsNetworkOn()) {
                    //noinspection MissingPermission
                    Location data = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (data == null)
                        _geoLocation = new GeoLocation(0, 0);
                    else
                        _geoLocation = new GeoLocation(data.getLatitude(), data.getLongitude());
                } else {
                    _geoLocation = new GeoLocation(0, 0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return _geoLocation;
    }

    public void AddListener(GeoLocationListener listenerCallBack){
        if (_listeners.contains(listenerCallBack))
            return;

        _listeners.add(listenerCallBack);
    }

    public void RemoveListener(GeoLocationListener listenerCallBack){
        if (!_listeners.contains(listenerCallBack))
            return;

        _listeners.remove(listenerCallBack);
    }

    public boolean IsGeoLocationPermissionEnabled() {
        return ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean IsGpsOn(){
        return IsGeoLocationPermissionEnabled() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean IsNetworkOn(){
        return IsGeoLocationPermissionEnabled() && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static double calculateDistance(GeoLocation location1, GeoLocation location2){

        double x1 = location1.getLatitude();
        double y1 = location1.getLongitude();
        double z1 = 1;   // Default 1 meter

        double x2 = location2.getLatitude();
        double y2 = location2.getLongitude();
        double z2 = 1;   // Default 1 meter

        return calculateDistance(x1, y1, z1, x2, y2, z2);
    }

    /// <param name="x1">Latitude</param>
    /// <param name="y1">Longitude</param>
    /// <param name="z1">Altitude</param>
    /// <param name="x2">Latitude of second</param>
    /// <param name="y2">Longitude of second</param>
    /// <param name="z2">Altitude of second</param>
    public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double earthRadius = 6335.437;

        double p_r1 = earthRadius + z1 / 1000;
        double p_x1 = p_r1 * Math.cos(2 * Math.PI * y1 / 360) * Math.cos(2 * Math.PI * x1 / 360);
        double p_y1 = p_r1 * Math.cos(2 * Math.PI * y1 / 360) * Math.sin(2 * Math.PI * x1 / 360);
        double p_z1 = p_r1 * Math.sin(2 * Math.PI * y1 / 360);

        double p_r2 = earthRadius + z2 / 1000;
        double p_x2 = p_r2 * Math.cos(2 * Math.PI * y2 / 360) * Math.cos(2 * Math.PI * x2 / 360);
        double p_y2 = p_r2 * Math.cos(2 * Math.PI * y2 / 360) * Math.sin(2 * Math.PI * x2 / 360);
        double p_z2 = p_r2 * Math.sin(2 * Math.PI * y2 / 360);

        double dist = Math.sqrt((p_x1 - p_x2) * (p_x1 - p_x2) + (p_y1 - p_y2) * (p_y1 - p_y2) + (p_z1 - p_z2) * (p_z1 - p_z2));
        double distance = Math.round(dist * 1000);

        return distance;
    }


    public String GetAddress(Context context) {

        GeoLocation location = GetGeoLocation();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String response = null;

        try {
            if (location == null)
                addresses = new ArrayList<>();
            else
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                response = address;
            }
            return response;
        }
        catch (IOException e){
            return null;
        }
    }

    public ArrayList<Address> GetAddressList() {

        try {
            GeoLocation location = GetGeoLocation();
            Geocoder geocoder;
            ArrayList<Address> addresses;
            geocoder = new Geocoder(_context, Locale.getDefault());
            String response = null;

            if (location == null)
                addresses = new ArrayList<>();
            else {
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                return (ArrayList) geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);


            }

            return addresses;
        }
        catch (Exception e){
            return new ArrayList<Address>();
        }
    }

    public ArrayList<Address> getLocationsFromAddress( String strAddress )
    {
        ArrayList<Address> res = new ArrayList<Address>();
        try {
            Geocoder geocoder;
            geocoder = new Geocoder(_context, Locale.getDefault());
            List<Address> address;
            GeoLocation p1 = null;

            address = geocoder.getFromLocationName(strAddress, 5);

            res.addAll(address);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public GeoLocation getLocationFromAddress(String strAddress){
        try {

            Geocoder geocoder;
            geocoder = new Geocoder(_context, Locale.getDefault());
            List<Address> address;
            GeoLocation p1 = null;

            address = geocoder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            if (address.size()==0)
                return null;

            Address location=address.get(0);

            p1 = new GeoLocation( location.getLatitude(), location.getLongitude());

            return p1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
