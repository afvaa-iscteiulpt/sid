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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

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

    private HashMap<Integer, Integer> culturas;
    private int currentCulturaId = -1;
    private String currentCulturaName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.dbClear();
        fetchCulturasParaSpinner();
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
            culturas = new HashMap<>();

            if (jsonCultura!=null){
                for (int i = 0; i < jsonCultura.length(); i++) {
                    JSONObject c = jsonCultura.getJSONObject(i);
                    culturas.put(i,c.getInt("idCultura"));
                    list.add(c.getString("nomeCultura"));
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                updateCulturaSelecionada();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCulturaSelecionada() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        currentCulturaId = culturas.get(spinner.getSelectedItemPosition());
        currentCulturaName = spinner.getSelectedItem().toString();
    }

    public void addListenerOnSpinnerItemSelection() {
        updateCulturaSelecionada();
        refreshDB(null);
    }

    public void refreshDB(View v){
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        if (spinner != null){
            copyDataToDBWithCulturaID(culturas.get(spinner.getSelectedItemPosition()).toString());
            updateNomeCultura();
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

    private void updateNomeCultura(){

        //To do?
        DataBaseReader dbReader = new DataBaseReader(db);

        Cursor cursor = dbReader.readCultura(currentCulturaName);
        while (cursor.moveToNext()){

        }

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
            JSONArray jsonHumidadeTemperatura = jParser.getJSONFromUrl(READ_HUMIDADE_TEMPERATURA, params);
            db.dbClear();
            if (jsonHumidadeTemperatura !=null){
                for (int i = 0; i < jsonHumidadeTemperatura.length(); i++) {
                    JSONObject c = jsonHumidadeTemperatura.getJSONObject(i);
                    int idMedicao = c.getInt("IDMedicao");
                    String dataHoraMedicao = c.getString("DataHoraMedicao");
                    double valorMedicaoTemperatura = c.getDouble("ValorMedicaoTemperatura");
                    double valorMedicaoHumidade = c.getDouble("ValorMedicaoHumidade");
                    int idCultura2 = c.getInt("IDCultura");
                    db.insert_Humidade_Temperatura(idMedicao,dataHoraMedicao,valorMedicaoTemperatura,valorMedicaoHumidade);
                }
            }

            JSONArray jsonAlertas = jParser.getJSONFromUrl(READ_ALERTAS,params);
            if (jsonAlertas!=null){
                for (int i = 0; i < jsonAlertas.length(); i++) {
                    JSONObject c = jsonAlertas.getJSONObject(i);
                    int IDAlerta = c.getInt("IDAlerta");
                    String dataMedicao = c.getString("DataHoraMedicao");
                    double valorMedicao = c.getDouble("ValorMedicao");
                    String dataHoraMedicao = c.getString("HoraMedicao");
                    String alerta = c.getString("Alerta");
                    db.insert_Alertas(IDAlerta,dataMedicao,valorMedicao,dataHoraMedicao,alerta);
                }

            }

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_CULTURA,params);
            if (jsonCultura!=null){
                for (int i = 0; i < jsonCultura.length(); i++) {
                    JSONObject c = jsonCultura.getJSONObject(i);
                    String nomeCultura = c.getString("NomeCultura");
                    double limSupTempCultura = c.getDouble("LimiteSuperiorTemperatura");
                    double limInfTempCultura = c.getDouble("LimiteInferiorTemperatura");
                    double limSupHumiCultura = c.getDouble("LimiteSuperiorHumidade");
                    double limInfHumiCultura = c.getDouble("LimiteInferiorHumidade");
                    db.insert_Cultura(Integer.parseInt(idCultura),nomeCultura,limSupTempCultura,limInfTempCultura,limSupHumiCultura,limInfHumiCultura);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
