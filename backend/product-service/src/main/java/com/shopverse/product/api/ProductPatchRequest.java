package com.shopverse.product.api;

import com.shopverse.product.catalog.Category;
import com.shopverse.product.catalog.Product;

import java.math.BigDecimal;

public record ProductPatchRequest(String name, String brand, String description,
                                  Long categoryId, BigDecimal price) { }
