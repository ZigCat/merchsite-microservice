package com.github.zigcat.merchsite_microservice.main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zigcat.merchsite_microservice.main.dto.UserDTO;
import com.github.zigcat.merchsite_microservice.main.dto.requests.JwtRequest;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.entity.enums.Role;
import com.github.zigcat.merchsite_microservice.main.security.user.AppUserDetails;
import com.github.zigcat.merchsite_microservice.main.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private final String BASE_URL = "/api/user";
    private ObjectMapper objectMapper;
    private MockMvc mvc;

    @BeforeEach
    void setup(){
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        AppUser user = new AppUser(1,
                "John",
                "Doe",
                "jdoe@example.com",
                "123456",
                Role.USER,
                LocalDate.now());
        String userJson = objectMapper.writeValueAsString(List.of(user));
        when(service.getAll()).thenReturn(List.of(user));
        mvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(userJson));
        verify(service, times(1))
                .getAll();
    }

    @Test
    void getById() throws Exception {
        AppUser user = new AppUser(1,
                "John",
                "Doe",
                "jdoe@example.com",
                "123456",
                Role.USER,
                LocalDate.now());
        when(service.getById(1)).thenReturn(Optional.of(user));
        String userJson = objectMapper.writeValueAsString(user);
        mvc.perform(get(BASE_URL+"/id?id="+1))
                .andExpect(status().isOk())
                .andExpect(content().json(userJson));
        verify(service, times(1))
                .getById(1);
    }

    @Test
    void register() throws Exception {
        UserDTO user = new UserDTO("John",
                "Doe",
                "jdoe@example.com",
                "123456",
                Role.USER.toString());
        String userJson = objectMapper.writeValueAsString(user);
        mvc.perform(post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());
        verify(service, times(1))
                .register(user);
    }

    @Test
    void login() throws Exception {
        JwtRequest request = new JwtRequest("jdoe@example.com",
                "123456");
        String json = objectMapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        verify(service, times(1))
                .login(request);
    }

    @Test
    void update() throws Exception {
        UserDTO userDTO = new UserDTO("John",
                "Doe",
                "jdoe@example.com",
                "123456",
                Role.USER.toString());
        AppUser user = new AppUser(1,
                "John",
                "Doe",
                "jdoe@example.com",
                "123456",
                Role.USER,
                LocalDate.now());
        AppUserDetails userDetails = new AppUserDetails(user);
        String body = objectMapper.writeValueAsString(userDTO);
        mvc.perform(put(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(user(userDetails)))
                .andExpect(status().isOk());
        verify(service, times(1))
                .update(eq(userDTO), any(AppUserDetails.class));
    }

    @Test
    void deleteUser() throws Exception {
        AppUser user = new AppUser(1,
                "John",
                "Doe",
                "jdoe@example.com",
                "123456",
                Role.USER,
                LocalDate.now());
        AppUserDetails userDetails = new AppUserDetails(user);
        mvc.perform(delete(BASE_URL+"?id="+1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetails)))
                .andExpect(status().isOk());
        verify(service, times(1))
                .delete(eq(1), any(AppUserDetails.class));
    }
}
