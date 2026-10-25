package com.shopverse.product.api;

import com.shopverse.product.domain.Product;
import com.shopverse.product.domain.ProductImage;

import java.math.BigDecimal;

public record ProductSummary(Long id, String sku, String name, String brand, String slug,
                             BigDecimal price, String currency, BigDecimal rating,
                             Long categoryId, String categoryName, String mainImage) {

    public static ProductSummary from(Product p) {
        return new ProductSummary(p.getId(), p.getSku(), p.getName(), p.getBrand(), p.getSlug(),
                p.getPrice(), p.getCurrency(), p.getAvgRating(),
                p.getCategory().getId(), p.getCategory().getName(),
                p.getImages().stream().map(ProductImage::getUrl).findFirst().orElse(null));
    }
}
