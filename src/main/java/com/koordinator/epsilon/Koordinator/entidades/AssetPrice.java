package com.koordinator.epsilon.Koordinator.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "PreciosActivos")
@ApiModel(description="Model for retrieving general price-data about a cryptocurrency")
public class AssetPrice {
    //compuesto por dos activos
    @Id
    @ApiModelProperty(notes="Id of the asset, formed by the base pair with his counter",example = "BTCUSDT")
    private String id;
    @ApiModelProperty(notes="Live price of the asset",example = "10523,23")
    private double price;
    @JsonIgnore
    @ApiModelProperty(notes="Records of historic wrapped data",example = "[{ period : 1h , numRecords : 500 , rawHistoricData : [{ open_time: 1563698748 , open : 20.2 , high : 24.8 , low : 19.8 , close : 22.1 , volume : 2343553 },]}]")
    private ArrayList<HistoricDataWrapper> historicData =new ArrayList<>();
    @JsonIgnore
    @ApiModelProperty(notes="Base pair of the asset",example = "BTC")
    private String basePair;
    @JsonIgnore
    @ApiModelProperty(notes="Counter pair of the asset",example = "USDT")
    private String counterPair;
    @JsonIgnore
    @ApiModelProperty(notes="Records of technical indicators associated with the cryptocurrency",example = "USD")
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
