package com.koordinator.epsilon.Koordinator.servicios;

import com.koordinator.epsilon.Koordinator.entidades.HistoricData;
import com.koordinator.epsilon.Koordinator.entidades.TechnicalRegistry;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class TALibDemo {


    //you need to instantiate some basic variables
    private static ArrayList<String> timestampList = new ArrayList<>();
    private static double[] input;
    private static int[] inputInt;
    private static double[] output;
    private static double[] output2;
    private static double[] output3;
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
        source.map(dato -> dato.getOpen_time()).toList().subscribe(res -> {
            timestampList = (ArrayList<String>) res;
        });
        Observable<List<Double>> listaRes = null;
        if (tipoSeries.equalsIgnoreCase("close")) {
            listaRes = source.map(dato -> dato.getClose()).toList();
        } else if (tipoSeries.equalsIgnoreCase("high")) {
            listaRes = source.map(dato -> dato.getHigh()).toList();
        } else if (tipoSeries.equalsIgnoreCase("low")) {
            listaRes = source.map(dato -> dato.getLow()).toList();
        } else if (tipoSeries.equalsIgnoreCase("open")) {
            listaRes = source.map(dato -> dato.getOpen()).toList();
        } else {
            listaRes = null;
        }
        return listaRes.toBlocking().single();
    }

    public static List<Double> inicializar(ArrayList<HistoricData> listaHistoricos) {
        //initialize everything required for holding data
        Observable<HistoricData> source = Observable.from(listaHistoricos);
        source.map(dato -> dato.getOpen_time()).toList().subscribe(res -> {
            timestampList = (ArrayList<String>) res;
        });
        return null;
    }

    private static void instanciar(double[] list) {
        close = list;
        lib = new Core();
        input = new double[close.length];
        inputInt = new int[close.length];
        output = new double[close.length];
        outputInt = new int[close.length];
        outBegIdx = new MInteger();
        outNbElement = new MInteger();
    }

    public static TechnicalRegistry[][] ejecutarOperacionSMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return simpleMovingAverageCall(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionEMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return ExponentialMovingAverageCall(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionDEMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return DoubleExponentialMovingAverageCall(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionKAMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return KaufmanAdaptiveMovingAverageCall(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionMAMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return MotherOfMovingAverageCall(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionTEMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return TripleExponencialMovingAverage(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionT3(double[] list, int periodoTiempo) {
        instanciar(list);
        return TilsonMovingAverageCall(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionTMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return TriangularMovingAverage(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionWMA(double[] list, int periodoTiempo) {
        instanciar(list);
        return WeightedMovingAverage(periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionRSI(double[] list, int periodoTiempo) {
        instanciar(list);
        return RSICall(list, periodoTiempo);
    }

    public static TechnicalRegistry[][] ejecutarOperacionSTOCH(List<Double> listCierre, List<Double> listMax, List<Double> listMin, Map<String, String> queryParams) {
        double[] listMaximo = listMax.stream().mapToDouble(Double::doubleValue).toArray();
        double[] listClose = listCierre.stream().mapToDouble(Double::doubleValue).toArray();
        double[] listMinimo = listMin.stream().mapToDouble(Double::doubleValue).toArray();
        instanciar(listMaximo);
        return StochasticCall(listClose, listMaximo, listMinimo, queryParams);
    }

    public static TechnicalRegistry[][] ejecutarOperacionMACD(List<Double> listCerrada, Map<String, String> queryParams) {
        double[] listClose = listCerrada.stream().mapToDouble(Double::doubleValue).toArray();
        instanciar(listClose);
        return MACDCall(listClose,queryParams);
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


    private static TechnicalRegistry[][] showFinalOutput(String mensaje) {
        //ArrayList<TechnicalRegistry> listaResultante = new ArrayList<>();
        TechnicalRegistry[][] listaResultante = new TechnicalRegistry[output.length][1];
        int j = 0;
        for (int i = 0; i < output.length; i++) {
            Timestamp stamp = new Timestamp(Long.parseLong(timestampList.get(i)));
            if (i >= lookback) {
                //listaResultante.add(new TechnicalRegistry(i, close[i], output[j++], new Date(stamp.getTime())));
                listaResultante[i][0] = new TechnicalRegistry(i, mensaje + " #" + i, close[i], output[j++], new Date(stamp.getTime()));
            } else {
                // listaResultante.add(new TechnicalRegistry(i, close[i], 0, new Date(stamp.getTime())));
                listaResultante[i][0] = new TechnicalRegistry(i, mensaje + " #" + i, close[i], 0, new Date(stamp.getTime()));
            }
        }
        return listaResultante;
    }

    private static TechnicalRegistry[][] showFinalOutputDual(String mensajeA, String mensajeB) {
        int j = 0;
        TechnicalRegistry[][] listaResultante = new TechnicalRegistry[output.length][2];
        for (int i = 0; i < output.length; i++) {
            Timestamp stamp = new Timestamp(Long.parseLong(timestampList.get(i)));
            if (i >= lookback) {
                //listaResultante.add(new TechnicalRegistry(i, close[i], output[j++], new Date(stamp.getTime())));
                listaResultante[i][0] = new TechnicalRegistry(i, mensajeA + " #" + i, close[i], output[j], new Date(stamp.getTime()));
                listaResultante[i][1] = new TechnicalRegistry(i, mensajeB + " #" + i, close[i], output2[j], new Date(stamp.getTime()));
                j++;
            } else {
                // listaResultante.add(new TechnicalRegistry(i, close[i], 0, new Date(stamp.getTime())));
                listaResultante[i][0] = new TechnicalRegistry(i, mensajeA + " #" + i, close[i], 0, new Date(stamp.getTime()));
                listaResultante[i][1] = new TechnicalRegistry(i, mensajeB + " #" + i, close[i], 0, new Date(stamp.getTime()));
            }
        }
        return listaResultante;
    }

    private static TechnicalRegistry[][] showFinalOutputTriple(String mensajeA, String mensajeB,String mensajeC) {
        int j = 0;
        TechnicalRegistry[][] listaResultante = new TechnicalRegistry[output.length][3];
        for (int i = 0; i < output.length; i++) {
            Timestamp stamp = new Timestamp(Long.parseLong(timestampList.get(i)));
            if (i >= lookback) {
                //listaResultante.add(new TechnicalRegistry(i, close[i], output[j++], new Date(stamp.getTime())));
                listaResultante[i][0] = new TechnicalRegistry(i, mensajeA + " #" + i, close[i], output[j], new Date(stamp.getTime()));
                listaResultante[i][1] = new TechnicalRegistry(i, mensajeB + " #" + i, close[i], output2[j], new Date(stamp.getTime()));
                listaResultante[i][2] = new TechnicalRegistry(i, mensajeC + " #" + i, close[i], output3[j], new Date(stamp.getTime()));
                j++;
            } else {
                // listaResultante.add(new TechnicalRegistry(i, close[i], 0, new Date(stamp.getTime())));
                listaResultante[i][0] = new TechnicalRegistry(i, mensajeA + " #" + i, close[i], 0, new Date(stamp.getTime()));
                listaResultante[i][1] = new TechnicalRegistry(i, mensajeB + " #" + i, close[i], 0, new Date(stamp.getTime()));
                listaResultante[i][2] = new TechnicalRegistry(i, mensajeC + " #" + i, close[i],0, new Date(stamp.getTime()));
            }
        }
        return listaResultante;
    }

    public static TechnicalRegistry[][] MACDCall(double[] listaPreciosLive,Map<String, String> queryParams) {
        resetArrayValues();
        output = new double[listaPreciosLive.length];
        output2 = new double[listaPreciosLive.length];
        output3=new double[listaPreciosLive.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;
        retCode=lib.macd(0,listaPreciosLive.length-1,listaPreciosLive,OperadorTernarioJS.MACDDevolverFastPeriod(queryParams),OperadorTernarioJS.MACDDevolverSlowPeriod(queryParams),OperadorTernarioJS.MACDSignalPeriod(queryParams),begin, length,output, output2,output3);
        return showFinalOutputTriple("MACD","MACD Signal","MACD Histogram");
    }

    public static TechnicalRegistry[][] StochasticCall(double[] listCierre, double[] listMax, double[] listMin, Map<String, String> queryParams) {
        resetArrayValues();
        output = new double[listCierre.length];
        output2 = new double[listCierre.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;
        retCode = lib.stoch(0, listCierre.length - 1, listMax, listMin, listCierre, OperadorTernarioJS.devolverFastK_Period(queryParams), OperadorTernarioJS.devolverSlowK_Period(queryParams), OperadorTernarioJS.KdevolverMATypeBasadaEnNumero(queryParams), OperadorTernarioJS.devolverSlowD_Period(queryParams), OperadorTernarioJS.DdevolverMATypeBasadaEnNumero(queryParams), begin, length, output, output2);
        return showFinalOutputDual("SlowK", "SlowD");
    }


    public static TechnicalRegistry[][] RSICall(double[] prices, int period) {
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
        return showFinalOutput("RSI Registry");
    }


    public static TechnicalRegistry[][] ExponentialMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Ema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Ema, outBegIdx, outNbElement, output);
        return showFinalOutput("EMA Registry");
    }

    public static TechnicalRegistry[][] simpleMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Sma);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Sma, outBegIdx, outNbElement, output);
        return showFinalOutput("SMA Registry");
    }

    public static TechnicalRegistry[][] DoubleExponentialMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Dema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Dema, outBegIdx, outNbElement, output);
        return showFinalOutput("DEMA Registry");
    }

    public static TechnicalRegistry[][] KaufmanAdaptiveMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Kama);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Kama, outBegIdx, outNbElement, output);
        return showFinalOutput("KAMA Registry");
    }

    public static TechnicalRegistry[][] MotherOfMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Mama);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Mama, outBegIdx, outNbElement, output);
        return showFinalOutput("MAMA Registry");
    }

    public static TechnicalRegistry[][] TilsonMovingAverageCall(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.T3);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.T3, outBegIdx, outNbElement, output);
        return showFinalOutput("T3 Registry");
    }

    public static TechnicalRegistry[][] TripleExponencialMovingAverage(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Tema);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Tema, outBegIdx, outNbElement, output);
        return showFinalOutput("TEMA Registry");
    }

    public static TechnicalRegistry[][] TriangularMovingAverage(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Trima);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Trima, outBegIdx, outNbElement, output);
        return showFinalOutput("TRIMA Registry");
    }

    public static TechnicalRegistry[][] WeightedMovingAverage(int periodoTiempo) {
        resetArrayValues();
        lookback = lib.movingAverageLookback(periodoTiempo, MAType.Wma);
        retCode = lib.movingAverage(0, close.length - 1, close, lookback + 1, MAType.Wma, outBegIdx, outNbElement, output);
        return showFinalOutput("WMA Registry");
    }


}