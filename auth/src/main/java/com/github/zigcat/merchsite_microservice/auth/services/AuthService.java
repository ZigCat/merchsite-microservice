package com.github.zigcat.merchsite_microservice.auth.services;

import com.github.zigcat.merchsite_microservice.auth.dto.UserDTO;
import com.github.zigcat.merchsite_microservice.auth.dto.enums.Role;
import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.JwtProvider;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.JwtResponse;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public JwtResponse login(@NonNull JwtRequest request) throws AuthException, EntityNotFoundException {
        final AppUser user = service.getByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (service.getEncoder().matches(request.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return new JwtResponse(accessToken, refreshToken, user);
        } else {
            throw new AuthException("Wrong password");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = AuthException.class)
    public JwtResponse register(@NonNull UserDTO request) throws AuthException {
        Optional<AppUser> existingUser = service.getByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new AuthException("Username is already taken");
        }
        AppUser user = new AppUser();
        user.setFname(request.getFname());
        user.setLname(request.getLname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        if(request.getRole() == null){
            user.setRole(Role.USER);
        } else {
            user.setRole(Role.valueOf(request.getRole()));
        }
        user.setCreationDate(LocalDate.now());
        AppUser savedUser = service.saveUser(user);
        final String accessToken = jwtProvider.generateAccessToken(savedUser);
        final String refreshToken = jwtProvider.generateRefreshToken(savedUser);
        refreshStorage.put(user.getEmail(), refreshToken);
        return new JwtResponse(accessToken, refreshToken, savedUser);
    }

    public boolean validateToken(String token, TokenType type) throws IllegalArgumentException {
        if(type == TokenType.ACCESS){
            return jwtProvider.validateAccessToken(token);
        } else if (type == TokenType.REFRESH){
            return jwtProvider.validateRefreshToken(token);
        } else {
            throw new IllegalArgumentException("Unknown token type "+type);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JwtResponse getAccessToken(String token) throws AuthException, NoSuchElementException {
        if(jwtProvider.validateRefreshToken(token)){
            Claims claims = jwtProvider.getRefreshClaims(token);
            String email = claims.getSubject();
            String savedRefreshToken = refreshStorage.get(email);
            if(savedRefreshToken != null && savedRefreshToken.equals(token)){
                AppUser user = service.getByEmail(email)
                        .orElseThrow(() -> new AuthException("Wrong Jwt token"));
                String accessToken = jwtProvider.generateAccessToken(user);
                String refreshToken = jwtProvider.generateRefreshToken(user);
                return new JwtResponse(accessToken, refreshToken, user);
            }
        }
        throw new NoSuchElementException("Jwt token not found");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JwtResponse getRefreshToken(String token) throws AuthException, NoSuchElementException {
        if(jwtProvider.validateRefreshToken(token)){
            Claims claims = jwtProvider.getRefreshClaims(token);
            String email = claims.getSubject();
            String savedRefreshToken = refreshStorage.get(email);
            if(savedRefreshToken != null && savedRefreshToken.equals(token)){
                AppUser user = service.getByEmail(email)
                        .orElseThrow(() -> new AuthException("Wrong Jwt token"));
                String accessToken = jwtProvider.generateAccessToken(user);
                String refreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getEmail(), refreshToken);
                return new JwtResponse(accessToken, refreshToken, user);
            }
        }
        throw new NoSuchElementException("Jwt token not found");
    }
}
