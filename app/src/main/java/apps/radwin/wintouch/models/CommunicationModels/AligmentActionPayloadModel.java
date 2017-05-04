package apps.radwin.wintouch.models.CommunicationModels;

import com.google.gson.annotations.SerializedName;

public class AligmentActionPayloadModel {

    @SerializedName("sectorId")
    private String sectorId;

    @SerializedName("param")
    private String params;

    public AligmentActionPayloadModel(String sectorId, String params) {
        this.sectorId = sectorId;
        this.params = params;
    }
}