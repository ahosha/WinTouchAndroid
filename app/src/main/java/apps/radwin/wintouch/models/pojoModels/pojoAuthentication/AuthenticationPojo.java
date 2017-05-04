
package apps.radwin.wintouch.models.pojoModels.pojoAuthentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class AuthenticationPojo {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("data")
    @Expose
    private Object data;
    @SerializedName("error")
    @Expose
    private Error error;

    /**
     * 
     * @return
     *     The accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 
     * @param accessToken
     *     The access_token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 
     * @return
     *     The data
     */
    public Object getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 
     * @return
     *     The error
     */
    public Error getError() {
        return error;
    }

    /**
     * 
     * @param error
     *     The error
     */
    public void setError(Error error) {
        this.error = error;
    }

}
