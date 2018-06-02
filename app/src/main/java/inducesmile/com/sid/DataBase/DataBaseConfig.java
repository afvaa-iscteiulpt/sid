package inducesmile.com.sid.DataBase;

import android.provider.BaseColumns;

/**
 * Created by joao on 11/04/2018.
 */

public class DataBaseConfig {

    public static class HumidadeTemperatura implements BaseColumns {
        public static final String TABLE_NAME = "HumidadeTemperatura";
        public static final String COLUMN_NAME_IDMEDICAO = "IDMedicao";
        public static final String COLUMN_NAME_DATAHORAMEDICAO = "DataHoraMedicao";
        public static final String COLUMN_NAME_VALORMEDICAOTEMPERATURA = "ValorMedicaoTemperatura";
        public static final String COLUMN_NAME_VALORMEDICAOHUMIDADE = "ValorMedicaoHumidade";

    }

    public static class Alertas implements BaseColumns{
        public static final String TABLE_NAME="Alertas";
        public static final String COLUMN_NAME_IDALERTA="IDAlerta";
        public static final String COLUMN_NAME_DATAHORAMEDICAO ="DataMedicao";
        public static final String COLUMN_NAME_VALORMEDICAO="ValorMedicao";
        public static final String COLUMN_NAME_IDCULTURA ="NomeVariavel";
        public static final String COLUMN_NAME_TIPOALERTAS ="Alertas";
    }

    public static class Cultura implements BaseColumns{
        public static final String TABLE_NAME="Cultura";
        public static final String COLUMN_NAME_IDCULTURA="IDCultura";
        public static final String COLUMN_NAME_NOMECULTURA="NomeCultura";
        public static final String COLUMN_NAME_LIMITE_SUP_TEMP="LimiteSuperiorTemperatura";
        public static final String COLUMN_NAME_LIMITE_INF_TEMP="LimiteInferiorTemperatura";
        public static final String COLUMN_NAME_LIMITE_SUP_HUMI="LimiteSuperiorHumidade";
        public static final String COLUMN_NAME_LIMITE_INF_HUMI="LimiteInferiorHumidade";
    }


    protected static final String SQL_CREATE_HUMIDADE_TEMPERATURA =
            "CREATE TABLE " + HumidadeTemperatura.TABLE_NAME +
                    " (" + HumidadeTemperatura.COLUMN_NAME_IDMEDICAO + " INTEGER PRIMARY KEY," +
                    HumidadeTemperatura.COLUMN_NAME_DATAHORAMEDICAO + " DATETIME," +
                    HumidadeTemperatura.COLUMN_NAME_VALORMEDICAOTEMPERATURA + " REAL," +
                    HumidadeTemperatura.COLUMN_NAME_VALORMEDICAOHUMIDADE + " REAL)";


    protected static final String SQL_CREATE_ALERTAS =
            "CREATE TABLE " + Alertas.TABLE_NAME +
                    " (" + Alertas.COLUMN_NAME_IDALERTA + " INTEGER PRIMARY KEY," +
                    Alertas.COLUMN_NAME_DATAHORAMEDICAO + " DATETIME," +
                    Alertas.COLUMN_NAME_VALORMEDICAO + " REAL," +
                    Alertas.COLUMN_NAME_IDCULTURA + " INTEGER, "+
                    Alertas.COLUMN_NAME_TIPOALERTAS + " TEXT )";


    protected static final String SQL_CREATE_CULTURA=
            "CREATE TABLE " + Cultura.TABLE_NAME +
                    " (" + Cultura.COLUMN_NAME_IDCULTURA + " INTEGER PRIMARY KEY," +
                    Cultura.COLUMN_NAME_NOMECULTURA + " TEXT," +
                    Cultura.COLUMN_NAME_LIMITE_SUP_TEMP + " REAL," +
                    Cultura.COLUMN_NAME_LIMITE_INF_TEMP + " REAL," +
                    Cultura.COLUMN_NAME_LIMITE_SUP_HUMI + " REAL," +
                    Cultura.COLUMN_NAME_LIMITE_INF_HUMI + " REAL)";


    protected static final String SQL_DELETE_HUMIDADE_TEMPERATURA =
            "DROP TABLE IF EXISTS " + HumidadeTemperatura.TABLE_NAME;


    protected static final String SQL_DELETE_ALERTAS=
            "DROP TABLE IF EXISTS " + Alertas.TABLE_NAME;


    protected static final String SQL_DELETE_CULTURA=
            "DROP TABLE IF EXISTS " + Cultura.TABLE_NAME;
}
