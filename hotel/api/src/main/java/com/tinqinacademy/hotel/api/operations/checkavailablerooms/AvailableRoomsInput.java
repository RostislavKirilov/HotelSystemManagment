package com.tinqinacademy.hotel.api.operations.checkavailablerooms;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class AvailableRoomsInput implements OperationInput {

    @NotNull(message = "Starting date must not be blank.")
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull(message = "End date must not be blank.")
    @FutureOrPresent
    private LocalDate endDate;

    @NotNull(message = "Bed count date must not be blank.")
    @PositiveOrZero(message = "Bed count is equals or greater than zero.")

    @NotNull(message = "Bed size must not be blank.")
    private String bedSize;

    @NotNull(message = "Bathroom type must not be blank.")
    private String bathRoomType;

    

}
