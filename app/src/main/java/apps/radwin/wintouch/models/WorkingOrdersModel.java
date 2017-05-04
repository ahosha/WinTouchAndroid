package apps.radwin.wintouch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shay_v on 23/06/2016.
 */
@Table(name = "WorkingOrders")
public class WorkingOrdersModel extends Model {

    @Column(name = "ProjectId") //connected project id
    public String projectId;

    @Column(name = "WorkordertId") //connected project id
    public String workordertId;

    @Column(name = "LatestUplink")
    public String latestUplink;

    @Column(name = "LateseDownlink")
    public String lateseDownlink;

    @Column(name = "WorkingOrderName")
    public String workingOrderName;

    @Column(name = "WorkingOrderAdress")
    public String workingOrderAdress;

    @Column(name = "WorkingOrderPhoneNumber")
    public String workingOrderPhoneNumber;

    @Column(name = "IsBestEffort")
    public Boolean isBestEffort;

    @Column(name = "CurrentSelectedBand")
    public String CurrentSelectedBand;

    @Column(name = "CurrentChannelBandwith")
    public String currentChannelBandwith;

    @Column(name = "TruePutUp")
    public Double truePutUp;

    @Column(name = "TruePutDown")
    public Double truePutDown;

    @Column(name = "SelectedFrequencysList")
    public String selectedFrequencysList;

    @Column(name = "LastUpdateTime")
    public String lastUpdateTime;

    @Column(name = "LastUpdateStatus")
    public String lastUpdateStatus;

    @Column(name = "WorkorderWifiSSID")
    public String workorderWifiSSID;

    @Column(name = "UpLinkResult")
    public Double upLinkResult;

    @Column(name = "DownLinkResult")
    public Double downLinkResult;

    @Column(name = "SelectedFrequency")
    public Integer selectedFrequency;

    @Column(name = "SelectedSectorId")
    public String selectedSectorId;

    @Column(name = "WifiPassword")
    public String wifiPassword;

    @Column(name = "UnitPassword")
    public String unitPassword;
	
	 @Column(name = "OrderLatitude")
    public double orderLatitude;

    @Column(name = "OrderLongitude")
    public double orderLongitude;

    @Column(name = "HsuId")
    public Integer hsuId;

    @Column(name = "ElevationBeamwidth")
    public Integer elevationBeamwidth;

    @Column(name = "AzimutBeamwidth")
    public Integer azimutBeamwidth;

    @Column(name = "HsuLinkState")
    public String hsuLinkState;

    @Column(name = "HsuServiceType")
    public String hsuServiceType;

    @Column(name = "NumOfElevationZones")
    public Integer numOfElevationZones;

    @Column(name = "RadiusInstallConfirmationRequired")
    public String radiusInstallConfirmationRequired;

    // sending data in workorder
    ///////////////////////
    @Column(name = "ImageOne")
    public String imageOne;

    @Column(name = "ImageTwo")
    public String imageTwo;

    @Column(name = "ImageThree")
    public String imageThree;

    @Column(name = "MacAddress")
    public String macAddress;
    ////////////////////
    // Queries
    ///////////////////

