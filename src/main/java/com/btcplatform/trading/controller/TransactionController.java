package com.btcplatform.trading.controller;

import com.btcplatform.trading.model.Transaction;
import com.btcplatform.trading.service.TransactionService;
import com.btcplatform.trading.service.BtcPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BtcPriceService btcPriceService;

    @PostMapping("/buy")
    public ResponseEntity<Transaction> buyBtc(@RequestParam Long userId, @RequestParam BigDecimal btcAmount) {
        try {
            Transaction transaction = transactionService.buyBtc(userId, btcAmount);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<Transaction> sellBtc(@RequestParam Long userId, @RequestParam BigDecimal btcAmount) {
        try {
            Transaction transaction = transactionService.sellBtc(userId, btcAmount);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable Long userId) {
        try {
            List<Transaction> transactions = transactionService.getUserTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        }
    }

    @GetMapping("/btc/price")
    public ResponseEntity<BigDecimal> getBtcPrice() {
        BigDecimal currentPrice = btcPriceService.getCurrentPrice();
        return ResponseEntity.ok(currentPrice);
    }
}