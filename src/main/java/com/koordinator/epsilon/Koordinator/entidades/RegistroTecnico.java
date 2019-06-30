package com.koordinator.epsilon.Koordinator.entidades;

import java.util.Date;

public class RegistroTecnico {
    private int numRegistry;
    private double historicPrice;
    private double technicalIndicator;
    private Date date;

    @Override
    public String toString() {
        return "RegistroTecnico{" +
                "numRegistry=" + numRegistry +
                ", historicPrice=" + historicPrice +
                ", technicalIndicator=" + technicalIndicator +
                ", date=" + date +
                '}';
    }

    public RegistroTecnico(int numRegistry, double historicPrice, double technicalIndicator, Date date) {
        this.numRegistry = numRegistry;
        this.historicPrice = historicPrice;
        this.technicalIndicator = technicalIndicator;
        this.date = date;
    }

    public RegistroTecnico() {
    }

    public int getNumRegistry() {
        return numRegistry;
    }

    public void setNumRegistry(int numRegistry) {
        this.numRegistry = numRegistry;
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
