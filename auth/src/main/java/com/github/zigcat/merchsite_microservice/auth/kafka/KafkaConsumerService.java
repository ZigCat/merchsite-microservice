package com.github.zigcat.merchsite_microservice.auth.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.auth.dto.requests.AuthRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.requests.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.responses.JwtResponse;
import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import com.github.zigcat.merchsite_microservice.auth.exceptions.RecordNotFoundException;
import com.github.zigcat.merchsite_microservice.auth.exceptions.WrongJwtException;
import com.github.zigcat.merchsite_microservice.auth.exceptions.WrongPasswordException;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.JwtProvider;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.TokenType;
import com.github.zigcat.merchsite_microservice.auth.services.AuthService;
import com.github.zigcat.merchsite_microservice.auth.services.UserService;
import com.github.zigcat.merchsite_microservice.auth.services.jackson.AppDeserializer;
import com.github.zigcat.merchsite_microservice.auth.services.jackson.AppSerializer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class KafkaConsumerService {
    private final AuthService service;
    private final UserService userService;
    private final JwtProvider provider;
    private final AppSerializer<AppUser> userSerializer;
    private final AppSerializer<JwtResponse> jwtResponseSerializer;
    private final AppDeserializer<AuthRequest> authRequestDeserializer;
    private final AppDeserializer<JwtRequest> jwtRequestDeserializer;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaConsumerService(AuthService service,
                                UserService userService,
                                JwtProvider provider,
                                AppSerializer<AppUser> userSerializer,
                                AppSerializer<JwtResponse> jwtResponseSerializer,
                                AppDeserializer<AuthRequest> authRequestDeserializer,
                                AppDeserializer<JwtRequest> jwtRequestDeserializer,
                                KafkaTemplate<String, String> kafkaTemplate) {
        this.service = service;
        this.userService = userService;
        this.provider = provider;
        this.userSerializer = userSerializer;
        this.jwtResponseSerializer = jwtResponseSerializer;
        this.authRequestDeserializer = authRequestDeserializer;
        this.jwtRequestDeserializer = jwtRequestDeserializer;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "login-request", containerFactory = "containerFactory")
    @SendTo
    public String listenAndLogin(String json, @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic){
        log.info("----------------------");
        log.info("Received LOGIN request");
        try {
            log.info("Deserializing JwtRequest...");
            JwtRequest request = jwtRequestDeserializer.deserialize(json);
            log.info("Performing login method...");
            JwtResponse response = service.login(request);
            log.warn("Serializing and sending JwtResponse to MAIN server");
            return jwtResponseSerializer.serialize(response);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            kafkaTemplate.send("auth-error",
                    LocalDateTime.now().toString()+" 500: "+e.getMessage());
            return "Error 500";
        } catch (WrongPasswordException e) {
            log.error(e.getMessage());
            kafkaTemplate.send("auth-error",
                    LocalDateTime.now().toString()+" 401: "+e.getMessage());
            return "Error 401";
        } catch (RecordNotFoundException e){
            log.error(e.getMessage());
            kafkaTemplate.send("auth-error",
                    LocalDateTime.now().toString()+" 404: "+e.getMessage());
            return "Error 404";
        }
    }

    @KafkaListener(topics = "auth-request", containerFactory = "containerFactory")
    @SendTo
    public String listenAndAuthorize(String json, @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic){
        log.info("----------------------");
        log.info("Received AUTH request");
        try {
            log.info("Deserializing AuthRequest...");
            AuthRequest request = authRequestDeserializer.deserialize(json);
            log.info("Checking token validity...");
            if(service.validateToken(request.getToken(), TokenType.ACCESS)){
                log.info("Token is valid, getting User from subject...");
                AppUser user = userService.getByEmail(
                        provider.getAccessSubject(request.getToken()))
                        .orElseThrow(WrongJwtException::new);
                log.warn("User is present, sending User to MAIN server");
                return userSerializer.serialize(user);
            } else {
                log.warn("Token is invalid, returning null User");
                return userSerializer.serialize(new AppUser());
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            kafkaTemplate.send("auth-error",
                    LocalDateTime.now()+" 500: "+e.getMessage());
            return "Error 500";
        } catch (WrongJwtException e){
            log.error(e.getMessage());
            kafkaTemplate.send("auth-error",
                    LocalDateTime.now()+" 401: "+e.getMessage());
            return "Error 401";
        }
    }
}
