package com.github.zigcat.merchsite_microservice.auth.services.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class AppSerializer<T>{
    private final ObjectMapper objectMapper;

    public AppSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public String serialize(T t) throws JsonProcessingException {
        return objectMapper.writeValueAsString(t);
    }
}
