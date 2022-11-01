package com.nimfid.commons.validation.constraint;

import com.nimfid.commons.validation.validator.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({METHOD,FIELD,ANNOTATION_TYPE,CONSTRUCTOR,PARAMETER,TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraint {
    Class<?>[] groups() default {};

    String message() default "";

    Class<? extends Payload>[] payload() default {};
}