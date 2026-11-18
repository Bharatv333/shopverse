package com.shopverse.product.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

// Same package as Slugs, so we can reach its package-private method.
class SlugsTest {

    // One test method, run once per line. Each line = input, expected output.
    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource({
            "Laptop Pro 14,            laptop-pro-14",
            "Spring in Action,         spring-in-action",
            "'  Hello,   World!  ',    hello-world",
            "C++ & Java,               c-java",
            "Café Crème,               caf-cr-me"
    })
    void makesUrlFriendlySlugs(String name, String expected) {
        assertThat(Slugs.of(name)).isEqualTo(expected);
    }
}
