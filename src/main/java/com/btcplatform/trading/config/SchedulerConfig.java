package com.btcplatform.trading.config;

import com.btcplatform.trading.service.BtcPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private BtcPriceService btcPriceService;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void updateBtcPrice() {
        btcPriceService.getCurrentPrice(); // This will trigger the price update
    }
}