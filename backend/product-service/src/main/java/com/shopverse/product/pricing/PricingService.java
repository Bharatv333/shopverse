package com.shopverse.product.pricing;

import com.shopverse.product.catalog.PricingRule;
import com.shopverse.product.catalog.Product;
import com.shopverse.product.config.ShopProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service
public class PricingService {
    private static final Logger log = LoggerFactory.getLogger(PricingService.class);

    private final Map<String, PricingRule> rules;
    private final ShopProperties shopProperties;

    public PricingService(Map<String, PricingRule> rules, ShopProperties shopProperties){
        this.rules = rules;
        this.shopProperties = shopProperties;
    }

    @PostConstruct
    void logSetup(){
        log.info("Pricing rules found: {} | active rules: {}", rules.keySet(), shopProperties.activePricingRule());
    }

    public PricingRule activeRule(){
        PricingRule rule = rules.get(shopProperties.activePricingRule());
        if(rule == null){
            throw new IllegalStateException("Unknown pricing rule: " + shopProperties.activePricingRule()
                    + ". Available: " + rules.keySet());
        }
        return rule;
    }

    public BigDecimal finalPrice(Product product){
        return activeRule().apply(product);
    }
}
