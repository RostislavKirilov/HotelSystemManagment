package com.tinqinacademy.hotel.api.operations.partialupdate;

import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.customannotations.ValidBed;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PartialUpdateInput implements OperationInput {

    @NotNull
    private String roomId;

    @ValidBed
    private String bed_size;

    @NotNull
    private String bathroomType;

    @NotNull
    private Integer floor;

    @NotNull
    private Integer roomNo;

    @NotNull
    private BigDecimal price;
}
