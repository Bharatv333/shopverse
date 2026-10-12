package com.shopverse.product.cart;

import com.shopverse.product.pricing.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BasketScopeTest {

    @Autowired ApplicationContext context;

    @Test
    void prototype_givesNewObjectEachTime() {
        Basket b1 = context.getBean(Basket.class);
        Basket b2 = context.getBean(Basket.class);

        assertNotSame(b1, b2);                 // two different baskets

        b1.add("Mouse");
        assertTrue(b2.items().isEmpty());      // b2 is not affected by b1
    }

    @Test
    void singleton_givesSameObjectEachTime() {
        assertSame(context.getBean(PricingService.class),
                context.getBean(PricingService.class));
    }
}