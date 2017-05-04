package apps.radwin.wintouch.models;

/**
 * Created by shay_v on 04/05/2016.
 */
public class AligmentListModel {

    //Constructor

    public AligmentListModel(Integer curserLocation_cellNumber, String curserLocation_elevation, String curserLocation_elevationCell, String curserLocation_horizontal, Boolean samples_elevationLow_beResourcesExist, Boolean samples_elevationLow_cirResourcesExist, Boolean samples_elevationLow_scanned, Boolean samples_elevationLow_sectorFound, Boolean samples_elevationLow_sectorWithNetwork, Boolean samples_elevationLow_sectorWithSpecSectorId, Boolean samples_elevationMedium_beResourcesExist, Boolean samples_elevationMedium_cirResourcesExist, Boolean samples_elevationMedium_scanned, Boolean samples_elevationMedium_sectorFound, Boolean samples_elevationMedium_sectorWithNetwork, Boolean samples_elevationMedium_sectorWithSpecSectorId, Boolean samples_elevationHigh_beResourcesExist, Boolean samples_elevationHigh_cirResourcesExist, Boolean samples_elevationHigh_scanned, Boolean samples_elevationHigh_sectorFound, Boolean samples_elevationHigh_sectorWithNetwork, Boolean samples_elevationHigh_sectorWithSpecSectorId) {
        this.curserLocation_cellNumber = curserLocation_cellNumber;
        this.curserLocation_elevation = curserLocation_elevation;
        this.curserLocation_elevationCell = curserLocation_elevationCell;
        this.curserLocation_horizontal = curserLocation_horizontal;
        this.samples_elevationLow_beResourcesExist = samples_elevationLow_beResourcesExist;
        this.samples_elevationLow_cirResourcesExist = samples_elevationLow_cirResourcesExist;
        this.samples_elevationLow_scanned = samples_elevationLow_scanned;
        this.samples_elevationLow_sectorFound = samples_elevationLow_sectorFound;
        this.samples_elevationLow_sectorWithNetwork = samples_elevationLow_sectorWithNetwork;
        this.samples_elevationLow_sectorWithSpecSectorId = samples_elevationLow_sectorWithSpecSectorId;
        this.samples_elevationMedium_beResourcesExist = samples_elevationMedium_beResourcesExist;
        this.samples_elevationMedium_cirResourcesExist = samples_elevationMedium_cirResourcesExist;
        this.samples_elevationMedium_scanned = samples_elevationMedium_scanned;
        this.samples_elevationMedium_sectorFound = samples_elevationMedium_sectorFound;
        this.samples_elevationMedium_sectorWithNetwork = samples_elevationMedium_sectorWithNetwork;
        this.samples_elevationMedium_sectorWithSpecSectorId = samples_elevationMedium_sectorWithSpecSectorId;
        this.samples_elevationHigh_beResourcesExist = samples_elevationHigh_beResourcesExist;
        this.samples_elevationHigh_cirResourcesExist = samples_elevationHigh_cirResourcesExist;
        this.samples_elevationHigh_scanned = samples_elevationHigh_scanned;
        this.samples_elevationHigh_sectorFound = samples_elevationHigh_sectorFound;
        this.samples_elevationHigh_sectorWithNetwork = samples_elevationHigh_sectorWithNetwork;
        this.samples_elevationHigh_sectorWithSpecSectorId = samples_elevationHigh_sectorWithSpecSectorId;
    }


    //MODEL DEFINITION

    //Cursor Location
    public Integer curserLocation_cellNumber;
    public String curserLocation_elevation;
    public String curserLocation_elevationCell;
    public String curserLocation_horizontal;

    //Samples
    public Boolean samples_elevationLow_beResourcesExist;
    public Boolean samples_elevationLow_cirResourcesExist;
    public Boolean samples_elevationLow_scanned;
    public Boolean samples_elevationLow_sectorFound;
    public Boolean samples_elevationLow_sectorWithNetwork;
    public Boolean samples_elevationLow_sectorWithSpecSectorId;

    public Boolean samples_elevationMedium_beResourcesExist;
    public Boolean samples_elevationMedium_cirResourcesExist;
    public Boolean samples_elevationMedium_scanned;
    public Boolean samples_elevationMedium_sectorFound;
    public Boolean samples_elevationMedium_sectorWithNetwork;
    public Boolean samples_elevationMedium_sectorWithSpecSectorId;


    public Boolean samples_elevationHigh_beResourcesExist;
    public Boolean samples_elevationHigh_cirResourcesExist;
    public Boolean samples_elevationHigh_scanned;
    public Boolean samples_elevationHigh_sectorFound;
    public Boolean samples_elevationHigh_sectorWithNetwork;
    public Boolean samples_elevationHigh_sectorWithSpecSectorId;


}
