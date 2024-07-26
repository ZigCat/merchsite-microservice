package com.github.zigcat.merchsite_microservice.main.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.main.dto.requests.JwtRequest;
import com.github.zigcat.merchsite_microservice.main.dto.responses.JwtResponse;
import com.github.zigcat.merchsite_microservice.main.dto.UserDTO;
import com.github.zigcat.merchsite_microservice.main.entity.enums.Role;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.kafka.KafkaProducerService;
import com.github.zigcat.merchsite_microservice.main.repositories.UserRepository;
import com.github.zigcat.merchsite_microservice.main.security.user.AppUserDetails;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppDeserializer;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppSerializer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final KafkaProducerService kafkaProducerService;
    private final AppSerializer<JwtRequest> jwtRequestSerializer;
    private final AppDeserializer<JwtResponse> jwtResponseDeserializer;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repository,
                       KafkaProducerService kafkaProducerService,
                       AppSerializer<JwtRequest> jwtRequestSerializer,
                       AppDeserializer<JwtResponse> jwtResponseDeserializer,
                       PasswordEncoder encoder) {
        this.repository = repository;
        this.kafkaProducerService = kafkaProducerService;
        this.jwtRequestSerializer = jwtRequestSerializer;
        this.jwtResponseDeserializer = jwtResponseDeserializer;
        this.encoder = encoder;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<AppUser> getByEmail(String email){
        return Optional.ofNullable(repository.getByEmail(email));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<AppUser> getById(Integer id){
        return repository.findById(id);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public List<AppUser> getAll(){
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public AppUser save(@NonNull UserDTO user){
        return repository.save(new AppUser(
                user.getFname(),
                user.getLname(),
                user.getEmail(),
                encoder.encode(user.getPassword()),
                Role.valueOf(user.getRole()),
                LocalDate.now()));
    }

    @Transactional(rollbackFor = DuplicateKeyException.class)
    public AppUser register(UserDTO request) {
        if(getByEmail(request.getEmail()).isEmpty()){
            return save(request);
        }
        throw new DuplicateKeyException("User with the same email already exists in database");
    }

    public JwtResponse login(JwtRequest request) throws JsonProcessingException, ExecutionException, InterruptedException {
        String requestJson = jwtRequestSerializer.serialize(request);
        String responseJson = kafkaProducerService.sendUserForLogin(requestJson);
        return jwtResponseDeserializer.deserialize(responseJson);
    }

    @Transactional(rollbackFor = {AuthException.class, EntityNotFoundException.class})
    public AppUser update(UserDTO request, AppUserDetails userDetails) throws AuthException {
        AppUser user = getByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(user.getEmail().equals(userDetails.getUsername())){
            return save(request);
        }
        throw new AuthException("Unauthorized access to user");
    }

    @Transactional(rollbackFor = {AuthException.class, EntityNotFoundException.class})
    public void delete(Integer id, AppUserDetails userDetails) throws AuthException {
        AppUser user = getById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(user.getEmail().equals(userDetails.getUsername())
                || userDetails.getUser().getRole().equals(Role.ADMIN)){
            repository.deleteById(id);
        } else {
            throw new AuthException("Unauthorized access to user");
        }
    }
}
