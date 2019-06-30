package com.koordinator.epsilon.Koordinator.entidades;

import java.util.ArrayList;

public class IndicadorTecnico
{
    private String indicatorName;
    private String periodoDatosHistoricos;
    private String tipoSeries;
    private int intervalo;
    private ArrayList<RegistroTecnico>datosTecnicos=new ArrayList<>();

    public IndicadorTecnico(String indicatorName, String periodoDatosHistoricos, String tipoSeries, int intervalo, ArrayList<RegistroTecnico> datosTecnicos) {
        this.indicatorName = indicatorName;
        this.periodoDatosHistoricos = periodoDatosHistoricos;
        this.tipoSeries = tipoSeries;
        this.intervalo = intervalo;
        this.datosTecnicos = datosTecnicos;
    }


    public IndicadorTecnico() {
    }

    @Override
    public String toString() {
        return "IndicadorTecnico{" +
                "indicatorName='" + indicatorName + '\'' +
                ", periodoDatosHistoricos='" + periodoDatosHistoricos + '\'' +
                ", tipoSeries='" + tipoSeries + '\'' +
                ", intervalo=" + intervalo +
                ", datosTecnicos=" + datosTecnicos +
                '}';
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getPeriodoDatosHistoricos() {
        return periodoDatosHistoricos;
    }

    public void setPeriodoDatosHistoricos(String periodoDatosHistoricos) {
        this.periodoDatosHistoricos = periodoDatosHistoricos;
    }

    public String getTipoSeries() {
        return tipoSeries;
    }

    public void setTipoSeries(String tipoSeries) {
        this.tipoSeries = tipoSeries;
    }

    public int getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }

    public ArrayList<RegistroTecnico> getDatosTecnicos() {
        return datosTecnicos;
    }

    public void setDatosTecnicos(ArrayList<RegistroTecnico> datosTecnicos) {
        this.datosTecnicos = datosTecnicos;
    }
}
