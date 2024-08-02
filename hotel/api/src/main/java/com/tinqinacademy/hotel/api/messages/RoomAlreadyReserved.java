package com.tinqinacademy.hotel.api.messages;

public class RoomAlreadyReserved extends RuntimeException {
    public RoomAlreadyReserved() {
        super(ExceptionMessages.ROOM_ALREADY_BOOKED);
    }
}
