package com.tinqinacademy.hotel.core.converters;

import com.tinqinacademy.hotel.api.operations.findroom.RoomId;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;

@Component
public class RoomToRoomIdConverter implements Converter<Room, RoomId> {

    @Override
    public RoomId convert ( Room room ) {

        return RoomId.builder()
                .roomId(room.getId().toString())
                .price(room.getPrice())
                .floor(String.valueOf(room.getRoomFloor()))
                .bathroomType(room.getBathroomType().name())
                .datesOccupied(new ArrayList<>())
                .build();
    }
}
