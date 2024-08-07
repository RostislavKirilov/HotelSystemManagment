package com.tinqinacademy.hotel.api.operations.checkavailablerooms;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class AvailableRoomsOutput implements OperationOutput {

    List<Integer> roomsId;
    private Integer id;
    private List<Integer> ids;
}
