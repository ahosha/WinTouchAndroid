package apps.radwin.wintouch.interfaces;

import apps.radwin.wintouch.models.CommunicationModels.GeoLocation;

public interface GeoLocationListener {
    public void OnGeoLocationChanged(GeoLocation location);
}
