package com.shopverse.product.service;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(Long id){
        super("Category "+id+" not found");
    }
}
