package com.example.library.security;


import com.example.library.entity.Token;
import com.example.library.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${myapp.secret.key}")
    private String secretKey;
    private final long validityTenMinutes = 600000;
    private final long validityThirtyMinutes = 1800000;
    @Autowired
    private TokenRepository tokenRepository;

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityTenMinutes))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityThirtyMinutes))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void invalidateToken(String accessToken) {
        Optional<Token> tokenOpt = tokenRepository.findByAccessToken(accessToken);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.setValid(false);
            tokenRepository.save(token);
        } else {
            throw new RuntimeException("Token not found");
        }
    }

    public void saveToken(String username, String accessToken) {
        Token token = new Token();
        token.setUsername(username);
        token.setAccessToken(accessToken);
        token.setValid(true);
        tokenRepository.save(token);
    }

    public Token getToken(String token){
        Token tokenObj = null;
        if (token.startsWith("Bearer ")){
            tokenObj = tokenRepository.findByAccessToken(token.substring(7)).get();
        } else {
            tokenObj = tokenRepository.findByAccessToken(token).get();
        }
        return tokenObj;
    }
}