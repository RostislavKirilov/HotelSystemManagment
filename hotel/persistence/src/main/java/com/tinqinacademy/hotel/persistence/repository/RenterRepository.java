package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.entitites.Renter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RenterRepository extends JpaRepository<Renter, UUID> {
}
