package apps.radwin.wintouch.models;

/**
 * Created by shay_v on 22/05/2016.
 */
public class BestPositionListModel {

    //constructor for sectors scanned
    //MODEL DEFINITION
    public String sectorType;
    public Integer sectorDirection;
    public Integer antennaBeamwidth;

    public BestPositionListModel(String sectorType, Integer sectorDirection, Integer antennaBeamwidth, Integer channel, Integer channelBw, Integer bestRSS, String sectorID, Integer availableResourcesDL, Integer availableResourcesUL, boolean bestEffortEnabled, Double latitude, Double longitude, boolean sectorIdMatched, Integer cellNumber, String elvationCell, Integer elevation, Integer horizontal) {
        this.sectorType = sectorType;
        this.sectorDirection = sectorDirection;
        this.antennaBeamwidth = antennaBeamwidth;
        this.channel = channel;
        this.channelBw = channelBw;
        this.bestRSS = bestRSS;
        this.sectorID = sectorID;
        this.availableResourcesDL = availableResourcesDL;
        this.availableResourcesUL = availableResourcesUL;
        this.bestEffortEnabled = bestEffortEnabled;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sectorIdMatched = sectorIdMatched;
        this.cellNumber = cellNumber;
        this.elvationCell = elvationCell;
        this.elevation = elevation;
        this.horizontal = horizontal;
    }

    public Integer channel;
    public Integer channelBw;
    public Integer bestRSS;
    public String sectorID;
    public Integer availableResourcesDL;
    public Integer availableResourcesUL;
    public boolean bestEffortEnabled;
    public Double latitude;
    public Double longitude;
    public boolean sectorIdMatched;


    public Integer cellNumber;
    public String elvationCell;
    public Integer elevation;
    public Integer horizontal;

}
