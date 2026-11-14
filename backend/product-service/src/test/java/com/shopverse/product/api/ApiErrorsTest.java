package com.shopverse.product.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiErrorsTest {

    @Autowired MockMvc mvc;

    @Test
    void invalidProductListsEveryBadField() throws Exception {
        String body = """
                {"sku":"bad sku","name":"  ","categoryId":2,"price":-5}
                """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.instance").value("/api/products"))
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("name")))
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("price")))
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("sku")));
    }

    @Test
    void skuIsRequiredOnCreate() throws Exception {
        String body = """
                {"name":"Desk Lamp","categoryId":4,"price":1299}
                """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("sku"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
    }

    @Test
    void skuIsNotNeededOnReplace() throws Exception {   // validation group: OnCreate rules don't run for PUT
        String body = """
                {"name":"Wireless Mouse v2","categoryId":1,"price":899}
                """;
        mvc.perform(put("/api/products/2").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wireless Mouse v2"));
    }

    @Test
    void patchRejectsBlankNameButAllowsMissingName() throws Exception {
        mvc.perform(patch("/api/products/2").contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"   "}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"));

        mvc.perform(patch("/api/products/2").contentType(MediaType.APPLICATION_JSON).content("""
                {"price":799}
                """))
                .andExpect(status().isOk());
    }

    @Test
    void negativeMinPriceInQueryGives400() throws Exception {
        mvc.perform(get("/api/products").param("minPrice", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("minPrice"));
    }

    @Test
    void brokenJsonGives400() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("{\"name\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void reserveZeroGives400() throws Exception {
        mvc.perform(post("/api/stock/1/reserve").contentType(MediaType.APPLICATION_JSON).content("""
                {"quantity":0}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("quantity"));
    }

    @Test
    void outOfStockGives409FromTheGlobalHandler() throws Exception {
        mvc.perform(post("/api/stock/4/reserve").contentType(MediaType.APPLICATION_JSON).content("""
                {"quantity":1}
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }

    @Test
    void openApiSpecIsPublished() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("ShopVerse Product API"))
                .andExpect(jsonPath("$.paths['/api/products'].get.operationId").value("listProducts"))
                .andExpect(jsonPath("$.components.schemas.ProductRequest.properties.price.minimum").value(0.01));
    }
}
