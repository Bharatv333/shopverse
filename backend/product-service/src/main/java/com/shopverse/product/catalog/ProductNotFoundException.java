package com.shopverse.product.catalog;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id){
        super("Product "+id+" not found");
    }
}
