package com.shopverse.product.api;

import java.math.BigDecimal;

public record ProductRequest(String sku, String name, String brand, String description,
                             Long categoryId, BigDecimal price) { }
