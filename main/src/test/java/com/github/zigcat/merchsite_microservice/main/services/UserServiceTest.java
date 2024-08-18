package com.github.zigcat.merchsite_microservice.main.services;

import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.entity.enums.Role;
import com.github.zigcat.merchsite_microservice.main.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService service;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getByEmail(){
        AppUser user = new AppUser(1,
                "John",
                "Doe",
                "jdoe@example.com",
                "123456",
                Role.USER,
                LocalDate.now());
        when(userRepository.findByEmail("jdoe@example.com"))
                .thenReturn(Optional.of(user));
        Optional<AppUser> result = service.getByEmail("jdoe@example.com");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1))
                .findByEmail(anyString());
    }
}
