package com.github.zigcat.merchsite_microservice.auth.repositories;

import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {
    AppUser getByEmail(String email);
}
