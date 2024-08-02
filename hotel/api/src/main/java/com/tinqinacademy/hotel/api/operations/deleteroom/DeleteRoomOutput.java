package com.tinqinacademy.hotel.api.operations.deleteroom;
import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRoomOutput implements OperationOutput {

    private String message;

}
