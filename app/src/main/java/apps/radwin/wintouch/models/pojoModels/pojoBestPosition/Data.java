
package apps.radwin.wintouch.models.pojoModels.pojoBestPosition;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("HBS")
    @Expose
    private List<HB> hBS = new ArrayList<HB>();

    /**
     * 
     * @return
     *     The hBS
     */
    public List<HB> getHBS() {
        return hBS;
    }

    /**
     * 
     * @param hBS
     *     The HBS
     */
    public void setHBS(List<HB> hBS) {
        this.hBS = hBS;
    }

}
