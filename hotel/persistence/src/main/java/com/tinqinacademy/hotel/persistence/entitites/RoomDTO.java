package com.tinqinacademy.hotel.persistence.entitites;

import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class RoomDTO {
    private UUID id;
    private Integer roomFloor;
    private String roomNumber;
    private BathroomType bathroomType;
    private BigDecimal price;
    private RoomStatus status;
    private List<BedDTO> beds;

}