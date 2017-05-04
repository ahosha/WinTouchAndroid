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

public class Data {

    @SerializedName("frequency")
    @Expose
    private String frequency;

    /**
     *
     * @return
     *     The linkState
     */
    public String getfrequency() {
        return frequency;
    }

    /**
     *
     * @param linkState
     *     The LinkState
     */
    public void setfrequency(String linkState) {
        this.frequency = frequency;
    }

}
