
package apps.radwin.wintouch.models.pojoModels.pojoBestPosition;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class CursorLocation {

    @SerializedName("cellNumber")
    @Expose
    private Integer cellNumber;
    @SerializedName("elevationCell")
    @Expose
    private String elevationCell;
    @SerializedName("elevation")
    @Expose
    private Integer elevation;
    @SerializedName("horizontal")
    @Expose
    private Integer horizontal;

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
     *     The elevation
     */
    public Integer getElevation() {
        return elevation;
    }

    /**
     * 
     * @param elevation
     *     The elevation
     */
    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }

    /**
     * 
     * @return
     *     The horizontal
     */
    public Integer getHorizontal() {
        return horizontal;
    }

    /**
     * 
     * @param horizontal
     *     The horizontal
     */
    public void setHorizontal(Integer horizontal) {
        this.horizontal = horizontal;
    }

}
