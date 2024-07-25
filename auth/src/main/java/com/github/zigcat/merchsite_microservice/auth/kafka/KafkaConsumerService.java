package com.github.zigcat.merchsite_microservice.auth.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.auth.dto.AuthRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.JwtResponse;
import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import com.github.zigcat.merchsite_microservice.auth.security.jwt.JwtProvider;
import com.github.zigcat.merchsite_microservice.auth.services.AuthService;
import com.github.zigcat.merchsite_microservice.auth.services.UserService;
import com.github.zigcat.merchsite_microservice.auth.services.jackson.AppDeserializer;
import com.github.zigcat.merchsite_microservice.auth.services.jackson.AppSerializer;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final AuthService service;
    private final UserService userService;
    private final JwtProvider provider;
    private final AppSerializer<AppUser> userSerializer;
    private final AppSerializer<JwtResponse> jwtResponseSerializer;
    private final AppDeserializer<AuthRequest> authRequestDeserializer;
    private final AppDeserializer<JwtRequest> jwtRequestDeserializer;

    @Autowired
    public KafkaConsumerService(AuthService service,
                                UserService userService,
                                JwtProvider provider,
                                AppSerializer<AppUser> userSerializer,
                                AppSerializer<JwtResponse> jwtResponseSerializer,
                                AppDeserializer<AuthRequest> authRequestDeserializer,
                                AppDeserializer<JwtRequest> jwtRequestDeserializer) {
        this.service = service;
        this.userService = userService;
        this.provider = provider;
        this.userSerializer = userSerializer;
        this.jwtResponseSerializer = jwtResponseSerializer;
        this.authRequestDeserializer = authRequestDeserializer;
        this.jwtRequestDeserializer = jwtRequestDeserializer;
    }

    @KafkaListener(topics = "login-request", containerFactory = "containerFactory")
    @SendTo
    public String listenAndLogin(String json, @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic){
        try {
            JwtRequest request = jwtRequestDeserializer.deserialize(json);
            JwtResponse response = service.login(request);
            return jwtResponseSerializer.serialize(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "auth-request", containerFactory = "containerFactory")
    @SendTo
    public String listenAndAuthorize(String json, @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic){
        try {
            AuthRequest request = authRequestDeserializer.deserialize(json);
            if(provider.validateAccessToken(request.getToken())){
                AppUser user = userService.getByEmail(
                        provider.getAccessSubject(request.getToken())).get();
                return userSerializer.serialize(user);
            } else {
                return userSerializer.serialize(new AppUser());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
