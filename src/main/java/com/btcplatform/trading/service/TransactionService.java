package com.btcplatform.trading.service;

import com.btcplatform.trading.model.Transaction;
import com.btcplatform.trading.model.User;
import com.btcplatform.trading.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BtcPriceService btcPriceService;

    @Transactional
    public Transaction buyBtc(Long userId, BigDecimal btcAmount) {
        User user = userService.getUserById(userId);
        BigDecimal currentBtcPrice = btcPriceService.getCurrentPrice();
        BigDecimal totalCost = currentBtcPrice.multiply(btcAmount);

        if (user.getUsdBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient USD balance");
        }

        user.setUsdBalance(user.getUsdBalance().subtract(totalCost));
        user.setBtcBalance(user.getBtcBalance().add(btcAmount));
        userService.updateUser(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType("BUY");
        transaction.setBtcAmount(btcAmount);
        transaction.setUsdAmount(totalCost);
        transaction.setBtcPrice(currentBtcPrice);
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction sellBtc(Long userId, BigDecimal btcAmount) {
        User user = userService.getUserById(userId);

        if (user.getBtcBalance().compareTo(btcAmount) < 0) {
            throw new RuntimeException("Insufficient BTC balance");
        }

        BigDecimal currentBtcPrice = btcPriceService.getCurrentPrice();
        BigDecimal totalEarned = currentBtcPrice.multiply(btcAmount);

        user.setBtcBalance(user.getBtcBalance().subtract(btcAmount));
        user.setUsdBalance(user.getUsdBalance().add(totalEarned));
        userService.updateUser(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType("SELL");
        transaction.setBtcAmount(btcAmount);
        transaction.setUsdAmount(totalEarned);
        transaction.setBtcPrice(currentBtcPrice);
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
    
    public List<Transaction> getUserTransactions(Long userId) {
        User user = userService.getUserById(userId);
        return transactionRepository.findByUserOrderByTimestampDesc(user);
    }
}