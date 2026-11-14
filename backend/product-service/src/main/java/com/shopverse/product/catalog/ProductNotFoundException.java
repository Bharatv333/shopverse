package com.shopverse.product.catalog;

import com.shopverse.product.error.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(Long id){
        super("Product "+id+" not found");
    }
}
