package com.tinqinacademy.hotel.api.operations.deleteroom;


import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class DeleteRoomInput implements OperationInput {
    @NotNull
    private String roomId;
}
