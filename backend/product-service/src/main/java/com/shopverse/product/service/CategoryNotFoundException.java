package com.shopverse.product.service;

import com.shopverse.product.error.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(Long id){
        super("Category "+id+" not found");
    }

    public CategoryNotFoundException(String slug){
        super("Category "+slug+" not found");
    }

}
