package com.tinqinacademy.hotel.api.operations.createroom;

import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.customannotations.ValidBed;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import lombok.*;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRoomInput implements OperationInput {
    @NotNull
    private Integer roomFloor;

    @NotBlank
    private String roomNumber;

    @NotBlank
    private String bathroomType;

    @NotNull
    private BigDecimal price;

    @ValidBed
    private String bedSize;

    @Getter
    private List<String> bedSizes;

    private RoomStatus status;

}
