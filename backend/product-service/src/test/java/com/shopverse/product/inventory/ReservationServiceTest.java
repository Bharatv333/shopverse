package com.shopverse.product.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

// The retry loop from Lesson 6, tested in milliseconds instead of with 20 real threads and MySQL.
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock StockService stockService;
    @InjectMocks ReservationService reservations;

    private static ObjectOptimisticLockingFailureException versionClash() {
        return new ObjectOptimisticLockingFailureException(Stock.class, 1L);
    }

    @Test
    void retriesAfterVersionClashesAndThenSucceeds() {
        // 1st call: clash, 2nd call: clash, 3rd call: works. (void method -> doThrow/doNothing style)
        doThrow(versionClash())
                .doThrow(versionClash())
                .doNothing()
                .when(stockService).reserveOptimistic(1L, 2);

        assertThatNoException().isThrownBy(() -> reservations.reserveWithRetry(1L, 2));
        verify(stockService, times(3)).reserveOptimistic(1L, 2);
    }

    @Test
    void givesUpAfterMaxAttempts() {
        doThrow(versionClash()).when(stockService).reserveOptimistic(1L, 1);   // clashes forever

        assertThatThrownBy(() -> reservations.reserveWithRetry(1L, 1))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
        verify(stockService, times(ReservationService.MAX_ATTEMPTS)).reserveOptimistic(1L, 1);
    }

    @Test
    void outOfStockIsNotRetried() {
        doThrow(new OutOfStockException(1L, 5, 0)).when(stockService).reserveOptimistic(1L, 5);

        assertThatThrownBy(() -> reservations.reserveWithRetry(1L, 5)).isInstanceOf(OutOfStockException.class);
        verify(stockService, times(1)).reserveOptimistic(1L, 5);      // a "no" is final: retrying won't make stock appear
    }
}
