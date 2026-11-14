package com.shopverse.product.api;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailsV2(Long id, String sku, String name, Money price, String categoryName,
                               List<String> images) {

    public record Money(BigDecimal amount, String currency){}
}
