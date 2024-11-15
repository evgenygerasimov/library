package com.example.library.controller;

import com.example.library.security.AuthRequest;
import com.example.library.security.JwtService;
import com.example.library.security.TokenDTO;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public TokenDTO login(@RequestBody AuthRequest authRequest) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

            TokenDTO tokens = new TokenDTO();
            tokens.setAccessToken(accessToken);
            tokens.setRefreshToken(refreshToken);
            return tokens;
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        String username;

        try {
            Claims claims = jwtService.validateToken(refreshToken);
            username = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(username);
        Map<String, String> newTokens = new HashMap<>();
        newTokens.put("accessToken", newAccessToken);
        return newTokens;
    }
}