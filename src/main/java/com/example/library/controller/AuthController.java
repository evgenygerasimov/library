package com.example.library.controller;

import com.example.library.entity.Token;
import com.example.library.security.AuthRequest;
import com.example.library.service.AuthService;
import com.example.library.service.JwtService;
import com.example.library.security.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        TokenDTO tokenDTO = authService.login(authRequest);
        if (tokenDTO.getRefreshToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(tokenDTO);
        }
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/refresh")
    public TokenDTO refresh(@RequestBody TokenDTO tokenDTO) {
        return authService.refreshToken(tokenDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody TokenDTO tokenDTO) {
        Token token = jwtService.getAccessToken(tokenDTO.getAccessToken());
        if (!token.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already logged out");
        }
        String accessToken = tokenDTO.getAccessToken();
        try {
            jwtService.invalidateToken(accessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to logout");
        }
        return ResponseEntity.ok("Successfully logged out");
    }
}