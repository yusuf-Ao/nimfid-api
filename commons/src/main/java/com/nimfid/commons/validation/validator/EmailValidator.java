package com.nimfid.commons.validation.validator;

import com.nimfid.commons.validation.constraint.EmailConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

    @Override
    public void initialize(final EmailConstraint contactNumber) {
    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext cxt) {
        return email != null
                && email.matches(
                "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})")
                && email.length() >= 5 && email.length() <= 60;
    }

}