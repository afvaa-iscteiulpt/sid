package inducesmile.com.sid.DataBase;

import android.provider.BaseColumns;

/**
 * Created by joao on 11/04/2018.
 */

public class DataBaseConfig {

    public static class HumidadeTemperatura implements BaseColumns {
        public static final String TABLE_NAME = "HumidadeTemperatura";
        public static final String COLUMN_NAME_IDMEDICAO = "idMedicao";
        public static final String COLUMN_NAME_DATAHORAMEDICAO = "dataHoraMedicao";
        public static final String COLUMN_NAME_VALORMEDICAOTEMPERATURA = "valorMedicaoTemperatura";
        public static final String COLUMN_NAME_VALORMEDICAOHUMIDADE = "valorMedicaoHumidade";

    }

    public static class Alertas implements BaseColumns{
        public static final String TABLE_NAME="AlertasHumidadeTemperatura";
        public static final String COLUMN_NAME_IDALERTA="idAlerta";
        public static final String COLUMN_NAME_DATAHORAMEDICAO ="dataHora";
        public static final String COLUMN_NAME_VALORMEDICAO="valorReg";
        public static final String COLUMN_NAME_IDCULTURA ="idCultura";
        public static final String COLUMN_NAME_TIPOALERTAS ="tipoAlerta";
    }

    public static class Cultura implements BaseColumns{
        public static final String TABLE_NAME="Cultura";
        public static final String COLUMN_NAME_IDCULTURA="idCultura";
        public static final String COLUMN_NAME_NOMECULTURA="nomeCultura";
        public static final String COLUMN_NAME_LIMITE_SUP_TEMP="limiteSuperiorTemperatura";
        public static final String COLUMN_NAME_LIMITE_INF_TEMP="limiteInferiorTemperatura";
        public static final String COLUMN_NAME_LIMITE_SUP_HUMI="limiteSuperiorHumidade";
        public static final String COLUMN_NAME_LIMITE_INF_HUMI="limiteInferiorHumidade";
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
