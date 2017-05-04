package apps.radwin.wintouch.screenManagers;

/**
 * This class (singleton) holds the JWT Token received from the authentication service
 */
public class TokenHolder {

    private static TokenHolder instance;
    private String jwtToken;

    private TokenHolder() {}

    public String getJwtToken() {
        if(jwtToken == null) {
            jwtToken = "";
        }

        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public static synchronized TokenHolder getInstance() {
        if(instance == null) {
            instance = new TokenHolder();
        }

        return instance;
    }
}
