package com.shopverse.product.service;


import com.shopverse.product.api.*;
import com.shopverse.product.catalog.ProductNotFoundException;
import com.shopverse.product.domain.Category;
import com.shopverse.product.domain.Product;
import com.shopverse.product.repository.CategoryRepository;
import com.shopverse.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.shopverse.product.repository.ProductSpecs.*;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final ProductMapper mapper;

    public ProductService(ProductRepository products, CategoryRepository categories, ProductMapper mapper){
        this.products = products;
        this.categories = categories;
        this.mapper = mapper;
    }

    public Page<ProductSummary> search(ProductFilter filter, Pageable pageable){
        Specification<Product> spec = Specification.allOf(
                inCategory(filter.categoryId()),
                priceAtLeast(filter.minPrice()),
                priceAtMost(filter.maxPrice()),
                nameContains(filter.q()));
        return products.findAll(spec, pageable).map(mapper::toSummary);
    }

    public ProductDetails get(Long id){
        return products.findWithDetailsById(id)
                .map(mapper::toDetails)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public ProductDetails create(ProductRequest request){
        if(products.existsBySku(request.sku())){
            throw new DuplicateSkuException(request.sku());
        }
        Product product = new Product(request.sku(), request.name(), category(request.categoryId()), request.price());
        product.setBrand(request.brand());
        product.setDescription(request.description());
        Product saved = products.save(product);
        log.info("Created product {} with id {}", saved.getSku(), saved.getId());   // {} = placeholder, no string +
        return mapper.toDetails(saved);
    }

    @Transactional
    public ProductDetails replace(Long id, ProductRequest request) {
        Product product = load(id);
        product.rename(request.name());
        product.setBrand(request.brand());
        product.setDescription(request.description());
        product.setCategory(category(request.categoryId()));
        product.setPrice(request.price());
        return mapper.toDetails(product);  // no save() needed: dirty checking writes the UPDATE
    }

    @Transactional
    public ProductDetails patch(Long id, ProductPatchRequest request) {
        Product product = load(id);
        if (request.name() != null) product.rename(request.name());
        if (request.brand() != null) product.setBrand(request.brand());
        if (request.description() != null) product.setDescription(request.description());
        if (request.categoryId() != null) product.setCategory(category(request.categoryId()));
        if (request.price() != null) product.setPrice(request.price());
        return mapper.toDetails(product);
    }

    @Transactional
    public void delete(Long id) {
        products.delete(load(id));           // runs our @SQLDelete: a soft delete
    }

    private Product load(Long id) {
        return products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Category category(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("categoryId is required");
        }
        return categories.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public List<ProductSummary> findByCategorySlug(String slug){
        categories.findBySlug(slug).orElseThrow(() -> new CategoryNotFoundException(slug));
        return products.findByCategorySlugOrderByPriceAsc(slug)
                .stream().map(mapper::toSummary).toList();
    }
}
