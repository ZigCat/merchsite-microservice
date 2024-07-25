package com.github.zigcat.merchsite_microservice.auth.security.jwt;

import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ){
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull AppUser user){
        Instant accessExpirationInstant = LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("roles", user.getRole())
                .claim("id", user.getId())
                .compact();
    }

    public String generateRefreshToken(@NonNull AppUser user){
        Instant refreshExpirationInstant = LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    private boolean validateToken(@NonNull String token, SecretKey secret){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }  catch (ExpiredJwtException e) {
            log.error("Token expired");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported jwt");
        } catch (MalformedJwtException e) {
            log.error("Malformed jwt");
        } catch (SignatureException e) {
            log.error("Invalid signature");
        } catch (Exception e) {
            log.error("invalid token");
        }
        return false;
    }

    private Claims getClaims(@NonNull String token, SecretKey key){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateAccessToken(@NonNull String token){
        return validateToken(token, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String token){
        return validateToken(token, jwtRefreshSecret);
    }

    public Claims getAccessClaims(@NonNull String token){
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token){
        return getClaims(token, jwtRefreshSecret);
    }

    public String getAccessSubject(@NonNull String token){
        return getAccessClaims(token).getSubject();
    }
}
