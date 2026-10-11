package com.shopverse.product.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductCatalogTest {
    private ProductCatalog catalog;

    @BeforeEach
    void setUp(){
        catalog = new ProductCatalog(List.of(
                new Product(1L, "Laptop Pro 14", Category.ELECTRONICS, new BigDecimal("85000"), 4.6, 10),
                new Product(2L, "Wireless Mouse", Category.ELECTRONICS, new BigDecimal("799"), 4.2, 150),
                new Product(3L, "Clean Code", Category.BOOKS, new BigDecimal("650"), 4.8, 40),
                new Product(4L, "Spring in Action", Category.BOOKS, new BigDecimal("900"), 4.5, 0),
                new Product(5L, "Denim Jacket", Category.FASHION, new BigDecimal("2499"), 3.9, 25),
                new Product(6L, "Coffee Mug", Category.HOME, new BigDecimal("299"), 4.0, 200)
        ));
    }

    @Test
    void findById_returnsProducts_whenExists(){
        assertEquals("Clean Code",catalog.findById(3L).orElseThrow().name());
    }

    @Test
    void findById_returnsEmpty_whenMissing() {
        assertTrue(catalog.findById(99L).isEmpty());
    }

    @Test
    void getById_throws_whenMissing() {
        assertThrows(ProductNotFoundException.class, () -> catalog.getById(99L));
    }

    @Test
    void findByCategory_returnsOnlyThatCategory() {
        assertEquals(2, catalog.findByCategory(Category.BOOKS).size());
    }

    @Test
    void findByPriceRange_isInclusive_andSortedCheapestFirst() {
        List<Product> result = catalog.findByPriceRange(new BigDecimal("650"), new BigDecimal("2499"));
        assertEquals(List.of(3L, 2L, 4L, 5L), result.stream().map(Product::id).toList());
    }

    @Test
    void topRated_returnsHighestFirst() {
        assertEquals(List.of("Clean Code", "Laptop Pro 14"),
                catalog.topRated(2).stream().map(Product::name).toList());
    }

    @Test
    void search_ignoresCase() {
        assertEquals(1, catalog.search("LAPTOP").size());
    }

    @Test
    void groupByCategory_hasAllCategories() {
        assertEquals(4, catalog.groupByCategory().size());
        assertEquals(2, catalog.groupByCategory().get(Category.ELECTRONICS).size());
    }

    @Test
    void averagePriceByCategory_isCorrect() {
        assertEquals(775.0, catalog.averagePriceByCategory().get(Category.BOOKS));
    }

    @Test
    void totalInventoryValue_isCorrect() {
        // 85000*10 + 799*150 + 650*40 + 900*0 + 2499*25 + 299*200
        assertEquals(0, new BigDecimal("1118125").compareTo(catalog.totalInventoryValue()));
    }

    @Test
    void product_withBlankName_isRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(7L, " ", Category.HOME, BigDecimal.TEN, 4.0, 1));
    }
}
