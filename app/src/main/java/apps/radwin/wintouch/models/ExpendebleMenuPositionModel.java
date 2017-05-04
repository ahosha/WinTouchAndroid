package apps.radwin.wintouch.models;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 05/03/2017.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class ExpendebleMenuPositionModel {

    public int groupPosition;
    public int itemPosition;
    public boolean isItemChecked;

    public ExpendebleMenuPositionModel(int groupPosition, int itemPosition, boolean isItemChecked, String frequencyId) {
        this.groupPosition = groupPosition;
        this.itemPosition = itemPosition;
        this.isItemChecked = isItemChecked;
        this.frequencyId = frequencyId;
    }

    public String frequencyId;


}
