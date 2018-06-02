package inducesmile.com.sid.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joao on 11/04/2018.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sid.db";

    DataBaseConfig config = new DataBaseConfig();
    SQLiteDatabase sqLiteDatabase = null;

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        this.sqLiteDatabase = sqLiteDatabase;

        sqLiteDatabase.execSQL(config.SQL_CREATE_HUMIDADE_TEMPERATURA);
        sqLiteDatabase.execSQL(config.SQL_CREATE_INDEX_HUMITEMP);
        sqLiteDatabase.execSQL(config.SQL_CREATE_ALERTAS);
        sqLiteDatabase.execSQL(config.SQL_CREATE_CULTURA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(config.SQL_DELETE_HUMIDADE_TEMPERATURA);
        sqLiteDatabase.execSQL(config.SQL_DELETE_ALERTAS);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dbClear(){
        getWritableDatabase().execSQL(config.SQL_DELETE_HUMIDADE_TEMPERATURA);
        getWritableDatabase().execSQL(config.SQL_DELETE_ALERTAS);
        getWritableDatabase().execSQL(config.SQL_DELETE_CULTURA);
        onCreate(getWritableDatabase());
    }

    public void humiTempClear() {
        getWritableDatabase().execSQL(config.SQL_DELETE_HUMIDADE_TEMPERATURA);
    }

    public void dbClearAlertas() {
        getWritableDatabase().execSQL(config.SQL_CLEAN_ALERTAS);
    }

    public void insert_Humidade_Temperatura(int idMedicao,String datahoraMedicao,double valorMedicaoTemperatura,double valorMedicaoHumidade){
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_IDMEDICAO,idMedicao);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_DATAHORAMEDICAO,datahoraMedicao);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_VALORMEDICAOTEMPERATURA,valorMedicaoTemperatura);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_VALORMEDICAOHUMIDADE,valorMedicaoHumidade);

        getWritableDatabase().insert(DataBaseConfig.HumidadeTemperatura.TABLE_NAME,null,values);
    }

    public void insert_Alertas(int idAlerta,String datahoraMedicao,double valorMedicao,String idCultura,String tipoAlerta){
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_IDALERTA,idAlerta);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_DATAHORAMEDICAO,datahoraMedicao);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_VALORMEDICAO,valorMedicao);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_IDCULTURA,idCultura);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_TIPOALERTAS,tipoAlerta);
        getWritableDatabase().insert(DataBaseConfig.Alertas.TABLE_NAME,null,values);
    }

    public void insert_Cultura(int idCultura,String nomeCultura,double limSupTemp, double limInfTemp, double limSupHumi, double limInfHumi){
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_IDCULTURA,idCultura);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_NOMECULTURA,nomeCultura);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_LIMITE_SUP_TEMP,limSupTemp);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_LIMITE_INF_TEMP,limInfTemp);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_LIMITE_SUP_HUMI,limSupHumi);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_LIMITE_INF_HUMI,limInfHumi);
        getWritableDatabase().insert(DataBaseConfig.Cultura.TABLE_NAME,null,values);
    }

}
