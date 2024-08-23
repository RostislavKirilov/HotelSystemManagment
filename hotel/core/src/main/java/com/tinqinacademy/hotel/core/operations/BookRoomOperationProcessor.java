package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.messages.RoomAlreadyReserved;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.entitites.User;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BookRoomOperationProcessor extends BaseOperation implements BookRoomOperation {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public BookRoomOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository, UserRepository userRepository, BookingRepository bookingRepository ) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Either<Errors, BookRoomOutput> process ( BookRoomInput input ) {
        // Логваме началото на операцията за проверка на налични стаи, за по-добра видимост при отстраняване на проблеми
        // и за следене на изпълнението на операцията.
        return Try.of(() -> {
                    log.info("Start booking room input: {}", input);
                    validate(input);

                    UUID roomId = UUID.fromString(input.getRoomId());
                    UUID userId = UUID.fromString(input.getUserId());

                    Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new RuntimeException(ExceptionMessages.ROOM_NOT_FOUND + " for ID: " + roomId));
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException(ExceptionMessages.USER_NOT_FOUND + " for ID: " + userId));


                    if (room.getStatus() == RoomStatus.OCCUPIED) {
                        throw new RoomAlreadyReserved();
                    }


                    if (!isRoomAvailable(roomId, input.getStartDate(), input.getEndDate())) {
                        throw new RoomAlreadyReserved();
                    }

                    roomRepository.updateRoomStatus(roomId, RoomStatus.OCCUPIED);

                    Booking booking = Booking.builder()
                            .user(user)
                            .room(room)
                            .startDate(input.getStartDate())
                            .endDate(input.getEndDate())
                            .build();
                    Booking savedBooking = bookingRepository.save(booking);
                    return BookRoomOutput.builder()
                            .bookingId(String.valueOf(savedBooking.getId()))
                            .build();
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private boolean isRoomAvailable ( UUID roomId, LocalDate startDate, LocalDate endDate ) {
        List<Booking> existingBookings = bookingRepository.findConflictingBookings(roomId, startDate, endDate);
        return existingBookings.isEmpty();
    }

    private Errors handleErrors(Throwable throwable) {
        ErrorOutput errorOutput = API.Match(throwable)
                .of(
                        API.Case(API.$(RoomAlreadyReserved.class::isInstance),
                                () -> new ErrorOutput(
                                        List.of(new Error(ExceptionMessages.ROOM_ALREADY_BOOKED)),
                                        HttpStatus.CONFLICT)),
                        caseRoomNotFound(throwable),
                        caseBedNotFound(throwable),
                        caseBookingNotFound(throwable),
                        caseUserNotFound(throwable),
                        caseInvalidInput(throwable),
                        defaultCase(throwable)
                );

        return new Errors(Optional.ofNullable(errorOutput)
                .map(ErrorOutput::getErrors)
                .orElse(List.of(new Error("Unknown error"))));
    }


}