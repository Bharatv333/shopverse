package com.shopverse.product.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private StockRepository stock;

    public StockService(StockRepository stock){
        this.stock = stock;
    }

    @Transactional(readOnly = true)
    public Stock get(Long productId){
        return stock.findById(productId).orElseThrow(() -> new StockNotFoundException(productId));
    }
    // ❌ 1. No locking: read, check in Java, write back. Two buyers can read the same number.
    @Transactional
    public void reserveNoLocking(Long productId, int quantity){
        Stock s = get(productId);
        if(s.getAvailable() < quantity)
            throw new OutOfStockException(productId, quantity, s.getAvailable());
        stock.overwrite(productId, s.getAvailable()-quantity, s.getReserved()+quantity);
    }

    // 2. Optimistic: change the entity; at commit Hibernate runs
    //    UPDATE ... SET version = v+1 WHERE product_id = ? AND version = v
    //    If someone else changed the row first, 0 rows match -> ObjectOptimisticLockingFailureException.
    @Transactional
    public void reserveOptimistic(Long productId, int quantity){
        get(productId).reserve(quantity);
    }

    // 3. Pessimistic: lock the row while we read it. Everyone else waits in line.
    @Transactional
    public void reservePessimistic(Long productId, int quantity){
        Stock s = stock.findByIdForUpdate(productId).orElseThrow(() -> new StockNotFoundException(productId));
        s.reserve(quantity);
    }

    // 4. Atomic UPDATE: let MySQL check and subtract in one step.
    @Transactional
    public void reserveAtomic(Long productId, int quantity){
        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        if(stock.reserveAutomically(productId, quantity) == 0)
            throw new OutOfStockException(productId, quantity, get(productId).getAvailable());
    }
}
