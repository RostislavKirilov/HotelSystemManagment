package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entitites.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("SELECT b FROM Booking b WHERE b.endDate < :date")
    List<Booking> findAllByEndDateBefore(@Param("date") LocalDate date);

    void deleteById ( UUID id );

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND ((b.startDate BETWEEN :startDate AND :endDate) OR (b.endDate BETWEEN :startDate AND :endDate))")
    List<Booking> findConflictingBookings ( @Param("roomId") UUID roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate );
}