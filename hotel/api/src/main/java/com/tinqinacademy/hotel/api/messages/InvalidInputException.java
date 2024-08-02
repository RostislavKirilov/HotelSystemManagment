package com.tinqinacademy.hotel.api.messages;

import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import lombok.Getter;

@Getter
public class InvalidInputException extends RuntimeException {
    private final ErrorOutput errorOutput;

    public InvalidInputException(ErrorOutput errorOutput) {
        super(errorOutput.getMessage());
        this.errorOutput = errorOutput;
    }
}
