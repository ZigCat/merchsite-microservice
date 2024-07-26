package com.github.zigcat.merchsite_microservice.main.services;

import com.github.zigcat.merchsite_microservice.main.dto.OrderDTO;
import com.github.zigcat.merchsite_microservice.main.entity.AppOrder;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.entity.enums.Role;
import com.github.zigcat.merchsite_microservice.main.repositories.OrderRepository;
import com.github.zigcat.merchsite_microservice.main.security.user.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository repository,
                        UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<AppOrder> getByUser(AppUser user){
        return Optional.ofNullable(repository.getByUser(user));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<AppOrder> getById(Integer id){
        return repository.findById(id);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public List<AppOrder> getAll(){
        return repository.findAll();
    }

    @Transactional(rollbackFor = {AuthException.class, EntityNotFoundException.class})
    public AppOrder save(OrderDTO request, AppUserDetails userDetails) throws AuthException {
        AppUser user = userService.getById(request.getUser())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(userDetails.getUsername().equals(user.getEmail())){
            return repository.save(new AppOrder(user));
        }
        throw new AuthException("Unauthorized access to order");
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public AppOrder update(OrderDTO request) {
        AppUser user = userService.getById(request.getUser())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return repository.save(new AppOrder(user));
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public void delete(Integer id) {
        AppOrder order = getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        repository.deleteById(id);
    }
}
