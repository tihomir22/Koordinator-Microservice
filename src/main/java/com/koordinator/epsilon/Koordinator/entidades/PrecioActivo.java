package com.koordinator.epsilon.Koordinator.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "PreciosActivos")
public class PrecioActivo {
    //compuesto por dos activos
    @Id
    private String id;
    private double precio;
    @JsonIgnore
    private ArrayList<TipoDatoHistorico> listaDatosHora=new ArrayList<>();
    @JsonIgnore
    private String parBase;
    @JsonIgnore
    private String parContra;
    @JsonIgnore
    private ArrayList<IndicadorTecnico>listadoIndicatores=new ArrayList<>();


    public PrecioActivo(String id, double precio, ArrayList<TipoDatoHistorico> listaDatosHora, String parBase, String parContra, ArrayList<IndicadorTecnico> listadoIndicatores) {
        this.id = id;
        this.precio = precio;
        this.listaDatosHora = listaDatosHora;
        this.parBase = parBase;
        this.parContra = parContra;
        this.listadoIndicatores = listadoIndicatores;
    }

    public PrecioActivo(String id, double precio, ArrayList<TipoDatoHistorico> listaDatosHora, String parBase, String parContra) {
        this.id = id;
        this.precio = precio;
        this.listaDatosHora = listaDatosHora;
        this.parBase = parBase;
        this.parContra = parContra;
    }

    public PrecioActivo() {
    }

    @Override
    public String toString() {
        return "PrecioActivo{" +
                "id='" + id + '\'' +
                ", precio=" + precio +
                ", listaDatosHora=" + listaDatosHora +
                ", parBase='" + parBase + '\'' +
                ", parContra='" + parContra + '\'' +
                '}';
    }

    public ArrayList<TipoDatoHistorico> getListaDatosHora() {
        return listaDatosHora;
    }

    public void modificarItemTipoHistoricoLista(int indice,TipoDatoHistorico item){
        this.listaDatosHora.set(indice,item);
    }

    public void modificarIndicadorTecnico(int indice,IndicadorTecnico indicador){
        this.listadoIndicatores.set(indice,indicador);
    }

    public void setListaDatosHora(ArrayList<TipoDatoHistorico> listaDatosHora) {
        this.listaDatosHora = listaDatosHora;
    }

    public void setListaIndicatoresTecnicos(ArrayList<IndicadorTecnico>listadoIndicatores){
        this.listadoIndicatores=listadoIndicatores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getParBase() {
        return parBase;
    }

    public void setParBase(String parBase) {
        this.parBase = parBase;
    }

    public String getParContra() {
        return parContra;
    }

    public void setParContra(String parContra) {
        this.parContra = parContra;
    }

    public ArrayList<IndicadorTecnico> getListadoIndicatores() {
        return listadoIndicatores;
    }

    public void setListadoIndicatores(ArrayList<IndicadorTecnico> listadoIndicatores) {
        this.listadoIndicatores = listadoIndicatores;
    }
}
