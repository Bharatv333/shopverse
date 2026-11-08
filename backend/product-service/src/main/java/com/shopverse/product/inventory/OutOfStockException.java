package com.shopverse.product.inventory;

public class OutOfStockException extends RuntimeException{
    public OutOfStockException(Long productId, int wanted, int available){
        super("Product "+productId+": wanted "+wanted+", only "+available+" available");
    }
}
