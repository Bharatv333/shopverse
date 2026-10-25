package com.shopverse.product.repository;

import com.shopverse.product.domain.Product;
import com.shopverse.product.service.ProductFilter;
import com.shopverse.product.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class ProductRepositoryTest {

    @Autowired ProductRepository products;
    @Autowired ProductService service;
    @Autowired EntityManagerFactory emf;
    @Autowired EntityManager em;

    Statistics stats;

    @BeforeEach
    void resetStats() {
        stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();
    }

    @Test
    void productListWithCategoriesIsOneQuery() {
        service.search(new ProductFilter(null, null, null, null), PageRequest.of(0, 20));
        assertEquals(2, stats.getPrepareStatementCount(), "expected 1 SQL query, not N+1");
        //With default_batch_fetch_size as 20, hibernate collects all 20 product ids and fetches their images all at once it takes only 1 query.
        //Without default_batch_fetch_size as 20, hibernate fetches images per product as one query so it becomes 6 queries.
    }

    @Test
    void derivedQueryFindsByCategorySlug() {
        List<Product> books = products.findByCategorySlugOrderByPriceAsc("books");
        assertEquals(List.of("Clean Code", "Spring in Action"), books.stream().map(Product::getName).toList());
    }

    @Test
    void jpqlPriceRange() {
        List<Product> found = products.findInPriceRange(new BigDecimal("500"), new BigDecimal("1000"));
        assertEquals(3, found.size());
    }

    @Test
    @Transactional
    void dirtyCheckingSavesWithoutCallingSave() {
        Product mug = products.findById(6L).orElseThrow();
        mug.setPrice(new BigDecimal("349.00"));     // just change the Java object...
        em.flush();                                  // ...Hibernate sees the change and sends an UPDATE
        em.clear();                                  // forget everything, read fresh from MySQL
        assertEquals(new BigDecimal("349.00"), products.findById(6L).orElseThrow().getPrice());
    }

    @Test
    @Transactional
    void deleteIsSoft() {
        products.deleteById(5L);
        em.flush();
        assertFalse(products.existsById(5L));        // hidden from JPA...
        assertEquals(1, products.countDeleted());    // ...but still in the table
    }
}