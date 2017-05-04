
package apps.radwin.wintouch.models.pojoModels.pojoModule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class CurserLocation {

    @SerializedName("elevation")
    @Expose
    private Integer elevation;
    @SerializedName("horizontal")
    @Expose
    private Integer horizontal;

    /**
     * 
     * @return
     *     The elevation
     */
    public Integer getElevation() {
        return elevation;
    }

    /**
     * 
     * @param elevation
     *     The elevation
     */
    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }

    /**
     * 
     * @return
     *     The horizontal
     */
    public Integer getHorizontal() {
        return horizontal;
    }

    /**
     * 
     * @param horizontal
     *     The horizontal
     */
    public void setHorizontal(Integer horizontal) {
        this.horizontal = horizontal;
    }

}
