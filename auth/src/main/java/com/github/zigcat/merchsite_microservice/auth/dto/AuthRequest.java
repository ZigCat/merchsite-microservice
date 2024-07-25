package com.github.zigcat.merchsite_microservice.auth.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String token;
}
