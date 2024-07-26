package com.github.zigcat.merchsite_microservice.main.dto.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    private String email;
    private String password;
}