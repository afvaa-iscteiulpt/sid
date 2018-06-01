package inducesmile.com.sid.App;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import inducesmile.com.sid.Connection.ConnectionHandler;
import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.Helper.UserLogin;
import inducesmile.com.sid.R;

public class MainActivity extends AppCompatActivity {

    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username= UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();
    DataBaseHandler db = new DataBaseHandler(this);

    //info for download data from sybase
    public static final String READ_HUMIDADE_TEMPERATURA = "http://" + IP + ":" + PORT + "/getHumidade_Temperatura.php";
    public static final String READ_ALERTAS = "http://" + IP + ":" + PORT + "/getAlertas.php";
    public static final String READ_CULTURA = "http://" + IP + ":" + PORT + "/getCultura.php";
    private int spinner;
    private HashMap<Integer, Integer> culturasId;

    private String nomeCultura = "";
    private int idCultura = -1;
    private double limSupTemp = 0;
    private double limInfTemp = 0;
    private double limSupHumi = 0;
    private double limInfHumi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.dbClear();
        fetchCulturasParaSpinner();
        updateDadosCultura();
    }

    public void drawGraph(View v){
        Intent i = new Intent(this, GraphicActivity.class);
        startActivity(i);

    }

    public void showAlertas(View v){
        Intent i = new Intent(this,AlertasActivity.class);
        startActivity(i);
    }

    private void fetchCulturasParaSpinner() {
        Spinner spinner;

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            ConnectionHandler jParser = new ConnectionHandler();

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_CULTURA,params);
            spinner = (Spinner) findViewById(R.id.spinner);
            List<String> list = new ArrayList<String>();
            culturasId = new HashMap<>();

            if (jsonCultura!=null){
                for (int i = 0; i < jsonCultura.length(); i++) {
                    JSONObject c = jsonCultura.getJSONObject(i);
                    culturasId.put(i,c.getInt("idCultura"));
                    list.add(c.getString("nomeCultura"));
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);

                updateDadosCultura();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        refreshDB(null);
    }

    public void refreshDB(View v){
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String idCultura = culturasId.get(spinner.getSelectedItemPosition()).toString();
        if (idCultura != null){
            copyDataToDBWithCulturaID(idCultura);

            updateNumeroMedicoes();
            updateNumeroAlertas();
        }
    }

    public void updateNumeroMedicoes(){

        //To Do

        DataBaseReader dbReader = new DataBaseReader(db);

        Cursor cursor = dbReader.ReadHumidadeTemperatura(null);
        int totalMedicoes = cursor.getCount();
        TextView text = findViewById(R.id.numeroMedicoesInt);
        text.setText(Integer.toString(totalMedicoes));

    }

    public void updateNumeroAlertas(){

        //To Do
        DataBaseReader dbReader = new DataBaseReader(db);

        Cursor cursor = dbReader.readAlertas();
        int totalAlertas = cursor.getCount();
        TextView text = findViewById(R.id.numeroAlertasInt);
        text.setText(Integer.toString(totalAlertas));

    }

    private void updateDadosCultura(){

        refreshDB(null);
        //To do?
        /*DataBaseReader dbReader = new DataBaseReader(db);

        TextView nomeCultura_tv= findViewById(R.id.nomeCultura_tv);
        Cursor cursor = dbReader.readCultura();
        String nomeCultura=null;
        while (cursor.moveToNext()){
            nomeCultura = cursor.getString(cursor.getColumnIndex("NomeCultura"));
        }

        if (nomeCultura!=null){
            nomeCultura_tv.setText(nomeCultura);
            nomeCultura_tv.setTextColor(Color.BLACK);
        }
        else{
            nomeCultura_tv.setText("Cultura Invalida!");
            nomeCultura_tv.setTextColor(Color.RED);
        }

        nomeCultura_tv.setVisibility(View.VISIBLE);*/
    }

//A minha base de dados pode não ser exatamente igual à vossa ou podem concluir que é melhor implementar isto de outra maneira, para mudarem a base de dados no android usem as classes DatabaseConfig(criação) e DatabaseHandler(escrita)

    public void copyDataToDBWithCulturaID(String idCultura) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            params.put("idCultura",idCultura);
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

                    db.insert_Alertas(idAlerta,dataHoraMedicao,Double.valueOf(valorReg),idCulturaResult,tipoAlerta);
                }

            }

            Log.d("msg", "ok");

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_CULTURA,params);
            if (jsonCultura!=null){
                for (int i = 0; i < jsonCultura.length(); i++) {
                    JSONObject c = jsonCultura.getJSONObject(i);
                    String nomeCultura = c.getString("nomeCultura");
                    double limSupTempCultura = c.getDouble("limiteSuperiorTemperatura");
                    double limInfTempCultura = c.getDouble("limiteInferiorTemperatura");
                    double limSupHumiCultura = c.getDouble("limiteSuperiorHumidade");
                    double limInfHumiCultura = c.getDouble("limiteInferiorHumidade");
                    db.insert_Cultura(Integer.parseInt(idCultura),nomeCultura,limSupTempCultura,limInfTempCultura,limSupHumiCultura,limInfHumiCultura);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
