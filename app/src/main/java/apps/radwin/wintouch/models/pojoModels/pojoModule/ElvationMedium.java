
package apps.radwin.wintouch.models.pojoModels.pojoModule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ElvationMedium {

    @SerializedName("baseStations")
    @Expose
    private List<String> baseStations = new ArrayList<String>();
    @SerializedName("scanned")
    @Expose
    private String scanned;

    /**
     * 
     * @return
     *     The baseStations
     */
    public List<String> getBaseStations() {
        return baseStations;
    }

    /**
     * 
     * @param baseStations
     *     The baseStations
     */
    public void setBaseStations(List<String> baseStations) {
        this.baseStations = baseStations;
    }

    /**
     * 
     * @return
     *     The scanned
     */
    public String getScanned() {
        return scanned;
    }

    /**
     * 
     * @param scanned
     *     The scanned
     */
    public void setScanned(String scanned) {
        this.scanned = scanned;
    }

}
