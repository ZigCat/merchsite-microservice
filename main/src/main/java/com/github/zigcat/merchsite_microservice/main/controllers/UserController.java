package com.github.zigcat.merchsite_microservice.main.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.main.dto.requests.JwtRequest;
import com.github.zigcat.merchsite_microservice.main.dto.responses.JwtResponse;
import com.github.zigcat.merchsite_microservice.main.dto.UserDTO;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.exceptions.AuthServerErrorException;
import com.github.zigcat.merchsite_microservice.main.exceptions.AuthenticationErrorException;
import com.github.zigcat.merchsite_microservice.main.exceptions.RecordAlreadyExistsException;
import com.github.zigcat.merchsite_microservice.main.exceptions.RecordNotFoundException;
import com.github.zigcat.merchsite_microservice.main.security.user.AppUserDetails;
import com.github.zigcat.merchsite_microservice.main.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("api/user")
@Slf4j
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getAll(){
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ResponseEntity<?> getById(@RequestParam Integer id){
        try {
            AppUser user = service.getById(id)
                    .orElseThrow(() -> new RecordNotFoundException("User not found"));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RecordNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody UserDTO request){
        try {
            AppUser user = service.register(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (RecordAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        try {
            JwtResponse response = service.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (JsonProcessingException | ExecutionException | InterruptedException | AuthServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AuthenticationErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UserDTO request,
                                    @AuthenticationPrincipal AppUserDetails userDetails){
        try {
            AppUser user = service.update(request, userDetails);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AuthenticationErrorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Integer id,
                                    @AuthenticationPrincipal AppUserDetails userDetails){
        try {
            service.delete(id, userDetails);
            return ResponseEntity.ok().build();
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AuthenticationErrorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
