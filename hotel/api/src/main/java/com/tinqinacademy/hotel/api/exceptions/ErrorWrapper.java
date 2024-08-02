package com.tinqinacademy.hotel.api.exceptions;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorWrapper {

    private List<ErrorWrapper> errors = new ArrayList<ErrorWrapper>();

}
