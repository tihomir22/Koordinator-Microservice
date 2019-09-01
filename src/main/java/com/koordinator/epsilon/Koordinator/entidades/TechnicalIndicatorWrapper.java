package com.koordinator.epsilon.Koordinator.entidades;


import java.util.Arrays;
import java.util.Map;

public class TechnicalIndicatorWrapper
{
    private String indicatorName;
    private String historicPeriod;
    private String seriesType;
    private int interval;
    private TechnicalRegistry [][] rawTechnicalData;

    private Map<String,String> queryParameters;


    public TechnicalIndicatorWrapper(String indicatorName, String historicPeriod, String seriesType, int interval, TechnicalRegistry[][] rawTechnicalData, Map<String, String> queryParameters) {
        this.indicatorName = indicatorName;
        this.historicPeriod = historicPeriod;
        this.seriesType = seriesType;
        this.interval = interval;
        this.rawTechnicalData = rawTechnicalData;
        this.queryParameters = queryParameters;
    }

    public TechnicalIndicatorWrapper() {
    }

    @Override
    public String toString() {
        return "TechnicalIndicatorWrapper{" +
                "indicatorName='" + indicatorName + '\'' +
                ", historicPeriod='" + historicPeriod + '\'' +
                ", seriesType='" + seriesType + '\'' +
                ", interval=" + interval +
                ", rawTechnicalData=" + Arrays.toString(rawTechnicalData) +
                ", queryParameters=" + queryParameters +
                '}';
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getHistoricPeriod() {
        return historicPeriod;
    }

    public void setHistoricPeriod(String historicPeriod) {
        this.historicPeriod = historicPeriod;
    }

    public String getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public TechnicalRegistry[][] getRawTechnicalData() {
        return rawTechnicalData;
    }

    public void setRawTechnicalData(TechnicalRegistry[][] rawTechnicalData) {
        this.rawTechnicalData = rawTechnicalData;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }
}
