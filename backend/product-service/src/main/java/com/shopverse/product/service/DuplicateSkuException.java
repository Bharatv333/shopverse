package com.shopverse.product.service;

import com.shopverse.product.error.ConflictException;

public class DuplicateSkuException extends ConflictException {
    public DuplicateSkuException(String sku){
        super("SKU "+sku+" already exists");
    }
}
