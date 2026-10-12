package com.shopverse.product.pricing;

import com.shopverse.product.catalog.*;
import com.shopverse.product.config.ShopProperties;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PricingServiceTest {
    private final Product mug = new Product(6L, "Coffee Mug", Category.HOME, new BigDecimal("299"), 4.0, 200);

    private PricingService serviceWith(String activeRule) {
        Map<String, PricingRule> rules = Map.of(
                "none", new NoDiscount(),
                "festival", new FestivalSale(),
                "clearance", new ClearanceSale());
        return new PricingService(rules, new ShopProperties("INR", activeRule));
    }

    @Test
    void usesTheActiveRule() {
        assertEquals(new BigDecimal("269.10"), serviceWith("festival").finalPrice(mug));
        assertEquals(new BigDecimal("209.30"), serviceWith("clearance").finalPrice(mug));
        assertEquals(new BigDecimal("299"), serviceWith("none").finalPrice(mug));
    }

    @Test
    void unknownRule_givesClearError() {
        assertThrows(IllegalStateException.class, () -> serviceWith("black-friday").finalPrice(mug));
    }
}
