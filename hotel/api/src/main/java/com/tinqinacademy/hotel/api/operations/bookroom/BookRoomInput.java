package com.tinqinacademy.hotel.api.operations.bookroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRoomInput implements OperationInput {

    @JsonIgnore
    //@NotNull(message = "Room id must not be blank.")
    private String roomId;

    @NotNull(message = "Starting date must not be blank.")
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull(message = "End date must not be blank.")
    @FutureOrPresent
    private LocalDate endDate;

    private String firstName;
    private String lastName;
    private String phoneNo;

    @NotNull(message = "User ID must not be blank.")
    private String userId;
}
