package com.tinqinacademy.hotel.api.operations.updateroom;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomInput implements OperationInput {



    @NotNull
    private String bedSize;

    @NotNull
    private UUID roomId;

    @NotNull
    private String bathroomType;

    @NotNull
    private Integer room_floor;

    @NotNull
    private String room_number;

    @NotNull
    @Positive
    private BigDecimal price;

}
