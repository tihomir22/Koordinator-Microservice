package com.koordinator.epsilon.Koordinator.entidades;

import io.swagger.annotations.ApiModel;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "FormularioContacto")
@ApiModel(description = "Model for saving the contact data from the website")
public class FormularioContactoWeb {
    private String nombre;
    private String email;
    private String mensaje;
    private boolean leido = false;

    @Override
    public String toString() {
        return "FormularioContactoWeb{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", leido=" + leido +
                '}';
    }

    public FormularioContactoWeb(String nombre, String email, String mensaje) {
        this.nombre = nombre;
        this.email = email;
        this.mensaje = mensaje;
    }

    public FormularioContactoWeb() {
    }

    public boolean comprobarValidez() {
        return this.nombre != null && this.email != null && this.mensaje != null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}


