package com.koordinator.epsilon.Koordinator.entidades;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Raw History data model")
public class HistoricData {
    //hourly daily minute etc
    @ApiModelProperty(notes="Open time of this candle",example = "1563700675 ")
    private String open_time;
    @ApiModelProperty(notes="Open historical price",example = "10500.20")
    private double open;
    @ApiModelProperty(notes="High historical price",example = "11770.20")
    private double high;
    @ApiModelProperty(notes="Low historical price",example = "9575.10")
    private double low;
    @ApiModelProperty(notes="Close historical price",example = "10665.10")
    private double close;
    @ApiModelProperty(notes="Volume",example = "3410115.10")
    private double volume;


    public HistoricData() {
    }

    public HistoricData(String open_time, double open, double high, double low, double close, double volume) {
        this.open_time = open_time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "HistoricData{" +
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
