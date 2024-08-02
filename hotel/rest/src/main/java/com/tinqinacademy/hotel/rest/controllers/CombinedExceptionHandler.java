package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.exceptions.CustomExceptionHandler;
import com.tinqinacademy.hotel.api.exceptions.ErrorWrapper;
import com.tinqinacademy.hotel.api.exceptions.TestingException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CombinedExceptionHandler {

    private final Map<Class<? extends Exception>, CustomExceptionHandler<? extends Exception>> exceptionHandlers = new HashMap<>();

    public CombinedExceptionHandler () {
        registerHandler(MethodArgumentNotValidException.class, this::handleValidationException);
        registerHandler(TestingException.class, this::handleTestingException);
        registerHandler(Exception.class, this::handleGenericException);
    }

    private <T extends Exception> void registerHandler ( Class<T> type, CustomExceptionHandler<T> handler ) {
        exceptionHandlers.put(type, handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends Exception> ErrorWrapper handleException ( T ex ) {
        CustomExceptionHandler<T> handler = (CustomExceptionHandler<T>) exceptionHandlers.get(ex.getClass());
        if (handler == null) {
            handler = (CustomExceptionHandler<T>) exceptionHandlers.get(Exception.class);
        }
        return handler.handleException(ex);
    }

    private ErrorWrapper handleValidationException ( MethodArgumentNotValidException ex ) {
        List<ErrorWrapper> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .map(message -> ErrorWrapper.builder()
                        .errors(List.of(ErrorWrapper.builder()
                                .errors(new ArrayList<>())
                                .build()))
                        .build())
                .collect(Collectors.toList());

        return ErrorWrapper.builder()
                .errors(errors)
                .build();
    }

    private ErrorWrapper handleTestingException ( TestingException ex ) {
        return ErrorWrapper.builder()
                .errors(List.of(ErrorWrapper.builder()
                        .errors(new ArrayList<>())
                        .build()))
                .build();
    }

    private ErrorWrapper handleGenericException ( Exception ex ) {
        return ErrorWrapper.builder()
                .errors(List.of(ErrorWrapper.builder()
                        .errors(new ArrayList<>())
                        .build()))
                .build();
    }
}
