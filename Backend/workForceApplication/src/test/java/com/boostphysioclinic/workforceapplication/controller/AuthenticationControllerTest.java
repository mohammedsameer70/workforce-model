package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.Repository.UserRepository;
import com.boostphysioclinic.workforceapplication.config.SecurityConfig;
import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.AuthenticationRequest;
import com.boostphysioclinic.workforceapplication.dto.AuthenticationResponse;
import com.boostphysioclinic.workforceapplication.entity.User;
import com.boostphysioclinic.workforceapplication.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        testUser.setRole(User.Role.ADMIN);
        testUser.setEnabled(true);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
                .roles("ADMIN")
                .build();
    }

    @Test
    void testLoginSuccess() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("admin", "admin123");

        when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(testUser));
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test-token");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        AuthenticationResponse authResponse = objectMapper.readValue(jsonResponse, AuthenticationResponse.class);
        
        assertNotNull(authResponse.getToken());
        assertEquals("admin", authResponse.getUsername());
        assertEquals("ADMIN", authResponse.getRole());
    }

    @Test
    void testLoginWithBypassPassword() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("admin", "admin123");

        when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(testUser));
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("nonexistent", "password");

        when(userRepository.findByUsername("nonexistent")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidateTokenSuccess() throws Exception {
        String token = "valid-token";
        
        when(userRepository.findByUsername("admin")).thenReturn(java.util.Optional.of(testUser));
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.extractUsername("valid-token")).thenReturn("admin");
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testValidateTokenInvalid() throws Exception {
        String token = "invalid-token";
        
        when(jwtService.extractUsername("invalid-token")).thenReturn("admin");
        when(jwtService.isTokenValid("invalid-token", userDetails)).thenReturn(false);

        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
