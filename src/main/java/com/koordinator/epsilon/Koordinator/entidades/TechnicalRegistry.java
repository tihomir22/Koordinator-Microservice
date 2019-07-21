package com.koordinator.epsilon.Koordinator.entidades;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(description="Raw Technical Indicator Record")
public class TechnicalRegistry {
    @ApiModelProperty(notes="Number of the record , similar to id",example = "1")
    private int numRegistry;
    @ApiModelProperty(notes="Historic price attached to the technical indicator registry below",example = "10343.34")
    private double historicPrice;
    @ApiModelProperty(notes="Technical indicator registry",example = "10255.14")
    private double technicalIndicator;
    @ApiModelProperty(notes="Date of the technical registry ", example = "1563700393")
    private Date date;

    @Override
    public String toString() {
        return "TechnicalRegistry{" +
                "numRegistry=" + numRegistry +
                ", historicPrice=" + historicPrice +
                ", technicalIndicator=" + technicalIndicator +
                ", date=" + date +
                '}';
    }

    public TechnicalRegistry(int numRegistry, double historicPrice, double technicalIndicator, Date date) {
        this.numRegistry = numRegistry;
        this.historicPrice = historicPrice;
        this.technicalIndicator = technicalIndicator;
        this.date = date;
    }

    public TechnicalRegistry() {
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
