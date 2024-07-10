package com.btcplatform.trading.service;

import com.btcplatform.trading.model.Transaction;
import com.btcplatform.trading.model.User;
import com.btcplatform.trading.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private BtcPriceService btcPriceService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuyBtc() {
        Long userId = 1L;
        BigDecimal btcAmount = new BigDecimal("0.1");
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsdBalance(new BigDecimal("10000"));
        mockUser.setBtcBalance(BigDecimal.ZERO);

        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(btcPriceService.getCurrentPrice()).thenReturn(new BigDecimal("50000"));
        when(userService.updateUser(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction transaction = transactionService.buyBtc(userId, btcAmount);

        assertNotNull(transaction);
        assertEquals("BUY", transaction.getType());
        assertEquals(0, btcAmount.compareTo(transaction.getBtcAmount()));
        assertEquals(0, new BigDecimal("5000").compareTo(transaction.getUsdAmount()));
        
        verify(userService, times(1)).getUserById(userId);
        verify(btcPriceService, times(1)).getCurrentPrice();
        verify(userService, times(1)).updateUser(any(User.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSellBtc() {
        Long userId = 1L;
        BigDecimal btcAmount = new BigDecimal("0.1");
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsdBalance(new BigDecimal("1000"));
        mockUser.setBtcBalance(new BigDecimal("0.2"));

        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(btcPriceService.getCurrentPrice()).thenReturn(new BigDecimal("50000"));
        when(userService.updateUser(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction transaction = transactionService.sellBtc(userId, btcAmount);

        assertNotNull(transaction);
        assertEquals("SELL", transaction.getType());
        assertEquals(0, btcAmount.compareTo(transaction.getBtcAmount()));
        assertEquals(0, new BigDecimal("5000").compareTo(transaction.getUsdAmount()));
        
        verify(userService, times(1)).getUserById(userId);
        verify(btcPriceService, times(1)).getCurrentPrice();
        verify(userService, times(1)).updateUser(any(User.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testGetUserTransactions() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        Transaction mockTransaction1 = new Transaction();
        Transaction mockTransaction2 = new Transaction();
        List<Transaction> mockTransactions = Arrays.asList(mockTransaction1, mockTransaction2);

        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(transactionRepository.findByUserOrderByTimestampDesc(mockUser)).thenReturn(mockTransactions);

        List<Transaction> result = transactionService.getUserTransactions(userId);

        assertEquals(2, result.size());
        verify(userService, times(1)).getUserById(userId);
        verify(transactionRepository, times(1)).findByUserOrderByTimestampDesc(mockUser);
    }

    @Test
    void testBuyBtcInsufficientBalance() {
        Long userId = 1L;
        BigDecimal btcAmount = new BigDecimal("0.1");
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsdBalance(new BigDecimal("100")); // Not enough balance

        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(btcPriceService.getCurrentPrice()).thenReturn(new BigDecimal("50000"));

        assertThrows(RuntimeException.class, () -> transactionService.buyBtc(userId, btcAmount));

        verify(userService, times(1)).getUserById(userId);
        verify(btcPriceService, times(1)).getCurrentPrice();
        verify(userService, never()).updateUser(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testSellBtcInsufficientBalance() {
        Long userId = 1L;
        BigDecimal btcAmount = new BigDecimal("0.1");
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBtcBalance(new BigDecimal("0.05")); // Not enough balance

        when(userService.getUserById(userId)).thenReturn(mockUser);

        assertThrows(RuntimeException.class, () -> transactionService.sellBtc(userId, btcAmount));

        verify(userService, times(1)).getUserById(userId);
        verify(btcPriceService, never()).getCurrentPrice();
        verify(userService, never()).updateUser(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}