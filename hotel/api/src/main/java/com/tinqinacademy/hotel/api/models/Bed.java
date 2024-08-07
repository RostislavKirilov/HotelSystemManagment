package com.tinqinacademy.hotel.api.models;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Bed {
    KING_SIZE("king"),
    SINGLE("single"),
    DOUBLE("double"),
    SMALL_DOUBLE("small_double"),
    QUEEN_SIZE("queen_size"),
    UNKNOWN("unknown");

    private final String code;

    Bed(String code) {
        this.code = code;
    }

    public static Bed getByCode(String stringValue) {
        Optional<Bed> bedSize = Arrays.stream(Bed.values())
                .filter(t -> t.getCode().equals(stringValue))
                .findFirst();

        return bedSize.orElse(UNKNOWN);
    }
}
