package com.shopverse.product.api;

import com.shopverse.product.catalog.ProductNotFoundException;
import com.shopverse.product.service.CategoryNotFoundException;
import com.shopverse.product.service.ProductFilter;
import com.shopverse.product.service.ProductService;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping
    public PagedModel<ProductSummary> list(@RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) BigDecimal minPrice,
                                           @RequestParam(required = false) BigDecimal maxPrice,
                                           @RequestParam(required = false) String q,
                                           @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        ProductFilter filter = new ProductFilter(categoryId, minPrice, maxPrice, q);
        return new PagedModel<>(service.search(filter, pageable));
    }

    @GetMapping("/{id}")
    public ProductDetails getOne(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<ProductDetails> create(@RequestBody ProductRequest request) {
        ProductDetails saved = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ProductDetails replace(@PathVariable Long id, @RequestBody ProductRequest request) {
        return service.replace(id, request);
    }

    @PatchMapping("/{id}")
    public ProductDetails patch(@PathVariable Long id, @RequestBody ProductPatchRequest request) {
        return service.patch(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({ProductNotFoundException.class, CategoryNotFoundException.class})
    public ResponseEntity<ProblemDetail> notFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    // IllegalArgumentException: our own checks. PropertyReferenceException: ?sort=noSuchField
    @ExceptionHandler({IllegalArgumentException.class, PropertyReferenceException.class})
    public ResponseEntity<ProblemDetail> badRequest(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }
}
