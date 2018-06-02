package inducesmile.com.sid.App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import inducesmile.com.sid.Connection.ConnectionHandler;
import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.Helper.UserLogin;
import inducesmile.com.sid.R;
import android.view.View.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class AlertasActivity extends AppCompatActivity {

    DataBaseHandler db = new DataBaseHandler(this);
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas);
        Cursor alertasCursor= getAlertasCursor();
        Cursor culturaCursor = getCulturaCursor();
        listAlertas(alertasCursor);

        //call to reset readed alerts
        //resetReadedAlertas();
    }

    public Cursor getCulturaCursor(){
        DataBaseReader dbReader = new DataBaseReader(db);
        Cursor cursor = dbReader.readCultura();
        return cursor;
    }

    public Cursor getAlertasCursor(){
        //To do
        DataBaseReader dbReader = new DataBaseReader(db);
        Cursor cursor = dbReader.readAlertas();
        return cursor;
    }

    private void listAlertas(Cursor alertasCursor){

        TableLayout table = findViewById(R.id.tableAlertas);
        while (alertasCursor.moveToNext()){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView idAlerta = new TextView(this);
            idAlerta.setText(alertasCursor.getString(alertasCursor.getColumnIndex("idAlerta")));
            idAlerta.setPadding(dpAsPixels(5), dpAsPixels(5), 0,dpAsPixels(5));

            TextView dataHora = new TextView(this);
            dataHora.setText(alertasCursor.getString(alertasCursor.getColumnIndex("dataHora")));
            dataHora.setPadding(dpAsPixels(5), dpAsPixels(5), 0,dpAsPixels(5));

            String alertidCultura = alertasCursor.getString(alertasCursor.getColumnIndex("idCultura"));
            if(alertidCultura.isEmpty() || alertidCultura.equals("null")) {
                alertidCultura = "-";
            }
            TextView idCultura = new TextView(this);
            idCultura.setText(alertidCultura);
            idCultura.setPadding(dpAsPixels(5), dpAsPixels(5), 0,dpAsPixels(5));

            String alertValorRegText = alertasCursor.getString(alertasCursor.getColumnIndex("valorReg"));
            if(alertValorRegText.isEmpty() || alertValorRegText.equals("null")) {
                alertValorRegText = "-";
            }
            TextView valorReg = new TextView(this);
            valorReg.setText(alertValorRegText);
            valorReg.setPadding(dpAsPixels(5), dpAsPixels(5), 0,dpAsPixels(5));

            TextView tipoAlerta = new TextView(this);
            tipoAlerta.setText(alertasCursor.getString(alertasCursor.getColumnIndex("tipoAlerta")));
            tipoAlerta.setPadding(dpAsPixels(5), dpAsPixels(5), 0,dpAsPixels(5));

            row.addView(tipoAlerta);
            row.addView(valorReg);
            row.addView(idCultura);
            row.addView(dataHora);
            row.addView(idAlerta);

            row.setClickable(true);

            checkIfIsNewAlert(row, alertasCursor.getString(alertasCursor.getColumnIndex("idAlerta")));

            row.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {

                    v.setBackgroundColor(Color.TRANSPARENT);

                    TableRow tablerow = (TableRow) v;
                    TextView rowView = (TextView) tablerow.getChildAt(4);
                    String idAlerta = rowView.getText().toString();

                    addToAlertReadMemory(idAlerta);
                }
            });

            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void checkIfIsNewAlert(TableRow row, String idAlerta) {

        SharedPreferences alertAlreadyRead;
        alertAlreadyRead = getSharedPreferences("alertAlreadyRead", MODE_PRIVATE);
        String alertIds = alertAlreadyRead.getString("alertIds", "");

        //string to list
        ArrayList<String> list = new ArrayList<String>();
        for(String s: alertIds.split(",")){

            if(s != idAlerta)
                list.add(s);
        }

        if(!list.contains(idAlerta))
            row.setBackgroundColor(Color.rgb(255,255,153));
    }

    private void addToAlertReadMemory(String idAlerta) {

        SharedPreferences alertAlreadyRead;
        alertAlreadyRead = getSharedPreferences("alertAlreadyRead", MODE_PRIVATE);
        String alertIds = alertAlreadyRead.getString("alertIds", "");

        //string to list
        ArrayList<String> list = new ArrayList<String>();
        for(String s: alertIds.split(",")){

            if(s != idAlerta)
                list.add(s);
        }

        if(!list.contains(idAlerta))
            list.add(idAlerta);

        //list to string again to put on sharedpreferences
        StringBuilder stringB = new StringBuilder();
        Iterator<?> it = list.iterator();

        while (it.hasNext()) {
            stringB.append(it.next() + ",");
        }

        SharedPreferences.Editor editor = alertAlreadyRead.edit();
        editor.putString("alertIds", "");
        editor.commit();
    }

    public void mainActivityRefreshDB(View v) {
        spinner = (Spinner) findViewById(R.id.spinner);
        //String idCultura = culturasId.get(spinner.getSelectedItemPosition()).toString();

        copyDataToDBWithCulturaID("");
        listAlertas(getAlertasCursor());

    }

    private int dpAsPixels(int dp){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

    public void copyDataToDBWithCulturaID(String idCultura) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HashMap<String, String> params = new HashMap<>();
            params.put("username", UserLogin.getInstance().getUsername());
            params.put("password", UserLogin.getInstance().getPassword());
            params.put("idCultura",idCultura);
            ConnectionHandler jParser = new ConnectionHandler();
            db.dbClearAlertas();

            JSONArray jsonAlertas = jParser.getJSONFromUrl(MainActivity.READ_ALERTAS,params);
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void resetReadedAlertas() {
        SharedPreferences alertAlreadyRead;
        alertAlreadyRead = getSharedPreferences("alertAlreadyRead", MODE_PRIVATE);
        SharedPreferences.Editor editor = alertAlreadyRead.edit();
        editor.putString("alertIds", "");
        editor.commit();
    }


    public void backToMainView(View v) {
        finish();
    }
}
