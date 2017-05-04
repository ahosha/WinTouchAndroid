package apps.radwin.wintouch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

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

@Table(name = "InstallationGuide")
public class InstallationGuideModel extends Model {

    // id to give the installation guide
    @Column(name = "InstallationGuideId") //connected project id
    public String installationGuideId;

    // installation guide name to show in the list
    @Column(name = "InstallationGuideName") //connected project id
    public String installationGuideName;

    // installation guide desciption to show in the list
    @Column(name = "InstallationGuideDescription") //connected project id
    public String installationGuideDescription;


    public static InstallationGuideModel getInstallationGuideById (String installationGuideId) {
        try {
            return new Select()
                    .from(InstallationGuideModel.class)
                    .where("InstallationGuideId = ?", installationGuideId)
                    .executeSingle();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public static List<InstallationGuideModel> getAllInstallationGuides () {
        return new Select().from(InstallationGuideModel.class).execute();
    }

}
