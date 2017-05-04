
package apps.radwin.wintouch.models.pojoModels.pojoCountryCode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryCodePojo {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("dial_code")
    @Expose
    private String dialCode;
    @SerializedName("code")
    @Expose
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
