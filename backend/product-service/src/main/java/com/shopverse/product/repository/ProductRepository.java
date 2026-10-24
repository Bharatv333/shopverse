package com.shopverse.product.repository;

import com.shopverse.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);
    List<Product> findByCategorySlugOrderByPriceAsc(String categorySlug);

    // The list page: load each product's category in the SAME query (fixes N+1)
    @Override
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    // The detail page: product + category + images
    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Product> findWithDetailsById(Long id);

    @Query("select p from Product p where p.price between :min and :max order by p.price")
    List<Product> findInPriceRange(BigDecimal min, BigDecimal max);

    @Query(value = "SELECT COUNT(*) FROM products WHERE deleted_at IS NOT NULL", nativeQuery = true)
    long countDeleted();
}
