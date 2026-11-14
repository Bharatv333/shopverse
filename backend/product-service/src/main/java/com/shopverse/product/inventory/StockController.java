package com.shopverse.product.inventory;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;
    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    public record StockView(Long productId, int available, int reserved){}
    public record ReserveRequest(@Min(1) @Max(100) int quantity){}

    @Operation(operationId = "getStock", summary = "How many items are available and reserved")
    @GetMapping("/{productId}")
    public StockView get(@PathVariable Long productId){
        Stock s = stockService.get(productId);
        return new StockView(s.getProductId(), s.getAvailable(), s.getReserved());
    }

    @Operation(operationId = "reserveStock", summary = "Reserve items for an order")
    @PostMapping("/{productId}/reserve")
    public StockView reserve(@PathVariable Long productId, @Valid @RequestBody ReserveRequest request){
        stockService.reserveAtomic(productId, request.quantity());
        return get(productId);
    }
    // 404 / 409 / 400 now come from GlobalExceptionHandler
}
