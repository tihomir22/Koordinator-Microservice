package com.koordinator.epsilon.Koordinator.servicios;

import com.koordinator.epsilon.Koordinator.Validaciones.ValidacionesEstaticas;
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

    public static int devolverSlowD_Period(Map<String,String>queryParams){
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
        }else{
            return devolverMA(0);
        }
    }

    public static MAType DdevolverMATypeBasadaEnNumero(Map<String, String> queryParams) {
        if (queryParams.get(ValidacionesEstaticas.DMAType) != null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.DMAType))) {
            return devolverMA(Integer.parseInt(queryParams.get(ValidacionesEstaticas.DMAType)));
        }else{
            return devolverMA(0);
        }
    }

    public static int MACDDevolverFastPeriod(Map<String,String> queryParams){
        if(queryParams.get(ValidacionesEstaticas.MACDfastPeriod)!=null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.MACDfastPeriod))){
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.MACDfastPeriod));
        }else{
            return 12;
        }
    }

    public static int MACDDevolverSlowPeriod(Map<String,String>queryParams){
        if(queryParams.get(ValidacionesEstaticas.MACDslowPeriod)!=null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.MACDslowPeriod))){
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.MACDslowPeriod));
        }else{
            return 26;
        }
    }

    public static int MACDSignalPeriod(Map<String,String>queryParams){
        if(queryParams.get(ValidacionesEstaticas.MACDsignalPeriod)!=null && NumberUtils.isCreatable(queryParams.get(ValidacionesEstaticas.MACDsignalPeriod))){
            return Integer.parseInt(queryParams.get(ValidacionesEstaticas.MACDsignalPeriod));
        }else{
            return 9;
        }
    }



}
