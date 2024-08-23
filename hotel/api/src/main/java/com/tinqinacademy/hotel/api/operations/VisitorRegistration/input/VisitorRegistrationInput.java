package com.tinqinacademy.hotel.api.operations.visitorregistration.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VisitorRegistrationInput implements OperationInput {

    private String roomId;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    @NotNull(message = "First name cannot be null")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @NotNull(message = "Phone number cannot be null")
    private String phoneNo;

    @NotNull(message = "ID card number cannot be null")
    private String idCardNo;

    @NotNull(message = "ID card validity cannot be null")
    private LocalDate idCardValidity;

    @NotNull(message = "ID card issue authority cannot be null")
    private String idCardIssueAuthority;

    @NotNull(message = "ID card issue date cannot be null")
    private LocalDate idCardIssueDate;
}
