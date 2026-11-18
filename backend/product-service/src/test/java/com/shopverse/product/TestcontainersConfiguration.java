package com.shopverse.product;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

// One real MySQL 8.4 in Docker, started automatically for the tests.
// @ServiceConnection: Spring Boot reads the container's random port, user and password
// and points spring.datasource.* at it. No URL to configure, nothing to start by hand.
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final MySQLContainer MYSQL = new MySQLContainer(DockerImageName.parse("mysql:8.4"));

    @Bean
    @ServiceConnection
    MySQLContainer mysql(){
        return MYSQL;
    }
}
