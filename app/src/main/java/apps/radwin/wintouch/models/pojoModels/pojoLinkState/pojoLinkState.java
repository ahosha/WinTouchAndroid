package apps.radwin.wintouch.models.pojoModels.pojoLinkState;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 25/12/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class pojoLinkState {

    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }

}
