package inducesmile.com.sid.App;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import inducesmile.com.sid.Helper.Alert;
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
    private Spinner spinner;
    private HashMap<Integer, Integer> culturasId;

    private String nomeCultura = "";
    private int idCultura = -1;
    private String limSupTemp = "";
    private String limInfTemp = "";
    private String limSupHumi = "";
    private String limInfHumi = "";
    ArrayAdapter<String> dataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.dbClear();
        spinner = (Spinner) findViewById(R.id.spinner);
        clearInputs();
        refreshDB(null);

    }

    private void clearInputs() {
        TextView text = findViewById(R.id.limSupTemp);
        text.setText("-");

        text = findViewById(R.id.limInfTemp);
        text.setText("-");

        text = findViewById(R.id.limSupHumi);
        text.setText("-");

        text = findViewById(R.id.limInfHumi);
        text.setText("-");

    }

    public void drawGraph(View v){
        Intent i = new Intent(this, GraphicActivity.class);
        startActivity(i);

    }

    public void showAlertas(View v){
        Intent i = new Intent(this,AlertasActivity.class);
        startActivity(i);
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshDB(null);

                Log.d("msg", "ola");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void refreshDB(View v){

        String idCultura = null;

        if(culturasId != null) {
            idCultura = String.valueOf(culturasId.get(spinner.getSelectedItemPosition()));
        }

        Log.d("idcultura",String.valueOf(idCultura));

        if(idCultura != null && !idCultura.equals("null")) {
            updateDadosCultura(idCultura);
            copyDataToDBWithCulturaID(idCultura);
        } else {
            copyDataToDBWithCulturaID("null");

            clearInputs();
        }

        updateNumeroMedicoes();
        updateNumeroAlertas();
    }

    private void updateDadosCultura(String idCultura) {
        DataBaseReader dbReader = new DataBaseReader(db);
        Cursor cursor = dbReader.readCultura();

        while (cursor.moveToNext()){
            if (cursor.getInt(cursor.getColumnIndex("idCultura")) == Integer.parseInt(idCultura)) {
                this.idCultura = Integer.parseInt(idCultura);
                nomeCultura = cursor.getString(cursor.getColumnIndex("nomeCultura"));
                limSupTemp = cursor.getString(cursor.getColumnIndex("limiteSuperiorTemperatura"));
                limInfTemp = cursor.getString(cursor.getColumnIndex("limiteInferiorTemperatura"));
                limSupHumi = cursor.getString(cursor.getColumnIndex("limiteSuperiorHumidade"));
                limInfHumi = cursor.getString(cursor.getColumnIndex("limiteInferiorHumidade"));
            }
        }

        TextView text = findViewById(R.id.limSupTemp);
        text.setText(limSupTemp);

        text = findViewById(R.id.limInfTemp);
        text.setText(limInfTemp);

        text = findViewById(R.id.limSupHumi);
        text.setText(limSupHumi);

        text = findViewById(R.id.limInfHumi);
        text.setText(limInfHumi);

        Log.d("Message", idCultura);
    }




    public void updateNumeroMedicoes(){

        DataBaseReader dbReader = new DataBaseReader(db);

        TextView text = findViewById(R.id.totalMedicoes);
        text.setText(Integer.toString(0));

        Cursor cursor = dbReader.ReadHumidadeTemperatura();
        int totalMedicoes = cursor.getCount();
        text.setText(Integer.toString(totalMedicoes));

    }

    public void updateNumeroAlertas(){

        DataBaseReader dbReader = new DataBaseReader(db);

        TextView text = findViewById(R.id.totalAlertas);
        text.setText(Integer.toString(0));

        Cursor cursor = dbReader.readAlertas();
        int totalAlertas = cursor.getCount();
        text.setText(Integer.toString(totalAlertas));

    }

    //bug - quando arranca chama duas vezes esta função , não é grave. Os pedidos restantes funcionam sem problemas.
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

            spinner = (Spinner) findViewById(R.id.spinner);
            List<String> list = new ArrayList<String>();
            list.add(" - ");
            culturasId = new HashMap<>();
            culturasId.put(0,null);

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_CULTURA,params);
            if (jsonCultura!=null){
                for (int i = 1; i < jsonCultura.length(); i++) {

                    JSONObject c = jsonCultura.getJSONObject(i);

                    culturasId.put(i,c.getInt("idCultura"));
                    list.add(c.getString("nomeCultura"));

                    String nomeCultura = c.getString("nomeCultura");
                    double limSupTempCultura = c.getDouble("limiteSuperiorTemperatura");
                    double limInfTempCultura = c.getDouble("limiteInferiorTemperatura");
                    double limSupHumiCultura = c.getDouble("limiteSuperiorHumidade");
                    double limInfHumiCultura = c.getDouble("limiteInferiorHumidade");
                    db.insert_Cultura(c.getInt("idCultura"),nomeCultura,limSupTempCultura,limInfTempCultura,limSupHumiCultura,limInfHumiCultura);
                }

                //only updates the select box on statup
                if(dataAdapter == null || list.size() != dataAdapter.getCount()) {
                    dataAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    addListenerOnSpinnerItemSelection();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
