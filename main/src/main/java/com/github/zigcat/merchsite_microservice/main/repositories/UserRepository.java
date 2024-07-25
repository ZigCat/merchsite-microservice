package com.github.zigcat.merchsite_microservice.main.repositories;

import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {
    AppUser getByEmail(String email);
}
