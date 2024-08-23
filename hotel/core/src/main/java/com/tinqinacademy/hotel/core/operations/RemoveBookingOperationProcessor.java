package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.removebooking.RemoveBookingInput;
import com.tinqinacademy.hotel.api.operations.removebooking.RemoveBookingOperation;
import com.tinqinacademy.hotel.api.operations.removebooking.RemoveBookingOutput;
import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RemoveBookingOperationProcessor extends BaseOperation implements RemoveBookingOperation {

    private final BookingRepository bookingRepository;

    public RemoveBookingOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, BookingRepository bookingRepository ) {
        super(validator, conversionService, errorMapper);
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Either<Errors, RemoveBookingOutput> process(RemoveBookingInput input) {
        // Логваме началото на операцията за проверка на налични стаи, за по-добра видимост при отстраняване на проблеми
        // и за следене на изпълнението на операцията.
        return Try.of(() -> {
                    log.info("Start removing booking input: {}", input);
                    validateInput(input);

                    UUID bookingId = UUID.fromString(input.getBookingId());
                    Booking booking = bookingRepository.findById(bookingId)
                            .orElseThrow(() -> new RuntimeException(ExceptionMessages.BOOKING_NOT_FOUND + " for ID: " + bookingId));

                    bookingRepository.deleteById(bookingId);

                    RemoveBookingOutput output = new RemoveBookingOutput();
                    log.info("End removing booking output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(this::createErrors);
    }

    private void validateInput(RemoveBookingInput input) {
        if (input.getBookingId() == null) {
            throw new RuntimeException(ExceptionMessages.INVALID_BOOKING_ID);
        }
    }

    private Errors createErrors(Throwable throwable) {
        String message = throwable.getMessage() != null ? throwable.getMessage() : "An unexpected error occurred.";
        Error error = Error.builder()
                .message(message)
                .build();
        return new Errors(List.of(error));
    }
}
