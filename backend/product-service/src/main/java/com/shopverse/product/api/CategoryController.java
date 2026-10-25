package com.shopverse.product.api;

import com.shopverse.product.repository.CategoryRepository;
import com.shopverse.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryRepository categories;
    private final ProductService service;


    public CategoryController(CategoryRepository categories, ProductService service) {
        this.categories = categories;
        this.service = service;
    }

    // GET /api/categories -> [{"id":2,"name":"Books","slug":"books","productCount":2}, ...]
    @GetMapping("/api/categories")
    public List<CategoryRepository.CategorySummary> list() {
        return categories.findAllWithProductCount();
    }

    @GetMapping("/api/categories/{slug}/products")
    public List<ProductSummary> findByCategorySlug(@PathVariable String slug){
        return service.findByCategorySlug(slug);
    }


}
