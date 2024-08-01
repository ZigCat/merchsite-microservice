package com.github.zigcat.merchsite_microservice.auth.dto.responses;

import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
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
