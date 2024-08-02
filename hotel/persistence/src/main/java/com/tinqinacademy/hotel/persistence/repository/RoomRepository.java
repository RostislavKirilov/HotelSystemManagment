package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {


    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
            "(SELECT b.room.id FROM Booking b WHERE b.startDate <= :endDate AND b.endDate >= :startDate) " +
            "AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRooms(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Optional<Room> findByRoomNumber(String roomNumber);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND (b.startDate < :endDate AND b.endDate > :startDate)")
    boolean isRoomBookedForPeriod(@Param("roomId") UUID roomId, @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.endDate < :date")
    List<Booking> findAllByEndDateBefore( @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Room r SET r.status = :status WHERE r.id = :roomId")
    void updateRoomStatus(@Param("roomId") UUID roomId, @Param("status") RoomStatus status);

}
