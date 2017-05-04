package apps.radwin.wintouch.devicePackage;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import apps.radwin.wintouch.interfaces.GeoLocationListener;
import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;

public class InternalLocationListener implements LocationListener {

    String _currentStatus;
    GeoLocation _internalLocation;
    GeoLocationListener _callback;

    public GeoLocation getGeoLocation(){
        return _internalLocation;
    }

    public String getCurrentStatus(){
        return _currentStatus;
    }

    public void onLocationChanged(Location location) {
        if (location != null)
            _internalLocation = new GeoLocation(location.getLatitude(), location.getLongitude());

        if (_callback != null)
            _callback.OnGeoLocationChanged(_internalLocation);
    }

    public void AddListener(GeoLocationListener callback){
        _callback = callback;
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {
        _currentStatus = String.valueOf(status);
    }

    public void onProviderEnabled(String provider) {
        _currentStatus = "GPS Enabled";
    }

    public void onProviderDisabled(String provider) {
        _currentStatus = "GPS Disabled";
    }
}
