package com.tinqinacademy.hotel.api.operations.VisitorRegistration;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VisitorRegistrationOutput implements OperationOutput {
    private UUID guestId;
}
