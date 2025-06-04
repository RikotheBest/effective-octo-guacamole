package com.example.springboot_webapp.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    private String secretKey = "tggvzgniylcbandcwdpdgdzvvniwzgaqsdasdasdasdagfgdfgdfg";
//    private String secretKey;
//
//    public JwtService() {
//        this.secretKey = generateSecretKey();
//    }
//
//    public String generateSecretKey(){
//        try {
//            KeyGenerator KeyGen = KeyGenerator.getInstance("HmacSHA256");
//            SecretKey secretKey = KeyGen.generateKey();
//            String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
//
//            return secretKeyString;
//        }catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims().add(claims).and()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try{
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build().parseSignedClaims(token).getPayload();
        }catch (JwtException e){
            System.out.println("Error verifying the Token");
            return null;
        }
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {

            return extractExpiration(token).before(new Date());

    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
