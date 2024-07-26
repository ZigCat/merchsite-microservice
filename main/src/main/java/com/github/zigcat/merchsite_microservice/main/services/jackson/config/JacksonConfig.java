package com.github.zigcat.merchsite_microservice.main.services.jackson.config;

import com.github.zigcat.merchsite_microservice.main.dto.requests.AuthRequest;
import com.github.zigcat.merchsite_microservice.main.dto.requests.JwtRequest;
import com.github.zigcat.merchsite_microservice.main.dto.responses.JwtResponse;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppDeserializer;
import com.github.zigcat.merchsite_microservice.main.services.jackson.AppSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public AppSerializer<JwtRequest> jwtRequestSerializer(){
        return new AppSerializer<>();
    }

    @Bean
    public AppSerializer<AuthRequest> authRequestSerializer(){
        return new AppSerializer<>();
    }

    @Bean
    public AppDeserializer<AppUser> userDeserializer(){
        AppDeserializer<AppUser> deserializer = new AppDeserializer<>();
        deserializer.setType(AppUser.class);
        return deserializer;
    }

    @Bean
    public AppDeserializer<JwtResponse> jwtResponseDeserializer(){
        AppDeserializer<JwtResponse> deserializer = new AppDeserializer<>();
        deserializer.setType(JwtResponse.class);
        return deserializer;
    }
}
