package com.shopverse.product.api;

import com.shopverse.product.repository.CategoryRepository;
import com.shopverse.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Categories")
@RestController
public class CategoryController {

    private final CategoryRepository categories;
    private final ProductService service;


    public CategoryController(CategoryRepository categories, ProductService service) {
        this.categories = categories;
        this.service = service;
    }

    // GET /api/categories -> [{"id":2,"name":"Books","slug":"books","productCount":2}, ...]
    @Operation(operationId = "listCategories", summary = "All categories with how many products each has")
    @GetMapping("/api/categories")
    public List<CategoryRepository.CategorySummary> list() {
        return categories.findAllWithProductCount();
    }

    @Operation(operationId = "listProductsInCategory", summary = "Products in one category, cheapest first")
    @GetMapping("/api/categories/{slug}/products")
    public List<ProductSummary> findByCategorySlug(@PathVariable String slug){
        return service.findByCategorySlug(slug);
    }

}
