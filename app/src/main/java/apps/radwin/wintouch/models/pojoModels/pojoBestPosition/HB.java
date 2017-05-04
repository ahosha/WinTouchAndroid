
package apps.radwin.wintouch.models.pojoModels.pojoBestPosition;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class HB {

    @SerializedName("sectorType")
    @Expose
    private String sectorType;
    @SerializedName("sectorDirection")
    @Expose
    private Integer sectorDirection;
    @SerializedName("antennaBeamwidth")
    @Expose
    private Integer antennaBeamwidth;
    @SerializedName("channel")
    @Expose
    private Integer channel;
    @SerializedName("channelBw")
    @Expose
    private Integer channelBw;
    @SerializedName("bestRSS")
    @Expose
    private Integer bestRSS;
    @SerializedName("sectorID")
    @Expose
    private String sectorID;
    @SerializedName("availableResourcesDL")
    @Expose
    private Integer availableResourcesDL;
    @SerializedName("availableResourcesUL")
    @Expose
    private Integer availableResourcesUL;
    @SerializedName("bestEffortEnabled")
    @Expose
    private Boolean bestEffortEnabled;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("sectorIdMatched")
    @Expose
    private Boolean sectorIdMatched;
    @SerializedName("cursorLocation")
    @Expose
    private CursorLocation cursorLocation;

    /**
     * 
     * @return
     *     The sectorType
     */
    public String getSectorType() {
        return sectorType;
    }

    /**
     * 
     * @param sectorType
     *     The sectorType
     */
    public void setSectorType(String sectorType) {
        this.sectorType = sectorType;
    }

    /**
     * 
     * @return
     *     The sectorDirection
     */
    public Integer getSectorDirection() {
        return sectorDirection;
    }

    /**
     * 
     * @param sectorDirection
     *     The sectorDirection
     */
    public void setSectorDirection(Integer sectorDirection) {
        this.sectorDirection = sectorDirection;
    }

    /**
     * 
     * @return
     *     The antennaBeamwidth
     */
    public Integer getAntennaBeamwidth() {
        return antennaBeamwidth;
    }

    /**
     * 
     * @param antennaBeamwidth
     *     The antennaBeamwidth
     */
    public void setAntennaBeamwidth(Integer antennaBeamwidth) {
        this.antennaBeamwidth = antennaBeamwidth;
    }

    /**
     * 
     * @return
     *     The channel
     */
    public Integer getChannel() {
        return channel;
    }

    /**
     * 
     * @param channel
     *     The channel
     */
    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    /**
     * 
     * @return
     *     The channelBw
     */
    public Integer getChannelBw() {
        return channelBw;
    }

    /**
     * 
     * @param channelBw
     *     The channelBw
     */
    public void setChannelBw(Integer channelBw) {
        this.channelBw = channelBw;
    }

    /**
     * 
     * @return
     *     The bestRSS
     */
    public Integer getBestRSS() {
        return bestRSS;
    }

    /**
     * 
     * @param bestRSS
     *     The bestRSS
     */
    public void setBestRSS(Integer bestRSS) {
        this.bestRSS = bestRSS;
    }

    /**
     * 
     * @return
     *     The sectorID
     */
    public String getSectorID() {
        return sectorID;
    }

    /**
     * 
     * @param sectorID
     *     The sectorID
     */
    public void setSectorID(String sectorID) {
        this.sectorID = sectorID;
    }

    /**
     * 
     * @return
     *     The availableResourcesDL
     */
    public Integer getAvailableResourcesDL() {
        return availableResourcesDL;
    }

    /**
     * 
     * @param availableResourcesDL
     *     The availableResourcesDL
     */
    public void setAvailableResourcesDL(Integer availableResourcesDL) {
        this.availableResourcesDL = availableResourcesDL;
    }

    /**
     * 
     * @return
     *     The availableResourcesUL
     */
    public Integer getAvailableResourcesUL() {
        return availableResourcesUL;
    }

    /**
     * 
     * @param availableResourcesUL
     *     The availableResourcesUL
     */
    public void setAvailableResourcesUL(Integer availableResourcesUL) {
        this.availableResourcesUL = availableResourcesUL;
    }

    /**
     * 
     * @return
     *     The bestEffortEnabled
     */
    public Boolean getBestEffortEnabled() {
        return bestEffortEnabled;
    }

    /**
     * 
     * @param bestEffortEnabled
     *     The bestEffortEnabled
     */
    public void setBestEffortEnabled(Boolean bestEffortEnabled) {
        this.bestEffortEnabled = bestEffortEnabled;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * 
     * @return
     *     The sectorIdMatched
     */
    public Boolean getSectorIdMatched() {
        return sectorIdMatched;
    }

    /**
     * 
     * @param sectorIdMatched
     *     The sectorIdMatched
     */
    public void setSectorIdMatched(Boolean sectorIdMatched) {
        this.sectorIdMatched = sectorIdMatched;
    }

    /**
     * 
     * @return
     *     The cursorLocation
     */
    public CursorLocation getCursorLocation() {
        return cursorLocation;
    }

    /**
     * 
     * @param cursorLocation
     *     The cursorLocation
     */
    public void setCursorLocation(CursorLocation cursorLocation) {
        this.cursorLocation = cursorLocation;
    }

}
