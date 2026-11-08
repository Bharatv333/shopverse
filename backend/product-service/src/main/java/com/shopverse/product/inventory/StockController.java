package com.shopverse.product.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;
    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    public record StockView(Long productId, int available, int reserved){}
    public record ReserveRequest(int quantity){}

    @GetMapping("/{productId}")
    public StockView get(@PathVariable Long productId){
        Stock s = stockService.get(productId);
        return new StockView(s.getProductId(), s.getAvailable(), s.getReserved());
    }

    @PostMapping("/{productId}/reserve")
    public StockView reserve(@PathVariable Long productId, @RequestBody ReserveRequest request){
        stockService.reserveAtomic(productId, request.quantity);
        return get(productId);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ProblemDetail> outOfStock(OutOfStockException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage()));
    }

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(StockNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> badRequest(IllegalArgumentException e){
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}
