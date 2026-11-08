package com.shopverse.product.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionProxyTest {
    @Autowired StockService stockService;

    @Test
    void springInjectsAProxyNotTheRealClass() {
        System.out.println("Injected class: " + stockService.getClass().getName());
        assertTrue(AopUtils.isAopProxy(stockService));
        assertTrue(AopUtils.isCglibProxy(stockService));      // a generated subclass of StockService
        assertNotEquals(StockService.class, stockService.getClass());
    }
}
