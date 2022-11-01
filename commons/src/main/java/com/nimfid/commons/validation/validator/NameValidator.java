package com.nimfid.commons.validation.validator;

import com.nimfid.commons.validation.constraint.NameConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<NameConstraint, String> {

    @Override
    public void initialize(final NameConstraint contactNumber) {
    }

    @Override
    public boolean isValid(final String name, final ConstraintValidatorContext cxt) {
        return name != null && name.matches("^[\\w.-]+(?: [\\w.-]+)*$") && name.length() >= 1 && name.length() <= 60;
    }
}
