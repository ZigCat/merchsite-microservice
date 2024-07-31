package com.github.zigcat.merchsite_microservice.main.controllers;

import com.github.zigcat.merchsite_microservice.main.dto.OrderDTO;
import com.github.zigcat.merchsite_microservice.main.entity.AppOrder;
import com.github.zigcat.merchsite_microservice.main.exceptions.AuthenticationErrorException;
import com.github.zigcat.merchsite_microservice.main.exceptions.RecordNotFoundException;
import com.github.zigcat.merchsite_microservice.main.security.user.AppUserDetails;
import com.github.zigcat.merchsite_microservice.main.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/order")
@Slf4j
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AppOrder>> getAll(){
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ResponseEntity<?> getById(@RequestParam Integer id){
        try{
            AppOrder order = service.getById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderDTO request,
                                           @AuthenticationPrincipal AppUserDetails userDetails){
        try {
            AppOrder order = service.save(request, userDetails);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AuthenticationErrorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody OrderDTO request){
        try {
            AppOrder order = service.update(request);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam Integer id){
        try {
            log.info("DELETING ORDER");
            service.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
