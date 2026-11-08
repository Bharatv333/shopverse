package com.shopverse.product.inventory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;


// Retries optimistic reservations. It must be a DIFFERENT bean from StockService:
// each call to stockService.reserveOptimistic(...) goes through the proxy and gets a FRESH transaction.
@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    static final int MAX_ATTEMPTS = 10;

    private final StockService stockService;

    public ReservationService(StockService stockService){
        this.stockService = stockService;
    }

    public void reserveWithRetry(Long productId, int quantity){
        for(int attempt = 1; ; attempt++){
            try{
                stockService.reserveOptimistic(productId, quantity);
                return;
            }catch (ObjectOptimisticLockingFailureException e){
                if(attempt == MAX_ATTEMPTS)
                    throw e;
                log.debug("Version conflict on product {}, attempt {} - retrying",productId,attempt);
            }
        }
    }
}
