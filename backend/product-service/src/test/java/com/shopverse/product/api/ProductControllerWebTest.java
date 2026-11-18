package com.shopverse.product.api;

import com.shopverse.product.catalog.ProductNotFoundException;
import com.shopverse.product.service.DuplicateSkuException;
import com.shopverse.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// A SLICE test: Spring starts ONLY the web layer for ProductController
// (controller, JSON, validation, GlobalExceptionHandler, filters). No database, no services, no Docker.
// The service is replaced by a Mockito mock that lives inside the Spring context (@MockitoBean).
@WebMvcTest(ProductController.class)
class ProductControllerWebTest {

    @Autowired MockMvc mvc;
    @MockitoBean ProductService service;

    private static ProductDetails mug() {
        return new ProductDetails(6L, "HM-MUG-006", "Coffee Mug", null, "coffee-mug", null,
                new BigDecimal("299.00"), "INR", BigDecimal.ZERO, 0, 4L, "Home", List.of());
    }

    @Test
    void returnsTheProductAsJson() throws Exception {
        when(service.get(6L)).thenReturn(mug());

        mvc.perform(get("/api/products/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Coffee Mug"))
                .andExpect(jsonPath("$.price").value(299.00));
    }

    @Test
    void notFoundExceptionBecomes404ProblemDetail() throws Exception {
        when(service.get(99L)).thenThrow(new ProductNotFoundException(99L));

        mvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Product 99 not found"));
    }

    @Test
    void invalidBodyNeverReachesTheService() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("""
                        {"sku":"HM-LMP-007","name":"","categoryId":4,"price":0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(2));

        verifyNoInteractions(service);          // validation stopped it at the door
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        when(service.create(any())).thenReturn(mug());

        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("""
                        {"sku":"HM-MUG-006","name":"Coffee Mug","categoryId":4,"price":299}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/6"));

        // JSON -> ProductRequest worked: check what the controller handed to the service
        verify(service).create(argThat(request -> request.sku().equals("HM-MUG-006")
                && request.price().compareTo(new BigDecimal("299")) == 0));
    }

    @Test
    void duplicateSkuBecomes409() throws Exception {
        when(service.create(any())).thenThrow(new DuplicateSkuException("HM-MUG-006"));

        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("""
                        {"sku":"HM-MUG-006","name":"Coffee Mug","categoryId":4,"price":299}
                        """))
                .andExpect(status().isConflict());
    }
}
