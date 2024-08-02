package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.VisitorRegistration.VisitorRegistrationInput;
import com.tinqinacademy.hotel.api.operations.VisitorRegistration.VisitorRegistrationOperation;
import com.tinqinacademy.hotel.api.operations.VisitorRegistration.VisitorRegistrationOutput;
import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Guest;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VisitorRegistrationOperationProcessor extends BaseOperation implements VisitorRegistrationOperation {
    public VisitorRegistrationOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository, GuestRepository guestRepository, BookingRepository bookingRepository ) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }


    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;



    @Override
    @Transactional
    public Either<Errors, VisitorRegistrationOutput> process(VisitorRegistrationInput input) {
        return Try.of(() -> {
                    log.info("Start visitor registration input: {}", input);
                    validate(input);

                    UUID roomId = input.getRoomId();
                    Optional<Room> roomOpt = roomRepository.findById(roomId);

                    if (roomOpt.isEmpty()) {
                        throw new RuntimeException("Room not found for ID: " + roomId);
                    }

                    Room room = roomOpt.get();
                    if (!isRoomAvailable(roomId, input.getStartDate(), input.getEndDate())) {
                        throw new RuntimeException("Room is not available for the selected dates");
                    }

                    Guest guest = Guest.builder()
                            .firstName(input.getFirstName())
                            .lastName(input.getLastName())
                            .startDate(input.getStartDate())
                            .endDate(input.getEndDate())
                            .phoneNo(input.getPhoneNo())
                            .idCardNo(input.getIdCardNo())
                            .idCardValidity(input.getIdCardValidity())
                            .idCardIssueAuthority(input.getIdCardIssueAuthority())
                            .idCardIssueDate(input.getIdCardIssueDate())
                            .room(room)
                            .build();
                    guestRepository.save(guest);

                    log.info("Visitor registered successfully for roomId={}", roomId);
                    return new VisitorRegistrationOutput();
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private boolean isRoomAvailable( UUID roomId, LocalDate startDate, LocalDate endDate) {
        List<Booking> existingBookings = bookingRepository.findConflictingBookings(roomId, startDate, endDate);
        return existingBookings.isEmpty();
    }

    private Errors handleErrors(Throwable throwable) {
        return new Errors(List.of(new Error(throwable.getMessage())).toString());
    }
}
