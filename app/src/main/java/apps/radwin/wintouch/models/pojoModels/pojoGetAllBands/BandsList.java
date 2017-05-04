
package apps.radwin.wintouch.models.pojoModels.pojoGetAllBands;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class BandsList {

    @SerializedName("bandDescription")
    @Expose
    private String bandDescription;
    @SerializedName("bandId")
    @Expose
    private String bandId;
    @SerializedName("bandMaxFreq")
    @Expose
    private String bandMaxFreq;
    @SerializedName("bandMinFreq")
    @Expose
    private String bandMinFreq;
    @SerializedName("bandResolution")
    @Expose
    private String bandResolution;
    @SerializedName("channelBW5Freq")
    @Expose
    private List<String> channelBW5Freq = new ArrayList<String>();
    @SerializedName("channelBW7Freq")
    @Expose
    private List<String> channelBW7Freq = new ArrayList<String>();
    @SerializedName("channelBW10Freq")
    @Expose
    private List<String> channelBW10Freq = new ArrayList<String>();
    @SerializedName("channelBW14Freq")
    @Expose
    private List<String> channelBW14Freq = new ArrayList<String>();
    @SerializedName("channelBW20Freq")
    @Expose
    private List<String> channelBW20Freq = new ArrayList<String>();
    @SerializedName("channelBW40Freq")
    @Expose
    private List<String> channelBW40Freq = new ArrayList<String>();
    @SerializedName("channelBW80Freq")
    @Expose
    private List<String> channelBW80Freq = new ArrayList<String>();

    /**
     * 
     * @return
     *     The bandDescription
     */
    public String getBandDescription() {
        return bandDescription;
    }

    /**
     * 
     * @param bandDescription
     *     The bandDescription
     */
    public void setBandDescription(String bandDescription) {
        this.bandDescription = bandDescription;
    }

    /**
     * 
     * @return
     *     The bandId
     */
    public String getBandId() {
        return bandId;
    }

    /**
     * 
     * @param bandId
     *     The bandId
     */
    public void setBandId(String bandId) {
        this.bandId = bandId;
    }

    /**
     * 
     * @return
     *     The bandMaxFreq
     */
    public String getBandMaxFreq() {
        return bandMaxFreq;
    }

    /**
     * 
     * @param bandMaxFreq
     *     The bandMaxFreq
     */
    public void setBandMaxFreq(String bandMaxFreq) {
        this.bandMaxFreq = bandMaxFreq;
    }

    /**
     * 
     * @return
     *     The bandMinFreq
     */
    public String getBandMinFreq() {
        return bandMinFreq;
    }

    /**
     * 
     * @param bandMinFreq
     *     The bandMinFreq
     */
    public void setBandMinFreq(String bandMinFreq) {
        this.bandMinFreq = bandMinFreq;
    }

    /**
     * 
     * @return
     *     The bandResolution
     */
    public String getBandResolution() {
        return bandResolution;
    }

    /**
     * 
     * @param bandResolution
     *     The bandResolution
     */
    public void setBandResolution(String bandResolution) {
        this.bandResolution = bandResolution;
    }
    /**
     *
     * @return
     *     The channelBW5Freq
     */
    public List<String> getChannelBW5Freq() {
        return channelBW5Freq;
    }

    /**
     *
     * @param channelBW5Freq
     *     The channelBW5Freq
     */
    public void setChannelBW5Freq(List<String> channelBW5Freq) {
        this.channelBW5Freq = channelBW5Freq;
    }

    /**
     * 
     * @return
     *     The channelBW10Freq
     */
    public List<String> getChannelBW10Freq() {
        return channelBW10Freq;
    }

    /**
     * 
     * @param channelBW10Freq
     *     The channelBW10Freq
     */
    public void setChannelBW10Freq(List<String> channelBW10Freq) {
        this.channelBW10Freq = channelBW10Freq;
    }

    /**
     * 
     * @return
     *     The channelBW14Freq
     */
    public List<String> getChannelBW14Freq() {
        return channelBW14Freq;
    }

    /**
     * 
     * @param channelBW14Freq
     *     The channelBW14Freq
     */
    public void setChannelBW14Freq(List<String> channelBW14Freq) {
        this.channelBW14Freq = channelBW14Freq;
    }

    /**
     * 
     * @return
     *     The channelBW20Freq
     */
    public List<String> getChannelBW20Freq() {
        return channelBW20Freq;
    }

    /**
     * 
     * @param channelBW20Freq
     *     The channelBW20Freq
     */
    public void setChannelBW20Freq(List<String> channelBW20Freq) {
        this.channelBW20Freq = channelBW20Freq;
    }

    /**
     * 
     * @return
     *     The channelBW40Freq
     */
    public List<String> getChannelBW40Freq() {
        return channelBW40Freq;
    }

    /**
     * 
     * @param channelBW40Freq
     *     The channelBW40Freq
     */
    public void setChannelBW40Freq(List<String> channelBW40Freq) {
        this.channelBW40Freq = channelBW40Freq;
    }

    /**
     * 
     * @return
     *     The channelBW7Freq
     */
    public List<String> getChannelBW7Freq() {
        return channelBW7Freq;
    }

    /**
     * 
     * @param channelBW7Freq
     *     The channelBW7Freq
     */
    public void setChannelBW7Freq(List<String> channelBW7Freq) {
        this.channelBW7Freq = channelBW7Freq;
    }

    /**
     *
     * @return
     *     The channelBW80Freq
     */
    public List<String> getChannelBW80Freq() {
        return channelBW80Freq;
    }

    /**
     *
     * @param channelBW80Freq
     *     The channelBW7Freq
     */
    public void setChannelBW80Freq(List<String> channelBW80Freq) {
        this.channelBW80Freq = channelBW80Freq;
    }
}
