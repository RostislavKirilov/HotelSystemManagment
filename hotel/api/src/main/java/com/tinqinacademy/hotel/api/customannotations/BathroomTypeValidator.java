package com.tinqinacademy.hotel.api.customannotations;

import com.tinqinacademy.hotel.persistence.models.BathroomType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BathroomTypeValidator implements ConstraintValidator<ValidBathroomType, String> {

    @Override
    public void initialize(ValidBathroomType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String bathroomCode, ConstraintValidatorContext context) {
        if (bathroomCode == null) {
            return false;
        }
        return BathroomType.getByCode(bathroomCode) != BathroomType.UNKNOWN;
    }
}