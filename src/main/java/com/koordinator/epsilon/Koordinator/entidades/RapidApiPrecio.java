package com.koordinator.epsilon.Koordinator.entidades;

import java.util.Date;

public class RapidApiPrecio {
    private boolean success;
    private String source;
    private String request_date;
    private String from_quantity;
    private String from_symbol;
    private String from_name;
    private String to_symbol;
    private String to_name;
    private double to_quantity;


    public RapidApiPrecio() {
    }

    public RapidApiPrecio(boolean success, String source, String request_date, String from_quantity, String from_symbol, String from_name, String to_symbol, String to_name, double to_quantity) {
        this.success = success;
        this.source = source;
        this.request_date = request_date;
        this.from_quantity = from_quantity;
        this.from_symbol = from_symbol;
        this.from_name = from_name;
        this.to_symbol = to_symbol;
        this.to_name = to_name;
        this.to_quantity = to_quantity;
    }

    @Override
    public String toString() {
        return "RapidApiPrecio{" +
                "success=" + success +
                ", source='" + source + '\'' +
                ", request_date=" + request_date +
                ", from_quantity='" + from_quantity + '\'' +
                ", from_symbol='" + from_symbol + '\'' +
                ", from_name='" + from_name + '\'' +
                ", to_symbol='" + to_symbol + '\'' +
                ", to_name='" + to_name + '\'' +
                ", to_quantity=" + to_quantity +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getFrom_quantity() {
        return from_quantity;
    }

    public void setFrom_quantity(String from_quantity) {
        this.from_quantity = from_quantity;
    }

    public String getFrom_symbol() {
        return from_symbol;
    }

    public void setFrom_symbol(String from_symbol) {
        this.from_symbol = from_symbol;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo_symbol() {
        return to_symbol;
    }

    public void setTo_symbol(String to_symbol) {
        this.to_symbol = to_symbol;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public double getTo_quantity() {
        return to_quantity;
    }

    public void setTo_quantity(double to_quantity) {
        this.to_quantity = to_quantity;
    }
}
