package com.koordinator.epsilon.Koordinator.entidades;


import java.util.Date;

public class TechnicalRegistry {
    private int numRegistry;
    private String extra;
    private double historicPrice;
    private double technicalIndicator;
    private Date date;


    public TechnicalRegistry(int numRegistry, String extra, double historicPrice, double technicalIndicator, Date date) {
        this.numRegistry = numRegistry;
        this.extra = extra;
        this.historicPrice = historicPrice;
        this.technicalIndicator = technicalIndicator;
        this.date = date;
    }

    public TechnicalRegistry() {
    }

    @Override
    public String toString() {
        return "TechnicalRegistry{" +
                "numRegistry=" + numRegistry +
                ", extra='" + extra + '\'' +
                ", historicPrice=" + historicPrice +
                ", technicalIndicator=" + technicalIndicator +
                ", date=" + date +
                '}';
    }

    public int getNumRegistry() {
        return numRegistry;
    }

    public void setNumRegistry(int numRegistry) {
        this.numRegistry = numRegistry;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public double getHistoricPrice() {
        return historicPrice;
    }

    public void setHistoricPrice(double historicPrice) {
        this.historicPrice = historicPrice;
    }

    public double getTechnicalIndicator() {
        return technicalIndicator;
    }

    public void setTechnicalIndicator(double technicalIndicator) {
        this.technicalIndicator = technicalIndicator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
