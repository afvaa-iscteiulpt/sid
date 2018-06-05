package inducesmile.com.sid.App;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import inducesmile.com.sid.Connection.FetchDataFromURL;
import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.R;
import android.view.View.OnClickListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class AlertasActivity extends AppCompatActivity {

    DataBaseHandler db = new DataBaseHandler(this);
    int numAlertas = 0;
    int ultimoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas);
        Cursor alertasCursor= getAlertasCursor();
        listAlertas(alertasCursor);

        //call to reset readed alerts
        //resetReadedAlertas();
    }

    public Cursor getAlertasCursor(){
        //To do
        DataBaseReader dbReader = new DataBaseReader(db);
        Cursor cursor = dbReader.readAlertas();
        return cursor;
    }

    private void listAlertas(Cursor alertasCursor){

        TableLayout table = findViewById(R.id.tableAlertas);
        table.removeViews(1, numAlertas);
        numAlertas = 0;
        ultimoId = -1;

        while (alertasCursor.moveToNext()){
            if (ultimoId == -1)
                ultimoId = alertasCursor.getInt(alertasCursor.getColumnIndex("idAlerta"));

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView idAlerta = new TextView(this);
            idAlerta.setText(alertasCursor.getString(alertasCursor.getColumnIndex("idAlerta")));
            idAlerta.setPadding(dpAsPixels(0), dpAsPixels(5), dpAsPixels(0),dpAsPixels(5));

            TextView data = new TextView(this);
            TextView hora = new TextView(this);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

            Calendar day = Calendar.getInstance();
            String dateTime = alertasCursor.getString(alertasCursor.getColumnIndex("dataHora"));

            if (dateFormat.format(day.getTime()).split(" ")[0].equals(dateTime.split(" ")[0]))
                data.setText("hoje");
            day.add(Calendar.DAY_OF_MONTH,-1);
            if (dateFormat.format(day.getTime()).split(" ")[0].equals(dateTime.split(" ")[0]))
                data.setText("ontem");
            day.add(Calendar.DAY_OF_MONTH,-1);
            if (dateFormat.format(day.getTime()).split(" ")[0].equals(dateTime.split(" ")[0]))
                data.setText(dateTime.split(" ")[0].split("-")[2] + "/" + dateTime.split(" ")[0].split("-")[1]);

            hora.setText(dateTime.split(" ")[1].split(":")[0] + ":" + dateTime.split(" ")[1].split(":")[1]);

            data.setPadding(dpAsPixels(5), dpAsPixels(5), dpAsPixels(5),dpAsPixels(5));
            hora.setPadding(dpAsPixels(5), dpAsPixels(5), dpAsPixels(10),dpAsPixels(5));

            String alertidCultura = alertasCursor.getString(alertasCursor.getColumnIndex("idCultura"));
            if(alertidCultura.isEmpty() || alertidCultura.equals("null")) {
                alertidCultura = "--";
            }
            TextView idCultura = new TextView(this);
            idCultura.setText(alertidCultura);
            idCultura.setPadding(dpAsPixels(5), dpAsPixels(5), dpAsPixels(5),dpAsPixels(5));

            String alertValorRegText = alertasCursor.getString(alertasCursor.getColumnIndex("valorReg"));
            if(alertValorRegText.isEmpty() || alertValorRegText.equals("null")) {
                alertValorRegText = "--.--";
            }
            TextView valorReg = new TextView(this);
            valorReg.setText(alertValorRegText);
            valorReg.setPadding(dpAsPixels(5), dpAsPixels(5), dpAsPixels(5),dpAsPixels(5));

            TextView tipoAlerta = new TextView(this);
            tipoAlerta.setText(alertasCursor.getString(alertasCursor.getColumnIndex("tipoAlerta")));
            tipoAlerta.setPadding(dpAsPixels(5), dpAsPixels(5), dpAsPixels(10),dpAsPixels(5));

            idAlerta.setVisibility(View.INVISIBLE);

            row.addView(data);
            row.addView(hora);
            row.addView(tipoAlerta);
            row.addView(valorReg);
            row.addView(idCultura);
            row.addView(idAlerta);

            row.setClickable(true);

            checkIfIsNewAlert(row, alertasCursor.getString(alertasCursor.getColumnIndex("idAlerta")));

            row.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {

                    v.setBackgroundColor(Color.WHITE);

                    TableRow tablerow = (TableRow) v;
                    TextView rowView = (TextView) tablerow.getChildAt(5);
                    String idAlerta = rowView.getText().toString();

                    addToAlertReadMemory(idAlerta);
                }
            });

            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            numAlertas++;
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
            row.setBackgroundColor(Color.rgb(255,255,180));
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
        editor.putString("alertIds", stringB.toString());
        editor.commit();
    }

    public void mainActivityRefreshDB(View v) {
        if (ultimoId != -1) FetchDataFromURL.fetchAlertasAfterID(this, ultimoId);
        listAlertas(getAlertasCursor());
    }

    private int dpAsPixels(int dp){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
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
