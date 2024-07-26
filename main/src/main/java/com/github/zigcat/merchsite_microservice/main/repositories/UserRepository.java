package com.github.zigcat.merchsite_microservice.main.repositories;

import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);
}
