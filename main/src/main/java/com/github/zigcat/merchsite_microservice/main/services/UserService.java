package com.github.zigcat.merchsite_microservice.main.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.main.dto.JwtRequest;
import com.github.zigcat.merchsite_microservice.main.dto.JwtResponse;
import com.github.zigcat.merchsite_microservice.main.dto.UserDTO;
import com.github.zigcat.merchsite_microservice.main.dto.enums.Role;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.kafka.KafkaProducerService;
import com.github.zigcat.merchsite_microservice.main.repositories.UserRepository;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppDeserializer;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppSerializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public Optional<AppUser> getByEmail(String email){
        return Optional.ofNullable(repository.getByEmail(email));
    }

    public AppUser saveUser(@NonNull AppUser user){
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
                    isolation = Isolation.SERIALIZABLE)
    public AppUser register(UserDTO request) {
        if(getByEmail(request.getEmail()).isEmpty()){
            return saveUser(new AppUser(request.getFname(),
                    request.getLname(),
                    request.getEmail(),
                    request.getPassword(),
                    Role.valueOf(request.getRole()),
                    LocalDate.now()));
        }
        throw new DuplicateKeyException("User with the same email already exists in database");
    }

    public JwtResponse login(JwtRequest request) throws JsonProcessingException, ExecutionException, InterruptedException {
        String requestJson = jwtRequestSerializer.serialize(request);
        String responseJson = kafkaProducerService.sendUserForAuth(requestJson);
        return jwtResponseDeserializer.deserialize(responseJson);
    }
}
