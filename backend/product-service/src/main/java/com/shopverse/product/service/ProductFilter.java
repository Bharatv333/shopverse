package com.shopverse.product.service;

import java.math.BigDecimal;

public record ProductFilter(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String q) {
}
