package com.koordinator.epsilon.Koordinator.entidades;

import java.util.ArrayList;

public class HistoricDataWrapper {

    private String period;

    private int numRecords;

    private ArrayList<HistoricData> rawHistoricData;

    private String startTime;
    private String endTime;
    private int limit;

    public HistoricDataWrapper(String period, ArrayList<HistoricData> rawHistoricData) {
        this.period = period;
        this.rawHistoricData = rawHistoricData;
    }

    public HistoricDataWrapper() {
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public void setNumRecords(int numRecords) {
        this.numRecords = numRecords;
    }

    public ArrayList<HistoricData> getRawHistoricData() {
        return rawHistoricData;
    }

    public void setRawHistoricData(ArrayList<HistoricData> rawHistoricData) {
        this.rawHistoricData = rawHistoricData;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