    public static List <WorkingOrdersModel>getWorkingOrderForProject (String projectId) {
        try {
            return new Select()
                    .from(WorkingOrdersModel.class)
                    .where("ProjectId = ?", projectId)
                    .execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<WorkingOrdersModel>();
    }


    public static List <WorkingOrdersModel>getCompletedWorkordersForProject (String projectId) {
        try {
            return new Select()
                    .from(WorkingOrdersModel.class)
                    .where("ProjectId = ?", projectId)
                    .where("LastUpdateStatus = ?", "complete")
                    .execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<WorkingOrdersModel>();
    }




    public static List <WorkingOrdersModel> getWorkingOrderForProjectByDistance(String ProjectId,String lat,String lon) {
        try {
            String strAbs = " abs(OrderLatitude -" + lat + ") ";
            String strAbsSq = "( " + strAbs + " * " + strAbs + " ) ";
            String strAbsL = " abs(OrderLongitude -" + lon + ") ";
            String strAbsLSq = "( " + strAbsL + " * " + strAbsL + " ) ";
            String t = strAbsSq + " + " + strAbsLSq + " ASC, workingOrderName ";

            return new Select()
                    .from(WorkingOrdersModel.class)
                    .where("ProjectId = ?", ProjectId)
                    .orderBy(t)
                    .execute();
        }
        catch (Exception e)
        {

        }
        return getWorkingOrderForProject(ProjectId);
    }

    public static WorkingOrdersModel getWorkingOrderByDistance(String lat,String lon) {
        try {
            String strAbs = " abs(OrderLatitude -" + lat + ") ";
            String strAbsSq = "( " + strAbs + " * " + strAbs + " ) ";
            String strAbsL = " abs(OrderLongitude -" + lon + ") ";
            String strAbsLSq = "( " + strAbsL + " * " + strAbsL + " ) ";
            String t = strAbsSq + " + " + strAbsLSq + " ASC, workingOrderName ";

            return new Select()
                    .from(WorkingOrdersModel.class)
                    .orderBy(t)
                    .executeSingle();
        }
        catch (Exception e)
        {

        }
        return getLastUpdatedWorkorder();
    }

    public static List <WorkingOrdersModel>getAllWorkingOrders () {
        try {
            return new Select().from(WorkingOrdersModel.class).execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<WorkingOrdersModel>();
    }

    public static WorkingOrdersModel getWorkorderByProjectId (String ProjectId) {
        try {
            return new Select()
                    .from(WorkingOrdersModel.class)
                    .where("ProjectId = ?", ProjectId)
                    .executeSingle();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static WorkingOrdersModel getWorkorderById (String workorderId) {
        try {
            return new Select()
                    .from(WorkingOrdersModel.class)
                    .where("WorkordertId = ?", workorderId)
                    .executeSingle();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static WorkingOrdersModel getLastUpdatedWorkorder () {
        try {
            return new Select()
                    .from(WorkingOrdersModel.class).orderBy("LastUpdateTime DESC")
                    .executeSingle();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static WorkingOrdersModel getLastUpdatedWorkorderByProjectId (String ProjectId) {
        return new Select()
                .from(WorkingOrdersModel.class)
                .where("ProjectId = ?", ProjectId)
                .orderBy("LastUpdateTime DESC")
                .executeSingle();
    }

    public static WorkingOrdersModel getClosestWorkorder(String lat,String lon) {
        String t = "abs(OrderLatitude -"+ lat + ") + abs(OrderLongitude - " + lon +") ASC ";
        return new Select()
                .from(WorkingOrdersModel.class)
                .orderBy(t )
                .executeSingle();
    }

    public static WorkingOrdersModel getClosestWorkorderByProjectId(String ProjectId,String lat,String lon) {
        String t = "abs(OrderLatitude -"+ lat + ") + abs(OrderLongitude - " + lon +") ASC ";
        return new Select()
                .from(WorkingOrdersModel.class)
                .where("ProjectId = ?", ProjectId)
                .orderBy(t)
                .executeSingle();
    }


    public static Boolean deleteWOWithId(String WorkorderId) {

        try { // try to delete project
            new Delete().from(WorkingOrdersModel.class).where("WorkordertId = ?", WorkorderId).execute();
            return true;

        } catch (Exception e) { // returns falls if deleteProjectWithId couldn't delte project
            e.printStackTrace();

            return false;

        }
    }

    public String toJsonString()
    {
        String str = "";

        try {
            JSONObject json = toJson();
            str = json.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return str;

    }
    public static WorkingOrdersModel fromJson( String strWorkOrder )
    {
        try {
            JSONObject obj = new JSONObject(strWorkOrder);
            return fromJson(obj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static WorkingOrdersModel fromJson( JSONObject obj )
    {
        WorkingOrdersModel wo = new WorkingOrdersModel();
        try {
            wo.projectId= obj.getString("projectId");
            wo.projectId= obj.getString("projectId");
            wo.workordertId=obj.getString("workOrdertId");
            wo.workingOrderName=obj.getString("workingOrderName");
            wo.workingOrderAdress=obj.getString("workingOrderAdress");
            wo.workingOrderPhoneNumber=obj.getString("workingOrderPhoneNumber");
            wo.isBestEffort=obj.getBoolean("isBestEffort");
            wo.CurrentSelectedBand=obj.getString("CurrentSelectedBand");
            wo.currentChannelBandwith=obj.getString("currentChannelBandwith");
            wo.truePutUp=obj.getDouble("throughputUp");
            wo.truePutDown=obj.getDouble("throughputDown");
            wo.selectedFrequencysList=obj.getString("FrequencyList");
            wo.lastUpdateTime=obj.getString("lastUpdateTime");
            wo.lastUpdateStatus=obj.getString("lastUpdateStatus");
            wo.workorderWifiSSID=obj.getString("workorderWifiSSID");
            wo.upLinkResult=obj.getDouble("upLinkResult");
            wo.downLinkResult=obj.getDouble("downLinkResult");
            wo.selectedFrequency=obj.getInt("selectedFrequency");
            wo.selectedSectorId=obj.getString("selectedSectorId");
            wo.wifiPassword=obj.getString("wifiPassword");
            wo.unitPassword=obj.getString("unitPassword");
            wo.orderLatitude=obj.getDouble("orderLatitude");
            wo.orderLongitude=obj.getDouble("orderLongitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wo;

    }
    public static ArrayList<WorkingOrdersModel> fromJson(JSONArray jsonObjects) {
        ArrayList<WorkingOrdersModel> workOrders = new ArrayList<WorkingOrdersModel>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                workOrders.add(WorkingOrdersModel.fromJson(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return workOrders;
    }


    public JSONObject toJson()
    {
        JSONObject json = new JSONObject();

        try {
            json.put("projectId", this.projectId);
            json.put("workOrdertId", this.workordertId);
            json.put("workingOrderName", this.workingOrderName);
            json.put("workingOrderAdress", this.workingOrderAdress);
            json.put("workingOrderPhoneNumber", this.workingOrderPhoneNumber);
            json.put("isBestEffort", this.isBestEffort);
            json.put("CurrentSelectedBand", this.CurrentSelectedBand);
            json.put("currentChannelBandwith", this.currentChannelBandwith);
            json.put("throughputUp", this.truePutUp);
            json.put("throughputDown", this.truePutDown);
            json.put("FrequencyList", this.selectedFrequencysList);
            json.put("lastUpdateTime", this.lastUpdateTime);
            json.put("lastUpdateStatus", this.lastUpdateStatus);
            json.put("workorderWifiSSID", this.workorderWifiSSID);
            json.put("upLinkResult", this.upLinkResult);
            json.put("downLinkResult", this.downLinkResult);
            json.put("selectedFrequency", this.selectedFrequency);
            json.put("selectedSectorId", this.selectedSectorId);
            json.put("wifiPassword", this.wifiPassword);
            json.put("unitPassword", this.unitPassword);
            json.put("orderLatitude", this.orderLatitude);
            json.put("orderLongitude", this.orderLongitude);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return json;
    }


    public static WorkingOrdersModel getLastUpdatedWorkOrder()
    {
        try {
            WorkingOrdersModel wom = new Select()
                    .from(WorkingOrdersModel.class)
                    .orderBy("lastUpdateTime desc")
                    .limit(1)
                    .executeSingle();

            return wom;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new WorkingOrdersModel();
    }

}
