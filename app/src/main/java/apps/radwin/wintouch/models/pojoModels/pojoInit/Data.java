
package apps.radwin.wintouch.models.pojoModels.pojoInit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("azimutBeamwidth")
    @Expose
    private Integer azimutBeamwidth;
    @SerializedName("elevationBeamwidth")
    @Expose
    private Integer elevationBeamwidth;
    @SerializedName("hsuId")
    @Expose
    private Integer hsuId;
    @SerializedName("hsuLinkState")
    @Expose
    private String hsuLinkState;
    @SerializedName("hsuServiceType")
    @Expose
    private String hsuServiceType;
    @SerializedName("numOfElevationZones")
    @Expose
    private Integer numOfElevationZones;
    @SerializedName("radiusInstallConfirmationRequired")
    @Expose
    private String radiusInstallConfirmationRequired;
    @SerializedName("channelScanLimit")
    @Expose
    private Integer channelScanLimit;
    @SerializedName("apiVersion")
    @Expose
    private String apiVersion;
    @SerializedName("macAddress")
    @Expose
    private String macAddress;

    /**
     * 
     * @return
     *     The azimutBeamwidth
     */
    public Integer getAzimutBeamwidth() {
        return azimutBeamwidth;
    }

    /**
     * 
     * @param azimutBeamwidth
     *     The azimutBeamwidth
     */
    public void setAzimutBeamwidth(Integer azimutBeamwidth) {
        this.azimutBeamwidth = azimutBeamwidth;
    }

    /**
     * 
     * @return
     *     The elevationBeamwidth
     */
    public Integer getElevationBeamwidth() {
        return elevationBeamwidth;
    }

    /**
     * 
     * @param elevationBeamwidth
     *     The elevationBeamwidth
     */
    public void setElevationBeamwidth(Integer elevationBeamwidth) {
        this.elevationBeamwidth = elevationBeamwidth;
    }

    /**
     * 
     * @return
     *     The hsuId
     */
    public Integer getHsuId() {
        return hsuId;
    }

    /**
     * 
     * @param hsuId
     *     The hsuId
     */
    public void setHsuId(Integer hsuId) {
        this.hsuId = hsuId;
    }

    /**
     * 
     * @return
     *     The hsuLinkState
     */
    public String getHsuLinkState() {
        return hsuLinkState;
    }

    /**
     * 
     * @param hsuLinkState
     *     The hsuLinkState
     */
    public void setHsuLinkState(String hsuLinkState) {
        this.hsuLinkState = hsuLinkState;
    }

    /**
     * 
     * @return
     *     The hsuServiceType
     */
    public String getHsuServiceType() {
        return hsuServiceType;
    }

    /**
     * 
     * @param hsuServiceType
     *     The hsuServiceType
     */
    public void setHsuServiceType(String hsuServiceType) {
        this.hsuServiceType = hsuServiceType;
    }

    /**
     * 
     * @return
     *     The numOfElevationZones
     */
    public Integer getNumOfElevationZones() {
        return numOfElevationZones;
    }

    /**
     * 
     * @param numOfElevationZones
     *     The numOfElevationZones
     */
    public void setNumOfElevationZones(Integer numOfElevationZones) {
        this.numOfElevationZones = numOfElevationZones;
    }

    /**
     * 
     * @return
     *     The radiusInstallConfirmationRequired
     */
    public String getRadiusInstallConfirmationRequired() {
        return radiusInstallConfirmationRequired;
    }

    /**
     * 
     * @param radiusInstallConfirmationRequired
     *     The radiusInstallConfirmationRequired
     */
    public void setRadiusInstallConfirmationRequired(String radiusInstallConfirmationRequired) {
        this.radiusInstallConfirmationRequired = radiusInstallConfirmationRequired;
    }
    /**
     *
     * @return
     *     The channelScanLimit
     */
    public Integer getChannelScanLimit() {
        return channelScanLimit;
    }

    /**
     *
     * @param channelScanLimit
     *     The channelScanLimit
     */
    public void setChannelScanLimit(Integer channelScanLimit) {
        this.channelScanLimit = channelScanLimit;
    }

    /**
     *
     * @return
     *     The radiusInstallConfirmationRequired
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     *
     * @param radiusInstallConfirmationRequired
     *     The radiusInstallConfirmationRequired
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

}
