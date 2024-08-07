package com.tinqinacademy.hotel.api.operations.findroom;

import com.tinqinacademy.hotel.api.base.OperationInput;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindRoomInput implements OperationInput {

    private String roomId;
}
