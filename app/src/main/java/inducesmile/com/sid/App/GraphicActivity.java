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
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import inducesmile.com.sid.Connection.FetchDataFromURL;
import inducesmile.com.sid.DataBase.DataBaseConfig;
import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.R;

public class GraphicActivity extends AppCompatActivity {

    private DataBaseHandler db = new DataBaseHandler(this);
    private GraphView graph;
    LineGraphSeries<DataPoint> seriesTemperatura;
    LineGraphSeries<DataPoint> seriesHumidade;
    private DataBaseReader reader;
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar beginDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private int scale = 1;

    private String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        graph = findViewById(R.id.graph);

        if (getIntent().hasExtra("date")){
            int[] yearMonthDay = getIntent().getIntArrayExtra("date");
            selectedDate.set(yearMonthDay[0],yearMonthDay[1],yearMonthDay[2]);
            DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
            FetchDataFromURL.updateGraphValues(this,dataFormat.format(selectedDate.getTime()));
            updateDates();
        }
        else {
            selectedDate = Calendar.getInstance();
            Log.d("data atual",selectedDate.getTime().toString());
            updateDates();
        }

        insertDateString();
        drawGraph();
        setGraphScale();

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                scale = i;
                updateDates();
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

    private void updateDates() {
        beginDate.set(selectedDate.get(Calendar.YEAR),selectedDate.get(Calendar.MONTH),selectedDate.get(Calendar.DAY_OF_MONTH),0,0,0);
        endDate.set(selectedDate.get(Calendar.YEAR),selectedDate.get(Calendar.MONTH),selectedDate.get(Calendar.DAY_OF_MONTH),0,0,0);
        endDate.add(Calendar.DAY_OF_MONTH,1);

        if (scale == 0) {
            beginDate.add(Calendar.DAY_OF_MONTH, -1);
        }
        else if (scale == 2) {
            beginDate.set(selectedDate.get(Calendar.YEAR),selectedDate.get(Calendar.MONTH),selectedDate.get(Calendar.DAY_OF_MONTH),
                    selectedDate.get(Calendar.HOUR_OF_DAY),selectedDate.get(Calendar.MINUTE),0);
            beginDate.add(Calendar.MINUTE,-50);
            endDate.set(selectedDate.get(Calendar.YEAR),selectedDate.get(Calendar.MONTH),selectedDate.get(Calendar.DAY_OF_MONTH),
                    selectedDate.get(Calendar.HOUR_OF_DAY),selectedDate.get(Calendar.MINUTE),0);
            endDate.add(Calendar.MINUTE,10);
        }

        Log.d("beginDate", beginDate.getTime().toString());
        Log.d("endDate", endDate.getTime().toString());
    }

    private void updateGraph() {
        DataPoint[] temp = generateTemperatura();
        DataPoint[] humi = generateHumidade();

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setProgress(scale);
        seekBar.refreshDrawableState();

        if (graph.getSeries().size() == 0 && temp.length != 0) {
            drawGraph();
        }
        else if (graph.getSeries().size() != 0 && temp.length == 0){
            seriesTemperatura = new LineGraphSeries<>(temp);
            seriesHumidade = new LineGraphSeries<>(humi);
            graph.removeAllSeries();
        }
        else {
            if (temp.length != 0) seriesTemperatura.resetData(temp);
            if (humi.length != 0) seriesHumidade.resetData(humi);
        }

        setGraphScale();
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
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Cursor cursor = reader.ReadHumidadeTemperatura(dataFormat.format(beginDate.getTime()));
        return cursor;
    }


     public void showDatePicker(View v){
        Intent intent = new Intent(GraphicActivity.this,DatePickerActivitiy.class);
        startActivity(intent);
        finish();
    }

    public void goToToday(View v) {
        if (selectedDate.get(Calendar.DAY_OF_MONTH) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ||
                selectedDate.get(Calendar.MONTH) != Calendar.getInstance().get(Calendar.MONTH) ||
                selectedDate.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)) {
            DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
            FetchDataFromURL.updateGraphValues(this,dataFormat.format(Calendar.getInstance().getTime()));
        }

