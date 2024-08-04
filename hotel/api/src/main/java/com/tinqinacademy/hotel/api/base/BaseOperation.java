package com.tinqinacademy.hotel.api.base;

import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.exceptions.*;
import com.tinqinacademy.hotel.api.messages.BookingNotFoundException;
import com.tinqinacademy.hotel.api.messages.InvalidInputException;
import com.tinqinacademy.hotel.api.messages.RoomAlreadyReserved;
import com.tinqinacademy.hotel.api.messages.RoomNotFoundException;
import io.vavr.API;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class BaseOperation {
    protected final Validator validator;
    protected final ConversionService conversionService;
    protected final ErrorMapper errorMapper;

    /**
     * Валидация на входящи данни.
     * Той проверява дали данните отговарят на зададените ограничения и правила за валидност.
     * Ако има нарушения - `InvalidInputException`.
     */

    public <T extends OperationInput> void validate(T input) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);

        if (!violations.isEmpty()) {
            List<Errors> errorList = new ArrayList<>();
            violations.forEach(violation -> {
                Errors error = Errors.builder()
                        .message(violation.getMessage())
                        .field(violation.getPropertyPath().toString())
                        .build();
                errorList.add(error);
            });
            throw new InvalidInputException(errorMapper.mapErrors(errorList, HttpStatus.BAD_REQUEST));
        }
    }

    protected API.Match.Case<RoomNotFoundException, ErrorOutput> caseRoomNotFound(Throwable cause) {
        return API.Case($(instanceOf(RoomNotFoundException.class)), errorMapper.map(cause, HttpStatus.NOT_FOUND));
    }

    protected API.Match.Case<BedNotFoundException, ErrorOutput> caseBedNotFound(Throwable cause) {
        return API.Case($(instanceOf(BedNotFoundException.class)), errorMapper.map(cause, HttpStatus.NOT_FOUND));
    }

    protected API.Match.Case<BookingNotFoundException, ErrorOutput> caseBookingNotFound(Throwable cause) {
        return API.Case($(instanceOf(BookingNotFoundException.class)), errorMapper.map(cause, HttpStatus.NOT_FOUND));
    }

    protected API.Match.Case<UserNotFoundException, ErrorOutput> caseUserNotFound(Throwable cause) {
        return API.Case($(instanceOf(UserNotFoundException.class)), errorMapper.map(cause, HttpStatus.NOT_FOUND));
    }

    protected API.Match.Case<InvalidInputException, ErrorOutput> caseInvalidInput(Throwable cause) {
        return API.Case($(instanceOf(InvalidInputException.class)), errorMapper.map(cause, HttpStatus.BAD_REQUEST));
    }

    protected API.Match.Case<RuntimeException, ErrorOutput> defaultCase(Throwable cause) {
        return API.Case($(), errorMapper.map(cause, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    protected API.Match.Case<RoomAlreadyReserved, ErrorOutput> caseRoomAlreadyReserved(Throwable cause) {
        return API.Case($(instanceOf(RoomAlreadyReserved.class)), errorMapper.map(cause, HttpStatus.BAD_REQUEST));
    }
}
