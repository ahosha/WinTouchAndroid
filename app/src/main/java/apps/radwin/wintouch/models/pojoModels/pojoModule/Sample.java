
package apps.radwin.wintouch.models.pojoModels.pojoModule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Sample {

    @SerializedName("elvationHigh")
    @Expose
    private ElvationHigh elvationHigh;
    @SerializedName("elvationLow")
    @Expose
    private ElvationLow elvationLow;
    @SerializedName("elvationMedium")
    @Expose
    private ElvationMedium elvationMedium;

    /**
     * 
     * @return
     *     The elvationHigh
     */
    public ElvationHigh getElvationHigh() {
        return elvationHigh;
    }

    /**
     * 
     * @param elvationHigh
     *     The elvationHigh
     */
    public void setElvationHigh(ElvationHigh elvationHigh) {
        this.elvationHigh = elvationHigh;
    }

    /**
     * 
     * @return
     *     The elvationLow
     */
    public ElvationLow getElvationLow() {
        return elvationLow;
    }

    /**
     * 
     * @param elvationLow
     *     The elvationLow
     */
    public void setElvationLow(ElvationLow elvationLow) {
        this.elvationLow = elvationLow;
    }

    /**
     * 
     * @return
     *     The elvationMedium
     */
    public ElvationMedium getElvationMedium() {
        return elvationMedium;
    }

    /**
     * 
     * @param elvationMedium
     *     The elvationMedium
     */
    public void setElvationMedium(ElvationMedium elvationMedium) {
        this.elvationMedium = elvationMedium;
    }

}
