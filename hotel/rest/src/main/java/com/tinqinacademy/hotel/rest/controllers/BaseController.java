package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class BaseController {
    public ResponseEntity<?> getOutput(Either<Errors, OperationOutput> result, HttpStatus successStatus) {
        return result.fold(
                errorOutput -> new ResponseEntity<>(errorOutput, HttpStatus.BAD_REQUEST),
                output -> new ResponseEntity<>(output, successStatus)
        );
    }
}
