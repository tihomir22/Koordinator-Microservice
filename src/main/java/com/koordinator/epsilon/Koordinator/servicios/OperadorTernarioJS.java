package com.koordinator.epsilon.Koordinator.servicios;

import com.koordinator.epsilon.Koordinator.Validaciones.MetacortexStaticLibrary.ValidacionesEstaticas;
import com.tictactec.ta.lib.MAType;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


public class OperadorTernarioJS {


    public static int devolverFastK_Period(Map<String, String> queryParams) {
        try {
            if (queryParams.get(ValidacionesEstaticas.fastKPeriod) != null) {
                return Integer.parseInt(queryParams.get(ValidacionesEstaticas.fastKPeriod));
            } else {
                return 5;
            }
        } catch (NumberFormatException ex) {
            return 5;
        }
    }

    public static int devolverSlowK_Period(Map<String, String> queryParams) {
        try {
            if (queryParams.get(ValidacionesEstaticas.slowKPeriod) != null) {
                return Integer.parseInt(queryParams.get(ValidacionesEstaticas.slowKPeriod));
            } else {
                return 3;
            }
        } catch (NumberFormatException ex) {
            return 3;
        }
    }

    public static int devolverSlowD_Period(Map<String, String> queryParams) {
        try {
            if (queryParams.get(ValidacionesEstaticas.slowDPeriod) != null) {
                return Integer.parseInt(queryParams.get(ValidacionesEstaticas.slowDPeriod));
            } else {
                return 3;
            }
        } catch (NumberFormatException ex) {
            return 3;
        }
    }

    private static MAType devolverMA(int num) {
        switch (num) {
            case 1:
                return MAType.Dema;
            case 2:
                return MAType.Ema;
            case 3:
                return MAType.Kama;
            case 4:
                return MAType.Mama;
            case 5:
                return MAType.T3;
            case 6:
                return MAType.Tema;
            case 7:
                return MAType.Trima;
            case 8:
                return MAType.Wma;
            default:
                return MAType.Sma;

        }
    }

    public static MAType KdevolverMATypeBasadaEnNumero(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.KMAType) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.KMAType))) {
            return devolverMA(Integer.parseInt(queryParams.get(ValidacionesEstaticas.KMAType)));
        } else {
            return devolverMA(0);
        }
    }

    static MAType BBANDSdevolverMATypeBasadaEnNumero(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.generalMATypeParameter) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.generalMATypeParameter))) {
            return devolverMA(Integer.parseInt(queryParams.get(ValidacionesEstaticas.generalMATypeParameter)));
        } else {
            return devolverMA(0);
        }
    }

    static int devolverBBANDSDesviationUpper(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.BBANDSDesviacionArriba) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.BBANDSDesviacionArriba))) {
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.BBANDSDesviacionArriba));
        } else {
            return 2;
        }
    }

    static int devolverBBANDSDesviationLower(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.BBANDSDesviacionAbajo) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.BBANDSDesviacionAbajo))) {
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.BBANDSDesviacionAbajo));
        } else {
            return 2;
        }
    }

    public static MAType DdevolverMATypeBasadaEnNumero(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.DMAType) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.DMAType))) {
            return devolverMA(Integer.parseInt(queryParams.get(ValidacionesEstaticas.DMAType)));
        } else {
            return devolverMA(0);
        }
    }

    public static int MACDDevolverFastPeriod(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.MACDfastPeriod) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.MACDfastPeriod))) {
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.MACDfastPeriod));
        } else {
            return 12;
        }
    }

    public static int MACDDevolverSlowPeriod(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.MACDslowPeriod) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.MACDslowPeriod))) {
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.MACDslowPeriod));
        } else {
            return 26;
        }
    }

    public static int MACDSignalPeriod(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.MACDsignalPeriod) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.MACDsignalPeriod))) {
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.MACDsignalPeriod));
        } else {
            return 9;
        }
    }

    public static String getSeriesTypeDefault(Map<String, String> queryparams) {
        if (queryparams.get(ValidacionesEstaticas.tipoSeriesIndicador) != null) {
            return queryparams.get(ValidacionesEstaticas.tipoSeriesIndicador);
        } else {
            return "close";
        }
    }

    public static int getIndicatorIntervalDefault(Map<String, String> queryparams) {
        if (queryparams.get(ValidacionesEstaticas.intervaloPeriodoIndicador) != null && NumberUtils.isCreatable(queryparams.get(ValidacionesEstaticas.MACDsignalPeriod))) {
            return Integer.parseInt(queryparams.get(ValidacionesEstaticas.intervaloPeriodoIndicador));
        } else {
            return -1;
        }
    }

    public static String obtenerStartTime(Map<String,String>queryParams){
        if(queryParams.get(ValidacionesEstaticas.HistoricStartTime)!=null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.HistoricStartTime))){
            return (queryParams.get(ValidacionesEstaticas.HistoricStartTime));
        }else{
            return "-1";
        }
    }

    public static String obtenerEndTime(Map<String,String>queryParams){
        if(queryParams.get(ValidacionesEstaticas.HistoricEndTime)!=null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.HistoricEndTime))){
            return queryParams.get(ValidacionesEstaticas.HistoricEndTime);
        }else{
            return "-1";
        }
    }

    public static int obtenerLimit(Map<String,String>queryParams){
        if(queryParams.get(ValidacionesEstaticas.HistoricLimit)!=null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.HistoricLimit))){
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.HistoricLimit));
        }else{
            return 1000;
        }
    }


}
