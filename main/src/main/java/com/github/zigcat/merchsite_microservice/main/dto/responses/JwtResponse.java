package com.github.zigcat.merchsite_microservice.main.dto.responses;

import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
    private AppUser user;
}