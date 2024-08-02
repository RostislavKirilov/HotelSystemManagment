package com.tinqinacademy.hotel.api.operations.createuser;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserOutput implements OperationOutput {

    private UUID userId;
}
