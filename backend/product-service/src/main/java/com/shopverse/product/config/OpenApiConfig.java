package com.shopverse.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// The title box at the top of Swagger UI. springdoc finds the endpoints on its own.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productApi(){
        return new OpenAPI().info(new Info()
                .title("ShopVerse Product API")
                .version("v1")
                .description("Products, categories and stock for the ShopVerse store."));
    }
}
