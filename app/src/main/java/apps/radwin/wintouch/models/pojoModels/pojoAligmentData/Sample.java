
package apps.radwin.wintouch.models.pojoModels.pojoAligmentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Sample {

    @SerializedName("elevationHigh")
    @Expose
    private ElevationHigh elevationHigh;
    @SerializedName("elevationLow")
    @Expose
    private ElevationLow elevationLow;
    @SerializedName("elevationMedium")
    @Expose
    private ElevationMedium elevationMedium;

    /**
     * 
     * @return
     *     The elevationHigh
     */
    public ElevationHigh getElevationHigh() {
        return elevationHigh;
    }

    /**
     * 
     * @param elevationHigh
     *     The elevationHigh
     */
    public void setElevationHigh(ElevationHigh elevationHigh) {
        this.elevationHigh = elevationHigh;
    }

    /**
     * 
     * @return
     *     The elevationLow
     */
    public ElevationLow getElevationLow() {
        return elevationLow;
    }

    /**
     * 
     * @param elevationLow
     *     The elevationLow
     */
    public void setElevationLow(ElevationLow elevationLow) {
        this.elevationLow = elevationLow;
    }

    /**
     * 
     * @return
     *     The elevationMedium
     */
    public ElevationMedium getElevationMedium() {
        return elevationMedium;
    }

    /**
     * 
     * @param elevationMedium
     *     The elevationMedium
     */
    public void setElevationMedium(ElevationMedium elevationMedium) {
        this.elevationMedium = elevationMedium;
    }

}
