package com.shopverse.product.inventory;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    // Pessimistic: SELECT ... FOR UPDATE. Other transactions wait until we commit.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where productId = :productId")
    Optional<Stock> findByIdForUpdate(Long productId);

    // Atomic: check and change in ONE statement. Returns how many rows changed (1 = success, 0 = not enough).
    @Modifying
    @Query("""
            update Stock s 
            set s.available = s.available - :quantity,
                s.reserved = s.reserved + :quantity,
                s.version = s.version + 1
            where s.productId = :productId and s.available >= :quantity
            """)
    int reserveAtomically(Long productId, int quantity);

    // ONLY for the "no locking" demo: writes numbers we calculated in Java, ignoring @Version
    @Modifying
    @Query("update Stock s set s.available = :available, s.reserved = :reserved where s.productId = :productId")
    int overwrite(Long productId, int available, int reserved);
}
