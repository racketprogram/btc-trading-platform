package com.btcplatform.trading;

import com.btcplatform.trading.service.BtcPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BtcPriceServiceTest {

    private BtcPriceService btcPriceService;

    @BeforeEach
    void setUp() {
        btcPriceService = new BtcPriceService(LocalDateTime.now());
    }

    @Test
    void testPriceCycle() {
        // 初始價格
        assertEquals(new BigDecimal("100"), btcPriceService.getCurrentPrice());

        // 1分鐘後 (價格上升)
        btcPriceService.updateStartTime(LocalDateTime.now().minusMinutes(1));
        BigDecimal price = btcPriceService.getCurrentPrice();
        assertTrue(price.compareTo(new BigDecimal("100")) > 0 && price.compareTo(new BigDecimal("220")) == 0);

        // 4分鐘後 (價格下降)
        btcPriceService.updateStartTime(LocalDateTime.now().minusMinutes(4));
        price = btcPriceService.getCurrentPrice();
        assertTrue(price.compareTo(new BigDecimal("460")) < 0 && price.compareTo(new BigDecimal("340")) == 0);

        // 6分鐘後 (完成一個週期)
        btcPriceService.updateStartTime(LocalDateTime.now().minusMinutes(6));
        assertEquals(new BigDecimal("100"), btcPriceService.getCurrentPrice());
    }
}