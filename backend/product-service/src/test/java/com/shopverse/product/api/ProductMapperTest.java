package com.shopverse.product.api;

import com.shopverse.product.domain.Category;
import com.shopverse.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

// A plain unit test: no Spring, no database. Runs in milliseconds.
class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void mapsEntityToSummary() {
        Product p = new Product("BK-TST-001", "Test Book", new Category("Books", "books"), new BigDecimal("499.00"));
        p.addImage("/img/test-1.jpg", "front");
        p.addImage("/img/test-2.jpg", "back");

        ProductSummary s = mapper.toSummary(p);

        assertEquals("Test Book", s.name());
        assertEquals("test-book", s.slug());
        assertEquals("Books", s.categoryName());
        assertEquals("/img/test-1.jpg", s.mainImage());       // only the first image
        assertEquals(0, BigDecimal.ZERO.compareTo(s.rating())); // avgRating -> rating
    }

    @Test
    void productWithoutImagesHasNoMainImage() {
        Product p = new Product("BK-TST-002", "No Pics", new Category("Books", "books"), BigDecimal.TEN);
        assertNull(mapper.toSummary(p).mainImage());
        assertTrue(mapper.toDetails(p).images().isEmpty());
    }
}
