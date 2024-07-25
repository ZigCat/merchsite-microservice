package com.github.zigcat.merchsite_microservice.main.services.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class AppSerializer<T> {
    private final ObjectMapper objectMapper;

    public AppSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    public String serialize(T t) throws JsonProcessingException {
        return objectMapper.writeValueAsString(t);
    }
}
