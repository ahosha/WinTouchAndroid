package apps.radwin.wintouch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shay_v on 22/06/2016.
 */
@Table(name = "Projects")
public class ProjectsModel extends Model implements Serializable {

    //this will tell us what strings are in the projects model

    @Column(name = "projectId")
    public String projectId;

    @Column(name = "Name")
    public String projectName;

    @Column(name = "Description")
    public String description;

    @Column(name = "NetworkId")
    public String networkId;

    @Column(name = "ProjectEmail")
    public String projectEmail;

    @Column(name = "IsBestEffort")
    public Boolean isBestEffort;

    @Column(name = "CurrentBandId")
    public String currentBandId;

    @Column(name = "TruePutUp")
    public Double truePutUp;

    @Column(name = "TruePutDown")
    public Double truePutDown;

    @Column(name = "SelectedFrequencysList")
    public String selectedFrequencysList;

    @Column(name = "CurrentChannelBandwith")
    public String currentChannelBandwith;

    @Column(name = "AvilableBandsIdLList")
    public String avilableBandsIdLList;

    @Column(name = "CreationDate")
    public String creationDate;

    ////////////////////
    //querys
    ///////////////////

    public static List <ProjectsModel>getAllProjects () {
        return new Select().from(ProjectsModel.class).orderBy("CreationDate DESC").execute();
    }

    public static ProjectsModel getProjectWithId (String ProjectId) {
        return new Select()
                .from(ProjectsModel.class)
                .where("projectId = ?", ProjectId)
                .executeSingle();
    }

    public static Boolean deleteProjectWithId(String ProjectId) {

        try { // try to delete project
            new Delete().from(ProjectsModel.class).where("projectId = ?", ProjectId).execute();
            new Delete().from(WorkingOrdersModel.class).where("projectId = ?", ProjectId).execute();
            return true;

        } catch (Exception e) { // returns falls if deleteProjectWithId couldn't delte project
            e.printStackTrace();
            return false;

        }
    }







}