        selectedDate = null;
        selectedDate = Calendar.getInstance();
        scale = 1;

        updateDates();
        insertDateString();
        updateGraph();
    }

    public void refreshData(View v) {
        scale = 1;
        updateDates();
        updateGraph();
    }

    public void backToMainView(View v){
        finish();
    }

    private DataPoint[] generateTemperatura() {
        Cursor cursor = getCursor();
        int helper = 0;

        DataPoint[] datapointsTemperatura = new DataPoint[cursor.getCount()];


        while (cursor.moveToNext()) {
            Double dataTemperatura = cursor.getDouble(cursor.getColumnIndex("valorMedicaoTemperatura"));
            String dataHoraString = cursor.getString(cursor.getColumnIndex("dataHoraMedicao"));

            Date dateTime = new Date();
            DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dateTime = dataFormat.parse(dataHoraString);
            } catch (ParseException e) {
                Log.e("DateTime message", "Parsing ISO8601 datetime failed", e);
            }
            datapointsTemperatura[helper] = new DataPoint(dateTime, dataTemperatura);
            helper++;
        }
        return datapointsTemperatura;
    }

    private DataPoint[] generateHumidade() {
        Cursor cursor = getCursor();
        int helper = 0;

        DataPoint[] datapointsHumidade = new DataPoint[cursor.getCount()];

        //Ir a cada entrada, converter os minutos para decimais e por no grafico
        while (cursor.moveToNext()) {
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

            datapointsHumidade[helper] = new DataPoint(dateTime, dataHumidade);
            helper++;
        }
        return datapointsHumidade;
    }

    private void drawGraph() {
        DataPoint[] temp = generateTemperatura();
        DataPoint[] humi = generateHumidade();

        if (temp.length != 0) {
            seriesTemperatura = new LineGraphSeries<>(temp);
            seriesTemperatura.setColor(Color.RED);
            seriesTemperatura.setTitle("Temperatura");
            graph.addSeries(seriesTemperatura);
        }

        if (humi.length != 0) {
            seriesHumidade = new LineGraphSeries<>(humi);
            seriesHumidade.setTitle("Humidade");
            graph.addSeries(seriesHumidade);
        }

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setBackgroundColor(Color.alpha(0));
    }

    private void setGraphScale() {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(beginDate.getTime().getTime());
        graph.getViewport().setMaxX(endDate.getTime().getTime());

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);

        setDefaultAxisFormat();
    }

    private void setDefaultAxisFormat() {
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);

        if (scale == 0) {
            Calendar middleDate = Calendar.getInstance();
            middleDate.set(beginDate.get(Calendar.YEAR),beginDate.get(Calendar.MONTH),beginDate.get(Calendar.DAY_OF_MONTH),0,0,0);

            staticLabelsFormatter.setHorizontalLabels(new String[] {
                    beginDate.get(Calendar.DAY_OF_MONTH)+ "/" +(beginDate.get(Calendar.MONTH)+1),
                    middleDate.get(Calendar.DAY_OF_MONTH)+ "/" +(middleDate.get(Calendar.MONTH)+1),
                    endDate.get(Calendar.DAY_OF_MONTH)+ "/" +(endDate.get(Calendar.MONTH)+1)});
        }
        else if (scale == 1) staticLabelsFormatter.setHorizontalLabels(new String[] {"0h","6h","12h","18h"});
        else {
            Calendar middleHour = Calendar.getInstance();
            middleHour.set(beginDate.get(Calendar.YEAR),beginDate.get(Calendar.MONTH),beginDate.get(Calendar.DAY_OF_MONTH),0,0,0);
            middleHour.add(Calendar.MINUTE, 30);

            staticLabelsFormatter.setHorizontalLabels(new String[] {
                    beginDate.get(Calendar.MINUTE)+ "'",
                    middleHour.get(Calendar.MINUTE)+ "'",
                    endDate.get(Calendar.MINUTE)+ "'"});
        }

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }
}
