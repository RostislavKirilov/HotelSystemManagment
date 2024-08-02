package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.AvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.AvailableRoomsOperation;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.AvailableRoomsOutput;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AvailableRoomsOperationProcessor extends BaseOperation implements AvailableRoomsOperation {

    private final RoomRepository roomRepository;

    public AvailableRoomsOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository ) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, AvailableRoomsOutput> process(AvailableRoomsInput input) {
        return Try.of(() -> {
                    log.info("Start checking available rooms with input: {}", input);
                    validate(input);

                    List<Room> availableRooms = roomRepository.findAvailableRooms(input.getStartDate(), input.getEndDate())
                            .stream()
                            .filter(room -> room.getBeds().stream().anyMatch(bed -> bed.getBed().name().equalsIgnoreCase(input.getBedSize())))
                            .filter(room -> room.getBathroomType().getCode().equalsIgnoreCase(input.getBathRoomType()))
                            .toList();

                    if (availableRooms.isEmpty()) {
                        throw new RuntimeException("No rooms available with the specified criteria.");
                    }

                    List<Integer> roomIds = availableRooms.stream()
                            .map(room -> room.getId().hashCode())
                            .collect(Collectors.toList());

                    AvailableRoomsOutput output = AvailableRoomsOutput.builder()
                            .roomsId(roomIds)
                            .build();

                    log.info("End checking available rooms with output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(this::handleErrors);
    }
    private Errors handleErrors ( Throwable throwable ) {
        ErrorOutput errorOutput = matchError(throwable);
        return new Errors(List.of(Error.builder()
                .message(errorOutput.getMessage())
                .build()).toString());
    }

//add comment

    private ErrorOutput matchError(Throwable throwable) {
        return io.vavr.API.Match(throwable).of(
                caseRoomNotFound(throwable),
                caseBedNotFound(throwable),
                caseBookingNotFound(throwable),
                caseUserNotFound(throwable),
                caseInvalidInput(throwable),
                defaultCase(throwable)
        );
    }

}