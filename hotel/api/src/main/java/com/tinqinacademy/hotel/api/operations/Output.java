package com.tinqinacademy.hotel.api.operations;


import com.tinqinacademy.hotel.api.models.BathroomType;
import com.tinqinacademy.hotel.api.models.Bed;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Output {

    private String id;
    private String roomNumber;
    private Bed bed;
    private Integer roomFloor;
    private BigDecimal price;
    private BathroomType bathroomType;
    private String message;
}
