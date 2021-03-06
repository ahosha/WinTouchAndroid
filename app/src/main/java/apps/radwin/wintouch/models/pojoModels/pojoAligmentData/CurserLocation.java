
package apps.radwin.wintouch.models.pojoModels.pojoAligmentData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class CurserLocation {

    @SerializedName("cellNumber")
    @Expose
    private Integer cellNumber;
    @SerializedName("elevation")
    @Expose
    private String elevation;
    @SerializedName("elevationCell")
    @Expose
    private String elevationCell;
    @SerializedName("horizontal")
    @Expose
    private String horizontal;

    /**
     * 
     * @return
     *     The cellNumber
     */
    public Integer getCellNumber() {
        return cellNumber;
    }

    /**
     * 
     * @param cellNumber
     *     The cellNumber
     */
    public void setCellNumber(Integer cellNumber) {
        this.cellNumber = cellNumber;
    }

    /**
     * 
     * @return
     *     The elevation
     */
    public String getElevation() {
        return elevation;
    }

    /**
     * 
     * @param elevation
     *     The elevation
     */
    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    /**
     * 
     * @return
     *     The elevationCell
     */
    public String getElevationCell() {
        return elevationCell;
    }

    /**
     * 
     * @param elevationCell
     *     The elevationCell
     */
    public void setElevationCell(String elevationCell) {
        this.elevationCell = elevationCell;
    }

    /**
     * 
     * @return
     *     The horizontal
     */
    public String getHorizontal() {
        return horizontal;
    }

    /**
     * 
     * @param horizontal
     *     The horizontal
     */
    public void setHorizontal(String horizontal) {
        this.horizontal = horizontal;
    }

}
