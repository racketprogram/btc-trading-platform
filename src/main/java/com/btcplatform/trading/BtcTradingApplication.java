package com.btcplatform.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BtcTradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BtcTradingApplication.class, args);
    }
}