package com.shopverse.product.inventory;

import com.shopverse.product.inventory.ReservationService;
import com.shopverse.product.inventory.Stock;
import com.shopverse.product.inventory.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StockConcurrencyTest {
    static final long PRODUCT = 1L;
    static final int IN_STOCK = 5;
    static final int BUYERS = 20;

    @Autowired StockService stockService;
    @Autowired ReservationService reservationService;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void fiveLeft() {
        jdbc.update("UPDATE stock SET available = ?, reserved = 0 WHERE product_id = ?", IN_STOCK, PRODUCT);
    }

    @AfterEach
    void restore() {
        jdbc.update("UPDATE stock SET available = 10, reserved = 0 WHERE product_id = ?", PRODUCT);
    }

    @Test
    void noLockingOversells() throws Exception {
        int sold = buyAtTheSameTime(id -> stockService.reserveNoLocking(id, 1));
        System.out.println("No locking: " + sold + " buyers got an item, but we only had " + IN_STOCK);
        assertTrue(sold > IN_STOCK, "expected overselling without locking, sold " + sold);
    }

    @Test
    void optimisticWithRetrySellsExactlyFive() throws Exception {
        int sold = buyAtTheSameTime(id -> reservationService.reserveWithRetry(id, 1));
        assertSoldOut(sold);
    }

    @Test
    void pessimisticSellsExactlyFive() throws Exception {
        int sold = buyAtTheSameTime(id -> stockService.reservePessimistic(id, 1));
        assertSoldOut(sold);
    }

    @Test
    void atomicUpdateSellsExactlyFive() throws Exception {
        int sold = buyAtTheSameTime(id -> stockService.reserveAtomic(id, 1));
        assertSoldOut(sold);
    }

    private void assertSoldOut(int sold) {
        Stock s = stockService.get(PRODUCT);
        assertEquals(IN_STOCK, sold, "buyers who got an item");
        assertEquals(0, s.getAvailable());
        assertEquals(IN_STOCK, s.getReserved());
    }

    // Starts 20 threads, holds them at a "starting gate", then releases them all at once.
    // Returns how many reservations succeeded.
    private int buyAtTheSameTime(Consumer<Long> buy) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(BUYERS);
        CountDownLatch gate = new CountDownLatch(1);
        AtomicInteger sold = new AtomicInteger();
        List<Future<?>> buyers = new ArrayList<>();

        for (int i = 0; i < BUYERS; i++) {
            buyers.add(pool.submit(() -> {
                gate.await();                      // wait for the starting gun
                try {
                    buy.accept(PRODUCT);
                    sold.incrementAndGet();
                } catch (OutOfStockException e) {
                    // sold out: a normal "no" for this buyer
                }
                return null;
            }));
        }
        gate.countDown();                          // GO!
        for (Future<?> f : buyers) {
            f.get(30, TimeUnit.SECONDS);           // rethrows any unexpected error from a buyer
        }
        pool.shutdown();
        return sold.get();
    }
}
