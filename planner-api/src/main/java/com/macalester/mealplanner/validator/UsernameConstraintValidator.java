package com.macalester.mealplanner.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameConstraintValidator implements ConstraintValidator<UsernameConstraint, String> {
    @Override
    public void initialize(UsernameConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("{name.error.null}").addConstraintViolation();
            return false;
        }

        if (s.isBlank()){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("{name.error.blank}").addConstraintViolation();
            return false;
        }

        if (!s.matches("^[a-zA-Z0-9 ]+$")){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("{name.error.invalidUsername}").addConstraintViolation();
            return false;
        }

        return true;
    }
}
