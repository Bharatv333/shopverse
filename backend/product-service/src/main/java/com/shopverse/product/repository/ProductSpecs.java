package com.shopverse.product.repository;

import com.shopverse.product.domain.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecs {

    private ProductSpecs(){}

    public static Specification<Product> inCategory(Long categoryId){
        return (root, query, cb) -> categoryId == null ? null
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> priceAtLeast(BigDecimal min){
        return (root, query, cb) -> min == null ? null
                : cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Product> priceAtMost(BigDecimal max){
        return (root, query, cb) -> max == null ? null
                : cb.lessThanOrEqualTo(root.get("price"), max);
    }

    public static Specification<Product> nameContains(String text){
        return (root, query, cb) -> text == null ? null
                : cb.like(cb.lower(root.get("name")), "%"+ text.toLowerCase() +"%");
    }
}
