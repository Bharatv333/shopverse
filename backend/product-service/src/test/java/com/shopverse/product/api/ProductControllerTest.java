package com.shopverse.product.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional          // each test's changes are rolled back at the end: the database stays clean
class ProductControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void listsFirstPage() throws Exception {
        mvc.perform(get("/api/products").param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(4))
                .andExpect(jsonPath("$.page.totalElements").value(6))
                .andExpect(jsonPath("$.page.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].categoryName").value("Electronics"));
    }

    @Test
    void sortsByPriceDescending() throws Exception {
        mvc.perform(get("/api/products").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop Pro 14"))
                .andExpect(jsonPath("$.content[5].name").value("Coffee Mug"));
    }

    @Test
    void combinesFilters() throws Exception {
        mvc.perform(get("/api/products")
                        .param("categoryId", "2")
                        .param("minPrice", "700")
                        .param("q", "spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].sku").value("BK-SIA-004"));
    }

    @Test
    void unknownSortFieldGives400() throws Exception {
        mvc.perform(get("/api/products").param("sort", "password"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getsOneWithImages() throws Exception {
        mvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro 14"))
                .andExpect(jsonPath("$.categoryName").value("Electronics"))
                .andExpect(jsonPath("$.images.length()").value(2));
    }

    @Test
    void unknownIdGives404() throws Exception {
        mvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product 99 not found"));
    }

    @Test
    void createsProduct() throws Exception {
        String body = """
                {"sku":"HM-LMP-007","name":"Desk Lamp","brand":"Lumo","categoryId":4,"price":1299}
                """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                // not "/7": a rolled-back insert still uses up an AUTO_INCREMENT number
                .andExpect(header().string("Location", matchesPattern(".*/api/products/\\d+")))
                .andExpect(jsonPath("$.slug").value("desk-lamp"))
                .andExpect(jsonPath("$.categoryName").value("Home"));
    }

    @Test
    void duplicateSkuGives400() throws Exception {
        String body = """
                {"sku":"BK-CLC-003","name":"Clean Code copy","categoryId":2,"price":10}
                """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("SKU BK-CLC-003 already exists"));
    }

    @Test
    void unknownCategoryGives404() throws Exception {
        String body = """
                {"sku":"X-1","name":"Ghost","categoryId":99,"price":10}
                """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchChangesOnlySentFields() throws Exception {
        mvc.perform(patch("/api/products/2").contentType(MediaType.APPLICATION_JSON).content("""
                {"price":749}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(749))
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.brand").value("ShopVerse Basics"));
    }

    @Test
    void deleteHidesProduct() throws Exception {
        mvc.perform(delete("/api/products/6")).andExpect(status().isNoContent());
        mvc.perform(get("/api/products/6")).andExpect(status().isNotFound());
        mvc.perform(get("/api/products")).andExpect(jsonPath("$.page.totalElements").value(5));
    }

    @Test
    void listsCategoriesWithCounts() throws Exception {
        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].productCount").value(2));
    }

    @Test
    void corsAllowsAngularDevServer() throws Exception {
        mvc.perform(options("/api/products")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void corsBlocksOtherSites() throws Exception {
        mvc.perform(options("/api/products")
                        .header("Origin", "http://evil.example")
                        .header("Access-Control-Request-Method", "DELETE"))
                .andExpect(status().isForbidden());
    }
}