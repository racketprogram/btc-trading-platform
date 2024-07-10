package com.btcplatform.trading.service;

import com.btcplatform.trading.model.User;
import com.btcplatform.trading.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        String name = "John Doe";
        String email = "john@example.com";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName(name);
        mockUser.setEmail(email);
        mockUser.setUsdBalance(new BigDecimal("1000"));
        mockUser.setBtcBalance(BigDecimal.ZERO);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User createdUser = userService.createUser(name, email);

        assertNotNull(createdUser);
        assertEquals(name, createdUser.getName());
        assertEquals(email, createdUser.getEmail());
        assertEquals(new BigDecimal("1000"), createdUser.getUsdBalance());
        assertEquals(BigDecimal.ZERO, createdUser.getBtcBalance());
        assertFalse(createdUser.isDeleted());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        userService.deleteUser(userId);

        assertTrue(mockUser.isDeleted());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testDeleteNonExistentUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setDeleted(false);

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(mockUser));

        User user = userService.getUserById(userId);

        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertFalse(user.isDeleted());

        verify(userRepository, times(1)).findByIdAndDeletedFalse(userId);
    }

    @Test
    void testGetNonExistentUser() {
        Long userId = 1L;
        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));

        verify(userRepository, times(1)).findByIdAndDeletedFalse(userId);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setUsdBalance(new BigDecimal("1000"));
        user.setBtcBalance(BigDecimal.ZERO);

        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getUsdBalance(), updatedUser.getUsdBalance());
        assertEquals(user.getBtcBalance(), updatedUser.getBtcBalance());

        verify(userRepository, times(1)).save(user);
    }
}