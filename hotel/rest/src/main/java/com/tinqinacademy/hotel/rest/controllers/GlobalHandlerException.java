package com.tinqinacademy.hotel.rest.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.tinqinacademy.hotel.core.services.ExceptionServiceImpl;

@ControllerAdvice
public class GlobalHandlerException {

    private final ExceptionServiceImpl exceptionService;

    @Autowired
    public GlobalHandlerException( ExceptionServiceImpl exceptionService) {
        this.exceptionService = exceptionService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex){
            String response = ex.getMessage();
            if (ex.getCause()!=null)
                response+="\nCause: "+ex.getCause();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
