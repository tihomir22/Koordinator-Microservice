package com.koordinator.epsilon.Koordinator.Respuestas;

import com.koordinator.epsilon.Koordinator.entidades.AssetPrice;

public class RespuestaPersonalizada extends RespuestaBase {

    private AssetPrice data;

    public RespuestaPersonalizada(int estado, String mensaje, AssetPrice data) {
        super(estado, mensaje);
        this.data = data;
    }

    public RespuestaPersonalizada(AssetPrice data) {
        this.data = data;
    }

    public RespuestaPersonalizada(int estado, String mensaje) {
        super(estado, mensaje);
    }

    public RespuestaPersonalizada() {
    }

    public AssetPrice getData() {
        return data;
    }

    public void setData(AssetPrice data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespuestaPersonalizada{" +
                "data=" + data +
                '}';
    }
}
