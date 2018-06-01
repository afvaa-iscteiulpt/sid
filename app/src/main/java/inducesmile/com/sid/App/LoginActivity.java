package inducesmile.com.sid.App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import inducesmile.com.sid.Connection.ConnectionHandler;
import inducesmile.com.sid.Helper.Alert;
import inducesmile.com.sid.Helper.LoginValidation;
import inducesmile.com.sid.Helper.UserLogin;
import inducesmile.com.sid.R;


//Esta aplicação serve como base para vos ajudar, precisam de completar os métodos To do de modo a que a aplicação faça o minimo que é suposto, podem adicionar novas features ou mudar a UI se acharem relevante.
public class LoginActivity extends AppCompatActivity {
    private EditText ip, port,username,password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        username=findViewById(R.id.username);
        password = findViewById(R.id.password);
        login =  findViewById(R.id.login);

        //check if previous login information is in cache
        checkDefaults();


    }



    private void checkDefaults() {

        //read saved data
        SharedPreferences userdetails;
        userdetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        String storedUsername =userdetails.getString("username", "");
        String storedPassword =userdetails.getString("password", "");
        String storedIP =userdetails.getString("ip", "");
        String storedPort =userdetails.getString("port", "");

        if(!storedUsername.isEmpty() && !storedPassword.isEmpty() && !storedIP.isEmpty() && !storedPort.isEmpty()) {
            password.setText(storedPassword);
            username.setText(storedUsername);
            ip.setText(String.valueOf(storedIP));
            port.setText(String.valueOf(storedPort));
        }

    }

    public void loginClick(View v) {

        UserLogin newuser = new UserLogin(ip.getText().toString(), port.getText().toString(), username.getText().toString(), password.getText().toString());

        if (!ip.getText().toString().isEmpty() && !port.getText().toString().isEmpty() && !username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

            tryLogin();

            if (UserLogin.getInstance().getLoginValidation() == LoginValidation.VALID) {

                saveCurrentLoginInfo();

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();

            } else {
                new Alert(this, "Login failed.", "Please check inputs");
            }

        } else {
            new Alert(this, "Alert", "Please fill all the inputs before login");
        }
    }

    private void saveCurrentLoginInfo() {
        // save current login infpormation for later use
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putString("username", UserLogin.getInstance().getUsername());
        editor.putString("password", UserLogin.getInstance().getPassword());
        editor.putString("ip", UserLogin.getInstance().getIp());
        editor.putString("port", UserLogin.getInstance().getPort());
        editor.commit();
    }

    public void tryLogin() {

        try {

            String READ_VALIDATION = "http://" + ip.getText().toString() + ":" + port.getText().toString() + "/trylogin.php";

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ConnectionHandler jParser = new ConnectionHandler();

            HashMap<String, String> params = new HashMap<>();
            params.put("username", String.valueOf(username.getText().toString()));
            params.put("password", String.valueOf(password.getText().toString()));
            params.put("ip", String.valueOf(ip.getText().toString()));
            params.put("port", String.valueOf(port.getText().toString()));
            JSONArray jsonArray = jParser.getJSONFromUrl(READ_VALIDATION, params);

            if (jsonArray!=null){
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    int valid = c.getInt("valid");

                    if(valid == 1) {
                        UserLogin.getInstance().setValidationStatus(LoginValidation.VALID);
                    }
                }
            }

        } catch (JSONException e) {
            UserLogin.getInstance().setValidationStatus(LoginValidation.INVALID);
            e.printStackTrace();
        }
    }

}
