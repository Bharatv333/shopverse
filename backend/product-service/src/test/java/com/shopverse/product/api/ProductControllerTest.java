package com.shopverse.product.api;

import com.shopverse.product.config.CatalogConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(CatalogConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void listsAllProducts() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void filtersByCategory() throws Exception {
        mvc.perform(get("/api/products").param("category", "BOOKS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Clean Code"));
    }

    @Test
    void getsOneProduct() throws Exception {
        mvc.perform(get("/api/products/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clean Code"))
                .andExpect(jsonPath("$.price").value(650));
    }

    @Test
    void unknownIdGives404() throws Exception {
        mvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product 99 not found"));
    }

    @Test
    void createsProductWith201AndLocation() throws Exception {
        String body = """
                {"name":"Desk Lamp","category":"HOME","price":1299,"stock":30}
                """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/products/7")))
                .andExpect(jsonPath("$.id").value(7));

        mvc.perform(get("/api/products/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Desk Lamp"));
    }

    @Test
    void blankNameGives400() throws Exception {
        String body = """
                {"name":"","category":"HOME","price":100,"stock":1}
                """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putReplacesWholeProduct() throws Exception {
        String body = """
                {"name":"Wireless Mouse V2","category":"ELECTRONICS","price":999,"stock":80}
                """;
        mvc.perform(put("/api/products/2").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wireless Mouse V2"))
                .andExpect(jsonPath("$.stock").value(80))
                .andExpect(jsonPath("$.rating").value(4.2));
    }

    @Test
    void patchChangesOnlySentFields() throws Exception {
        mvc.perform(patch("/api/products/2").contentType(MediaType.APPLICATION_JSON).content("""
                {"price":749}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(749))
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.stock").value(150));
    }

    @Test
    void deleteGives204ThenProductIsGone() throws Exception {
        mvc.perform(delete("/api/products/6")).andExpect(status().isNoContent());
        mvc.perform(get("/api/products/6")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/products/6")).andExpect(status().isNotFound());
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
