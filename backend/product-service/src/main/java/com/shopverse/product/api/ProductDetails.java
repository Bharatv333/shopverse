package com.shopverse.product.api;

import com.shopverse.product.domain.Product;
import com.shopverse.product.domain.ProductImage;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetails(Long id, String sku, String name, String brand, String slug,
                             String description, BigDecimal price, String currency,
                             BigDecimal rating, int reviewCount,
                             Long categoryId, String categoryName, List<String> images) {

    public static ProductDetails from(Product p) {
        return new ProductDetails(p.getId(), p.getSku(), p.getName(), p.getBrand(), p.getSlug(),
                p.getDescription(), p.getPrice(), p.getCurrency(),
                p.getAvgRating(), p.getReviewCount(),
                p.getCategory().getId(), p.getCategory().getName(),
                p.getImages().stream().map(ProductImage::getUrl).toList());
    }
}
