package apps.radwin.wintouch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by avner_a on 01/10/2016.
 */

public class HBSModel {
    public String name;
    public String network_id;
    public String rssi;
    public String distance;
    public int type;

    public String sectorType;
    public String antennaAngle;
    public String antennaElevation;
    public String sectorDirection;
    public String antennaBeamwidth;
    public String channel;
    public String channelBw;
    public String bestRSS;
    public String sectorID;
    public String availableResourcesDL;
    public String availableResourcesUL;
    public String bestEffortEnabled;
    public String latitude;
    public String longitude;
    public String sectorIdMatched;

    public HBSModel(String name, String id, String rssi, String distance, int type) {
        this.name = name;
        this.network_id = id;
        this.rssi = rssi;
        this.distance = distance;
        this.type = type;
    }

    public HBSModel(JSONObject object){
        try {
            this.name = object.getString("name");
            this.sectorType = object.getString("sectorType");
            this.antennaAngle = object.getString("antennaAngle");
            this.sectorDirection = object.getString("sectorDirectn");
            this.channel = object.getString("channel");
            this.availableResourcesDL = object.getString("availableResourcesDL");
            this.availableResourcesUL = object.getString("availableResourcesUL");
            this.bestRSS = object.getString("bestRSS");
            this.sectorID = object.getString("sectorID");
            this.bestEffortEnabled = object.getString("bestEffortEnabled");
            this.latitude = object.getString("latitude");
            this.longitude = object.getString("longitude");
            this.sectorIdMatched = object.getString("sectorIdMatched");
            this.channelBw = object.getString("channelBw");
            this.antennaBeamwidth = object.getString("antennaBeamwidth");
            this.antennaElevation = object.getString("antennalElevation");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<HBSModel> fromJson(JSONArray jsonObjects) {
        ArrayList<HBSModel> sectors = new ArrayList<HBSModel>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                sectors.add(new HBSModel(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sectors;
    }
}

