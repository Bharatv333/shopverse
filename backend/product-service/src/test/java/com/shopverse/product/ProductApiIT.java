package com.shopverse.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// END-TO-END: the whole app on a real port, real HTTP, real MySQL in Docker.
// The name ends in "IT", so Maven runs it with the failsafe plugin during "mvn verify", not "mvn test".
// NOT @Transactional: the request runs on a server thread, so a test transaction couldn't roll it back anyway.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class ProductApiIT {

    @Autowired RestTestClient client;

    @Test
    void productLifecycle() {
        // a unique SKU, so the test can run again on the same database
        String sku = "IT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 1. create
        URI location = client.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"sku":"%s","name":"Integration Lamp","categoryId":4,"price":1299}
                        """.formatted(sku))
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.slug").isEqualTo("integration-lamp")
                .returnResult().getResponseHeaders().getLocation();
        assertThat(location).isNotNull();

        // 2. read it back
        client.get().uri(location).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.sku").isEqualTo(sku);

        // 3. change the price
        client.patch().uri(location).contentType(MediaType.APPLICATION_JSON)
                .body("{\"price\":999}")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.price").isEqualTo(999.0);

        // 4. the same SKU again -> 409
        client.post().uri("/api/products").contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"sku":"%s","name":"Copy","categoryId":4,"price":1}
                        """.formatted(sku))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        // 5. delete, then it's gone
        client.delete().uri(location).exchange().expectStatus().isNoContent();
        client.get().uri(location).exchange().expectStatus().isNotFound();
    }

    @Test
    void responsesCarryARequestId() {
        client.get().uri("/api/products/99")
                .header("X-Request-Id", "it-test-1")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().valueEquals("X-Request-Id", "it-test-1")
                .expectBody().jsonPath("$.requestId").isEqualTo("it-test-1");
    }
}