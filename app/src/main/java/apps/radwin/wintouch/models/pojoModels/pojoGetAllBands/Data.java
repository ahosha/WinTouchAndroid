
package apps.radwin.wintouch.models.pojoModels.pojoGetAllBands;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("bandsList")
    @Expose
    private List<BandsList> bandsList = new ArrayList<BandsList>();
    @SerializedName("currentBandId")
    @Expose
    private String currentBandId;
    @SerializedName("currentCbw")
    @Expose
    private Integer currentCbw;

    /**
     * 
     * @return
     *     The bandsList
     */
    public List<BandsList> getBandsList() {
        return bandsList;
    }

    /**
     * 
     * @param bandsList
     *     The bandsList
     */
    public void setBandsList(List<BandsList> bandsList) {
        this.bandsList = bandsList;
    }

    /**
     * 
     * @return
     *     The currentBandId
     */
    public String getCurrentBandId() {
        return currentBandId;
    }

    /**
     * 
     * @param currentBandId
     *     The currentBandId
     */
    public void setCurrentBandId(String currentBandId) {
        this.currentBandId = currentBandId;
    }

    /**
     * 
     * @return
     *     The currentCbw
     */
    public Integer getCurrentCbw() {
        return currentCbw;
    }

    /**
     * 
     * @param currentCbw
     *     The currentCbw
     */
    public void setCurrentCbw(Integer currentCbw) {
        this.currentCbw = currentCbw;
    }

}
