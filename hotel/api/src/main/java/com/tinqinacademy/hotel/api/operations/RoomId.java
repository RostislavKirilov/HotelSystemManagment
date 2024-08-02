package com.tinqinacademy.hotel.api.operations;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class RoomId {

    private String Id;
    private BigDecimal price;
    private String floor;
    private String bedSize;
    private String bathroomType;
    private List<LocalDateTime> datesOccupied;
}
