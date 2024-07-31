package com.github.zigcat.merchsite_microservice.main.repositories;

import com.github.zigcat.merchsite_microservice.main.entity.AppOrder;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<AppOrder, Integer> {
    Optional<AppOrder> findByUser(AppUser user);
}
