package com.github.zigcat.merchsite_microservice.auth.services;

import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import com.github.zigcat.merchsite_microservice.auth.repositories.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;

    @Getter
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repository){
        this.repository = repository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public Optional<AppUser> getByEmail(String email){
        return Optional.ofNullable(repository.getByEmail(email));
    }
}
