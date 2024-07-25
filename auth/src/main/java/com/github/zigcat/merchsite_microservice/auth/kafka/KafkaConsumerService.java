package com.github.zigcat.merchsite_microservice.auth.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zigcat.merchsite_microservice.auth.dto.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.JwtResponse;
import com.github.zigcat.merchsite_microservice.auth.services.AuthService;
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
    private final AppDeserializer<JwtRequest> jwtRequestDeserializer;
    private final AppSerializer<JwtResponse> jwtResponseSerializer;

    @Autowired
    public KafkaConsumerService(AuthService service,
                                AppDeserializer<JwtRequest> jwtRequestDeserializer,
                                AppSerializer<JwtResponse> jwtResponseSerializer) {
        this.service = service;
        this.jwtRequestDeserializer = jwtRequestDeserializer;
        this.jwtResponseSerializer = jwtResponseSerializer;
    }

    @KafkaListener(topics = "auth-request", containerFactory = "containerFactory")
    @SendTo
    public String listenAndAuthorize(String json, @Header(KafkaHeaders.REPLY_TOPIC) String replyTopic){
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
}
