package com.github.zigcat.merchsite_microservice.main.services;

import com.github.zigcat.merchsite_microservice.main.dto.OrderDTO;
import com.github.zigcat.merchsite_microservice.main.entity.AppOrder;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.exceptions.AuthenticationErrorException;
import com.github.zigcat.merchsite_microservice.main.exceptions.RecordNotFoundException;
import com.github.zigcat.merchsite_microservice.main.repositories.OrderRepository;
import com.github.zigcat.merchsite_microservice.main.security.user.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService extends EntityService<AppOrder>{
    private final OrderRepository orderRepository;
    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository repository,
                        UserService userService) {
        super(repository, AppOrder.class);
        this.orderRepository = repository;
        this.userService = userService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<AppOrder> getByUser(AppUser user){
        return orderRepository.findByUser(user);
    }

    @Transactional(rollbackFor = {AuthenticationErrorException.class, RecordNotFoundException.class})
    public AppOrder save(OrderDTO request, AppUserDetails userDetails) throws RecordNotFoundException, AuthenticationErrorException {
        AppUser user = userService.getById(request.getUser())
                .orElseThrow(() -> new RecordNotFoundException("User"));
        if(userDetails.getUsername().equals(user.getEmail())){
            return repository.save(new AppOrder(user));
        }
        throw new AuthenticationErrorException("Order");
    }

    @Transactional(rollbackFor = {RecordNotFoundException.class})
    public AppOrder update(OrderDTO request) throws RecordNotFoundException {
        AppUser user = userService.getById(request.getUser())
                .orElseThrow(() -> new RecordNotFoundException("User"));
        return repository.save(new AppOrder(user));
    }
}
