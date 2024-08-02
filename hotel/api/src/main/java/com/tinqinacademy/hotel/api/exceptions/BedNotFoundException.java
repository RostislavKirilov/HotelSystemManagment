package com.tinqinacademy.hotel.api.exceptions;

import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BedNotFoundException extends RuntimeException{
    private final String message = ExceptionMessages.BED_NOT_FOUND;
}
