package com.github.zigcat.merchsite_microservice.main.dto;

import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
    private AppUser user;
}