package com.koordinator.epsilon.Koordinator.Respuestas;

import com.koordinator.epsilon.Koordinator.entidades.TipoDatoHistorico;

public class RespuestaPersonalizadaHistorico extends RespuestaBase{

    private TipoDatoHistorico data;

    public RespuestaPersonalizadaHistorico(int estado, String mensaje, TipoDatoHistorico data) {
        super(estado, mensaje);
        this.data = data;
    }

    public RespuestaPersonalizadaHistorico(TipoDatoHistorico data) {
        this.data = data;
    }

    public RespuestaPersonalizadaHistorico() {
    }

    public RespuestaPersonalizadaHistorico(int estado, String mensaje) {
        super(estado, mensaje);
    }

    public TipoDatoHistorico getData() {
        return data;
    }

    public void setData(TipoDatoHistorico data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespuestaPersonalizadaHistorico{" +
                "data=" + data +
                '}';
    }
}
