package com.tinqinacademy.hotel.api.operations.partialupdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.customannotations.ValidBed;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PartialUpdateInput implements OperationInput {

    @JsonIgnore
    private String roomId;

    @ValidBed
    private String bed_size;

    private String bathroomType;

    private Integer floor;

    private Integer roomNo;

    @Positive
    private BigDecimal price;
}
