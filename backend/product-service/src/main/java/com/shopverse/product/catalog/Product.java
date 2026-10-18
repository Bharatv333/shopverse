package com.shopverse.product.catalog;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        if(price == null){
            throw new IllegalArgumentException("Price is required");
        }
        if(price.signum() < 0){
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public boolean inStock(){
        return stock > 0;
    }

    //challenges
    public Product discount(BigDecimal percent){
        BigDecimal hundred = new BigDecimal(100);
        BigDecimal discountMultiplier = price.multiply(hundred.subtract(percent)).divide(hundred, 2, RoundingMode.HALF_UP);
        return new Product(this.id, this.name, this.category, discountMultiplier ,this.rating, this.stock);
    }
}
