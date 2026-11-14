package com.shopverse.product.api.validation;

import com.shopverse.product.service.ProductFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ValidPriceRangeValidator implements ConstraintValidator<ValidPriceRange, ProductFilter>{

    @Override
    public boolean isValid(ProductFilter value, ConstraintValidatorContext context) {
        if(value.minPrice() == null || value.maxPrice() == null)
            return true;
        return value.minPrice().compareTo(value.maxPrice()) <= 0;
    }
}
