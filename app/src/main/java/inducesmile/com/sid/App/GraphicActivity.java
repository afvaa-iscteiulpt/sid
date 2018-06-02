package inducesmile.com.sid.App;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.R;

public class GraphicActivity extends AppCompatActivity {

    private DataBaseHandler db = new DataBaseHandler(this);
    private GraphView graph;
    private DataBaseReader reader;
    private Calendar selectedDate = Calendar.getInstance();
    private int scale = 2;

    private String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        graph = findViewById(R.id.graph);

        if (getIntent().hasExtra("date")){
            int[] yearMonthDay = getIntent().getIntArrayExtra("date");
            Log.d("data atual", yearMonthDay[0] + "-" + yearMonthDay[1] + "-" + yearMonthDay[2]);
            selectedDate.set(yearMonthDay[0],yearMonthDay[1],yearMonthDay[2]);
        }
        else selectedDate = Calendar.getInstance();

        insertDateString();
        Cursor cursor = getCursor();
        drawGraph(cursor);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                scale = i;
                updateGraph();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateGraph() {

    }

    private void insertDateString(){
        String yearString = Integer.toString(selectedDate.get(Calendar.YEAR));
        String monthNameString = months[selectedDate.get(Calendar.MONTH)];
        String dayString=Integer.toString(selectedDate.get(Calendar.DAY_OF_MONTH));

        TextView text = findViewById(R.id.dataAtual);
        text.setText(dayString +" de "+monthNameString+" de "+yearString);
    }

    public Cursor getCursor() {
        reader = new DataBaseReader(db);
        Cursor cursor = reader.ReadHumidadeTemperatura();
        return cursor;
    }


     public void showDatePicker(View v){
        Intent intent = new Intent(GraphicActivity.this,DatePickerActivitiy.class);
        startActivity(intent);
        finish();
    }

    public void goToToday(View v) {
        selectedDate = Calendar.getInstance();

        insertDateString();
        Cursor cursor = getCursor();
        //drawGraph(cursor);
    }

    public void backToMainView(View v){
        finish();
    }

    private void drawGraph(Cursor cursor){
        int helper = 0;

        DataPoint[] datapointsTemperatura = new DataPoint[cursor.getCount()];
        DataPoint[] datapointsHumidade = new DataPoint[cursor.getCount()];

        //Ir a cada entrada, converter os minutos para decimais e por no grafico
        while(cursor.moveToNext()) {
            Double dataTemperatura = cursor.getDouble(cursor.getColumnIndex("valorMedicaoTemperatura"));
            Double dataHumidade = cursor.getDouble(cursor.getColumnIndex("valorMedicaoHumidade"));

            String dataHoraString = cursor.getString(cursor.getColumnIndex("dataHoraMedicao"));
            Log.d("String da BD", dataHoraString);

            Date dateTime = new Date();
            DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dateTime = dataFormat.parse(dataHoraString);
            } catch (ParseException e) {
                Log.e("DateTime message", "Parsing ISO8601 datetime failed", e);
            }

            datapointsTemperatura[helper] = new DataPoint(dateTime,dataTemperatura);
            datapointsHumidade[helper] = new DataPoint(dateTime,dataHumidade);
            helper++;
        }
        cursor.close();
        
        LineGraphSeries<DataPoint> seriesTemperatura = new LineGraphSeries<>(datapointsTemperatura);
        LineGraphSeries<DataPoint> seriesHumidade = new LineGraphSeries<>(datapointsHumidade);

        seriesTemperatura.setColor(Color.RED);
        seriesTemperatura.setTitle("Temperatura");
        seriesHumidade.setTitle("Humidade");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setBackgroundColor(Color.alpha(0));

        graph.getViewport().setXAxisBoundsManual(true);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        Calendar beginDate = Calendar.getInstance();
        if (scale == 0) beginDate.add(Calendar.DAY_OF_MONTH, -5);
        else if (scale == 1) beginDate.add(Calendar.DAY_OF_MONTH, -1);
        else beginDate.add(Calendar.HOUR_OF_DAY, -1);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(beginDate.getTime().getTime());
        graph.getViewport().setMaxX(selectedDate.getTime().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.addSeries(seriesTemperatura);
        graph.addSeries(seriesHumidade);
    }





}
