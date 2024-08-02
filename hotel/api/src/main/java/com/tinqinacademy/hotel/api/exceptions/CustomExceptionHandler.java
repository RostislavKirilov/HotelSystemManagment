package com.tinqinacademy.hotel.api.exceptions;

public interface CustomExceptionHandler<T extends Exception> {
    ErrorWrapper handleException(T ex);
}
