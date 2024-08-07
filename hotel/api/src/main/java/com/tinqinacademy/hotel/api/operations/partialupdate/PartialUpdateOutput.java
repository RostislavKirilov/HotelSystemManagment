package com.tinqinacademy.hotel.api.operations.partialupdate;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PartialUpdateOutput implements OperationOutput {

    String roomId;
}
