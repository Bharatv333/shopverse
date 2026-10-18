package com.shopverse.product.api;

import com.shopverse.product.catalog.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @GetMapping("/api/categories")
    public List<Category> list(){
        return List.of(Category.values());
    }

}
