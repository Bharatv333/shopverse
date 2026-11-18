package com.shopverse.product.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerWebTest {

    @Autowired MockMvc mvc;
    @MockitoBean StockService stockService;

    @Test
    void outOfStockBecomes409() throws Exception {
        doThrow(new OutOfStockException(4L, 1, 0)).when(stockService).reserveAtomic(4L, 1);

        mvc.perform(post("/api/stock/4/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Product 4: wanted 1, only 0 available"));
    }

    @Test
    void zeroQuantityNeverReachesTheService() throws Exception {

        mvc.perform(post("/api/stock/4/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("quantity"));

        verifyNoInteractions(stockService);
    }

    @Test
    void successReturnsTheNewStock() throws Exception {

        // Arrange: build a fake Stock (no public constructor) and teach it 3 answers
        Stock after = mock(Stock.class);
        when(after.getProductId()).thenReturn(1L);
        when(after.getAvailable()).thenReturn(7);
        when(after.getReserved()).thenReturn(3);
        when(stockService.get(1L)).thenReturn(after);       // the controller calls get() after reserving

        // Act + Assert
        mvc.perform(post("/api/stock/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(7))
                .andExpect(jsonPath("$.reserved").value(3));

        verify(stockService).reserveAtomic(1L, 3);      // the controller passed the right values on
    }
}
