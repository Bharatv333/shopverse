package com.shopverse.product.api.validation;

import com.shopverse.product.service.ProductFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ValidPriceRangeValidator implements ConstraintValidator<ValidPriceRange, ProductFilter>{

    @Override
    public boolean isValid(ProductFilter value, ConstraintValidatorContext context) {
        if(value.minPrice() == null || value.maxPrice() == null)
            return true;
        if(value.minPrice().compareTo(BigDecimal.ZERO) == -1 || value.maxPrice().compareTo(BigDecimal.ZERO) == -1)
            return false;
        return value.minPrice().compareTo(value.maxPrice()) != 1;
    }
}
