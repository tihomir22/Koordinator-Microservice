package com.koordinator.epsilon.Koordinator.servicios;

import com.koordinator.epsilon.Koordinator.entidades.HistoricData;
import com.koordinator.epsilon.Koordinator.entidades.TechnicalRegistry;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import rx.Observable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    public static List<Double> inicializar(ArrayList<HistoricData> listaHistoricos, int periodoTiempo, String tipoSeries) {
        //initialize everything required for holding data
        Observable<HistoricData> source = Observable.from(listaHistoricos);
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

    public static ArrayList<TechnicalRegistry> ejecutarOperacionSMA(double[] list, int periodoTiempo){
        instanciar(list);
        return simpleMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionEMA(double[] list, int periodoTiempo){
        instanciar(list);
        return ExponentialMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionDEMA(double[] list, int periodoTiempo){
        instanciar(list);
        return DoubleExponentialMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionKAMA(double[] list, int periodoTiempo){
        instanciar(list);
        return KaufmanAdaptiveMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionMAMA(double[] list, int periodoTiempo){
        instanciar(list);
        return MotherOfMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionTEMA(double[] list, int periodoTiempo){
        instanciar(list);
        return TripleExponencialMovingAverage(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionT3(double[] list, int periodoTiempo){
        instanciar(list);
        return TilsonMovingAverageCall(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionTMA(double[] list, int periodoTiempo){
        instanciar(list);
        return TriangularMovingAverage(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionWMA(double[] list, int periodoTiempo){
        instanciar(list);
        return WeightedMovingAverage(periodoTiempo);
    }

    public static ArrayList<TechnicalRegistry> ejecutarOperacionRSI(double[] list, int periodoTiempo){
        instanciar(list);
        return RSICall(list,periodoTiempo);
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



    private static ArrayList<TechnicalRegistry> showFinalOutput() {
        ArrayList<TechnicalRegistry>listaResultante=new ArrayList<>();
        int j=0;
        for (int i = 0; i < output.length; i++) {
            Timestamp stamp=new Timestamp(Long.parseLong(timestampList.get(i)));
            if(i>=lookback) {
                listaResultante.add(new TechnicalRegistry(i, close[i], output[j++], new Date(stamp.getTime())));
            }else {
                listaResultante.add(new TechnicalRegistry(i, close[i], 0, new Date(stamp.getTime())));
            }
        }
        return listaResultante;
    }

    public static ArrayList<TechnicalRegistry> RSICall(double[] prices, int period) {
        resetArrayValues();
        output = new double[prices.length];
        double[] tempOutPut = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;
        retCode = lib.rsi(0, prices.length - 1, prices, period, begin, length, tempOutPut);
        for (int i = period; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - period];
        }
       return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> ExponentialMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Ema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Ema, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> simpleMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Sma);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Sma, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> DoubleExponentialMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Dema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Dema, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> KaufmanAdaptiveMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Kama);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Kama, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> MotherOfMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Mama);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Mama, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> TilsonMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.T3);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.T3, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> TripleExponencialMovingAverage(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Tema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Tema, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> TriangularMovingAverage(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Trima);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Trima, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }

    public static ArrayList<TechnicalRegistry> WeightedMovingAverage(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Wma);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Wma, outBegIdx, outNbElement, output);
        return showFinalOutput();
    }




}