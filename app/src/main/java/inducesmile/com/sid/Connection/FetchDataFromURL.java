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

    public static void fetchCulturasAfterID(Context ct, int idCultura) {
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
            params.put("idCultura", String.valueOf(idCultura));
            ConnectionHandler jParser = new ConnectionHandler();

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
                Log.d("Novas culturas", String.valueOf(jsonCultura.length()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fetchAlertasAfterID(Context ct, int idAlerta) {
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
            params.put("idAlerta", String.valueOf(idAlerta));
            ConnectionHandler jParser = new ConnectionHandler();

            JSONArray jsonAlertas = jParser.getJSONFromUrl(READ_ALERTAS,params);
            if (jsonAlertas!=null){
                for (int i = 0; i < jsonAlertas.length(); i++) {

                    JSONObject c = jsonAlertas.getJSONObject(i);

                    int id = c.getInt("idAlerta");
                    String tipoAlerta = c.getString("tipoAlerta");
                    String idCulturaResult = c.getString("idCultura");
                    String dataHoraMedicao = c.getString("dataHora");
                    String valorReg = c.getString("valorReg");

                    db.insert_Alertas(id,dataHoraMedicao, valorReg,idCulturaResult,tipoAlerta);
                }
                Log.d("Novos alertas", String.valueOf(jsonAlertas.length()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fetchMedicoesAfterID(Context ct, int idMedicao) {
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
            params.put("idMedicao", String.valueOf(idMedicao));
            ConnectionHandler jParser = new ConnectionHandler();

            JSONArray jsonHumiTemp = jParser.getJSONFromUrl(READ_HUMIDADE_TEMPERATURA,params);
            if (jsonHumiTemp!=null){
                for (int i = 0; i < jsonHumiTemp.length(); i++) {

                    JSONObject c = jsonHumiTemp.getJSONObject(i);

                    int id = c.getInt("idMedicao");
                    String dataHoraMedicao = c.getString("dataHoraMedicao");
                    Double valorMedicaoTemperatura = null;
                    Double valorMedicaoHumidade = null;

                    if (!c.isNull("valorMedicaoTemperatura"))
                        valorMedicaoTemperatura = c.getDouble("valorMedicaoTemperatura");
                    if (!c.isNull("valorMedicaoHumidade"))
                        valorMedicaoHumidade = c.getDouble("valorMedicaoHumidade");

                    db.insert_Humidade_Temperatura(id,dataHoraMedicao,valorMedicaoTemperatura,valorMedicaoHumidade);
                }
                Log.d("Novas medicoes", String.valueOf(jsonHumiTemp.length()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fetchAllData(Context ct) {
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

                    Double valorMedicaoTemperatura = null;
                    Double valorMedicaoHumidade = null;

                    if (!c.isNull("valorMedicaoTemperatura"))
                        valorMedicaoTemperatura = c.getDouble("valorMedicaoTemperatura");

                    if (!c.isNull("valorMedicaoHumidade"))
                        valorMedicaoHumidade = c.getDouble("valorMedicaoHumidade");

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

            JSONArray jsonHumidadeTemperatura = jParser.getJSONFromUrl(READ_HUMIDADE_TEMPERATURA, params);
            if (jsonHumidadeTemperatura !=null){
                for (int i = 0; i < jsonHumidadeTemperatura.length(); i++) {
                    JSONObject c = jsonHumidadeTemperatura.getJSONObject(i);
                    int idMedicao = c.getInt("idMedicao");
                    String dataHoraMedicao = c.getString("dataHoraMedicao");

                    Double valorMedicaoTemperatura = null;
                    Double valorMedicaoHumidade = null;

                    if (!c.isNull("valorMedicaoTemperatura"))
                        valorMedicaoTemperatura = c.getDouble("valorMedicaoTemperatura");

                    if (!c.isNull("valorMedicaoHumidade"))
                        valorMedicaoHumidade = c.getDouble("valorMedicaoHumidade");

                    db.insert_Humidade_Temperatura(idMedicao,dataHoraMedicao,valorMedicaoTemperatura,valorMedicaoHumidade);
                }
            }
        }catch (JSONException e) {
        e.printStackTrace();
        }
    }
}
