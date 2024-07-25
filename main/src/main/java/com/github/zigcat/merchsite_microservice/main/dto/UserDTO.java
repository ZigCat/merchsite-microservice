package com.github.zigcat.merchsite_microservice.main.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDTO {
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String role;
}