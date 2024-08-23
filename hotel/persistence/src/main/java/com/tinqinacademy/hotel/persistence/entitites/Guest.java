package com.tinqinacademy.hotel.persistence.entitites;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private String phoneNo;

    @NotNull
    private String idCardNo;

    @NotNull
    private LocalDate idCardValidity;

    @NotNull
    private String idCardIssueAuthority;

    @NotNull
    private LocalDate idCardIssueDate;


    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuestRoom> guestRooms = new ArrayList<>();

    @OneToOne(mappedBy = "guest")
    private Booking booking;
}

