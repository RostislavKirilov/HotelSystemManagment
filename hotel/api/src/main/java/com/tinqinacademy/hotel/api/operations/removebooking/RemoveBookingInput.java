package com.tinqinacademy.hotel.api.operations.removebooking;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Builder
public class RemoveBookingInput implements OperationInput {

    @NotNull
    private String bookingId;

}
