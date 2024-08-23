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

    public Errors(List<Error> errors) {
        this.errors = errors;
        this.message = errors.stream()
                .map(Error::getMessage)
                .collect(Collectors.joining(", "));
    }

    // Добавяне на статичен метод за създаване на Errors от съобщение
    public static Errors of(String message) {
        return new Errors(List.of(Error.builder().message(message).build()));
    }
}
