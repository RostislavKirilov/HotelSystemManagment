package com.tinqinacademy.hotel.persistence.entitites;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.Bed;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {
//    public static class BasicView {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@JsonView(BasicView.class)
    private UUID id;

    @Column(nullable = false)
    private Integer roomFloor;

    @Column(nullable = false)
    //@JsonView(BasicView.class)
    private String roomNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BathroomType bathroomType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BedEntity> beds = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference
    private List<Booking> bookings = new ArrayList<>();

    private Bed bedSize;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuestRoom> guestRooms = new ArrayList<>();
}


