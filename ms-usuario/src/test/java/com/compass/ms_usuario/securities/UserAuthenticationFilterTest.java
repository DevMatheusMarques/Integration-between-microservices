package com.compass.ms_usuario.securities;

import com.compass.ms_usuario.models.User;
import com.compass.ms_usuario.repositories.UserRepository;
import com.compass.ms_usuario.securities.JwtTokenService;
import com.compass.ms_usuario.securities.UserAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationFilterTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAuthenticationFilter userAuthenticationFilter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testDoFilterInternal_UserAuthenticated() throws ServletException, IOException {
        String token = "valid-jwt-token";
        String userEmail = "user@example.com";

        User user = new User();
        user.setEmail(userEmail);

        when(jwtTokenService.getSubjectFromToken(token)).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        request.addHeader("Authorization", "Bearer " + token);

        userAuthenticationFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userEmail, authentication.getPrincipal());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_UserNotFound() {
        String token = "valid-jwt-token";
        String userEmail = "user@example.com";

        when(jwtTokenService.getSubjectFromToken(token)).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        request.addHeader("Authorization", "Bearer " + token);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userAuthenticationFilter.doFilterInternal(request, response, filterChain)
        );
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void testDoFilterInternal_NoTokenInRequest() throws ServletException, IOException {
        userAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }
}
