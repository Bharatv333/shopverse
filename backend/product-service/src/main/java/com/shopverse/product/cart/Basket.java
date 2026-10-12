package com.shopverse.product.cart;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")          // "give a NEW Basket to whoever asks"
public class Basket {
    private final List<String> items = new ArrayList<>();

    public void add(String item) { items.add(item); }
    public List<String> items() { return items; }
}