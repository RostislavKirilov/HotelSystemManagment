package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOperation;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
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
public class DeleteRoomOperationProcessor extends BaseOperation implements DeleteRoomOperation {

    private final RoomRepository roomRepository;

    public DeleteRoomOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository ) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public Either<Errors, DeleteRoomOutput> process(DeleteRoomInput input) {
        // Логваме началото на операцията за проверка на налични стаи, за по-добра видимост при отстраняване на проблеми
        // и за следене на изпълнението на операцията.
        return Try.of(() -> {
                    validateInput(input);

                    UUID roomId = UUID.fromString(input.getRoomId());
                    Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new RuntimeException(ExceptionMessages.ROOM_NOT_FOUND + " for ID: " + roomId));

                    roomRepository.deleteById(roomId);

                    log.info("Room with ID {} successfully removed", roomId);
                    return new DeleteRoomOutput("Room is removed!");
                })
                .toEither()
                .mapLeft(this::createErrors);
    }

    private void validateInput(DeleteRoomInput input) {
        if (input.getRoomId() == null) {
            throw new RuntimeException(ExceptionMessages.INVALID_ROOM_ID);
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
