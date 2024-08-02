package com.tinqinacademy.hotel.core.services;

import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomStatusUpdaterService {


    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateRoomStatuses () {
        LocalDate today = LocalDate.now();
        List<Booking> pastBookings = bookingRepository.findAllByEndDateBefore(today);

        for (Booking booking : pastBookings) {
            Room room = booking.getRoom();
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                roomRepository.updateRoomStatus(room.getId(), RoomStatus.AVAILABLE);
            }
        }
    }
}
