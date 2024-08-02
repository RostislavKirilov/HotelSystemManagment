package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entitites.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {

}
