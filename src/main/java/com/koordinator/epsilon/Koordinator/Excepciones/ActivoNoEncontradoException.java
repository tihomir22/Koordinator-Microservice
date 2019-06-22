package com.koordinator.epsilon.Koordinator.Excepciones;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ActivoNoEncontradoException extends RuntimeException {

    public ActivoNoEncontradoException(String message) {
        super(message);
    }
}
