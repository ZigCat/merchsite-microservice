package com.github.zigcat.merchsite_microservice.main.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.main.dto.JwtRequest;
import com.github.zigcat.merchsite_microservice.main.dto.JwtResponse;
import com.github.zigcat.merchsite_microservice.main.dto.UserDTO;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("api/user")
@Slf4j
public class AppUserController {
    private final UserService service;

    @Autowired
    public AppUserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody UserDTO request){
        try {
            AppUser user = service.register(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (DuplicateKeyException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        try {
            JwtResponse response = service.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
