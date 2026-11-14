package com.shopverse.product.api;

import java.math.BigDecimal;
public record ProductSummary(Long id, String sku, String name, String brand, String slug,
                             BigDecimal price, String currency, BigDecimal rating,
                             Long categoryId, String categoryName, String mainImage) { }
