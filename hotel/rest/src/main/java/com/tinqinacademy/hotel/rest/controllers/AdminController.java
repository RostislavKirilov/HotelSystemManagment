package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.operations.VisitorDetails;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.core.operations.PartialUpdateOperationProcessor;
import com.tinqinacademy.hotel.core.operations.UpdateRoomOperationProcessor;
import com.tinqinacademy.hotel.core.services.RoomService;
import com.tinqinacademy.hotel.persistence.models.Bed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/system")
public class AdminController {

    private final RoomService roomService;
    private final UpdateRoomOperationProcessor updateRoomOperationProcessor;
    private final PartialUpdateOperationProcessor partialUpdateOperationProcessor;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController ( RoomService roomService, UpdateRoomOperationProcessor updateRoomOperationProcessor, PartialUpdateOperationProcessor partialUpdateOperationProcessor ) {
        this.roomService = roomService;

        this.updateRoomOperationProcessor = updateRoomOperationProcessor;
        this.partialUpdateOperationProcessor = partialUpdateOperationProcessor;
    }


    @GetMapping("/register")
    public ResponseEntity<List<VisitorDetails>> getVisitors(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNo,
            @RequestParam String idCardNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate idCardValidity,
            @RequestParam String idCardIssueAuthority,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate idCardIssueDate,
            @RequestParam String roomNo
    ) {
        List<VisitorDetails> visitors = List.of();
        return ResponseEntity.ok(visitors);
    }

    @PutMapping("/room/{roomId}")
    @Operation(summary = "Update room information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room information successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "404", description = "Room not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<Object> editInfo(
            @PathVariable("roomId") UUID roomId,
            @RequestBody @Validated UpdateRoomInput input) {
        logger.info("Attempting to update room with ID: {}", roomId);

        if (input == null) {
            logger.error("Input data is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            if (input.getBedSize() == null || input.getBedSize().isBlank()) {
                logger.error("Bed size is missing in the input data.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Bed bed = Bed.getByCode(input.getBedSize());
            if (bed == null) {
                logger.error("Invalid bed size provided: {}", input.getBedSize());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            UpdateRoomInput updateRoomInput = UpdateRoomInput.builder()
                    .bedSize(input.getBedSize())
                    .bathroomType(input.getBathroomType())
                    .room_floor(input.getRoom_floor())
                    .room_number(input.getRoom_number())
                    .price(input.getPrice())
                    .build();

            var result = updateRoomOperationProcessor.process(updateRoomInput);

            return result
                    .map(updatedRoom -> ResponseEntity.ok().build())
                    .mapLeft(errors -> {
                        logger.error("Update failed: {}", errors);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    })
                    .getOrElse(() -> {
                        logger.error("Unexpected error during room update.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    });
        } catch (Exception e) {
            logger.error("Unexpected error during room update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PatchMapping("/room")
    @Operation(summary = "Partially update room information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid data."),
            @ApiResponse(responseCode = "404", description = "Room not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<Object> partialUpdateRoom( @RequestBody @Validated PartialUpdateInput input) {
        try {
            if (input == null || input.getRoomId() == null || input.getRoomId().isEmpty()) {
                logger.error("Input data is missing or room ID is null.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            UUID roomUUID;
            try {
                roomUUID = UUID.fromString(input.getRoomId());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid room ID format: {}", input.getRoomId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            var result = partialUpdateOperationProcessor.process(input);

            return result
                    .map(partialUpdateOutput -> ResponseEntity.ok().build())
                    .mapLeft(errors -> {
                        logger.error("Partial update failed: {}", errors);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    })
                    .getOrElse(() -> {
                        logger.error("Unexpected error during partial room update.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    });
        } catch (Exception e) {
            logger.error("Unexpected error during partial room update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

