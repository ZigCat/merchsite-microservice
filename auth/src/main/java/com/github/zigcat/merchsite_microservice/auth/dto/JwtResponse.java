package com.github.zigcat.merchsite_microservice.auth.dto;

import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
    private AppUser user;
}
