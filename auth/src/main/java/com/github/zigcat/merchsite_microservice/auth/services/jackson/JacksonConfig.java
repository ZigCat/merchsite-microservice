package com.github.zigcat.merchsite_microservice.auth.services.jackson;

import com.github.zigcat.merchsite_microservice.auth.dto.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.JwtResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public AppSerializer<JwtRequest> jwtRequestSerializer(){
        return new AppSerializer<>();
    }

    @Bean
    public AppSerializer<JwtResponse> jwtResponseSerializer(){
        return new AppSerializer<>();
    }

    @Bean
    public AppDeserializer<JwtRequest> jwtRequestDeserializer(){
        AppDeserializer<JwtRequest> deserializer = new AppDeserializer<>();
        deserializer.setType(JwtRequest.class);
        return deserializer;
    }

    @Bean
    public AppDeserializer<JwtResponse> jwtResponseDeserializer(){
        AppDeserializer<JwtResponse> deserializer = new AppDeserializer<>();
        deserializer.setType(JwtResponse.class);
        return deserializer;
    }
}
