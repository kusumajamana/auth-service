package com.demo.authentication_service.controller;

import com.demo.authentication_service.dao.entity.UserCredentialsEntity;
import com.demo.authentication_service.dao.entity.Role;
import com.demo.authentication_service.service.JwtService;
import com.demo.authentication_service.service.UserCredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserCredentialsController {
    @Autowired
    JwtService jwtService;

    @Autowired
    private UserCredentialsService userCredService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping("/register")
    public UserCredentialsEntity register(@RequestBody UserCredentialsEntity user) {
        return userCredService.register(user);
    }
    @GetMapping("/validate/token")
    public boolean validateToken(@RequestParam String token) {
        return userCredService.verifyToken(token);
    }
    @PostMapping("/login/user")
    public ResponseEntity<?> userLogin(@RequestBody UserCredentialsEntity user) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if (authenticate.isAuthenticated()) {
                UserCredentialsEntity userDetails = userCredService.findByName(user.getName());
                if (!hasRole(userDetails, Role.ERole.ROLE_USER)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Access denied: User role required");
                }
                return ResponseEntity.ok(userCredService.generateToken(user.getUsername()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> adminLogin(@RequestBody UserCredentialsEntity user) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if (authenticate.isAuthenticated()) {
                UserCredentialsEntity userDetails = userCredService.findByName(user.getName());
                if (!hasRole(userDetails, Role.ERole.ROLE_ADMIN)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Access denied: Admin role required");
                }
                return ResponseEntity.ok(userCredService.generateToken(user.getUsername()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean hasRole(UserCredentialsEntity user, Role.ERole roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == roleName);
    }
}

