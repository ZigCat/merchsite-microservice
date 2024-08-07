package com.github.zigcat.merchsite_microservice.auth.controllers;

import com.github.zigcat.merchsite_microservice.auth.dto.requests.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.responses.JwtResponse;
import com.github.zigcat.merchsite_microservice.auth.exceptions.RecordNotFoundException;
import com.github.zigcat.merchsite_microservice.auth.exceptions.WrongJwtException;
import com.github.zigcat.merchsite_microservice.auth.exceptions.WrongPasswordException;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.TokenType;
import com.github.zigcat.merchsite_microservice.auth.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("api/auth")
public class AuthController {
    private AuthService service;

    @Autowired
    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        try {
            JwtResponse response = service.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (WrongPasswordException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RecordNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token,
                                           @RequestParam TokenType type){
        boolean isValid = false;
        try {
            service.validateToken(token, type);
            return ResponseEntity.ok().build();
        } catch (WrongJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestParam String token){
        try {
            JwtResponse response = service.getRefreshToken(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (WrongJwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }
}
