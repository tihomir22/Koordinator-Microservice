package com.koordinator.epsilon.Koordinator.Validaciones;

import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValidacionesEstaticas {

    public static String nombreParBase = "base";
    public static String nombreParContra = "contra";
    public static String intervaloHistorico = "intervaloHistorico";
    public static String intervaloPeriodoIndicador = "intervalo";
    public static String tipoSeriesIndicador = "tipoSeries";


    public static boolean validacionSMA(Map<String, String> parametrosRecibidos) {
        if (parametrosRecibidos.containsKey(nombreParBase) && parametrosRecibidos.containsKey(nombreParContra) && parametrosRecibidos.containsKey(intervaloHistorico) && parametrosRecibidos.containsKey(intervaloPeriodoIndicador) && parametrosRecibidos.containsKey(tipoSeriesIndicador)) {
            if (esIntervaloDeBinance(parametrosRecibidos.get(intervaloHistorico))) {
                if(comprobarTipoSeries(parametrosRecibidos.get(tipoSeriesIndicador))){
                    return true;
                }else{
                    throw new ActivoNoEncontradoException("Has introducido un tipo de serie incorrecto! , los aceptados son : open close low high");
                }
            } else {
                throw new ActivoNoEncontradoException("Has introducido un intervalo incorrecto! , los aceptados son : 3m,5m,15m,30m,1h,2h,4h,6h,8h,12h,1d,3d,1w");
            }

        } else {
            throw new ActivoNoEncontradoException("Te falta alguna variable obligatoria por introducir o has introducido un nombre incorrecto!");
        }
    }


    public static boolean esIntervaloDeBinance(String intervalo) {
        if (intervalo.equals("1m")
                || intervalo.equalsIgnoreCase("3m")
                || intervalo.equalsIgnoreCase("5m")
                || intervalo.equalsIgnoreCase("15m")
                || intervalo.equalsIgnoreCase("30m")
                || intervalo.equalsIgnoreCase("1h")
                || intervalo.equalsIgnoreCase("2h")
                || intervalo.equalsIgnoreCase("4h")
                || intervalo.equalsIgnoreCase("6h")
                || intervalo.equalsIgnoreCase("8h")
                || intervalo.equalsIgnoreCase("12h")
                || intervalo.equalsIgnoreCase("1d")
                || intervalo.equalsIgnoreCase("3d")
                || intervalo.equalsIgnoreCase("1w")
                || intervalo.equals("1M")
        ) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean comprobarTipoSeries(String tipoSeries){
        if(tipoSeries.equalsIgnoreCase("open") || tipoSeries.equalsIgnoreCase("close")  ||tipoSeries.equalsIgnoreCase("low") || tipoSeries.equalsIgnoreCase("high")){
            return true;
        }else{
            return false;
        }
    }

}
