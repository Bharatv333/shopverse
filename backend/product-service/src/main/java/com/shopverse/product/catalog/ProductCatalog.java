package com.shopverse.product.catalog;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ProductCatalog {
    private final List<Product> products;
    private final AtomicLong nextId;

    public ProductCatalog(List<Product> products){
        this.products = new CopyOnWriteArrayList<>(products);
        long maxId = products.stream().mapToLong(Product::id).max().orElse(0);
        this.nextId = new AtomicLong(maxId + 1);
    }

    public Product add(Product product){
        Product saved = new Product(nextId.getAndIncrement(), product.name(), product.category(), product.price(),
                product.rating(), product.stock());
        products.add(saved);
        return saved;
    }

    public synchronized Product replace(Long id, Product product){
        Product existing = getById(id);
        Product updated = new Product(id, product.name(), product.category(), product.price(), product.rating(), product.stock());
        products.set(products.indexOf(existing), updated);
        return updated;
    }

    public boolean delete(Long id){
        return products.removeIf(p -> p.id().equals(id));
    }

    public List<Product> findAll(){
        return List.copyOf(products);
    }

    public Optional<Product> findById(Long id){
        return products.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    public Product getById(Long id){
        return findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> findByCategory(Category category){
        return products.stream().filter(product -> product.category().equals(category)).toList();
    }

    public List<Product>  findByPriceRange(BigDecimal min, BigDecimal max){
        return products.stream()
                .filter(p -> p.price().compareTo(min) >=0 && p.price().compareTo(max) <= 0)
                .sorted(Comparator.comparing(Product::price))
                .toList();
    }

    public List<Product> topRated(int n){
        return  products.stream()
                .sorted(Comparator.comparing(Product::rating).reversed())
                .limit(n)
                .toList();
    }

    public List<Product> search(String text){
        String str = text.toLowerCase();
        return products.stream()
                .filter(p -> p.name().toLowerCase().contains(str))
                .toList();
    }

    public Map<Category, List<Product>> groupByCategory(){
        return products.stream()
                .collect(Collectors.groupingBy(Product::category));
    }

    public Map<Category, Double> averagePriceByCategory(){
        return products.stream()
                .collect(Collectors.groupingBy(Product::category,
                        Collectors.averagingDouble(p -> p.price().doubleValue())));
    }

    public BigDecimal totalInventoryValue(){
        return products.stream()
                .map(p -> p.price().multiply(BigDecimal.valueOf(p.stock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //challenges
    public List<Product> lowStock(int threshold){
        return products.stream()
                .filter(p -> p.stock()<threshold )
                .sorted(Comparator.comparingInt(Product::stock))
                .toList();
    }

}
