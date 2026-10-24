package com.shopverse.product.repository;

import com.shopverse.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    interface CategorySummary{
        Long getId();
        String getName();
        String getSlug();
        long getProductCount();
    }

    @Query("""
            select c.id as id, c.name as name, c.slug as slug, count(p) as productCount 
            from Category c left join Product p on p.category = c
            group by c.id, c.name, c.slug
            order by c.id
            """)
    List<CategorySummary> findAllWithProductCount();

}
