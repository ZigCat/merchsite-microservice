package com.github.zigcat.merchsite_microservice.auth.services.jackson;

import com.github.zigcat.merchsite_microservice.auth.dto.AuthRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.JwtRequest;
import com.github.zigcat.merchsite_microservice.auth.dto.JwtResponse;
import com.github.zigcat.merchsite_microservice.auth.entity.AppUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public AppSerializer<JwtResponse> jwtResponseSerializer(){
        return new AppSerializer<>();
    }

    @Bean
    public AppSerializer<AppUser> userSerializer(){
        return new AppSerializer<>();
    }

    @Bean
    public AppDeserializer<JwtRequest> jwtRequestDeserializer(){
        AppDeserializer<JwtRequest> deserializer = new AppDeserializer<>();
        deserializer.setType(JwtRequest.class);
        return deserializer;
    }

    @Bean
    public AppDeserializer<AuthRequest> authRequestDeserializer(){
        AppDeserializer<AuthRequest> deserializer = new AppDeserializer<>();
        deserializer.setType(AuthRequest.class);
        return deserializer;
    }
}
