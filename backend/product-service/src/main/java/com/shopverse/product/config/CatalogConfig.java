package com.shopverse.product.config;

import com.shopverse.product.catalog.Category;
import com.shopverse.product.catalog.Product;
import com.shopverse.product.catalog.ProductCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class CatalogConfig {

    @Bean
    public ProductCatalog productCatalog(){
        return new ProductCatalog(List.of(
                new Product(1L, "Laptop Pro 14", Category.ELECTRONICS, new BigDecimal("85000"), 4.6, 10),
                new Product(2L, "Wireless Mouse", Category.ELECTRONICS, new BigDecimal("799"), 4.2, 150),
                new Product(3L, "Clean Code", Category.BOOKS, new BigDecimal("650"), 4.8, 40),
                new Product(4L, "Spring in Action", Category.BOOKS, new BigDecimal("900"), 4.5, 0),
                new Product(5L, "Denim Jacket", Category.FASHION, new BigDecimal("2499"), 3.9, 25),
                new Product(6L, "Coffee Mug", Category.HOME, new BigDecimal("299"), 4.0, 200)
        ));
    }
}
