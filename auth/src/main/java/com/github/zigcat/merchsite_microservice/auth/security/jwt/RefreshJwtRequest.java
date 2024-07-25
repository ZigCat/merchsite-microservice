package com.github.zigcat.merchsite_microservice.auth.security.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtRequest {
    private String token;
}
