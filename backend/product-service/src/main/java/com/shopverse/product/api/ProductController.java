package com.shopverse.product.api;

import com.shopverse.product.api.validation.OnCreate;
import com.shopverse.product.service.ProductFilter;
import com.shopverse.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    // @ParameterObject: show Pageable in Swagger as 3 simple query params (page, size, sort), not one "pageable" object
    @Operation(operationId = "listProducts", summary = "Search products, one page at a time")
    @GetMapping
    public PagedModel<ProductSummary> list(@Valid @ParameterObject ProductFilter filter,
                                           @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return new PagedModel<>(service.search(filter, pageable));
    }

    @Operation(operationId = "getProduct", summary = "Get one product with its images")
    @GetMapping(path = "/{id}", version = "1")
    public ProductDetails getOne(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(operationId = "getProductV2", summary = "Get one product (v2: price is an object)")
    @GetMapping(path = "/{id}", version = "2")
    public ProductDetailsV2 getOneV2(@PathVariable Long id) {
        ProductDetails d = service.get(id);
        return new ProductDetailsV2(d.id(), d.sku(), d.name(), new ProductDetailsV2.Money(d.price(), d.currency()),
                                    d.categoryName(), d.images());
    }

    // @Validated(OnCreate.class): run the normal rules AND the OnCreate-only rules (sku required)
    @Operation(operationId = "createProduct", summary = "Create a product")
    @PostMapping
    public ResponseEntity<ProductDetails> create(@Validated(OnCreate.class) @RequestBody ProductRequest request) {
        ProductDetails saved = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // @Valid: run only the normal rules (sku may be left out)
    @Operation(operationId = "replaceProduct", summary = "Replace all editable fields of a product")
    @PutMapping("/{id}")
    public ProductDetails replace(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.replace(id, request);
    }

    @Operation(operationId = "patchProduct", summary = "Change only the fields you send")
    @PatchMapping("/{id}")
    public ProductDetails patch(@PathVariable Long id, @Valid @RequestBody ProductPatchRequest request) {
        return service.patch(id, request);
    }

    @Operation(operationId = "deleteProduct", summary = "Soft-delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    // No @ExceptionHandler methods here any more: GlobalExceptionHandler handles errors for every controller.
}
