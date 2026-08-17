package com.boostphysioclinic.workforceapplication.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set the secret key using reflection to simulate @Value injection
        // Use a simple BASE64-encoded key that's valid
        String base64Key = java.util.Base64.getEncoder().encodeToString("mySecretKeyForTesting12345678901234567890".getBytes());
        ReflectionTestUtils.setField(jwtService, "secret", base64Key);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
        
        userDetails = User.builder()
                .username("admin")
                .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
                .roles("ADMIN")
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);
        
        assertEquals("admin", extractedUsername);
    }

    @Test
    void testIsTokenValid() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        
        assertTrue(isValid);
    }

    @Test
    void testIsTokenInvalid() {
        // Create a valid token but then modify it to make it invalid
        String validToken = jwtService.generateToken(userDetails);
        String invalidToken = validToken + "corrupted";
        
        // Corrupted tokens should throw signature exceptions or return false
        try {
            boolean isValid = jwtService.isTokenValid(invalidToken, userDetails);
            assertFalse(isValid);
        } catch (Exception e) {
            // Expected for corrupted tokens (signature exception)
            assertTrue(true);
        }
    }

    @Test
    void testIsTokenValidWithWrongUser() {
        String token = jwtService.generateToken(userDetails);
        
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .roles("USER")
                .build();
        
        boolean isValid = jwtService.isTokenValid(token, differentUser);
        
        assertFalse(isValid);
    }

    @Test
    void testExtractUsernameFromInvalidToken() {
        String invalidToken = "invalid.token";
        
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(invalidToken);
        });
    }
}
