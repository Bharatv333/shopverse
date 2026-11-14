package com.shopverse.product.api;

import com.shopverse.product.domain.Product;
import com.shopverse.product.domain.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

// MapStruct writes the implementation (ProductMapperImpl) for us at COMPILE time.
// componentModel = "spring"     -> the generated class is a @Component, so we can inject it.
// unmappedTargetPolicy = ERROR  -> if a DTO field has no source, the BUILD fails (no silent nulls).
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

    // Same names (id, sku, name, price, ...) are copied automatically. We only list the differences.
    @Mapping(target = "rating", source = "avgRating")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "mainImage", source = "images", qualifiedByName = "firstImageUrl")
    ProductSummary toSummary(Product product);

    @Mapping(target = "rating", source = "avgRating")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDetails toDetails(Product product);    // images: List<ProductImage> -> List<String> uses imageUrl() below

    // MapStruct uses this automatically whenever it must turn ONE ProductImage into a String.
    default String imageUrl(ProductImage image){
        return image.getUrl();
    }

    @Named("firstImageUrl")
    default String firstImageUrl(List<ProductImage> images){
        return images.isEmpty() ? null : images.getFirst().getUrl();
    }

}
