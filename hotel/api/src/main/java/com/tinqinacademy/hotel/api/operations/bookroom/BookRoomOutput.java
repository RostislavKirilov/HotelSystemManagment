package com.tinqinacademy.hotel.api.operations.bookroom;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookRoomOutput implements OperationOutput {

    private String bookingId;


}
