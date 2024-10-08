package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOperation;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CreateRoomOperationProcessor extends BaseOperation implements CreateRoomOperation {

    private final RoomRepository roomRepository;

    public CreateRoomOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository ) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
    }

    // Логваме началото на операцията за проверка на налични стаи, за по-добра видимост при отстраняване на проблеми
    // и за следене на изпълнението на операцията.
    @Override
    public Either<Errors, CreateRoomOutput> process(CreateRoomInput input) {
        return validateBathroomType(input.getBathroomType())
                .flatMap(validBathroomType -> createRoom(input, validBathroomType))
                .mapLeft(this::createErrors);
    }

    private Either<String, String> validateBathroomType(String bathroomType) {
        return Try.of(() -> BathroomType.valueOf(bathroomType.toUpperCase()))
                .toEither()
                .map(Enum::name)
                //.mapLeft(Throwable::getMessage);
                .mapLeft(ex -> "Invalid value for bathroomType: " + bathroomType);

    }

    private Either<String, CreateRoomOutput> createRoom(CreateRoomInput input, String validBathroomType) {
        return Try.of(() -> {
                    Room room = Room.builder()
                            .roomFloor(input.getRoomFloor())
                            .roomNumber(input.getRoomNumber())
                            .bathroomType(BathroomType.valueOf(validBathroomType)) // Ensure correct conversion
                            .price(input.getPrice())
                            .status(RoomStatus.AVAILABLE) // Make sure this is set if required
                            .build();
                    roomRepository.save(room);
                    return new CreateRoomOutput("Room added successfully!");
                }).toEither()
                .mapLeft(Throwable::getMessage);
    }


    private Errors createErrors(String message) {
        Error error = new Error();
        error.setMessage(message);
        return new Errors(List.of(error));
    }
}
