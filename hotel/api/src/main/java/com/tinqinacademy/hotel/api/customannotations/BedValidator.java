package com.tinqinacademy.hotel.api.customannotations;

import com.tinqinacademy.hotel.api.models.Bed;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BedValidator implements ConstraintValidator<ValidBed, String> {

    @Override
    public void initialize(ValidBed constraintAnnotation) {
    }

    @Override
    public boolean isValid(String bedCode, ConstraintValidatorContext context) {
        if (bedCode == null) {
            return false;
        }
        return Bed.getByCode(bedCode) != Bed.UNKNOWN;
    }
}