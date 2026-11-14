package com.shopverse.product.api;

import com.shopverse.product.api.validation.NullOrNotBlank;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

// PATCH: every field is optional. null = "don't change it". The rules only check what you DO send.
public record ProductPatchRequest(
        @NullOrNotBlank @Size(max = 200) String name,
        @Size(max = 100) String brand,
        @Size(max = 5000) String description,
        @Positive Long categoryId,
        @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal price) { }
