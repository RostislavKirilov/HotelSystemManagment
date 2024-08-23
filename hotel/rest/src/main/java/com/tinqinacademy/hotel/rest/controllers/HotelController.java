package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.findroom.FindRoomInput;
import com.tinqinacademy.hotel.api.operations.findroom.RoomId;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.removebooking.RemoveBookingInput;
import com.tinqinacademy.hotel.api.operations.removebooking.RemoveBookingOperation;
import com.tinqinacademy.hotel.api.operations.removebooking.RemoveBookingOutput;
import com.tinqinacademy.hotel.core.converters.RoomToRoomIdConverter;
import com.tinqinacademy.hotel.core.operations.*;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@ControllerAdvice
public class HotelController extends BaseOperation {

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);
    private final RoomRepository roomRepository;
    private final RoomToRoomIdConverter roomToRoomIdConverter;
    private final BookRoomOperationProcessor bookRoomOperationProcessor;
    private final CreateRoomOperationProcessor createRoomOperationProcessor;
    private final DeleteRoomOperationProcessor deleteRoomOperationProcessor;
    private final RemoveBookingOperation removeBookingOperation;
    private final AvailableRoomsOperationProcessor availableRoomsOperationProcessor;
    private final FindRoomOperationProcessor findRoomOperationProcessor;

    public HotelController ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper,
                             RoomRepository roomRepository, RoomToRoomIdConverter roomToRoomIdConverter,
                             BookRoomOperationProcessor bookRoomOperationProcessor,
                             CreateRoomOperationProcessor createRoomOperationProcessor,
                             DeleteRoomOperationProcessor deleteRoomOperationProcessor,
                             RemoveBookingOperation removeBookingOperation,
                             AvailableRoomsOperationProcessor availableRoomsOperationProcessor,
                             FindRoomOperationProcessor findRoomOperationProcessor) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
        this.roomToRoomIdConverter = roomToRoomIdConverter;
        this.bookRoomOperationProcessor = bookRoomOperationProcessor;
        this.createRoomOperationProcessor = createRoomOperationProcessor;
        this.deleteRoomOperationProcessor = deleteRoomOperationProcessor;
        this.removeBookingOperation = removeBookingOperation;
        this.availableRoomsOperationProcessor = availableRoomsOperationProcessor;
        this.findRoomOperationProcessor = findRoomOperationProcessor;
    }

    @PostMapping(RestApiRoutes.ADD_ROOM)
    @Operation(summary = "Add a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room is added!"),
            @ApiResponse(responseCode = "400", description = "Invalid data!"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> addRoom(@RequestBody @Validated CreateRoomInput createRoomInput) {
        try {
            Either<Errors, CreateRoomOutput> result = createRoomOperationProcessor.process(createRoomInput);

            if (result.isRight()) {
                CreateRoomOutput createRoomOutput = result.get();
                return ResponseEntity.status(HttpStatus.CREATED).body(createRoomOutput);
            } else {
                Errors errors = result.getLeft();
                return ResponseEntity.badRequest().body(errors);
            }
        } catch (Exception e) {
            // Log and handle unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping(RestApiRoutes.BOOK_ROOM)
    @Operation(summary = "Book a room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room booked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Room or user not found"),
            @ApiResponse(responseCode = "409", description = "Room already reserved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> bookRoom(@PathVariable String roomId, @RequestBody @Validated BookRoomInput input) {
        log.info("Booking room with input: {}", input);
        input.setRoomId(roomId);

        try {
            Either<Errors, BookRoomOutput> result = bookRoomOperationProcessor.process(input);

            if (result.isRight()) {
                BookRoomOutput bookRoomOutput = result.get();
                return ResponseEntity.ok(bookRoomOutput);
            } else {
                Errors errors = result.getLeft();
                log.warn("Booking failed: {}", errors.getMessage());
                return ResponseEntity.badRequest().body(errors);
            }
        } catch (RuntimeException ex) {
            log.error("Internal server error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal server error occurred: " + ex.getMessage());
        }
    }


    @DeleteMapping(RestApiRoutes.REMOVE_BOOKING)
    @Operation(summary = "Remove booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully removed booking."),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> removeBooking (@PathVariable String bookId) {
        RemoveBookingInput input = RemoveBookingInput.builder()
                .bookingId(bookId)
                .build();

        Either<Errors, RemoveBookingOutput> result = removeBookingOperation.process(input);

        if (result.isRight()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
    }

    @GetMapping(RestApiRoutes.AVAILABLE_ROOMS)
    public ResponseEntity<?> getAvailableRooms(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        try {
            List<Room> availableRooms = roomRepository.findAvailableRooms(startDate, endDate);
            if (availableRooms.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(availableRooms);
        } catch (Exception e) {
            log.error("Error while fetching available rooms: ", e);

            // Създаване на Error обект и добавянето му в списък
            Error error = new Error("Unexpected error");
            Errors errors = new Errors(List.of());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorOutput(List.of(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping(RestApiRoutes.FIND_ROOM)
    @Operation(summary = "Get room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room found"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getRoomById(@PathVariable UUID roomId) {
        FindRoomInput input = FindRoomInput.builder().roomId(roomId.toString()).build();
        Either<Errors, RoomId> result = findRoomOperationProcessor.process(input);

        if (result.isRight()) {
            RoomId roomIdDto = result.get();
            return ResponseEntity.ok(roomIdDto);
        } else {
            Errors errors = result.getLeft();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getMessage());
        }
    }


    @DeleteMapping(RestApiRoutes.DELETE_ROOM)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deletes room by Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room deleted!"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
    })
    public ResponseEntity<?> deleteRoom (@PathVariable String id) {
        log.info("Attempting to delete room with ID: {}", id);

        Either<Errors, DeleteRoomOutput> result = deleteRoomOperationProcessor.process(new DeleteRoomInput(id));

        if (result.isRight()) {
            log.info("Room with ID {} successfully removed", id);
            return ResponseEntity.ok(result.get());
        } else {
            Errors errors = result.getLeft();
            log.warn("Failed to delete room: {}", errors.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getMessage());
        }
    }
}