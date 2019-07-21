package com.koordinator.epsilon.Koordinator.entidades;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Model for retrieving aditional info about a crypto-asset")
public class Asset {
    @ApiModelProperty(notes="ID of the cryptocurrency stored in the DB",example = "1")
    private String id;
    @ApiModelProperty(notes="Name of the cryptocurrency (without it pair)",example = "BTC")
    private String name;
    @ApiModelProperty(notes="Description of the cryptocurrency",example = "Bitcoin uses peer-to-peer technology to operate with no central authority or banks; managing transactions and the issuing of bitcoins is carried out collectively by the network.")
    private String desc;

    public Asset(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
