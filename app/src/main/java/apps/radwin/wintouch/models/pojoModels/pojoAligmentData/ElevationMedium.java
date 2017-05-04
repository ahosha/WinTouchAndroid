
package apps.radwin.wintouch.models.pojoModels.pojoAligmentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ElevationMedium {

    @SerializedName("beResourcesExist")
    @Expose
    private Boolean beResourcesExist;
    @SerializedName("cirResourcesExist")
    @Expose
    private Boolean cirResourcesExist;
    @SerializedName("scanned")
    @Expose
    private Boolean scanned;
    @SerializedName("sectorFound")
    @Expose
    private Boolean sectorFound;
    @SerializedName("sectorWithNetwork")
    @Expose
    private Boolean sectorWithNetwork;
    @SerializedName("sectorWithSpecSectorId")
    @Expose
    private Boolean sectorWithSpecSectorId;

    /**
     * 
     * @return
     *     The beResourcesExist
     */
    public Boolean getBeResourcesExist() {
        return beResourcesExist;
    }

    /**
     * 
     * @param beResourcesExist
     *     The beResourcesExist
     */
    public void setBeResourcesExist(Boolean beResourcesExist) {
        this.beResourcesExist = beResourcesExist;
    }

    /**
     * 
     * @return
     *     The cirResourcesExist
     */
    public Boolean getCirResourcesExist() {
        return cirResourcesExist;
    }

    /**
     * 
     * @param cirResourcesExist
     *     The cirResourcesExist
     */
    public void setCirResourcesExist(Boolean cirResourcesExist) {
        this.cirResourcesExist = cirResourcesExist;
    }

    /**
     * 
     * @return
     *     The scanned
     */
    public Boolean getScanned() {
        return scanned;
    }

    /**
     * 
     * @param scanned
     *     The scanned
     */
    public void setScanned(Boolean scanned) {
        this.scanned = scanned;
    }

    /**
     * 
     * @return
     *     The sectorFound
     */
    public Boolean getSectorFound() {
        return sectorFound;
    }

    /**
     * 
     * @param sectorFound
     *     The sectorFound
     */
    public void setSectorFound(Boolean sectorFound) {
        this.sectorFound = sectorFound;
    }

    /**
     * 
     * @return
     *     The sectorWithNetwork
     */
    public Boolean getSectorWithNetwork() {
        return sectorWithNetwork;
    }

    /**
     * 
     * @param sectorWithNetwork
     *     The sectorWithNetwork
     */
    public void setSectorWithNetwork(Boolean sectorWithNetwork) {
        this.sectorWithNetwork = sectorWithNetwork;
    }

    /**
     * 
     * @return
     *     The sectorWithSpecSectorId
     */
    public Boolean getSectorWithSpecSectorId() {
        return sectorWithSpecSectorId;
    }

    /**
     * 
     * @param sectorWithSpecSectorId
     *     The sectorWithSpecSectorId
     */
    public void setSectorWithSpecSectorId(Boolean sectorWithSpecSectorId) {
        this.sectorWithSpecSectorId = sectorWithSpecSectorId;
    }

}
