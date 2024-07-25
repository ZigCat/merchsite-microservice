package com.github.zigcat.merchsite_microservice.auth.security.jwt;

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
