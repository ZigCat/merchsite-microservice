package com.github.zigcat.merchsite_microservice.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class JwtRequest {
    private String email;
    private String password;
}
