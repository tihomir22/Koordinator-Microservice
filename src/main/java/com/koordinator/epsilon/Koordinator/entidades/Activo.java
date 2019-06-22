package com.koordinator.epsilon.Koordinator.entidades;

public class Activo {
    private String id;
    private String nombre;
    private String desc;

    public Activo(String id, String nombre, String desc) {
        this.id = id;
        this.nombre = nombre;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Activo{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
