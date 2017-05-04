
package apps.radwin.wintouch.models.pojoModels.pojoSetBandCallback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class PojoSetBand {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("error")
    @Expose
    private Error error;

    /**
     * 
     * @return
     *     The data
     */
    public Data getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    public void setData(Data data) {
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
