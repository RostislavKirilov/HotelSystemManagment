package com.tinqinacademy.hotel.persistence.repository;


import com.tinqinacademy.hotel.persistence.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findAllByFirstNameAndLastName(String firstName, String lastName);
    Optional<User> findByEmail(String email);
    List<User> findByPhoneNumber(String phoneNumber);
}
