package com.shopverse.product.inventory;

import com.shopverse.product.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

// A SLICE test for the database layer: entities, repositories, Flyway and a real MySQL (Testcontainers).
// No controllers, no services. Every test runs in a transaction that is ROLLED BACK at the end.
@DataJpaTest
@Import(TestcontainersConfiguration.class)
class StockRepositoryTest {

    @Autowired StockRepository stock;
    @Autowired TestEntityManager em;        // a small helper around EntityManager, made for tests

    @Test
    void atomicReserveSucceedsWhenEnoughIsAvailable() {
        // Arrange our OWN data: other tests may have changed this row (it's rolled back after this test)
        stock.overwrite(1L, 10, 0);
        long versionBefore = stock.findById(1L).orElseThrow().getVersion();

        int changed = stock.reserveAtomically(1L, 3);

        assertThat(changed).isEqualTo(1);
        em.clear();                                            // forget cached entities, read fresh from MySQL
        Stock s = stock.findById(1L).orElseThrow();
        assertThat(s.getAvailable()).isEqualTo(7);
        assertThat(s.getReserved()).isEqualTo(3);
        assertThat(s.getVersion()).isEqualTo(versionBefore + 1);
    }

    @Test
    void atomicReserveChangesNothingWhenNotEnough() {
        stock.overwrite(4L, 0, 0);
        int changed = stock.reserveAtomically(4L, 1);

        assertThat(changed).isZero();
    }

    @Test
    void forUpdateFindsTheRow() {
        assertThat(stock.findByIdForUpdate(2L)).hasValueSatisfying(s -> assertThat(s.getAvailable()).isEqualTo(150));
        assertThat(stock.findByIdForUpdate(99L)).isEmpty();
    }
}