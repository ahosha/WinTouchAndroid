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
@Table(name = "InstallationGuideMovies")
public class InstallationGuideMoviesModel extends Model {

    //connected installation id
    @Column(name = "ConnectedInstallationModel")
    public String connectedInstallationModel;

    // picture link to download the main picture for the list
    @Column(name = "PictureLink") //connected project id
    public int pictureLink;

    // name to show in the list
    @Column(name = "MovieName") //connected project id
    public String movieName;

    // description to show in the list
    @Column(name = "MovieDescription") //connected project id
    public String movieDescription;

    // link to start the movie
    @Column(name = "MovieLink") //connected project id
    public int movieLink;

    // check list to see if the movie has been seen by the user or not
    @Column(name = "IsSeen") //connected project id
    public Boolean isSeen;

    public static List<InstallationGuideMoviesModel> getMovieModelById (String installationId) {
        try {
            return new Select()
                    .from(InstallationGuideMoviesModel.class)
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
