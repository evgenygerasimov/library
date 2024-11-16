package com.example.library.controller;

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
        String response = authService.logout(tokenDTO);
        if (response.equals("You have already logged out")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
}