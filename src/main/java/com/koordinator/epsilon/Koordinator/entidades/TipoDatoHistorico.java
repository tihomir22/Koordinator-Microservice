package com.koordinator.epsilon.Koordinator.entidades;

import java.util.ArrayList;

public class TipoDatoHistorico {
    private String periodo;
    private int numRegistros;
    private ArrayList<DatoHistorico> dato;

    public TipoDatoHistorico(String periodo, ArrayList<DatoHistorico> dato) {
        this.periodo = periodo;
        this.dato = dato;
    }

    public TipoDatoHistorico() {
    }

    @Override
    public String toString() {
        return "TipoDatoHistorico{" +
                "periodo='" + periodo + '\'' +
                ", numRegistros=" + numRegistros +
                ", dato=" + dato +
                '}';
    }

    public int getNumRegistros() {
        return numRegistros;
    }

    public void setNumRegistros(int numRegistros) {
        this.numRegistros = numRegistros;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public ArrayList<DatoHistorico> getDato() {
        return dato;
    }

    public void setDato(ArrayList<DatoHistorico> dato) {
        this.dato = dato;
    }
}
