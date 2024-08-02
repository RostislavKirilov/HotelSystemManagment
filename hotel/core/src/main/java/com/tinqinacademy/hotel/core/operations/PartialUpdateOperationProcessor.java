package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateInput;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateOperation;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateOutput;
import com.tinqinacademy.hotel.persistence.entitites.Room;
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
    public Either<Errors, PartialUpdateOutput> process(PartialUpdateInput input) {
        // Логваме началото на операцията за проверка на налични стаи, за по-добра видимост при отстраняване на проблеми
        // и за следене на изпълнението на операцията.
        return Try.of(() -> {
                    log.info("Starting partial update operation for input: {}", input);
                    validate(input);

                    UUID roomId = UUID.fromString(input.getRoomId());

                    Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.ROOM_NOT_FOUND + " for ID: " + roomId));

                    room = input.toRoomEntity(room);

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
                API.Case(API.$(IllegalArgumentException.class::isInstance),
                        Errors.of(ExceptionMessages.INVALID_DATA_INPUT)),
                API.Case(API.$(RuntimeException.class::isInstance),
                        Errors.of(ExceptionMessages.UNEXPECTED_ERROR)),
                API.Case(API.$(),
                        Errors.of(ExceptionMessages.UNEXPECTED_ERROR))
        );
    }
}
