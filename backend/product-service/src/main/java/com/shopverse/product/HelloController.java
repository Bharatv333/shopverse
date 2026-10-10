package com.shopverse.product;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")  //GET http://localhost:8081/hello?name=MT
    public Map<String, String> hello(@RequestParam(defaultValue = "shopper") String name){
        return Map.of("message","Welcome to shopverse, "+name+"!");
    }
}