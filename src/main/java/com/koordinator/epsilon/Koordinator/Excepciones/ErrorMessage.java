package com.koordinator.epsilon.Koordinator.Excepciones;

import java.util.Date;

public class ErrorMessage {
    private Date date;
    private String message;

    public ErrorMessage(Date date, String message) {
        this.date = date;
        this.message = message;
    }

    public ErrorMessage() {
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "date=" + date +
                ", message='" + message + '\'' +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
