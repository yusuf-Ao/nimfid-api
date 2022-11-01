package com.nimfid.commons.validation.validator;

import com.nimfid.commons.validation.constraint.PasswordConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    @Override
    public void initialize(final PasswordConstraint password) {
    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext cxt) {
        return password != null && password.matches("(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,60}$")
                && password.length() >= 8 && password.length() <= 60;
    }

}