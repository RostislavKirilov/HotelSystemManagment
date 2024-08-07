package com.tinqinacademy.hotel.api.operations.reserveroom;

import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.customannotations.ValidBed;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ReserveRoomInput implements OperationInput {

    @NotNull
    private String id;

    @NotNull
    private Integer roomFloor;

    @ValidBed
    private String bed;

}
