package com.koordinator.epsilon.Koordinator.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@ApiModel(description="Technical indicator wrapper")
public class TechnicalIndicatorWrapper
{
    @ApiModelProperty(notes="Short name of the Technical Indicator",example = "RSI")
    private String indicatorName;
    @ApiModelProperty(notes="Historical data period",example = "4h")
    private String historicPeriod;
    @ApiModelProperty(notes="Type of the period series (open,high,close,low)",example = "open")
    private String seriesType;
    @ApiModelProperty(notes="Interval of the technical indicator (20,50,100,200,500...)",example = "20")
    private int interval;
    @ApiModelProperty(notes="Raw technical data",example = "{ numRegistry : 1 , historicPrice : 10234 , technicalIndicator : 22.43 , date : 1563700393 }")
    private TechnicalRegistry [][] rawTechnicalData;

    @ApiModelProperty(notes="Query parameters used for this indicator",example = "{ base : BTC , counter : USDT , historicInterval : 1h }",hidden = true)
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
