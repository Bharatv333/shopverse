package com.shopverse.product.inventory;

import com.shopverse.product.error.ConflictException;

public class OutOfStockException extends ConflictException {
    public OutOfStockException(Long productId, int wanted, int available){
        super("Product "+productId+": wanted "+wanted+", only "+available+" available");
    }
}
