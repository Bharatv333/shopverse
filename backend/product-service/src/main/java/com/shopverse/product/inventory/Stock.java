package com.shopverse.product.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "available")
    private int available;

    @Column(name = "reserved")
    private int reserved;

    @Version
    private long version;

    protected Stock(){

    }

    public void reserve(int quantity)
    {
        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        if(available < quantity)
            throw new OutOfStockException(productId, quantity, available);
        available -= quantity;
        reserved += quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getReserved() {
        return reserved;
    }

    public int getAvailable() {
        return available;
    }

    public long getVersion() {
        return version;
    }
}
