package apps.radwin.wintouch.models.CommunicationModels;

public class GeoLocation {
    double _longitude;
    double _latitude;

    public GeoLocation(double latitude, double longitude)
    {
        _longitude = longitude;
        _latitude = latitude;
    }

    public double getLongitude(){ return _longitude; }

    public double getLatitude(){
        return _latitude;
    }

    public Boolean isAllZero() { return ( (_latitude==0.0) && (_longitude==0.0)); }
}
