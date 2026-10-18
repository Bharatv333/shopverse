package com.shopverse.product.api;

import com.shopverse.product.catalog.Category;
import com.shopverse.product.catalog.Product;

import java.math.BigDecimal;

public record ProductRequest(String name, Category category, BigDecimal price, int stock) {

    public Product toProduct(Long id, double rating){
        return new Product(id, name, category, price, rating, stock);
    }

}
