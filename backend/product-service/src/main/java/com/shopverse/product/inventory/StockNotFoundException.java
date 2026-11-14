package com.shopverse.product.inventory;

import com.shopverse.product.error.NotFoundException;

public class StockNotFoundException extends NotFoundException {
    public StockNotFoundException(Long productId){
        super("No stock record for product " + productId);
    }
}
