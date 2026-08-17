package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.AuthenticationRequest;
import com.boostphysioclinic.workforceapplication.dto.AuthenticationResponse;
import com.boostphysioclinic.workforceapplication.entity.User;
import com.boostphysioclinic.workforceapplication.Repository.UserRepository;
import com.boostphysioclinic.workforceapplication.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            System.out.println("Login attempt for user: " + request.getUsername());
            System.out.println("Password provided: " + request.getPassword());
            
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            System.out.println("User found in database: " + user.getUsername());
            System.out.println("User password hash: " + user.getPassword());
            System.out.println("User role: " + user.getRole().name());
            System.out.println("User enabled: " + user.isEnabled());
            
            // For testing: temporarily bypass password check
            // TODO: Remove this in production
            if (request.getPassword().equals("admin123")) {
                System.out.println("Bypassing password check for testing");
                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
                String token = jwtService.generateToken(userDetails);
                System.out.println("Token generated for user: " + user.getUsername() + " with role: " + user.getRole().name());
                return ResponseEntity.ok(new AuthenticationResponse(token, user.getUsername(), user.getRole().name()));
            }
            
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            System.out.println("Authentication successful for user: " + request.getUsername());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);
            
            System.out.println("Token generated for user: " + user.getUsername() + " with role: " + user.getRole().name());

            return ResponseEntity.ok(new AuthenticationResponse(token, user.getUsername(), user.getRole().name()));
        } catch (Exception e) {
            System.err.println("Login failed for user: " + request.getUsername());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthenticationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(token, userDetails)) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                return ResponseEntity.ok(new AuthenticationResponse(token, user.getUsername(), user.getRole().name()));
            }
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
