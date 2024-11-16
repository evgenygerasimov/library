package com.example.library.controller;

import com.example.library.entity.Token;
import com.example.library.security.AuthRequest;
import com.example.library.security.JwtService;
import com.example.library.security.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            jwtService.saveToken(userDetails.getUsername(), accessToken);
            return tokens;
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @PostMapping("/refresh")
    public TokenDTO refresh(@RequestBody TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        String username;
        try {
            username = jwtService.extractUserName(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
        String newAccessToken = jwtService.generateAccessToken(username);
        TokenDTO newTokenDTO = new TokenDTO();
        newTokenDTO.setAccessToken(newAccessToken);
        newTokenDTO.setRefreshToken(refreshToken);
        return newTokenDTO;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody TokenDTO tokenDTO) {
        Token token = jwtService.getToken(tokenDTO.getAccessToken());
        if (!token.isValid()){
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