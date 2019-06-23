package com.koordinator.epsilon.Koordinator.Validaciones;

import org.springframework.stereotype.Component;

@Component
public class ValidacionesEstaticas {

    public static boolean esIntervaloDeBinance(String intervalo){
        if(intervalo.equals("1m")
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
        ){
            return true;
        }else{
            return false;
        }
    }

}
