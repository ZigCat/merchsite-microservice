package com.github.zigcat.merchsite_microservice.auth.services;

import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import com.github.zigcat.merchsite_microservice.auth.exceptions.RecordNotFoundException;
import com.github.zigcat.merchsite_microservice.auth.exceptions.WrongJwtException;
import com.github.zigcat.merchsite_microservice.auth.exceptions.WrongPasswordException;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.JwtProvider;
import com.github.zigcat.merchsite_microservice.auth.dto.requests.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.responses.JwtResponse;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class AuthService {
    private final UserService service;
    private final JwtProvider jwtProvider;
    private HashMap<String, String> refreshStorage = new HashMap<>();

    @Autowired
    public AuthService(UserService service, JwtProvider provider){
        this.service = service;
        this.jwtProvider = provider;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public JwtResponse login(@NonNull JwtRequest request) throws RecordNotFoundException, WrongPasswordException {
        log.info("Getting User by email...");
        final AppUser user = service.getByEmail(request.getEmail())
                .orElseThrow(() -> new RecordNotFoundException("User"));
        log.info("User received, checking password...");
        if (service.getEncoder().matches(request.getPassword(), user.getPassword())) {
            log.info("Passwords match, generating tokens");
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return new JwtResponse(accessToken, refreshToken, user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public boolean validateToken(String token, TokenType type) throws IllegalArgumentException, WrongJwtException {
        if(type == TokenType.ACCESS){
            return jwtProvider.validateAccessToken(token);
        } else if (type == TokenType.REFRESH){
            return jwtProvider.validateRefreshToken(token);
        } else {
            throw new WrongJwtException();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JwtResponse getAccessToken(String token) throws NoSuchElementException, WrongJwtException {
        if(jwtProvider.validateRefreshToken(token)){
            Claims claims = jwtProvider.getRefreshClaims(token);
            String email = claims.getSubject();
            String savedRefreshToken = refreshStorage.get(email);
            if(savedRefreshToken != null && savedRefreshToken.equals(token)){
                AppUser user = service.getByEmail(email)
                        .orElseThrow(WrongJwtException::new);
                String accessToken = jwtProvider.generateAccessToken(user);
                String refreshToken = jwtProvider.generateRefreshToken(user);
                return new JwtResponse(accessToken, refreshToken, user);
            }
        }
        throw new WrongJwtException();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JwtResponse getRefreshToken(String token) throws NoSuchElementException, WrongJwtException {
        if(jwtProvider.validateRefreshToken(token)){
            Claims claims = jwtProvider.getRefreshClaims(token);
            String email = claims.getSubject();
            String savedRefreshToken = refreshStorage.get(email);
            if(savedRefreshToken != null && savedRefreshToken.equals(token)){
                AppUser user = service.getByEmail(email)
                        .orElseThrow(WrongJwtException::new);
                String accessToken = jwtProvider.generateAccessToken(user);
                String refreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getEmail(), refreshToken);
                return new JwtResponse(accessToken, refreshToken, user);
            }
        }
        throw new WrongJwtException();
    }
}
