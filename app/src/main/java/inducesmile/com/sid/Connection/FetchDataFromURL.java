package inducesmile.com.sid.Connection;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.Helper.UserLogin;
import inducesmile.com.sid.R;

public class FetchDataFromURL {
    private static String IP = UserLogin.getInstance().getIp();
    private static String PORT = UserLogin.getInstance().getPort();
    private static String username= UserLogin.getInstance().getUsername();
    private static String password = UserLogin.getInstance().getPassword();

    public static final String READ_HUMIDADE_TEMPERATURA = "http://" + IP + ":" + PORT + "/getHumidade_Temperatura.php";
    public static final String READ_ALERTAS = "http://" + IP + ":" + PORT + "/getAlertas.php";
    public static final String READ_CULTURA = "http://" + IP + ":" + PORT + "/getCultura.php";

    public static void copyDataToDBWithCulturaID(Context ct) {
        DataBaseHandler db = new DataBaseHandler(ct);
        try {

            IP = UserLogin.getInstance().getIp();
            PORT = UserLogin.getInstance().getPort();
            username= UserLogin.getInstance().getUsername();
            password = UserLogin.getInstance().getPassword();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            ConnectionHandler jParser = new ConnectionHandler();
            db.dbClear();

            JSONArray jsonHumidadeTemperatura = jParser.getJSONFromUrl(READ_HUMIDADE_TEMPERATURA, params);
            if (jsonHumidadeTemperatura !=null){
                for (int i = 0; i < jsonHumidadeTemperatura.length(); i++) {
                    JSONObject c = jsonHumidadeTemperatura.getJSONObject(i);
                    int idMedicao = c.getInt("idMedicao");
                    String dataHoraMedicao = c.getString("dataHoraMedicao");
                    double valorMedicaoTemperatura = c.getDouble("valorMedicaoTemperatura");
                    double valorMedicaoHumidade = c.getDouble("valorMedicaoHumidade");

                    db.insert_Humidade_Temperatura(idMedicao,dataHoraMedicao,valorMedicaoTemperatura,valorMedicaoHumidade);
                }
            }

            JSONArray jsonAlertas = jParser.getJSONFromUrl(READ_ALERTAS,params);
            if (jsonAlertas!=null){
                for (int i = 0; i < jsonAlertas.length(); i++) {
                    JSONObject c = jsonAlertas.getJSONObject(i);

                    int idAlerta = c.getInt("idAlerta");
                    String tipoAlerta = c.getString("tipoAlerta");
                    String idCulturaResult = c.getString("idCultura");
                    String dataHoraMedicao = c.getString("dataHora");
                    String valorReg = c.getString("valorReg");
                    Log.d("alerta vazio", valorReg);

                    db.insert_Alertas(idAlerta,dataHoraMedicao, valorReg,idCulturaResult,tipoAlerta);

                }

            }

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_CULTURA,params);
            if (jsonCultura!=null){
                for (int i = 0; i < jsonCultura.length(); i++) {

                    JSONObject c = jsonCultura.getJSONObject(i);

                    String nomeCultura = c.getString("nomeCultura");
                    double limSupTempCultura = c.getDouble("limiteSuperiorTemperatura");
                    double limInfTempCultura = c.getDouble("limiteInferiorTemperatura");
                    double limSupHumiCultura = c.getDouble("limiteSuperiorHumidade");
                    double limInfHumiCultura = c.getDouble("limiteInferiorHumidade");
                    db.insert_Cultura(c.getInt("idCultura"),nomeCultura,limSupTempCultura,limInfTempCultura,limSupHumiCultura,limInfHumiCultura);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateGraphValues(Context ct, String date) {
        DataBaseHandler db = new DataBaseHandler(ct);
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HashMap<String, String> params = new HashMap<>();

            params.put("username", username);
            params.put("password", password);
            params.put("datepickerDate",date);
            ConnectionHandler jParser = new ConnectionHandler();
            db.dbClear();

            JSONArray jsonHumidadeTemperatura = jParser.getJSONFromUrl(READ_HUMIDADE_TEMPERATURA, params);
            if (jsonHumidadeTemperatura !=null){
                for (int i = 0; i < jsonHumidadeTemperatura.length(); i++) {
                    JSONObject c = jsonHumidadeTemperatura.getJSONObject(i);
                    int idMedicao = c.getInt("idMedicao");
                    String dataHoraMedicao = c.getString("dataHoraMedicao");
                    double valorMedicaoTemperatura = c.getDouble("valorMedicaoTemperatura");
                    double valorMedicaoHumidade = c.getDouble("valorMedicaoHumidade");

                    db.insert_Humidade_Temperatura(idMedicao,dataHoraMedicao,valorMedicaoTemperatura,valorMedicaoHumidade);
                }
            }

            JSONArray jsonAlertas = jParser.getJSONFromUrl(READ_ALERTAS,params);
            if (jsonAlertas!=null){
                for (int i = 0; i < jsonAlertas.length(); i++) {
                    JSONObject c = jsonAlertas.getJSONObject(i);

                    int idAlerta = c.getInt("idAlerta");
                    String tipoAlerta = c.getString("tipoAlerta");
                    String idCulturaResult = c.getString("idCultura");
                    String dataHoraMedicao = c.getString("dataHora");
                    String valorReg = c.getString("valorReg");

                    db.insert_Alertas(idAlerta,dataHoraMedicao,valorReg,idCulturaResult,tipoAlerta);
                }

            }

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_CULTURA,params);
            if (jsonCultura!=null){
                for (int i = 1; i < jsonCultura.length(); i++) {

                    JSONObject c = jsonCultura.getJSONObject(i);

                    String nomeCultura = c.getString("nomeCultura");
                    double limSupTempCultura = c.getDouble("limiteSuperiorTemperatura");
                    double limInfTempCultura = c.getDouble("limiteInferiorTemperatura");
                    double limSupHumiCultura = c.getDouble("limiteSuperiorHumidade");
                    double limInfHumiCultura = c.getDouble("limiteInferiorHumidade");
                    db.insert_Cultura(c.getInt("idCultura"),nomeCultura,limSupTempCultura,limInfTempCultura,limSupHumiCultura,limInfHumiCultura);
                }
            }
        }catch (JSONException e) {
        e.printStackTrace();
        }
    }
}
