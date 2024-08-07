package com.tinqinacademy.hotel.core.services;



import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.entitites.User;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private static final Logger logger = Logger.getLogger(RoomService.class.getName());

    public Room createRoom( Room room) {
        try {
            room.setId(UUID.randomUUID());
            return roomRepository.save(room);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating room: " + e.getMessage(), e);
            throw e;
        }
    }
    public void deleteRoom(UUID roomId) {
        try {
            roomRepository.deleteById(roomId);
            logger.log(Level.INFO, "Room with ID " + roomId + " deleted successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting room with ID " + roomId + ": " + e.getMessage(), e);
            throw e;
        }
    }
    public String bookRoom(String roomNumber, String firstName, String lastName, LocalDate startDate, LocalDate endDate) {
        Optional<Room> roomOpt = roomRepository.findByRoomNumber(roomNumber);
        List<User> userOpt = userRepository.findAllByFirstNameAndLastName(firstName, lastName);

        if (roomOpt.isEmpty()) {
            return "Room not found";
        }

        if (userOpt.isEmpty()) {
            return "User not found";
        }

        Room room = roomOpt.get();
        User user = userOpt.get(0);


        boolean isOverlapping = bookingRepository.findAll()
                .stream()
                .anyMatch(b -> b.getRoom().getId().equals(room.getId())
                        && !(b.getEndDate().isBefore(startDate) || b.getStartDate().isAfter(endDate)));

        if (isOverlapping) {
            return "Room is already booked for the selected dates";
        }

        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .room(room)
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        bookingRepository.save(booking);

        roomRepository.save(room);

        return "Booking successful";
    }

    public boolean editInfo(String roomId, UpdateRoomInput input) {
        Room room = roomRepository.findById(UUID.fromString(roomId)).orElse(null);

        if (room == null) {
            return false;
        }


        if (input.getBathroomType() != null) {
            BathroomType bathroomType = BathroomType.getByCode(input.getBathroomType());
            room.setBathroomType(bathroomType);
        }

        roomRepository.save(room);
        return true;
    }
}
