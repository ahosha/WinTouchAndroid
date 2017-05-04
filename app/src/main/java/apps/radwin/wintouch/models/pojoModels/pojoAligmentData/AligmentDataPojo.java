
package apps.radwin.wintouch.models.pojoModels.pojoAligmentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class AligmentDataPojo {

    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * 
     * @return returns default data
     *     The data
     */
    public Data getData() {
        return data;
    }

    /**
     * 
     * @param data returns default data
     *     The data
     */
    public void setData(Data data) {
        this.data = data;
    }

}
