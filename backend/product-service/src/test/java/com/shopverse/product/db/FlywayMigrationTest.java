package com.shopverse.product.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FlywayMigrationTest {
    @Autowired JdbcTemplate jdbc;

    @Test
    void allMigrationsRanSuccessfully(){
        List<String> versions = jdbc.queryForList("SELECT version FROM flyway_schema_history WHERE success = 1 ORDER BY installed_rank", String.class);
        assertEquals(List.of("1", "2", "3"), versions);
    }

    @Test
    void seedDataIsThere(){
        assertEquals(4, jdbc.queryForObject("SELECT COUNT(*) FROM categories", Integer.class));
        assertEquals(6, jdbc.queryForObject("SELECT COUNT(*) FROM products", Integer.class));
        assertEquals(new BigDecimal("650.00"),
                jdbc.queryForObject("SELECT price FROM products WHERE sku = 'BK-CLC-003'", BigDecimal.class));
    }
}
