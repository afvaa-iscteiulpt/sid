package inducesmile.com.sid.App;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.R;
import android.view.View.OnClickListener;
public class AlertasActivity extends AppCompatActivity {

    DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas);
        Cursor alertasCursor= getAlertasCursor();
        Cursor culturaCursor = getCulturaCursor();
        updateNomeCultura(culturaCursor);
        listAlertas(alertasCursor);
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
    private void updateNomeCultura(Cursor culturaCursor){
        String nome=null;
        while (culturaCursor.moveToNext()){
            nome = culturaCursor.getString(culturaCursor.getColumnIndex("NomeCultura"));
        }

        TextView tv = findViewById(R.id.nome_cultura);
        if (nome!=null){
        tv.setText(nome);}
    }

    private void listAlertas(Cursor alertasCursor){

        TableLayout table = findViewById(R.id.tableAlertas);
        while (alertasCursor.moveToNext()){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView idAlerta = new TextView(this);
            idAlerta.setText(alertasCursor.getString(alertasCursor.getColumnIndex("idAlerta")));
            idAlerta.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView dataHora = new TextView(this);
            dataHora.setText(alertasCursor.getString(alertasCursor.getColumnIndex("dataHora")));
            dataHora.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView idCultura = new TextView(this);
            idCultura.setText(alertasCursor.getString(alertasCursor.getColumnIndex("idCultura")));
            idCultura.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView valorReg = new TextView(this);
            valorReg.setText(alertasCursor.getString(alertasCursor.getColumnIndex("valorReg")));
            valorReg.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView tipoAlerta = new TextView(this);
            tipoAlerta.setText(alertasCursor.getString(alertasCursor.getColumnIndex("tipoAlerta")));
            tipoAlerta.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            row.addView(idAlerta);
            row.addView(dataHora);
            row.addView(idCultura);
            row.addView(valorReg);
            row.addView(tipoAlerta);

            row.setBackgroundColor(Color.GREEN);
            row.setClickable(true);


            row.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    v.setBackgroundColor(Color.GRAY);
                    System.out.println("Row clicked: " + v.getId());

                    /*
                    //get the data you need
                    TableRow tablerow = (TableRow)v.getParent();
                    TextView sample = (TextView) tablerow.getChildAt(2);
                    String result=sample.getText().toString();
                    */
                }
            });


            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

private int dpAsPixels(int dp){
    float scale = getResources().getDisplayMetrics().density;
    return (int) (dp*scale + 0.5f);

}


}
