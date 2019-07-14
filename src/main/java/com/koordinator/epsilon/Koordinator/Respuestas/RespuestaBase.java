package com.koordinator.epsilon.Koordinator.Respuestas;

public class RespuestaBase {
    private int estado;
    private String mensaje;

    public RespuestaBase(int estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public RespuestaBase() {
    }

    @Override
    public String toString() {
        return "RespuestaBase{" +
                "estado=" + estado +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
