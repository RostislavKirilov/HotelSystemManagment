package com.tinqinacademy.hotel.api.exceptions;

import com.tinqinacademy.hotel.api.messages.ExceptionMessages;

public class UserNotFoundException extends RuntimeException{
    private final String message = ExceptionMessages.USER_NOT_FOUND;
}
