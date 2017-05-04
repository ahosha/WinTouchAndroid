package apps.radwin.wintouch.models.CommunicationModels;

import com.google.gson.annotations.SerializedName;

public class AuthenticationPayloadModel {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public AuthenticationPayloadModel(String user, String pass) {
        this.username = user;
        this.password = pass;
    }
}
