package com.koordinator.epsilon.Koordinator.Respuestas;

import com.koordinator.epsilon.Koordinator.entidades.TechnicalRegistry;

import java.util.ArrayList;

public class RespuestaIndicadorTecnico extends RespuestaBase{
    private TechnicalRegistry[][]listaTecnico;

    public RespuestaIndicadorTecnico(int estado, String mensaje, TechnicalRegistry[][] listaTecnico) {
        super(estado, mensaje);
        this.listaTecnico = listaTecnico;
    }


    public TechnicalRegistry[][] getListaTecnico() {
        return listaTecnico;
    }

    public void setListaTecnico(TechnicalRegistry[][] listaTecnico) {
        this.listaTecnico = listaTecnico;
    }

    @Override
    public String toString() {
        return "RespuestaIndicadorTecnico{" +
                "listaTecnico=" + listaTecnico +
                '}';
    }
}
