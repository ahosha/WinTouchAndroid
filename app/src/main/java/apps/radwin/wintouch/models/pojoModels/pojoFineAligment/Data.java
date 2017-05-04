
package apps.radwin.wintouch.models.pojoModels.pojoFineAligment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("LinkState")
    @Expose
    private String linkState;
    @SerializedName("RSSDL")
    @Expose
    private Integer rSSDL;
    @SerializedName("RSSUL")
    @Expose
    private Integer rSSUL;


    /**
     * 
     * @return
     *     The linkState
     */
    public String getLinkState() {
        return linkState;
    }

    /**
     * 
     * @param linkState
     *     The LinkState
     */
    public void setLinkState(String linkState) {
        this.linkState = linkState;
    }

    /**
     * 
     * @return
     *     The rSSDL
     */
    public Integer getRSSDL() {
        return rSSDL;
    }

    /**
     * 
     * @param rSSDL
     *     The RSSDL
     */
    public void setRSSDL(Integer rSSDL) {
        this.rSSDL = rSSDL;
    }

    /**
     * 
     * @return
     *     The rSSUL
     */
    public Integer getRSSUL() {
        return rSSUL;
    }

    /**
     * 
     * @param rSSUL
     *     The RSSUL
     */
    public void setRSSUL(Integer rSSUL) {
        this.rSSUL = rSSUL;
    }

}
