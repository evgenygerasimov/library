package com.example.library.service;

import com.example.library.entity.Token;
import com.example.library.security.AuthRequest;
import com.example.library.security.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public TokenDTO login(AuthRequest authRequest) {
        TokenDTO tokenDTO = new TokenDTO();
        for (Token token : jwtService.getTokens()) {
            if (token.getUsername().equals(authRequest.getUsername()) && token.isValid()) {
                tokenDTO.setAccessToken(token.getAccessToken());
                return tokenDTO;
            }
        }
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
            tokenDTO = new TokenDTO();
            tokenDTO.setAccessToken(accessToken);
            tokenDTO.setRefreshToken(refreshToken);
            jwtService.saveToken(userDetails.getUsername(), accessToken);
            return tokenDTO;
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public TokenDTO refreshToken(TokenDTO tokenDTO) {
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

    public String logout(TokenDTO tokenDTO) {
        Token token = jwtService.getAccessToken(tokenDTO.getAccessToken());
        if (!token.isValid()) {
            return "You have already logged out";
        }
        String accessToken = tokenDTO.getAccessToken();
        jwtService.invalidateToken(accessToken);
        return "Successfully logged out";
    }
}
