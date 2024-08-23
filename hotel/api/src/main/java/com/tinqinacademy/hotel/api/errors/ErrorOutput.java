package com.tinqinacademy.hotel.api.errors;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorOutput {
    private List<Error> errors;
    private HttpStatus status;

    public String getMessage() {
        if (errors == null || errors.isEmpty()) {
            return "Unknown error";
        }
        return errors.stream()
                .map(Error::getMessage)
                .collect(Collectors.joining(", "));
    }
}