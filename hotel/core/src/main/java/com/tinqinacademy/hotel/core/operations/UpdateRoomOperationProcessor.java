package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOperation;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateRoomOperationProcessor extends BaseOperation implements UpdateRoomOperation {

    private final RoomRepository roomRepository;

    public UpdateRoomOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository ) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, UpdateRoomOutput> process(UpdateRoomInput input) {
        // Логваме началото на операцията за проверка на налични стаи, за по-добра видимост при отстраняване на проблеми
        // и за следене на изпълнението на операцията.
        return Try.of(() -> {
                    validate(input);

                    Room room = roomRepository.findById(input.getRoomId())
                            .orElseThrow(() -> new RuntimeException(ExceptionMessages.ROOM_NOT_FOUND + " for ID: " + input.getRoomId()));

                    room.setRoomFloor(input.getRoom_floor());
                    room.setRoomNumber(input.getRoom_number());
                    room.setPrice(input.getPrice());

                    BathroomType bathroomType = validateBathroomType(input.getBathroomType());
                    room.setBathroomType(bathroomType);

                    roomRepository.save(room);

                    return new UpdateRoomOutput(room.getId().toString());
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }

    private BathroomType validateBathroomType(String bathroomType) {
        return Try.of(() -> BathroomType.valueOf(bathroomType.toUpperCase()))
                .getOrElseThrow(() -> new RuntimeException(ExceptionMessages.INVALID_DATA_INPUT + " for BathroomType: " + bathroomType));
    }

    private Errors handleErrors(Throwable throwable) {
        ErrorOutput errorOutput = API.Match(throwable)
                .of(
                        caseRoomNotFound(throwable),
                        caseInvalidInput(throwable),
                        defaultCase(throwable)
                );

        return new Errors(List.of(Errors.builder()
                .message(errorOutput.getMessage())
                .build()).toString());
    }
}
