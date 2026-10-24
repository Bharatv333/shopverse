package com.shopverse.product.api;

import com.shopverse.product.catalog.Category;
import com.shopverse.product.repository.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryRepository categories;

    public CategoryController(CategoryRepository categories) {
        this.categories = categories;
    }

    // GET /api/categories -> [{"id":2,"name":"Books","slug":"books","productCount":2}, ...]
    @GetMapping("/api/categories")
    public List<CategoryRepository.CategorySummary> list() {
        return categories.findAllWithProductCount();
    }

//    @GetMapping("/api/categories")
//    public List<Category> list(){
//        return List.of(Category.values());
//    }

}
