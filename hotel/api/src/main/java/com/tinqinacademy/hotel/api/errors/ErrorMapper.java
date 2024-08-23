package com.tinqinacademy.hotel.api.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Component
public class ErrorMapper {

    public <T extends Throwable> ErrorOutput map(T throwable, HttpStatus httpStatus) {
        return ErrorOutput.builder()
                .errors(List.of(Error.builder()
                        .message(throwable.getMessage())
                        .build()))
                .status(httpStatus)
                .build();
    }

    public ErrorOutput mapErrors(List<Errors> errors, HttpStatus httpStatus) {
        List<Error> errorList = errors.stream()
                .flatMap(e -> e.getErrors().stream())
                .collect(Collectors.toList());

        return ErrorOutput.builder()
                .errors(errorList)
                .status(httpStatus)
                .build();
    }
}