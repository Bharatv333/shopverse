package com.shopverse.product.api;

import com.shopverse.product.api.validation.OnCreate;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

// Used by POST (create) and PUT (replace).
// sku is required only when CREATING (group OnCreate); PUT ignores it because a SKU never changes.
public record ProductRequest(

        @NotBlank(groups = OnCreate.class)
        @Size(max = 40)
        @Pattern(regexp = "[A-Z0-9]+(-[A-Z0-9]+)*", message = "must look like BK-CLC-003 (capital letters, digits and dashes)")
        String sku,

        @NotBlank
        @Size(max = 200)
        String name,

        @Size(max = 100)
        String brand,

        @Size(max = 5000)
        String description,

        @NotNull
        @Positive
        Long categoryId,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price) { }
