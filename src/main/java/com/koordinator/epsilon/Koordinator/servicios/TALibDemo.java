package com.koordinator.epsilon.Koordinator.servicios;

import com.koordinator.epsilon.Koordinator.entidades.DatoHistorico;
import com.koordinator.epsilon.Koordinator.entidades.RegistroTecnico;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import rx.Observable;
import rx.Scheduler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;


public class TALibDemo  {
    //you need to instantiate some basic variables
    private static ArrayList<String>timestampList=new ArrayList<>();
    private static double[] input;
    private static int[] inputInt;
    private static double[] output;
    private static int[] outputInt;
    private static MInteger outBegIdx;
    private static MInteger outNbElement;
    private static RetCode retCode;
    private static Core lib;
    private static int lookback;

    //Here's some data over which we will calculate a moving average. I snagged it from this forum.
    static double[] close;

    public static List<Double> inicializar(ArrayList<DatoHistorico> listaHistoricos, int periodoTiempo, String tipoSeries) {
        //initialize everything required for holding data
        Observable<DatoHistorico> source = Observable.from(listaHistoricos);
        source.map(dato->dato.getOpen_time()).toList().subscribe(res->{
            timestampList= (ArrayList<String>) res;
        });
        Observable<List<Double>>listaRes=null;
        if (tipoSeries.equalsIgnoreCase("close")) {
            listaRes= source.map(dato -> dato.getClose()).toList();
        }else if(tipoSeries.equalsIgnoreCase("high")){
            listaRes= source.map(dato -> dato.getHigh()).toList();
        }else if(tipoSeries.equalsIgnoreCase("low")){
            listaRes= source.map(dato -> dato.getLow()).toList();
        }else if(tipoSeries.equalsIgnoreCase("open")){
            listaRes= source.map(dato -> dato.getOpen()).toList();
        }else{
            listaRes= null;
        }
        return listaRes.toBlocking().single();
    }

    private static void instanciar(double[] list){
        close=list;
        lib = new Core();
        input = new double[close.length];
        inputInt = new int[close.length];
        output = new double[close.length];
        outputInt = new int[close.length];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
    }

    public static ArrayList<RegistroTecnico> ejecutarOperacionSMA(double[] list, int periodoTiempo){
        instanciar(list);
        return simpleMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<RegistroTecnico> ejecutarOperacionEMA(double[] list, int periodoTiempo){
        instanciar(list);
        return ExponentialMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<RegistroTecnico> ejecutarOperacionDEMA(double[] list, int periodoTiempo){
        instanciar(list);
        return DoubleExponentialMovingAverageCall(periodoTiempo);
    }

    /**
     * resets the arrays used in this application since they are only
     * initialized once
     */
    private static void resetArrayValues() {
        //provide default "fill" values to avoid nulls.

        for (int i = 0; i < input.length; i++) {
            input[i] = (double) i;
            inputInt[i] = i;
        }
        for (int i = 0; i < output.length; i++) {
            output[i] = (double) -999999.0;
            outputInt[i] = -999999;
        }

        outBegIdx.value = -1;
        outNbElement.value = -1;
        retCode = RetCode.InternalError;
        lookback = -1;

    }



    private static ArrayList<RegistroTecnico> showFinalOutput() {
        ArrayList<RegistroTecnico>listaResultante=new ArrayList<>();
        int j=0;
        for (int i = 0; i < output.length; i++) {
            Timestamp stamp=new Timestamp(Long.parseLong(timestampList.get(i)));
            if(i>=lookback) {
                listaResultante.add(new RegistroTecnico(i, close[i], output[j++], new Date(stamp.getTime())));
            }else {
                listaResultante.add(new RegistroTecnico(i, close[i], 0, new Date(stamp.getTime())));
            }
        }
        return listaResultante;
    }

    public static ArrayList<RegistroTecnico> ExponentialMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Ema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Ema, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<RegistroTecnico> simpleMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Sma);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Sma, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<RegistroTecnico> DoubleExponentialMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Dema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Dema, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }


}