package com.github.zigcat.merchsite_microservice.auth.services.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class AppDeserializer<T> {
    private final ObjectMapper objectMapper;
    private Class<T> type;

    public AppDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    public void setType(Class<T> type){
        this.type = type;
    }

    public T deserialize(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, type);
    }
}
