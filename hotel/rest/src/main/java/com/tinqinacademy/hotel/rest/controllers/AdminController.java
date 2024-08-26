package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.visitorregistration.input.VisitorRegistrationInput;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateInput;
import com.tinqinacademy.hotel.api.operations.visitorregistration.output.VisitorRegistrationOutput;
import com.tinqinacademy.hotel.core.operations.PartialUpdateOperationProcessor;
import com.tinqinacademy.hotel.core.operations.UpdateRoomOperationProcessor;
import com.tinqinacademy.hotel.core.operations.VisitorRegistrationOperationProcessor;
import com.tinqinacademy.hotel.core.services.RoomService;
import com.tinqinacademy.hotel.persistence.repository.GuestRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRoomRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AdminController {

    private final UpdateRoomOperationProcessor updateRoomOperationProcessor;
    private final PartialUpdateOperationProcessor partialUpdateOperationProcessor;
    private final VisitorRegistrationOperationProcessor visitorRegistrationOperationProcessor;
    public AdminController ( RoomService roomService, UpdateRoomOperationProcessor updateRoomOperationProcessor, PartialUpdateOperationProcessor partialUpdateOperationProcessor, RoomRepository roomRepository, GuestRepository guestRepository, GuestRoomRepository guestRoomRepository, VisitorRegistrationOperationProcessor visitorRegistrationOperationProcessor ) {
        this.updateRoomOperationProcessor = updateRoomOperationProcessor;
        this.partialUpdateOperationProcessor = partialUpdateOperationProcessor;
        this.visitorRegistrationOperationProcessor = visitorRegistrationOperationProcessor;
    }

    // Endpoint, необходим за следене
    // на информацията на посетителите
    @PostMapping(RestApiRoutes.REGISTER_VISITOR)
    @Operation(summary = "Registers a new visitor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Visitor registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<?> registerVisitor(@RequestBody VisitorRegistrationInput input) {
        log.info("Received registration request for room ID: {}", input.getRoomId());

        Either<Errors, VisitorRegistrationOutput> result = visitorRegistrationOperationProcessor.process(input);

        if (result.isRight()) {
            VisitorRegistrationOutput visitorRegistrationOutput = result.get();
            return ResponseEntity.ok(visitorRegistrationOutput);
        } else {
            Errors errors = result.getLeft();
            log.error("Registration failed: {}", errors.getMessage());
            // Можете да адаптирате тук грешките, ако е нужно
            return ResponseEntity.badRequest().body(errors);
        }
    }
    @PutMapping(RestApiRoutes.UPDATE_ROOM)
    @Operation(summary = "Update room information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room information successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "404", description = "Room not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<?> editInfo(@PathVariable("roomId") String roomId, @RequestBody @Validated UpdateRoomInput input) {
        log.info("Attempting to update room with ID: {}", roomId);
        input.setRoomId(roomId);

        Either<Errors, UpdateRoomOutput> result = updateRoomOperationProcessor.process(input);

        if (result.isRight()) {
            return ResponseEntity.ok("Room information successfully updated");
        } else {
            Errors errors = result.getLeft();
            log.error("Update failed: {}", errors.getMessage());

            if (errors.getErrors().stream().anyMatch(e -> e.getMessage().contains("ROOM_NOT_FOUND"))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Room not found"));
            }

            // Връщаме JSON обект с грешки
            return ResponseEntity.badRequest().body(Map.of("errors", errors.getErrors().stream()
                    .map(error -> Map.of("message", error.getMessage()))
                    .collect(Collectors.toList())));
        }
    }



    @PatchMapping(RestApiRoutes.UPDATE_ROOM)
    @Operation(summary = "Partially update room information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid data."),
            @ApiResponse(responseCode = "404", description = "Room not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<?> partialUpdateRoom(@RequestBody @Validated PartialUpdateInput input, @PathVariable String roomId) {
        log.info("Attempting to partially update room with ID: {}", roomId);
        input.setRoomId(roomId);

        Either<Errors, PartialUpdateOutput> result = partialUpdateOperationProcessor.process(input);

        if (result.isRight()) {
            PartialUpdateOutput partialUpdateOutput = result.get();
            partialUpdateOutput.setMessage("Room successfully updated");
            return ResponseEntity.ok(partialUpdateOutput);
        } else {
            Errors errors = result.getLeft();
            log.error("Partial update failed: {}", errors.getMessage());

            // Check if the error message indicates room not found and return 404 Not Found
            if (errors.getErrors().stream().anyMatch(e -> e.getMessage().contains(ExceptionMessages.ROOM_NOT_FOUND))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
            }
            return ResponseEntity.badRequest().body(errors);
        }
    }
}
