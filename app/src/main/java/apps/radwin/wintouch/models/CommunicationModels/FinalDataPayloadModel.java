package apps.radwin.wintouch.models.CommunicationModels;

import com.google.gson.annotations.SerializedName;

/**
 * --------------------------------------------------------------------
 * Created by Shay A. Vidas.
 * Written: 13/12/2016.
 * created as Radwin
 * Email: shay3112@gmail.com
 * Personlized Licens: all copyrigts and details are saved for Radwin Ltd, and Rad Group
 * Compilation: javac
 * --------------------------------------------------------------------
 */

public class FinalDataPayloadModel {

    @SerializedName("latitude")
    private String latitude;


    @SerializedName("longitude")
    private String longitude;

    @SerializedName("confirmInstallation")
    private Boolean confirmInstallation;



    //

    @SerializedName("installationDateTime")
    private String installationDateTime;

    @SerializedName("installationSrvType")
    private String installationSrvType;

    @SerializedName("installationBandId")
    private String installationBandId;

    @SerializedName("installationCBW")
    private String installationCBW;

    @SerializedName("installationFreq")
    private String installationFreq;

    @SerializedName("installationUlRss")
    private String installationUlRss;

    @SerializedName("installationDlRss")
    private String installationDlRss;

    @SerializedName("installationUlTput")
    private String installationUlTput;

    @SerializedName("installationDlTput")
    private String installationDlTput;

    @SerializedName("installationGenStr")
    private String installationGenStr;

    public FinalDataPayloadModel(String latitude, String longitude, Boolean confirmInstallation, String installationDateTime, String installationSrvType, String installationBandId, String installationCBW, String installationFreq, String installationUlRss, String installationDlRss, String installationUlTput, String installationDlTput, String installationGenStr) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.confirmInstallation = confirmInstallation;
        this.installationDateTime = installationDateTime;
        this.installationSrvType = installationSrvType;
        this.installationBandId = installationBandId;
        this.installationCBW = installationCBW;
        this.installationFreq = installationFreq;
        this.installationUlRss = installationUlRss;
        this.installationDlRss = installationDlRss;
        this.installationUlTput = installationUlTput;
        this.installationDlTput = installationDlTput;
        this.installationGenStr = installationGenStr;
    }

}
