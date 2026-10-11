package com.shopverse.product.catalog;

import java.math.BigDecimal;

public interface PricingRule {
    BigDecimal apply(Product p);
}
