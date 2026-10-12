package com.shopverse.product.pricing;

import com.shopverse.product.catalog.FestivalSale;
import com.shopverse.product.catalog.PricingRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "shop.active-pricing-rule=festival")
public class PricingSpringTest {
    @Autowired ApplicationContext context;
    @Autowired PricingService pricingService;

    @Test
    void springFoundAllPricingRules() {
        assertEquals(Set.of("none", "festival", "clearance"),
                context.getBeansOfType(PricingRule.class).keySet());
    }

    @Test
    void activeRuleComesFromConfiguration() {
        assertInstanceOf(FestivalSale.class, pricingService.activeRule());
    }

    @Test
    void beansAreSingletonsByDefault() {
        assertSame(context.getBean(PricingService.class), context.getBean(PricingService.class));
    }
}
