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
import java.util.Timer;
import java.util.TimerTask;

import inducesmile.com.sid.Connection.ConnectionHandler;
import inducesmile.com.sid.Connection.FetchDataFromURL;
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
    private Spinner spinner;
    private HashMap<Integer, Integer> culturasId;

    private String nomeCultura = "";
    private int idCultura = -1;
    private String limSupTemp = "";
    private String limInfTemp = "";
    private String limSupHumi = "";
    private String limInfHumi = "";
    ArrayAdapter<String> dataAdapter = null;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.dbClear();
        spinner = (Spinner) findViewById(R.id.spinner);
        clearInputs();
        refreshDB(null);

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        refreshDB(null);
                    }
                });
            }
        },60000,60000);
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
                updateAll();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void refreshDB(View v){
        FetchDataFromURL.copyDataToDBWithCulturaID(this);
        updateAll();
    }

    private void updateAll() {
        String idCultura = null;

        if(culturasId != null) {
            idCultura = String.valueOf(culturasId.get(spinner.getSelectedItemPosition()));
        }

        if(idCultura != null && !idCultura.equals("null"))
            updateDadosCultura(idCultura);
        else clearInputs();

        updateSpinnerData();
        updateNumeroAlertas();
    }

    private void updateSpinnerData() {
        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add(" - ");
        culturasId = new HashMap<>();
        culturasId.put(0,null);

        DataBaseReader dbReader = new DataBaseReader(db);
        Cursor cursor = dbReader.readCultura();

        int i = 1;
        while (cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("nomeCultura")));
            culturasId.put(i,cursor.getInt(cursor.getColumnIndex("idCultura")));
            i++;
        }

        cursor.close();

        if(dataAdapter == null || list.size() != dataAdapter.getCount()) {
            dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            addListenerOnSpinnerItemSelection();
        }
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

        cursor.close();

        TextView text = findViewById(R.id.limSupTemp);
        text.setText(limSupTemp);

        text = findViewById(R.id.limInfTemp);
        text.setText(limInfTemp);

        text = findViewById(R.id.limSupHumi);
        text.setText(limSupHumi);

        text = findViewById(R.id.limInfHumi);
        text.setText(limInfHumi);

        text = findViewById(R.id.idCultura);
        text.setText(idCultura);

    }

    public void updateNumeroAlertas(){

        DataBaseReader dbReader = new DataBaseReader(db);

        TextView text = findViewById(R.id.totalAlertas);
        text.setText(Integer.toString(0));

        Cursor cursor = dbReader.readAlertas();
        int totalAlertas = cursor.getCount();
        text.setText(Integer.toString(totalAlertas));
        cursor.close();
    }

}
