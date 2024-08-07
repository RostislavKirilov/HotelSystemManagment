package com.tinqinacademy.hotel.api.operations.findroom;

import com.tinqinacademy.hotel.api.base.OperationOutput;
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
public class RoomId implements OperationOutput {

    private String roomId;
    private BigDecimal price;
    private String floor;
    private String bedSize;
    private String bathroomType;
    private List<LocalDateTime> datesOccupied;
}
