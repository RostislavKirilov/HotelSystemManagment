package com.tinqinacademy.hotel.api.exceptions;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorResponses {

    private Integer status;
    private String message;
    private String details;

}
