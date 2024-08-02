package com.tinqinacademy.hotel.api.errors;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Errors {

    private List<Error> errors;

    private String message;
    private String field;

    public Errors ( String unexpectedError ) {
        this.message = unexpectedError;
        this.errors = List.of();
    }

    public String getMessage() {
        if (errors == null || errors.isEmpty()) {
            return "";
        }
        return errors.stream()
                .map(Error::getMessage)
                .collect(Collectors.joining(", "));
    }

    public static Errors of(String message) {
        return new Errors(message);
    }
}
