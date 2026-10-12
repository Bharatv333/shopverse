package com.shopverse.product.pricing;

import com.shopverse.product.catalog.PricingRule;
import com.shopverse.product.catalog.Product;
import com.shopverse.product.catalog.ProductCatalog;
import com.shopverse.product.config.ShopProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class PricingController {

    private final ProductCatalog catalog;
    private final PricingService pricingService;
    private final ShopProperties shopProperties;


    public PricingController(ProductCatalog catalog, PricingService pricingService, ShopProperties shopProperties){
        this.catalog = catalog;
        this.pricingService = pricingService;
        this.shopProperties = shopProperties;
    }



    public record PriceView(Long id, String name, BigDecimal price, BigDecimal finalPrice, String currency){}

    @GetMapping("/pricing/products")
    public List<PriceView> prices(){
        return catalog.findAll().stream()
                .map(p -> new PriceView(p.id(), p.name(), p.price(), pricingService.finalPrice(p), shopProperties.currency()))
                .toList();
    }
}
