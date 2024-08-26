package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateInput;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateOperation;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateOutput;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.Bed;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
@Slf4j
public class PartialUpdateOperationProcessor extends BaseOperation implements PartialUpdateOperation {

    private final RoomRepository roomRepository;

    public PartialUpdateOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository ) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public Either<Errors, PartialUpdateOutput> process ( PartialUpdateInput input ) {
        return Try.of(() -> {
                    log.info("Starting partial update operation for input: {}", input);
                    validate(input);

                    UUID roomId = UUID.fromString(input.getRoomId());

                    Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.ROOM_NOT_FOUND + " for ID: " + roomId));

                    if (input.getBed_size() != null) {
                        room.setBedSize(Bed.getByCode(input.getBed_size()));
                    }
                    if (input.getBathroomType() != null) {
                        room.setBathroomType(BathroomType.valueOf(input.getBathroomType().toUpperCase()));
                    }
                    if (input.getFloor() != null) {
                        room.setRoomFloor(input.getFloor());
                    }
                    if (input.getRoomNo() != null) {
                        room.setRoomNumber(String.valueOf(input.getRoomNo()));
                    }
                    if (input.getPrice() != null) {
                        room.setPrice(input.getPrice());
                    }

                    Room updatedRoom = roomRepository.save(room);

                    return PartialUpdateOutput.builder()
                            .roomId(updatedRoom.getId().toString())
                            .build();
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private Errors handleErrors(Throwable throwable) {
        return API.Match(throwable).of(
                API.Case(API.$(IllegalArgumentException.class::isInstance), () -> {
                    String message = throwable.getMessage();
                    if (message.contains(ExceptionMessages.ROOM_NOT_FOUND)) {
                        return Errors.of(message);
                    } else {
                        return Errors.of(ExceptionMessages.INVALID_DATA_INPUT);
                    }
                }),
                API.Case(API.$(RuntimeException.class::isInstance),
                        Errors.of(ExceptionMessages.UNEXPECTED_ERROR)),
                API.Case(API.$(),
                        Errors.of(ExceptionMessages.UNEXPECTED_ERROR))
        );
    }

}

