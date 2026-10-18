package com.shopverse.product.api;

import com.shopverse.product.catalog.Category;
import com.shopverse.product.catalog.Product;
import com.shopverse.product.catalog.ProductCatalog;
import com.shopverse.product.catalog.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductCatalog catalog;
    public ProductController(ProductCatalog catalog){
        this.catalog = catalog;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false)Category category,
                              @RequestParam(required = false) String q
                              ){
        if(category!=null){
            return catalog.findByCategory(category);
        }
        if(q!=null){
            return catalog.search(q);
        }
        return catalog.findAll();
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id){
        return catalog.getById(id);
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        Product saved = catalog.add(request.toProduct(null, 0.0));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public Product replace(@PathVariable Long id, @RequestBody ProductRequest request){
        Product existing = catalog.getById(id);
        return catalog.replace(id, request.toProduct(id, existing.rating()));
    }

    @PatchMapping("/{id}")
    public Product patch(@PathVariable Long id, @RequestBody ProductPatchRequest request){
        Product existing = catalog.getById(id);
        return catalog.replace(id, (request.applyTo(existing)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!catalog.delete(id)){
            throw new ProductNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(ProductNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> badRequest(IllegalArgumentException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }
}
