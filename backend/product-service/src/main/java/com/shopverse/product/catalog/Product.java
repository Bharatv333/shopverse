package com.shopverse.product.catalog;

import java.math.BigDecimal;

public record Product(
        Long id,
        String name,
        Category category,
        BigDecimal price,
        double rating,
        int stock
) {

    public Product{
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Product name is required");
        }
        if(price.signum() < 0){
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public boolean inStock(){
        return stock > 0;
    }

}
