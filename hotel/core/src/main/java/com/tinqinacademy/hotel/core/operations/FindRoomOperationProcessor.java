package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.findroom.FindRoomInput;
import com.tinqinacademy.hotel.api.operations.findroom.FindRoomOperation;
import com.tinqinacademy.hotel.api.operations.findroom.RoomId;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FindRoomOperationProcessor extends BaseOperation implements FindRoomOperation {

    private final RoomRepository roomRepository;

    public FindRoomOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, RoomRepository roomRepository) {
        super(validator, conversionService, errorMapper);
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, RoomId> process(FindRoomInput input) {
        Room room = roomRepository.findById(UUID.fromString(input.getRoomId())).orElse(null);

        if (room == null) {
            Error error = Error.builder()
                    .message("Room not found")
                    .build();
            return Either.left(new Errors(List.of(error)));
        }

        RoomId roomIdDto = RoomId.builder()
                .roomId(room.getId().toString())
                .price(room.getPrice())
                .floor(String.valueOf(room.getRoomFloor()))
                .bedSize(String.valueOf(room.getBedSize()))
                .bathroomType(String.valueOf(room.getBathroomType()))
                .datesOccupied(room.getBookings().stream()
                        .flatMap(booking -> booking.getStartDate().datesUntil(booking.getEndDate().plusDays(1))
                                .map(LocalDate::atStartOfDay))
                        .collect(Collectors.toList()))
                .build();

        return Either.right(roomIdDto);
    }
}
