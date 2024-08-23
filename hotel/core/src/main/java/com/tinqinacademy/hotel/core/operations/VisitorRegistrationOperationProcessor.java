package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.visitorregistration.input.VisitorRegistrationInput;
import com.tinqinacademy.hotel.api.operations.visitorregistration.operation.VisitorRegistrationOperation;
import com.tinqinacademy.hotel.api.operations.visitorregistration.output.VisitorRegistrationOutput;
import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Guest;
import com.tinqinacademy.hotel.persistence.entitites.GuestRoom;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRoomRepository;
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
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final GuestRoomRepository guestRoomRepository; // ново

    public VisitorRegistrationOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository, GuestRepository guestRepository, BookingRepository bookingRepository, GuestRoomRepository guestRoomRepository) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
        this.guestRoomRepository = guestRoomRepository;
    }

    @Override
    @Transactional
    public Either<Errors, VisitorRegistrationOutput> process(VisitorRegistrationInput input) {
        return Try.of(() -> {
                    log.info("Start visitor registration with input: {}", input);
                    validate(input);

                    UUID roomId = UUID.fromString(input.getRoomId());
                    log.info("Checking for room with ID: {}", roomId);

                    Optional<Room> roomOpt = roomRepository.findById(roomId);
                    if (roomOpt.isEmpty()) {
                        log.error("Room not found for ID: {}", roomId);
                        throw new RuntimeException("Room not found for ID: " + roomId);
                    }

                    Room room = roomOpt.get();
                    log.info("Found room: {}", room);

                    if (!isRoomAvailable(roomId, input.getStartDate(), input.getEndDate())) {
                        log.error("Room is not available for the selected dates");
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
                            .build();

                    guestRepository.save(guest);
                    log.info("Guest saved with ID: {}", guest.getId());

                    GuestRoom guestRoom = GuestRoom.builder()
                            .guest(guest)
                            .room(room)
                            .build();

                    guestRoomRepository.save(guestRoom);
                    log.info("GuestRoom saved with guest ID: {} and room ID: {}", guest.getId(), room.getId());

                    return VisitorRegistrationOutput.builder()
                            .guestId(guest.getId().toString())
                            .build();
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private boolean isRoomAvailable(UUID roomId, LocalDate startDate, LocalDate endDate) {
        List<Booking> existingBookings = bookingRepository.findConflictingBookings(roomId, startDate, endDate);
        return existingBookings.isEmpty();
    }


    private Errors handleErrors(Throwable throwable) {
        // Create a new Error object with the throwable's message
        Error error = Error.builder()
                .message(throwable.getMessage())
                .build();
        // Return an Errors object with a list of one Error object
        return new Errors(List.of(error));
    }
}

