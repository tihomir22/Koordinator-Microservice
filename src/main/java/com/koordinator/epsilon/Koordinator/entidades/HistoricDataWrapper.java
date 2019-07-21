package com.koordinator.epsilon.Koordinator.entidades;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;

@ApiModel(description="Wrapper for the historic data")
public class HistoricDataWrapper {
    @ApiModelProperty(notes="Period of the historic data",example = "1h")
    private String period;
    @ApiModelProperty(notes="Number of historic entries returned",example = "500")
    private int numRecords;
    //example = "[{ "+"open_time"+": 1563698748 , "+"open"+" : 20.2 , "+"high"+" : 24.8 , "+"low"+" : 19.8 , "+"close"+" : 22.1 , "+"volume"+" : 2343553 },]"
    @ApiModelProperty(notes="List of Historic Data objects")
    private ArrayList<HistoricData> rawHistoricData;

    public HistoricDataWrapper(String period, ArrayList<HistoricData> rawHistoricData) {
        this.period = period;
        this.rawHistoricData = rawHistoricData;
    }

    public HistoricDataWrapper() {
    }

    @Override
    public String toString() {
        return "HistoricDataWrapper{" +
                "period='" + period + '\'' +
                ", numRecords=" + numRecords +
                ", rawHistoricData=" + rawHistoricData +
                '}';
    }

    public int getNumRecords() {
        return numRecords;
    }

    public void setNumRecords(int numRecords) {
        this.numRecords = numRecords;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public ArrayList<HistoricData> getRawHistoricData() {
        return rawHistoricData;
    }

    public void setRawHistoricData(ArrayList<HistoricData> rawHistoricData) {
        this.rawHistoricData = rawHistoricData;
    }
}
