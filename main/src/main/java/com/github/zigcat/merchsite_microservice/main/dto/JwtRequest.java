package com.github.zigcat.merchsite_microservice.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class JwtRequest {
    private String email;
    private String password;
}