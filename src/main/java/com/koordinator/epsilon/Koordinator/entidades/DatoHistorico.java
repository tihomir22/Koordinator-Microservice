package com.koordinator.epsilon.Koordinator.entidades;

import java.sql.Timestamp;

public class DatoHistorico {
    //hourly daily minute etc

    private String open_time;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;


    public DatoHistorico() {
    }

    public DatoHistorico( String open_time, double open, double high, double low, double close, double volume) {
        this.open_time = open_time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "DatoHistorico{" +
                ", open_time=" + open_time +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }



    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
