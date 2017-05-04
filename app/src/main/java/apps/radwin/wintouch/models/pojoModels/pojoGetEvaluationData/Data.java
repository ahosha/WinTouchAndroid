
package apps.radwin.wintouch.models.pojoModels.pojoGetEvaluationData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("DownLink")
    @Expose
    private Integer downLink;

    @SerializedName("LinkState")
    @Expose
    private String linkState;

    @SerializedName("UpLink")
    @Expose
    private Integer upLink;

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
     *     The downLink
     */
    public Integer getDownLink() {
        return downLink;
    }

    /**
     * 
     * @param downLink
     *     The DownLink
     */
    public void setDownLink(Integer downLink) {
        this.downLink = downLink;
    }

    /**
     * 
     * @return
     *     The upLink
     */
    public Integer getUpLink() {
        return upLink;
    }

    /**
     * 
     * @param upLink
     *     The UpLink
     */
    public void setUpLink(Integer upLink) {
        this.upLink = upLink;
    }

}
