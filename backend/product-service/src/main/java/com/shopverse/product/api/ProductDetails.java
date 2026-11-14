package com.shopverse.product.api;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetails(Long id, String sku, String name, String brand, String slug,
                             String description, BigDecimal price, String currency,
                             BigDecimal rating, int reviewCount,
                                 Long categoryId, String categoryName, List<String> images) { }
