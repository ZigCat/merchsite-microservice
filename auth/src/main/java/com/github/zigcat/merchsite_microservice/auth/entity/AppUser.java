package com.github.zigcat.merchsite_microservice.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.zigcat.merchsite_microservice.auth.dto.enums.Role;
import com.github.zigcat.merchsite_microservice.auth.services.LocalDateToStringConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @SequenceGenerator(
            name = "userSequence",
            sequenceName = "userSequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "userSequence"
    )
    private Integer id;
    private String fname;
    private String lname;
    private String email;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Convert(converter = LocalDateToStringConverter.class)
    private LocalDate creationDate;

    public AppUser(String fname, String lname, String email, String password, Role role, LocalDate creationDate) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.creationDate = creationDate;
    }
}
