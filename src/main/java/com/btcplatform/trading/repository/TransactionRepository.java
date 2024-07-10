package com.btcplatform.trading.repository;

import com.btcplatform.trading.model.Transaction;
import com.btcplatform.trading.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByTimestampDesc(User user);
}