package inducesmile.com.sid.Helper;

import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.*;
import android.os.AsyncTask;

import java.util.HashMap;

import inducesmile.com.sid.Connection.ConnectionHandler;

public class UserLogin {

    private static UserLogin instance;
    private String ip;
    private String port;
    private String username;
    private String password;
    private LoginValidation loginValidation = LoginValidation.INVALID;

    public UserLogin(String ip, String port, String username, String password) {

        instance = this;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public LoginValidation getLoginValidation() {
        return loginValidation;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static UserLogin getInstance() {
        return instance;
    }

    public void setValidationStatus(LoginValidation validationStatus) {
        this.loginValidation = validationStatus;
    }
}
