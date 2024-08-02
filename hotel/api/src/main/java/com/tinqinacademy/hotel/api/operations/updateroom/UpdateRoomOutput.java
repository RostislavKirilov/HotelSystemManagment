package com.tinqinacademy.hotel.api.operations.updateroom;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateRoomOutput implements OperationOutput {
    String roomId;
}
