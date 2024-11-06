package com.compass.ms_usuario.securities;

import com.compass.ms_usuario.models.User;
import com.compass.ms_usuario.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsername_UserFound() {
        String username = "user@example.com";
        User user = new User();
        user.setEmail(username);
        user.setPassword("password");

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername(username);

        assertNotNull(result);
        assertTrue(result instanceof UserDetailsImpl);
        assertEquals(username, result.getUsername());
        assertEquals("password", result.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistent@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        assertEquals("User not found.", exception.getMessage());
    }
}
