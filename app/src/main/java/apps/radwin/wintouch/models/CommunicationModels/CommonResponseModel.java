package apps.radwin.wintouch.models.CommunicationModels;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 30/10/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class CommonResponseModel {

    final Boolean status;
    final String message;

    public CommonResponseModel(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
