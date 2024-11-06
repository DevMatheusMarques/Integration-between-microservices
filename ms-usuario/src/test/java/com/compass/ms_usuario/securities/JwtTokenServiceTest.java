package com.compass.ms_usuario.securities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.ms_usuario.securities.JwtTokenService;
import com.compass.ms_usuario.securities.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void testGenerateToken_Success() {
        String token = jwtTokenService.generateToken(userDetails);
        assertNotNull(token);
    }

    @Test
    void testGenerateToken_Exception() {
        JwtTokenService serviceWithException = new JwtTokenService() {
            @Override
            public String generateToken(UserDetailsImpl user) {
                throw new JWTCreationException("Error generating token.", null);
            }
        };

        JWTCreationException exception = assertThrows(JWTCreationException.class, () ->
                serviceWithException.generateToken(userDetails)
        );
        assertEquals("Error generating token.", exception.getMessage());
    }

    @Test
    void testGetSubjectFromToken_Success() {
        String token = jwtTokenService.generateToken(userDetails);
        String subject = jwtTokenService.getSubjectFromToken(token);
        assertEquals("testUser", subject);
    }

    @Test
    void testGetSubjectFromToken_InvalidToken() {
        JWTVerificationException exception = assertThrows(JWTVerificationException.class, () ->
                jwtTokenService.getSubjectFromToken("invalid-token")
        );
        assertEquals("Invalid or expired token.", exception.getMessage());
    }

    @Test
    void testCreationDate() {
        Instant now = ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
        Instant createdDate = jwtTokenService.creationDate();
        assertTrue(createdDate.isAfter(now.minusSeconds(5)) && createdDate.isBefore(now.plusSeconds(5)));
    }

    @Test
    void testExpirationDate() {
        Instant now = ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
        Instant expirationDate = jwtTokenService.expirationDate();
        assertTrue(expirationDate.isAfter(now.plusSeconds(3595)) && expirationDate.isBefore(now.plusSeconds(3605)));
    }
}
