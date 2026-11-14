package com.shopverse.product.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Our own validation annotation. For PATCH: you may leave a field out (null),
// but if you send it, it can't be "" or "   ".
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)     // who does the checking
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
public @interface NullOrNotBlank {

    String message() default "must not be blank (leave it out to keep the current value)";

    // These two are required by the Bean Validation spec, even if you never use them.
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
