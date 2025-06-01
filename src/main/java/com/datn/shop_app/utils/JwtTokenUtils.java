package com.datn.shop_app.utils;

import com.datn.shop_app.entity.Token;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.exception.InvalidParamException;
import com.datn.shop_app.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final TokenRepository tokenRepository;

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1 ngày (24 giờ)

    public String generateToken(User user) throws InvalidParamException {
        //properties => claims
        Map<String, Object> claims = new HashMap<>();
        // Add subject identifier (phone number or email)
        String subject = getSubject(user);
        claims.put("subject", subject);
        // Add user ID
        claims.put("userId", user.getId());
        try{
            String token = Jwts.builder()
                               .setClaims(claims)
                               .setSubject(subject)
                               .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                               .signWith(getSignInKey(), Jwts.SIG.HS256)
                               .compact();
            return token;
        }catch (Exception e){
            throw new InvalidParamException("Cannot create jwt token, Error message: " + e.getMessage());
        }
    }


    private static String getSubject(User user) {
        // Determine subject identifier (phone number or email)
        String subject = user.getPhoneNumber();
        if (subject == null || subject.isBlank()) {
            // If phone number is null or blank, use email as subject
            subject = user.getEmail();
        }
        return subject;
    }

    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()  // Khởi tạo JwtParserBuilder
                .verifyWith(getSignInKey())  // Sử dụng verifyWith() để thiết lập signing key
                .build()  // Xây dựng JwtParser
                .parseSignedClaims(token)  // Phân tích token đã ký
                .getPayload();  // Lấy phần body của JWT, chứa claims
    }



    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    public String getSubject(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, User userDetails) {
        try {
            String subject = extractClaim(token, Claims::getSubject);
            //subject is phoneNumber or email
            Token existingToken = tokenRepository.findByToken(token);
            if(existingToken == null || existingToken.getRevoked() == true || !userDetails.getActive()) {
                return false;
            }
            return (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (MalformedJwtException e) {
//            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
//            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
//            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Kiểm tra token hợp lệ với email
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * // Tạo token có thời hạn 1 ngày với email
     * @param email
     * @return
     */
    public String generateTokenWithEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Giải mã token để lấy email
     * @param token
     * @return
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
