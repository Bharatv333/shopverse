package com.shopverse.product.catalog;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ClearanceSale implements PricingRule{
    @Override
    public BigDecimal apply(Product p) {
        if(p.stock() > 100){
            return p.price().multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
        }
        return p.price();
    }
}
