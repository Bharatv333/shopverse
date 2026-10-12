package com.shopverse.product.catalog;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("none")
public class NoDiscount implements PricingRule{
    @Override
    public BigDecimal apply(Product p){
        return p.price();
    }
}
