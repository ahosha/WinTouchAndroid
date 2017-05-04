package apps.radwin.wintouch.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by shay_v on 29/06/2016.
 */

@Table(name = "Bands")
public class BandsModel extends Model {

    @Column(name = "ConnectedProjectId")
    public String connectedProjectId;

    @Column(name = "BandId")
    public String bandId;

    @Column(name = "BandName")
    public String bandName;

    @Column(name = "BandwithList")
    public String bandwithList;

    @Column(name = "Frequency5List")
    public String frequency5List;

    @Column(name = "Frequency7List")
    public String frequency7List;

    @Column(name = "Frequency10List")
    public String frequency10List;

    @Column(name = "Frequency14List")
    public String frequency14List;

    @Column(name = "Frequency20List")
    public String frequency20List;

    @Column(name = "Frequency40List")
    public String frequency40List;

    @Column(name = "Frequency80List")
    public String frequency80List;




    public static List<BandsModel> getAllBands() {
        return new Select().from(BandsModel.class).execute();
    }

    public static List<BandsModel> getBandsByProject (String projectId) {
        return new Select()
                .from(BandsModel.class)
                .where("ConnectedProjectId = ?", projectId)
                .execute();
    }


    public static BandsModel getBandById(String bandId) {

        return new Select()
                .from(BandsModel.class)
                .where("BandId = ?", bandId)
                .executeSingle();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof BandsModel)
        {
            sameSame = this.bandId.equals(((BandsModel)object).bandId);
        }

        return sameSame;
    }

}
