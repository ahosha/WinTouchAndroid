package apps.radwin.wintouch.models.CommunicationModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shay_v on 25/07/2016.
 */
public class SetBandAligmentModel {

    @SerializedName("bandId")
    private String bandId;

    @SerializedName("channelBw")
    private String channelBw;

    @SerializedName("frequencies")
    private List<String> frequencies;

    @SerializedName("networkId")
    private String networkId;

    public SetBandAligmentModel(String networkId, String bandId, String channelBw, List<String> frequencies) {
        this.networkId = networkId;
        this.bandId = bandId;
        this.channelBw = channelBw;
        this.frequencies = frequencies;
    }
}
