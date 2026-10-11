package com.shopverse.product.catalog;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FestivalSale implements PricingRule{
    @Override
    public BigDecimal apply(Product p) {
        return p.price().multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
    }
}
