package com.koordinator.epsilon.Koordinator.Excepciones;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Date;

@ControllerAdvice
@RestController
public class CustomizeResponseEntityExcepcionHandler extends ResponseEntityExceptionHandler {


   /* @ExceptionHandler(ActivoNoEncontradoException.class)
    public final ResponseEntity<Object> handleActivoNoEncontradoException(Exception ex, WebRequest request){
        ExceptionResponse exceptionResponse= new ExceptionResponse(new Date(),"Dembow loco",request.getDescription(false));
        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request){
       ExceptionResponse exceptionResponse= new ExceptionResponse(new Date(),ex.getMessage(),request.getDescription(false));
       return new ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ExceptionResponse exceptionResponse= new ExceptionResponse(new Date(),"La validacion ha fallado",ex.getBindingResult().toString());
       return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }*/

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAnyException(Exception ex, WebRequest request) {
        String errorMessageDesc = ex.getLocalizedMessage();
        if (errorMessageDesc == null) errorMessageDesc = ex.getMessage();
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDesc);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex, WebRequest request) {
        String errorMessageDesc = ex.getLocalizedMessage();
        if (errorMessageDesc == null) errorMessageDesc = ex.getMessage();
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDesc);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ActivoNoEncontradoException.class})
    public ResponseEntity<Object> handleActivoNoEncontradoException(ActivoNoEncontradoException ex, WebRequest request) {
        String errorMessageDesc = ex.getLocalizedMessage();
        if (errorMessageDesc == null) errorMessageDesc = ex.getMessage();
        ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDesc);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
