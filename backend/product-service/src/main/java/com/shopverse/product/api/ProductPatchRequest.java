package com.shopverse.product.api;

import com.shopverse.product.catalog.Category;
import com.shopverse.product.catalog.Product;

import java.math.BigDecimal;

public record ProductPatchRequest(String name, Category category, BigDecimal price, Integer stock) {

    public Product applyTo(Product existing){
        return new Product(existing.id(),
                    name!=null ? name : existing.name(),
                    category!=null ? category : existing.category(),
                    price!=null ? price : existing.price(),
                    existing.rating(),
                    stock!=null ? stock : existing.stock()
                );
    }

}
