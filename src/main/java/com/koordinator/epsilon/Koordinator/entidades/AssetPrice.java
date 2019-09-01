package com.koordinator.epsilon.Koordinator.entidades;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "PreciosActivos")
public class AssetPrice {
   @Id
   private String id;
    private double price;
    @JsonIgnore
   private ArrayList<HistoricDataWrapper> historicData =new ArrayList<>();
    @JsonIgnore
    private String basePair;
    @JsonIgnore
    private String counterPair;
    @JsonIgnore
   private ArrayList<TechnicalIndicatorWrapper> indicatorList =new ArrayList<>();


    public AssetPrice(String id, double price, ArrayList<HistoricDataWrapper> historicData, String basePair, String counterPair, ArrayList<TechnicalIndicatorWrapper> indicatorList) {
        this.id = id;
        this.price = price;
        this.historicData = historicData;
        this.basePair = basePair;
        this.counterPair = counterPair;
        this.indicatorList = indicatorList;
    }

    public AssetPrice(String id, double price, ArrayList<HistoricDataWrapper> historicData, String basePair, String counterPair) {
        this.id = id;
        this.price = price;
        this.historicData = historicData;
        this.basePair = basePair;
        this.counterPair = counterPair;
    }

    public AssetPrice() {
    }

    @Override
    public String toString() {
        return "AssetPrice{" +
                "id='" + id + '\'' +
                ", price=" + price +
                ", historicData=" + historicData +
                ", basePair='" + basePair + '\'' +
                ", counterPair='" + counterPair + '\'' +
                '}';
    }

    public ArrayList<HistoricDataWrapper> getHistoricData() {
        return historicData;
    }

    public void modificarItemTipoHistoricoLista(int indice, HistoricDataWrapper item){
        this.historicData.set(indice,item);
    }

    public void modificarIndicadorTecnico(int indice, TechnicalIndicatorWrapper indicador){
        this.indicatorList.set(indice,indicador);
    }

    public void setHistoricData(ArrayList<HistoricDataWrapper> historicData) {
        this.historicData = historicData;
    }

    public void setListaIndicatoresTecnicos(ArrayList<TechnicalIndicatorWrapper>listadoIndicatores){
        this.indicatorList =listadoIndicatores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBasePair() {
        return basePair;
    }

    public void setBasePair(String basePair) {
        this.basePair = basePair;
    }

    public String getCounterPair() {
        return counterPair;
    }

    public void setCounterPair(String counterPair) {
        this.counterPair = counterPair;
    }

    public ArrayList<TechnicalIndicatorWrapper> getIndicatorList() {
        return indicatorList;
    }

    public void setIndicatorList(ArrayList<TechnicalIndicatorWrapper> indicatorList) {
        this.indicatorList = indicatorList;
    }
}
