package com.github.zigcat.merchsite_microservice.main.services.jackson;

import com.github.zigcat.merchsite_microservice.main.dto.AuthRequest;
import com.github.zigcat.merchsite_microservice.main.dto.JwtRequest;
import com.github.zigcat.merchsite_microservice.main.dto.JwtResponse;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
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
