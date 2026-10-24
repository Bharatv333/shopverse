package com.shopverse.product.domain;

import java.util.Locale;

final class Slugs {

    private Slugs(){

    }

    static String of(String text){
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+","-")
                .replaceAll("(^-|-$)","");
    }
}
