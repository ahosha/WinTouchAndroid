
package apps.radwin.wintouch.models.pojoModels.pojoCurserLocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("cursorLocation")
    @Expose
    private CurserLocation curserLocation;

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

}
