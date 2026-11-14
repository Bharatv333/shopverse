package com.shopverse.product.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidPriceRangeValidator.class)     // who does the checking
@Target({TYPE})
@Retention(RUNTIME)
public @interface ValidPriceRange {

    String message() default "minPrice must not be bigger than maxPrice";

    // These two are required by the Bean Validation spec, even if you never use them.
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
