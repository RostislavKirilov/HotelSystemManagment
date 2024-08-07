package com.tinqinacademy.hotel.restexport;

import com.tinqinacademy.hotel.api.contracts.FeignClientApiRoutes;
import com.tinqinacademy.hotel.api.operations.findroom.RoomId;
import com.tinqinacademy.hotel.api.operations.VisitorRegistration.VisitorRegistrationInput;
import com.tinqinacademy.hotel.api.operations.VisitorRegistration.VisitorRegistrationOutput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.AvailableRoomsOutput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.removebooking.RemoveBookingOutput;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Headers("Content-Type: application/json")
public interface RestExportInterface {

    @RequestLine(FeignClientApiRoutes.BOOK_ROOM)
    BookRoomOutput bookRoom(
            @Param("roomId") String roomId,
            BookRoomInput input);

    @RequestLine(FeignClientApiRoutes.ADD_ROOM)
    CreateRoomOutput addRoom( CreateRoomInput createRoomInput);

    @RequestLine(FeignClientApiRoutes.AVAILABLE_ROOMS)
    AvailableRoomsOutput getAvailableRooms( @Param("startDate") String startDate, @Param("endDate") String endDate);

    @RequestLine(FeignClientApiRoutes.REGISTER_VISITOR)
    VisitorRegistrationOutput registerVisitorAsRenterNew( VisitorRegistrationInput visitorInput);

    @RequestLine(FeignClientApiRoutes.REMOVE_BOOKING)
    RemoveBookingOutput removeBooking( @Param("bookId") UUID bookId);

    @RequestLine(FeignClientApiRoutes.DELETE_ROOM)
    DeleteRoomOutput deleteRoom(@Param("id") String id);

    @RequestLine(FeignClientApiRoutes.FIND_ROOM)
    RoomId getRoomById(@Param("roomId") String roomId);
}
