package com.tinqinacademy.hotel.api.operations.createroom;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomOutput implements OperationOutput {
    private String message;
}
