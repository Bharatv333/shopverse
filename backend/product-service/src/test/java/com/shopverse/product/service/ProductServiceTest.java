package com.shopverse.product.service;

import com.shopverse.product.api.ProductDetails;
import com.shopverse.product.api.ProductMapper;
import com.shopverse.product.api.ProductPatchRequest;
import com.shopverse.product.api.ProductRequest;
import com.shopverse.product.catalog.ProductNotFoundException;
import com.shopverse.product.domain.Category;
import com.shopverse.product.domain.Product;
import com.shopverse.product.repository.CategoryRepository;
import com.shopverse.product.repository.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// A UNIT test: no Spring, no database, no web server. Just the ProductService class and some fakes.
// MockitoExtension creates the @Mock/@Spy objects and builds ProductService with them (@InjectMocks).
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository products;     // fake: every method returns null/false/empty until we "stub" it
    @Mock CategoryRepository categories;
    @Spy ProductMapper mapper = Mappers.getMapper(ProductMapper.class);      // REAL mapper, but Mockito watches it
    @InjectMocks ProductService service;        // the real class we're testing, built with the 3 objects above

    private final Category books = new Category("Books", "books");

    @Nested
    class Create{

        @Test
        void savesTheProductAndReturnsItsDetails() {
            // Arrange (given)
            ProductRequest request = new ProductRequest("BK-DDD-007", "Domain-Driven Design", "Addison-Wesley",
                    null, 2L, new BigDecimal("1499.00"));
            when(products.existsBySku("BK-DDD-007")).thenReturn(false);
            when(categories.findById(2L)).thenReturn(Optional.of(books));
            when(products.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));   // return what was passed in

            // Act (when)
            ProductDetails result = service.create(request);

            // Assert (then)
            assertThat(result.name()).isEqualTo("Domain-Driven Design");
            assertThat(result.slug()).isEqualTo("domain-driven-design");
            assertThat(result.categoryName()).isEqualTo("Books");

            ArgumentCaptor<Product> saved = ArgumentCaptor.forClass(Product.class);   // "what exactly did you save?"
            verify(products).save(saved.capture());
            assertThat(saved.getValue().getBrand()).isEqualTo("Addison-Wesley");
            assertThat(saved.getValue().getPrice()).isEqualByComparingTo("1499");
            verify(mapper).toDetails(saved.getValue());                                // the spy lets us check the real mapper was used
        }

        @Test
        void duplicateSkuIsRejectedAndNothingIsSaved() {
            when(products.existsBySku("BK-CLC-003")).thenReturn(true);
            ProductRequest request = new ProductRequest("BK-CLC-003", "Copy", null, null, 2L, BigDecimal.TEN);

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(DuplicateSkuException.class)
                    .hasMessage("SKU BK-CLC-003 already exists");
            verify(products, never()).save(any());
            verifyNoInteractions(categories);       // we stopped before even looking up the category
        }

        @Test
        void unknownCategoryIsRejected() {
            when(products.existsBySku(any())).thenReturn(false);
            when(categories.findById(99L)).thenReturn(Optional.empty());
            ProductRequest request = new ProductRequest("X-1", "Ghost", null, null, 99L, BigDecimal.TEN);

            assertThatThrownBy(() -> service.create(request)).isInstanceOf(CategoryNotFoundException.class);
            verify(products, never()).save(any());
        }
    }

    @Nested
    class Patch {

        @Test
        void changesOnlyTheFieldsThatWereSent() {
            Product mouse = new Product("EL-MOU-002", "Wireless Mouse", books, new BigDecimal("799.00"));
            mouse.setBrand("ShopVerse Basics");
            when(products.findById(2L)).thenReturn(Optional.of(mouse));

            ProductDetails result = service.patch(2L, new ProductPatchRequest(null, null, null, null, new BigDecimal("749.00")));

            assertThat(result.price()).isEqualByComparingTo("749");
            assertThat(result.name()).isEqualTo("Wireless Mouse");     // untouched
            assertThat(result.brand()).isEqualTo("ShopVerse Basics");  // untouched
            verify(products, never()).save(any());                     // no save(): in real life dirty checking writes the UPDATE
        }
    }

    @Test
    void getUnknownIdThrowsNotFound() {
        when(products.findWithDetailsById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product 99 not found");
    }
}
