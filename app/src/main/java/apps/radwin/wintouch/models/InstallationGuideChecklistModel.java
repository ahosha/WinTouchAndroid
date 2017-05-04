package apps.radwin.wintouch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 01/01/2017.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

@Table(name = "InstallationGuideChecklist")
public class InstallationGuideChecklistModel extends Model {

    //connected installation id
    @Column(name = "ConnectedInstallationModel")
    public String connectedInstallationModel;

    @Column(name = "CheckMarkName")
    public String checkMarkName;

    @Column(name = "IsMarked")
    public Boolean isMarked;

    public static List<InstallationGuideChecklistModel> getCheckmarksById (String installationId) {
        try {
            return new Select()
                    .from(InstallationGuideChecklistModel.class)
                    .where("ConnectedInstallationModel = ?", installationId)
                    .execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
