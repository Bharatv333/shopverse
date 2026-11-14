package com.shopverse.product.service;

import com.shopverse.product.api.validation.ValidPriceRange;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@ValidPriceRange
public record ProductFilter(
        Long categoryId,
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice,
        @Size(max = 100) String q) {
}
