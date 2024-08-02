package com.tinqinacademy.hotel.api.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;

public class TestingException extends RuntimeException {

    public TestingException(String message){
        super(message);
    }
}
