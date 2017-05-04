
package apps.radwin.wintouch.models.pojoModels.pojoModule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("curserLocation")
    @Expose
    private CurserLocation curserLocation;
    @SerializedName("samples")
    @Expose
    private List<Sample> samples = new ArrayList<Sample>();

    /**
     * 
     * @return
     *     The curserLocation
     */
    public CurserLocation getCurserLocation() {
        return curserLocation;
    }

    /**
     * 
     * @param curserLocation
     *     The curserLocation
     */
    public void setCurserLocation(CurserLocation curserLocation) {
        this.curserLocation = curserLocation;
    }

    /**
     * 
     * @return
     *     The samples
     */
    public List<Sample> getSamples() {
        return samples;
    }

    /**
     * 
     * @param samples
     *     The samples
     */
    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

}
