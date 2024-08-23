package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entitites.GuestRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuestRoomRepository extends JpaRepository<GuestRoom, UUID> {
}