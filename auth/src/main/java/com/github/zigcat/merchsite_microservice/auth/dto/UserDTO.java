package com.github.zigcat.merchsite_microservice.auth.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String role;
}
