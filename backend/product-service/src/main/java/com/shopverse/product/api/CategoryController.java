package com.shopverse.product.api;

import com.shopverse.product.repository.CategoryRepository;
import com.shopverse.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryRepository categories;
    private final ProductRepository products;


    public CategoryController(CategoryRepository categories, ProductRepository products) {
        this.categories = categories;
        this.products = products;
    }

    // GET /api/categories -> [{"id":2,"name":"Books","slug":"books","productCount":2}, ...]
    @GetMapping("/api/categories")
    public List<CategoryRepository.CategorySummary> list() {
        return categories.findAllWithProductCount();
    }

    @GetMapping("/api/categories/{slug}/products")
    public List<ProductSummary> findByCategorySlug(@PathVariable String slug){
        categories.findBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category NOT FOUND"));
        return products.findByCategorySlugOrderByPriceAsc(slug)
                .stream().map(ProductSummary::from).toList();
    }

//    @GetMapping("/api/categories")
//    public List<Category> list(){
//        return List.of(Category.values());
//    }

}
