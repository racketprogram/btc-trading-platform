package com.btcplatform.trading.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
public class BtcPriceService {

    private LocalDateTime startTime;

    public BtcPriceService() {
        this.startTime = LocalDateTime.now();
    }

    // 為測試添加的構造函數
    public BtcPriceService(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // 為測試添加的方法
    public void updateStartTime(LocalDateTime newStartTime) {
        this.startTime = newStartTime;
    }

    public BigDecimal getCurrentPrice() {
        LocalDateTime now = LocalDateTime.now();
        long secondsElapsed = Duration.between(startTime, now).getSeconds();
        int cycleSeconds = 360; // 6 minutes
        int halfCycleSeconds = 180; // 3 minutes

        int secondsInCycle = (int) (secondsElapsed % cycleSeconds);
        
        if (secondsInCycle < halfCycleSeconds) {
            // Price increasing
            return new BigDecimal("100").add(new BigDecimal(secondsInCycle / 5 * 10));
        } else {
            // Price decreasing
            return new BigDecimal("460").subtract(new BigDecimal((secondsInCycle - halfCycleSeconds) / 5 * 10));
        }
    }
}