package com.tinqinacademy.hotel.api.operations.visitorregistration.output;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VisitorRegistrationOutput implements OperationOutput {
    private String guestId;
}
